package com.ga.acmebank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class LoginHandlerTest {
        LoginHandler login;
        @BeforeEach
        void setLogin(){
            login=new LoginHandler();
        }
    @Test
    @DisplayName("When user ID is set then it can be retrieved")
    public final void whenUserIdIsSetThenItCanBeRetrieved() {
        login.setUserID("C10005");
        assertEquals("C10005", login.getUserID());
    }
    @Test
    @DisplayName("When user name is set then it can be retrieved")
    public final void whenUserNameIsSetThenItCanBeRetrieved() {
        LoginHandler.setUserName("Jamie Rivera");
        assertEquals("Jamie Rivera", LoginHandler.getUserName());
    }

}