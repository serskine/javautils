package javautils.email;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class RfcMessageParser {

    private static final Pattern BOUNDARY_PATTERN =
            Pattern.compile("boundary\\s*=\\s*(?:\"([^\"]+)\"|([^;\\s]+))", Pattern.CASE_INSENSITIVE);
    private static final Pattern CHARSET_PATTERN =
            Pattern.compile("charset\\s*=\\s*(?:\"([^\"]+)\"|([^;\\s]+))", Pattern.CASE_INSENSITIVE);
    private static final Pattern FILENAME_PATTERN =
            Pattern.compile("(?:filename|name)\\s*=\\s*(?:\"([^\"]+)\"|([^;\\s]+))", Pattern.CASE_INSENSITIVE);
    private static final Pattern RFC2047_PATTERN =
            Pattern.compile("=\\?([^?]+)\\?([BbQq])\\?([^?]+)\\?=");

    private RfcMessageParser() {
    }

    static Email parse(String raw) {
        ParsedPart root = parsePart(raw);

        Map<String, String> headers = root.headers;
        String from = headers.getOrDefault("from", "unknown@localhost");
        String subject = headers.getOrDefault("subject", "");
        String replyTo = headers.get("reply-to");

        List<String> to = splitAddresses(headers.get("to"));
        if (to.isEmpty()) {
            to.add("undisclosed-recipients@localhost");
        }
        List<String> cc = splitAddresses(headers.get("cc"));
        List<String> bcc = splitAddresses(headers.get("bcc"));

        ExtractedContent content = new ExtractedContent();
        collectContent(root, content);

        if (content.textBody == null && content.htmlBody == null) {
            content.textBody = "";
        }

        Email.Builder builder = Email.builder()
                .from(from)
                .to(to)
                .subject(subject);

        for (String addr : cc) {
            builder.cc(addr);
        }
        for (String addr : bcc) {
            builder.bcc(addr);
        }
        if (replyTo != null && !replyTo.isBlank()) {
            builder.replyTo(replyTo);
        }
        if (content.textBody != null) {
            builder.textBody(content.textBody);
        }
        if (content.htmlBody != null) {
            builder.htmlBody(content.htmlBody);
        }
        for (File attachment : content.attachments) {
            builder.attachment(attachment);
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (!isStandardHeader(entry.getKey())) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }

        return builder.build();
    }

    private static ParsedPart parsePart(String raw) {
        int splitIdx = raw.indexOf("\r\n\r\n");
        int sepLen = 4;
        if (splitIdx == -1) {
            splitIdx = raw.indexOf("\n\n");
            sepLen = 2;
        }

        String headerSection = splitIdx >= 0 ? raw.substring(0, splitIdx) : raw;
        String bodySection = splitIdx >= 0 ? raw.substring(splitIdx + sepLen) : "";

        String unfolded = headerSection.replaceAll("\\r?\\n[ \\t]+", " ");
        Map<String, String> headers = new LinkedHashMap<>();

        for (String line : unfolded.split("\\r?\\n")) {
            int colon = line.indexOf(':');
            if (colon <= 0) continue;
            String key = line.substring(0, colon).trim().toLowerCase(Locale.ROOT);
            String val = decodeHeaderWords(line.substring(colon + 1).trim());
            headers.put(key, val);
        }

        ParsedPart part = new ParsedPart(headers, bodySection);
        String contentType = headers.getOrDefault("content-type", "text/plain");
        String boundary = extractParam(BOUNDARY_PATTERN, contentType);

        if (boundary != null && contentType.toLowerCase(Locale.ROOT).startsWith("multipart/")) {
            part.children.addAll(splitMultipart(bodySection, boundary));
        }

        return part;
    }

    private static List<ParsedPart> splitMultipart(String body, String boundary) {
        List<ParsedPart> parts = new ArrayList<>();
        String delimiter = "--" + boundary;
        String[] segments = body.split("\\r?\\n" + Pattern.quote(delimiter) + "(?:--)?(?:\\r?\\n|$)");

        for (int i = 0; i < segments.length; i++) {
            String seg = segments[i];
            if (i == 0 && !seg.startsWith(delimiter + "\r\n") && !seg.startsWith(delimiter + "\n")) {
                // Skip preamble before the first boundary
                int firstIdx = seg.indexOf(delimiter);
                if (firstIdx == -1) continue;
                seg = seg.substring(firstIdx);
            }
            if (seg.startsWith(delimiter + "--")) {
                continue;
            }
            if (seg.startsWith(delimiter + "\r\n")) {
                seg = seg.substring(delimiter.length() + 2);
            } else if (seg.startsWith(delimiter + "\n")) {
                seg = seg.substring(delimiter.length() + 1);
            }
            if (!seg.trim().isEmpty()) {
                parts.add(parsePart(seg));
            }
        }
        return parts;
    }

    private static void collectContent(ParsedPart part, ExtractedContent out) {
        if (!part.children.isEmpty()) {
            for (ParsedPart child : part.children) {
                collectContent(child, out);
            }
            return;
        }

        String contentType = part.headers.getOrDefault("content-type", "text/plain");
        String disposition = part.headers.getOrDefault("content-disposition", "");
        String encoding = part.headers.getOrDefault("content-transfer-encoding", "7bit").trim().toLowerCase(Locale.ROOT);
        String filename = extractParam(FILENAME_PATTERN, disposition);
        if (filename == null) {
            filename = extractParam(FILENAME_PATTERN, contentType);
        }

        boolean isAttachment = disposition.toLowerCase(Locale.ROOT).startsWith("attachment") || filename != null;
        byte[] decodedBytes = decodeBytes(part.rawBody, encoding);

        if (isAttachment) {
            File tempFile = writeAttachmentToTempFile(filename != null ? filename : "attachment.bin", decodedBytes);
            if (tempFile != null) {
                out.attachments.add(tempFile);
            }
            return;
        }

        Charset charset = resolveCharset(contentType);
        String text = new String(decodedBytes, charset);
        String lowerType = contentType.toLowerCase(Locale.ROOT);

        if (lowerType.contains("text/html")) {
            out.htmlBody = (out.htmlBody == null) ? text : out.htmlBody + "\n" + text;
        } else {
            out.textBody = (out.textBody == null) ? text : out.textBody + "\n" + text;
        }
    }

    private static byte[] decodeBytes(String rawBody, String encoding) {
        return switch (encoding) {
            case "base64" -> {
                String compact = rawBody.replaceAll("\\s+", "");
                yield Base64.getDecoder().decode(compact);
            }
            case "quoted-printable" -> decodeQuotedPrintable(rawBody);
            default -> rawBody.getBytes(StandardCharsets.ISO_8859_1);
        };
    }

    private static byte[] decodeQuotedPrintable(String input) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '=') {
                if (i + 2 < input.length() && input.charAt(i + 1) == '\r' && input.charAt(i + 2) == '\n') {
                    i += 2; // Soft line break
                } else if (i + 1 < input.length() && input.charAt(i + 1) == '\n') {
                    i += 1; // Soft LF break
                } else if (i + 2 < input.length()) {
                    int hi = Character.digit(input.charAt(i + 1), 16);
                    int lo = Character.digit(input.charAt(i + 2), 16);
                    if (hi != -1 && lo != -1) {
                        out.write((hi << 4) + lo);
                        i += 2;
                    } else {
                        out.write(c);
                    }
                } else {
                    out.write(c);
                }
            } else {
                out.write(c);
            }
        }
        return out.toByteArray();
    }

    private static File writeAttachmentToTempFile(String filename, byte[] data) {
        try {
            String safeName = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
            if (safeName.length() < 3) {
                safeName = "att_" + safeName;
            }
            int dot = safeName.lastIndexOf('.');
            String prefix = dot > 0 ? safeName.substring(0, dot) : safeName;
            String suffix = dot > 0 ? safeName.substring(dot) : ".bin";
            if (prefix.length() < 3) {
                prefix = "att_" + prefix;
            }
            File temp = File.createTempFile(prefix + "_", suffix);
            Files.write(temp.toPath(), data);
            return temp;
        } catch (IOException e) {
            throw new SmtpMailer.EmailException("Failed to extract attachment: " + filename, e);
        }
    }

    private static Charset resolveCharset(String contentType) {
        String name = extractParam(CHARSET_PATTERN, contentType);
        if (name != null) {
            try {
                return Charset.forName(name);
            } catch (Exception ignored) {
            }
        }
        return StandardCharsets.UTF_8;
    }

    private static String extractParam(Pattern pattern, String source) {
        if (source == null) return null;
        Matcher m = pattern.matcher(source);
        if (m.find()) {
            return m.group(1) != null ? m.group(1).trim() : m.group(2).trim();
        }
        return null;
    }

    private static String decodeHeaderWords(String input) {
        Matcher m = RFC2047_PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            Charset charset;
            try {
                charset = Charset.forName(m.group(1));
            } catch (Exception e) {
                charset = StandardCharsets.UTF_8;
            }
            String mode = m.group(2).toUpperCase(Locale.ROOT);
            String encoded = m.group(3);
            String replacement;
            if ("B".equals(mode)) {
                replacement = new String(Base64.getDecoder().decode(encoded.replaceAll("\\s+", "")), charset);
            } else {
                replacement = new String(decodeQuotedPrintable(encoded.replace('_', ' ')), charset);
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static List<String> splitAddresses(String raw) {
        List<String> list = new ArrayList<>();
        if (raw == null || raw.isBlank()) return list;
        for (String part : raw.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                list.add(trimmed);
            }
        }
        return list;
    }

    private static boolean isStandardHeader(String key) {
        return switch (key) {
            case "from", "to", "cc", "bcc", "reply-to", "subject",
                 "content-type", "content-transfer-encoding", "content-disposition" -> true;
            default -> false;
        };
    }

    private static final class ParsedPart {
        final Map<String, String> headers;
        final String rawBody;
        final List<ParsedPart> children = new ArrayList<>();

        ParsedPart(Map<String, String> headers, String rawBody) {
            this.headers = headers;
            this.rawBody = rawBody;
        }
    }

    private static final class ExtractedContent {
        String textBody;
        String htmlBody;
        final List<File> attachments = new ArrayList<>();
    }
}