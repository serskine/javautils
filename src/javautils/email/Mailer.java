package javautils.email;

import java.util.List;

public interface Mailer {
    void send(Email email);

    List<Email> getMessages();
}
