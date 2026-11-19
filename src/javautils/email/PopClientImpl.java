package javautils.email;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PopClientImpl implements PopClient {

    private static final String CRLF = "\r\n";

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean useStartTls;
    private final boolean useImplicitSsl;

    public PopClientImpl(String host, int port, String username, String password) {
        this(host, port, username, password, false, port == 995);
    }

    public PopClientImpl(String host, int port, String username, String password, boolean useStartTls, boolean useImplicitSsl) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.useStartTls = useStartTls;
        this.useImplicitSsl = useImplicitSsl;
    }

    @Override
    public void send(Email email) {
        throw new UnsupportedOperationException("POP3 does not support sending emails; use SmtpMailer.");
    }

    @Override
    public int getMessageCount() {
        try (Pop3Connection conn = openAuthenticatedConnection()) {
            return conn.statCount();
        } catch (IOException e) {
            throw new SmtpMailer.EmailException("Failed to get POP3 message count from " + host + ":" + port, e);
        }
    }

    @Override
    public List<Email> getMessages() {
        try (Pop3Connection conn = openAuthenticatedConnection()) {
            int total = conn.statCount();
            return fetchRange(conn, 1, total);
        } catch (IOException e) {
            throw new SmtpMailer.EmailException("Failed to retrieve POP3 messages from " + host + ":" + port, e);
        }
    }

    @Override
    public List<Email> getMessages(int startIndex, int count) {
        if (startIndex < 1 || count < 0) {
            throw new IllegalArgumentException("startIndex must be >= 1 and count must be >= 0");
        }
        try (Pop3Connection conn = openAuthenticatedConnection()) {
            int total = conn.statCount();
            if (total == 0 || count == 0 || startIndex > total) {
                return List.of();
            }
            int endIndex = Math.min(total, startIndex + count - 1);
            return fetchRange(conn, startIndex, endIndex);
        } catch (IOException e) {
            throw new SmtpMailer.EmailException("Failed to retrieve POP3 message range from " + host + ":" + port, e);
        }
    }

    @Override
    public void deleteMessage(int messageNumber) {
        if (messageNumber < 1) {
            throw new IllegalArgumentException("messageNumber must be >= 1");
        }
        try (Pop3Connection conn = openAuthenticatedConnection()) {
            conn.dele(messageNumber);
        } catch (IOException e) {
            throw new SmtpMailer.EmailException("Failed to delete POP3 message #" + messageNumber, e);
        }
    }

    private Pop3Connection openAuthenticatedConnection() throws IOException {
        Pop3Connection conn = new Pop3Connection();
        conn.connect();
        conn.authenticate(username, password);
        return conn;
    }

    private List<Email> fetchRange(Pop3Connection conn, int start, int end) throws IOException {
        List<Email> emails = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            String raw = conn.retr(i);
            emails.add(RfcMessageParser.parse(raw));
        }
        return emails;
    }

    private class Pop3Connection implements AutoCloseable {
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
            this.socket.setSoTimeout(30_000);
            initStreams();

            String greeting = reader.readLine();
            if (greeting == null || !greeting.startsWith("+OK")) {
                throw new IOException("Invalid POP3 greeting: " + greeting);
            }

            if (useStartTls && !useImplicitSsl) {
                sendSingleLine("STLS");
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

        void authenticate(String user, String pass) throws IOException {
            sendSingleLine("USER " + user);
            sendSingleLine("PASS " + pass);
        }

        int statCount() throws IOException {
            String response = sendSingleLine("STAT");
            String[] parts = response.split("\\s+");
            if (parts.length < 2) {
                throw new IOException("Unexpected STAT response: " + response);
            }
            return Integer.parseInt(parts[1]);
        }

        String retr(int msgNum) throws IOException {
            writer.write("RETR " + msgNum + CRLF);
            writer.flush();

            String status = reader.readLine();
            if (status == null || !status.startsWith("+OK")) {
                throw new IOException("POP3 RETR failed for message " + msgNum + ": " + status);
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (".".equals(line)) {
                    break;
                }
                if (line.startsWith("..")) {
                    line = line.substring(1);
                }
                sb.append(line).append(CRLF);
            }
            return sb.toString();
        }

        void dele(int msgNum) throws IOException {
            sendSingleLine("DELE " + msgNum);
        }

        private String sendSingleLine(String command) throws IOException {
            writer.write(command + CRLF);
            writer.flush();
            String response = reader.readLine();
            if (response == null || !response.startsWith("+OK")) {
                throw new IOException("POP3 command [" + command + "] failed: " + response);
            }
            return response;
        }

        @Override
        public void close() throws IOException {
            if (socket != null && !socket.isClosed()) {
                try {
                    writer.write("QUIT" + CRLF);
                    writer.flush();
                } catch (IOException ignored) {
                }
                socket.close();
            }
        }
    }
}