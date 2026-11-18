package javautils.email;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Email {

    private final String from;
    private final List<String> to;
    private final List<String> cc;
    private final List<String> bcc;
    private final String replyTo;
    private final String subject;
    private final String textBody;
    private final String htmlBody;
    private final List<File> attachments;
    private final Map<String, String> headers;

    private Email(Builder builder) {
        this.from = builder.from;
        this.to = Collections.unmodifiableList(new ArrayList<>(builder.to));
        this.cc = Collections.unmodifiableList(new ArrayList<>(builder.cc));
        this.bcc = Collections.unmodifiableList(new ArrayList<>(builder.bcc));
        this.replyTo = builder.replyTo;
        this.subject = builder.subject;
        this.textBody = builder.textBody;
        this.htmlBody = builder.htmlBody;
        this.attachments = Collections.unmodifiableList(new ArrayList<>(builder.attachments));
        this.headers = Collections.unmodifiableMap(new HashMap<>(builder.headers));
    }

    public String getFrom() { return from; }
    public List<String> getTo() { return to; }
    public List<String> getCc() { return cc; }
    public List<String> getBcc() { return bcc; }
    public String getReplyTo() { return replyTo; }
    public String getSubject() { return subject; }
    public String getTextBody() { return textBody; }
    public String getHtmlBody() { return htmlBody; }
    public List<File> getAttachments() { return attachments; }
    public Map<String, String> getHeaders() { return headers; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String from;
        private final List<String> to = new ArrayList<>();
        private final List<String> cc = new ArrayList<>();
        private final List<String> bcc = new ArrayList<>();
        private String replyTo;
        private String subject;
        private String textBody;
        private String htmlBody;
        private final List<File> attachments = new ArrayList<>();
        private final Map<String, String> headers = new HashMap<>();

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String email) {
            this.to.add(email);
            return this;
        }

        public Builder to(List<String> emails) {
            this.to.addAll(emails);
            return this;
        }

        public Builder cc(String email) {
            this.cc.add(email);
            return this;
        }

        public Builder bcc(String email) {
            this.bcc.add(email);
            return this;
        }

        public Builder replyTo(String replyTo) {
            this.replyTo = replyTo;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder textBody(String textBody) {
            this.textBody = textBody;
            return this;
        }

        public Builder htmlBody(String htmlBody) {
            this.htmlBody = htmlBody;
            return this;
        }

        public Builder attachment(File file) {
            this.attachments.add(file);
            return this;
        }

        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Email build() {
            Objects.requireNonNull(from, "'from' address is required");
            if (to.isEmpty()) {
                throw new IllegalStateException("At least one 'to' recipient is required");
            }
            if (textBody == null && htmlBody == null) {
                throw new IllegalStateException("Email must have either a textBody or htmlBody");
            }
            return new Email(this);
        }
    }
}