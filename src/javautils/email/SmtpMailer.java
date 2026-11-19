package javautils.email;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SmtpMailer implements Mailer {

    private static final String CRLF = "\r\n";

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean useStartTls;
    private final boolean useImplicitSsl;

    /**
     * @param host           SMTP server hostname
     * @param port           SMTP server port (e.g., 25, 587 for STARTTLS, 465 for Implicit SSL)
     * @param username       Username for AUTH LOGIN (null or empty for no auth)
     * @param password       Password for AUTH LOGIN
     * @param useStartTls    Upgrade plain connection via STARTTLS (typically port 587)
     */
    public SmtpMailer(String host, int port, String username, String password, boolean useStartTls) {
        this(host, port, username, password, useStartTls, port == 465 && !useStartTls);
    }

    public SmtpMailer(String host, int port, String username, String password, boolean useStartTls, boolean useImplicitSsl) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.useStartTls = useStartTls;
        this.useImplicitSsl = useImplicitSsl;
    }

    @Override
    public void send(Email email) {
        try (SmtpConnection conn = new SmtpConnection()) {
            conn.connect();
            conn.authenticate();
            conn.sendEnvelope(email);
            conn.sendData(buildMimeMessage(email));
            conn.quit();
        } catch (IOException e) {
            throw new EmailException("Failed to send email via SMTP to " + email.getTo(), e);
        }
    }

    @Override
    public List<Email> getMessages() {
        throw new UnsupportedOperationException("TODO: Implement.");
    }

    private String buildMimeMessage(Email email) throws IOException {
        StringBuilder sb = new StringBuilder();

        // Standard RFC 5322 Headers
        sb.append("Date: ").append(DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now())).append(CRLF);
        sb.append("From: ").append(email.getFrom()).append(CRLF);
        sb.append("To: ").append(String.join(", ", email.getTo())).append(CRLF);

        if (!email.getCc().isEmpty()) {
            sb.append("Cc: ").append(String.join(", ", email.getCc())).append(CRLF);
        }
        if (email.getReplyTo() != null) {
            sb.append("Reply-To: ").append(email.getReplyTo()).append(CRLF);
        }
        if (email.getSubject() != null) {
            sb.append("Subject: ").append(encodeHeaderValue(email.getSubject())).append(CRLF);
        }

        sb.append("Message-ID: <").append(UUID.randomUUID()).append("@").append(host).append(">").append(CRLF);
        sb.append("MIME-Version: 1.0").append(CRLF);

        // Custom Headers (excluding Bcc to maintain privacy)
        for (Map.Entry<String, String> header : email.getHeaders().entrySet()) {
            sb.append(header.getKey()).append(": ").append(header.getValue()).append(CRLF);
        }

        boolean hasText = email.getTextBody() != null;
        boolean hasHtml = email.getHtmlBody() != null;
        boolean hasAttachments = !email.getAttachments().isEmpty();

        // Simple Single-Part Message
        if (!hasAttachments && !(hasText && hasHtml)) {
            if (hasHtml) {
                appendBodyPart(sb, "text/html; charset=UTF-8", email.getHtmlBody());
            } else {
                appendBodyPart(sb, "text/plain; charset=UTF-8", email.getTextBody());
            }
            return sb.toString();
        }

        // Complex Multipart Message
        String mixedBoundary = "=_Mixed_" + UUID.randomUUID().toString().replace("-", "");
        sb.append("Content-Type: multipart/mixed; boundary=\"").append(mixedBoundary).append("\"").append(CRLF);
        sb.append(CRLF);

        // Text / HTML Body Section
        sb.append("--").append(mixedBoundary).append(CRLF);
        if (hasText && hasHtml) {
            String altBoundary = "=_Alt_" + UUID.randomUUID().toString().replace("-", "");
            sb.append("Content-Type: multipart/alternative; boundary=\"").append(altBoundary).append("\"").append(CRLF);
            sb.append(CRLF);

            sb.append("--").append(altBoundary).append(CRLF);
            appendBodyPart(sb, "text/plain; charset=UTF-8", email.getTextBody());
            sb.append(CRLF);

            sb.append("--").append(altBoundary).append(CRLF);
            appendBodyPart(sb, "text/html; charset=UTF-8", email.getHtmlBody());
            sb.append(CRLF);

            sb.append("--").append(altBoundary).append("--").append(CRLF);
        } else if (hasHtml) {
            appendBodyPart(sb, "text/html; charset=UTF-8", email.getHtmlBody());
            sb.append(CRLF);
        } else {
            appendBodyPart(sb, "text/plain; charset=UTF-8", email.getTextBody());
            sb.append(CRLF);
        }

        // File Attachments Section
        for (File file : email.getAttachments()) {
            sb.append("--").append(mixedBoundary).append(CRLF);
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            String fileName = encodeHeaderValue(file.getName());
            sb.append("Content-Type: ").append(mimeType).append("; name=\"").append(fileName).append("\"").append(CRLF);
            sb.append("Content-Disposition: attachment; filename=\"").append(fileName).append("\"").append(CRLF);
            sb.append("Content-Transfer-Encoding: base64").append(CRLF);
            sb.append(CRLF);
            sb.append(encodeBase64Mime(Files.readAllBytes(file.toPath()))).append(CRLF);
        }

        sb.append("--").append(mixedBoundary).append("--").append(CRLF);
        return sb.toString();
    }

    private void appendBodyPart(StringBuilder sb, String contentType, String content) {
        sb.append("Content-Type: ").append(contentType).append(CRLF);
        sb.append("Content-Transfer-Encoding: base64").append(CRLF);
        sb.append(CRLF);
        sb.append(encodeBase64Mime(content.getBytes(StandardCharsets.UTF_8))).append(CRLF);
    }

    private String encodeBase64Mime(byte[] bytes) {
        return Base64.getMimeEncoder(76, CRLF.getBytes(StandardCharsets.US_ASCII)).encodeToString(bytes);
    }

    private String encodeHeaderValue(String text) {
        // Use simple ASCII if all characters are standard printable ASCII
        boolean isAscii = text.chars().allMatch(c -> c >= 32 && c <= 126);
        if (isAscii) {
            return text;
        }
        // RFC 2047 UTF-8 Base64 encoding for non-ASCII subjects/filenames
        String b64 = Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
        return "=?UTF-8?B?" + b64 + "?=";
    }

    /**
     * Inner class managing the low-level SMTP Socket lifecycle and state machine.
     */
    private class SmtpConnection implements AutoCloseable {
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;

        void connect() throws IOException {
            if (useImplicitSsl) {
                SSLSocket sslSocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(host, port);
                sslSocket.startHandshake();
                this.socket = sslSocket;
            } else {
                this.socket = new Socket(host, port);
            }
            this.socket.setSoTimeout(30_000); // 30-second read timeout
            initStreams();

            expectCode(220); // Wait for server greeting

            String localHost = InetAddress.getLocalHost().getHostName();
            sendCommand("EHLO " + localHost, 250);

            if (useStartTls && !useImplicitSsl) {
                sendCommand("STARTTLS", 220);
                SSLSocket sslSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault())
                        .createSocket(socket, host, port, true);
                sslSocket.startHandshake();
                this.socket = sslSocket;
                initStreams();
                sendCommand("EHLO " + localHost, 250); // Re-issue EHLO over encrypted channel
            }
        }

        void authenticate() throws IOException {
            if (username != null && !username.isEmpty()) {
                sendCommand("AUTH LOGIN", 334);
                sendCommand(Base64.getEncoder().encodeToString(username.getBytes(StandardCharsets.UTF_8)), 334);
                sendCommand(Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8)), 235);
            }
        }

        void sendEnvelope(Email email) throws IOException {
            sendCommand("MAIL FROM:<" + extractAddress(email.getFrom()) + ">", 250);

            List<String> allRecipients = new ArrayList<>();
            allRecipients.addAll(email.getTo());
            allRecipients.addAll(email.getCc());
            allRecipients.addAll(email.getBcc());

            for (String recipient : allRecipients) {
                sendCommand("RCPT TO:<" + extractAddress(recipient) + ">", 250, 251);
            }
        }

        void sendData(String mimeMessage) throws IOException {
            sendCommand("DATA", 354);

            // RFC 5321 Section 4.5.2 Transparency (Dot-stuffing)
            String[] lines = mimeMessage.split("\r\n");
            for (String line : lines) {
                if (line.startsWith(".")) {
                    writer.write(".");
                }
                writer.write(line);
                writer.write(CRLF);
            }

            // End of DATA marker
            writer.write("." + CRLF);
            writer.flush();
            expectCode(250);
        }

        void quit() throws IOException {
            sendCommand("QUIT", 221);
        }

        private void initStreams() throws IOException {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        }

        private void sendCommand(String command, int... expectedCodes) throws IOException {
            writer.write(command + CRLF);
            writer.flush();
            expectCode(expectedCodes);
        }

        private void expectCode(int... expectedCodes) throws IOException {
            String line;
            int lastCode = -1;
            StringBuilder fullResponse = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                fullResponse.append(line).append("\n");
                if (line.length() >= 3) {
                    try {
                        lastCode = Integer.parseInt(line.substring(0, 3));
                    } catch (NumberFormatException ignored) {
                    }
                    // In SMTP, a hyphen after the 3-digit code (e.g., "250-PIPELINING") means more lines follow.
                    // A space (e.g., "250 OK") marks the final line of the response.
                    if (line.length() == 3 || line.charAt(3) != '-') {
                        break;
                    }
                }
            }

            for (int expected : expectedCodes) {
                if (lastCode == expected) {
                    return;
                }
            }
            throw new IOException("Unexpected SMTP response (Expected " + java.util.Arrays.toString(expectedCodes)
                    + "): " + fullResponse.toString().trim());
        }

        /**
         * Strips display names if provided (e.g., "Jane Doe <jane@example.com>" -> "jane@example.com")
         */
        private String extractAddress(String rawAddress) {
            int start = rawAddress.indexOf('<');
            int end = rawAddress.indexOf('>');
            if (start != -1 && end != -1 && end > start) {
                return rawAddress.substring(start + 1, end).trim();
            }
            return rawAddress.trim();
        }

        @Override
        public void close() throws IOException {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    public static class EmailException extends RuntimeException {
        public EmailException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
