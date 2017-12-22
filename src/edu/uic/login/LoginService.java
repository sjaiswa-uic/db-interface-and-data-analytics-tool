package edu.uic.login;

import edu.uic.dao.DatabaseAccess;

public class LoginService {

    private DatabaseAccess databaseAccess;

    public boolean authenticate(LoginForm form, MessageModel messageModel) {
        databaseAccess = new DatabaseAccess();
        return databaseAccess.login (form, messageModel);
    }
    
    public void logout (LoginForm form, MessageModel messageModel) {
        new DatabaseAccess().logout (form, messageModel);
    }
}
