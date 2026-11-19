package javautils.email;

import java.util.List;

public interface ImapClient extends Mailer {
    void selectFolder(String folderName);
    String getSelectedFolder();
    List<String> listFolders();
    List<Email> getUnreadMessages();
}