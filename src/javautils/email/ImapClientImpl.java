package javautils.email;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImapClientImpl implements ImapClient {

    private static final String CRLF = "\r\n";
    private static final Pattern FETCH_LITERAL_PATTERN =
            Pattern.compile("\\*\\s+\\d+\\s+FETCH\\s+\\([^)]*BODY\\[(?:PEEK\\[\\]|)\\]\\s+\\{(\\d+)\\}",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern LIST_MAILBOX_PATTERN =
            Pattern.compile("^\\*\\s+LIST\\s+\\([^)]*\\)\\s+(?:\"[^\"]*\"|NIL)\\s+\"?([^\"]+?)\"?$",
                    Pattern.CASE_INSENSITIVE);

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean useStartTls;
    private final boolean useImplicitSsl;
    private String selectedFolder;

    public ImapClientImpl(String host, int port, String username, String password) {
        this(host, port, username, password, false, port == 993, "INBOX");
    }

    public ImapClientImpl(String host, int port, String username, String password,
                          boolean useStartTls, boolean useImplicitSsl, String folder) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.useStartTls = useStartTls;
        this.useImplicitSsl = useImplicitSsl;
        this.selectedFolder = (folder == null || folder.isBlank()) ? "INBOX" : folder;
    }

    @Override
    public void send(Email email) {
        throw new UnsupportedOperationException("IMAP does not support sending emails; use SmtpMailer.");
    }

    @Override
    public void selectFolder(String folderName) {
        if (folderName == null || folderName.isBlank()) {
            throw new IllegalArgumentException("folderName must not be blank");
        }
        try (ImapConnection conn = openAuthenticatedConnection()) {
            conn.select(folderName);
            this.selectedFolder = folderName;
        } catch (IOException e) {
            throw new SmtpMailer.EmailException("Failed to select IMAP folder: " + folderName, e);
        }
    }

    @Override
    public String getSelectedFolder() {
        return selectedFolder;
    }

    @Override
    public List<String> listFolders() {
        try (ImapConnection conn = openAuthenticatedConnection()) {
            List<String> responseLines = conn.sendTagged("LIST \"\" \"*\"");
            List<String> folders = new ArrayList<>();
            for (String line : responseLines) {
                Matcher m = LIST_MAILBOX_PATTERN.matcher(line);
                if (m.find()) {
                    folders.add(m.group(1));
                }
            }
            return folders;
        } catch (IOException e) {
            throw new SmtpMailer.EmailException("Failed to list IMAP folders from " + host + ":" + port, e);
        }
    }

    @Override
    public List<Email> getMessages() {
        try (ImapConnection conn = openAuthenticatedConnection()) {
            int exists = conn.select(selectedFolder);
            List<Email> messages = new ArrayList<>();
            for (int seq = 1; seq <= exists; seq++) {
                messages.add(RfcMessageParser.parse(conn.fetchMessage(seq)));
            }
            return messages;
        } catch (IOException e) {
            throw new SmtpMailer.EmailException("Failed to retrieve messages from folder " + selectedFolder, e);
        }
    }

    @Override
    public List<Email> getUnreadMessages() {
        try (ImapConnection conn = openAuthenticatedConnection()) {
            conn.select(selectedFolder);
            List<Integer> unseenSeqs = conn.searchUnseen();
            List<Email> messages = new ArrayList<>();
            for (int seq : unseenSeqs) {
                messages.add(RfcMessageParser.parse(conn.fetchMessage(seq)));
            }
            return messages;
        } catch (IOException e) {
            throw new SmtpMailer.EmailException("Failed to retrieve unread messages from folder " + selectedFolder, e);
        }
    }

    private ImapConnection openAuthenticatedConnection() throws IOException {
        ImapConnection conn = new ImapConnection();
        conn.connect();
        conn.login(username, password);
        return conn;
    }

    private class ImapConnection implements AutoCloseable {
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;
        private int tagSeq = 0;

        void connect() throws IOException {
            if (useImplicitSsl) {
                SSLSocket sslSocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(host, port);
                sslSocket.startHandshake();
                this.socket = sslSocket;
            } else {
                this.socket = new Socket(host, port);
            }
            this.socket.setSoTimeout(30_000);
            initStreams();

            String greeting = reader.readLine();
            if (greeting == null || !greeting.startsWith("* OK")) {
                throw new IOException("Invalid IMAP greeting: " + greeting);
            }

            if (useStartTls && !useImplicitSsl) {
                sendTagged("STARTTLS");
                SSLSocket sslSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault())
                        .createSocket(socket, host, port, true);
                sslSocket.startHandshake();
                this.socket = sslSocket;
                initStreams();
            }
        }

        private void initStreams() throws IOException {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.ISO_8859_1));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        }

        void login(String user, String pass) throws IOException {
            sendTagged("LOGIN " + quote(user) + " " + quote(pass));
        }

        int select(String folder) throws IOException {
            List<String> lines = sendTagged("SELECT " + quote(folder));
            int exists = 0;
            for (String line : lines) {
                if (line.startsWith("* ") && line.endsWith(" EXISTS")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 3) {
                        exists = Integer.parseInt(parts[1]);
                    }
                }
            }
            return exists;
        }

        List<Integer> searchUnseen() throws IOException {
            List<String> lines = sendTagged("SEARCH UNSEEN");
            List<Integer> nums = new ArrayList<>();
            for (String line : lines) {
                if (line.startsWith("* SEARCH")) {
                    String[] tokens = line.substring("* SEARCH".length()).trim().split("\\s+");
                    for (String token : tokens) {
                        if (!token.isBlank()) {
                            nums.add(Integer.parseInt(token));
                        }
                    }
                }
            }
            return nums;
        }

        String fetchMessage(int seqNum) throws IOException {
            String tag = nextTag();
            writer.write(tag + " FETCH " + seqNum + " BODY.PEEK[]" + CRLF);
            writer.flush();

            int literalSize = -1;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(tag + " ")) {
                    if (!line.startsWith(tag + " OK")) {
                        throw new IOException("IMAP FETCH failed: " + line);
                    }
                    break;
                }
                Matcher matcher = FETCH_LITERAL_PATTERN.matcher(line);
                if (matcher.find()) {
                    literalSize = Integer.parseInt(matcher.group(1));
                    break;
                }
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            if (literalSize > 0) {
                InputStream in = socket.getInputStream();
                byte[] chunk = new byte[4096];
                int remaining = literalSize;
                while (remaining > 0) {
                    int n = in.read(chunk, 0, Math.min(chunk.length, remaining));
                    if (n == -1) break;
                    buffer.write(chunk, 0, n);
                    remaining -= n;
                }
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(tag + " ")) {
                        if (!line.startsWith(tag + " OK")) {
                            throw new IOException("IMAP FETCH completion error: " + line);
                        }
                        break;
                    }
                }
            }
            return buffer.toString(StandardCharsets.UTF_8);
        }

        List<String> sendTagged(String command) throws IOException {
            String tag = nextTag();
            writer.write(tag + " " + command + CRLF);
            writer.flush();

            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
                if (line.startsWith(tag + " ")) {
                    if (!line.startsWith(tag + " OK")) {
                        throw new IOException("IMAP command [" + command + "] failed: " + line);
                    }
                    return lines;
                }
            }
            throw new IOException("Unexpected EOF during IMAP command: " + command);
        }

        private String nextTag() {
            return "A" + (++tagSeq);
        }

        private String quote(String val) {
            return "\"" + val.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }

        @Override
        public void close() throws IOException {
            if (socket != null && !socket.isClosed()) {
                try {
                    sendTagged("LOGOUT");
                } catch (IOException ignored) {
                }
                socket.close();
            }
        }
    }
}