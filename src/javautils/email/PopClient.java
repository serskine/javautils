package javautils.email;

import java.util.List;

public interface PopClient extends Mailer {
    int getMessageCount();
    List<Email> getMessages(int startIndex, int count);
    void deleteMessage(int messageNumber);
}
