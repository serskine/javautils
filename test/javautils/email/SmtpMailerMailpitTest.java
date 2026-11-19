package javautils.email;

import javautils.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class SmtpMailerMailpitTest {

    public static final String PROTOCOL = "http";
    public static final String HOST = "localhost";
    public static final int PORT = 1025;
    public static final String USERNAME = "stuart.marr.erskine@gmail.com";
    public static final String PASSWORD = "1_jcolaspuaftew";
    public static final boolean USE_START_TLS = false;

    public static final String MAIL_SERVER_URL = PROTOCOL + "://" + HOST + ":" + PORT;

    private Mailer mailer;

    @BeforeEach
    public void onSetup() {
        // Mailpit SMTP runs on port 1025 by default (Web UI is on 8025)
        // No authentication required (pass null for username/password)
        mailer = new SmtpMailer(HOST, PORT, USERNAME, PASSWORD, USE_START_TLS);
    }


    @Test
    void sendEmailToMailpit() throws Exception {
        // 1. Create a temporary attachment file for testing
        File tempAttachment = File.createTempFile("test-invoice-", ".txt");
        tempAttachment.deleteOnExit();
        Files.write(tempAttachment.toPath(), "Invoice #12345: Paid in full.".getBytes(StandardCharsets.UTF_8));

        // 2. Build the test email
        Email email = Email.builder()
                .from("Sender Name <sender@example.com>")
                .to("recipient@example.com")
                .cc("cc-recipient@example.com")
                .bcc("bcc-recipient@example.com")
                .replyTo("reply@example.com")
                .subject("Hello from Java 8 SmtpMailer!")
                .textBody("This is a plain text body sent via pure Java 8 SmtpMailer.")
                .htmlBody("<h1>Hello from Mailpit!</h1><p>This email includes an attachment and custom headers.</p>")
                .attachment(tempAttachment)
                .header("X-Custom-Tracker", "Mailpit-123")
                .build();

        // 3. Send the email
        mailer.send(email);

        System.out.println("Email sent! Open " + MAIL_SERVER_URL + " in your browser to view it in Mailpit.");
    }

    @Test
    public void testImap() {
        final ImapClient imapClient = new ImapClientImpl(HOST, PORT, USERNAME, PASSWORD);


        final List<Email> unreadEmail = imapClient.getUnreadMessages();
        unreadEmail.stream().forEach( email -> Logger.info(email.toString()));

    }
}