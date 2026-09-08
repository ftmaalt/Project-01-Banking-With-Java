package com.ga.acmebank;
import java.util.*;
import java.io.*;

public class Login {
    long userID;
    String userName;
    String password;

    public Login(long userID, String password, String userName) {

        this.userID = userID;
        this.password = password;
        this.userName = userName;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
