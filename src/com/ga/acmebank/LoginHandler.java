package com.ga.acmebank;
import com.ga.acmebank.usertype.UserRole;

import java.util.*;
//must get role from userrole somehow
public class LoginHandler {
    long userID;
    String userName;
    String password;

    public LoginHandler(String password, String userName) {

//        this.userID = userID;
        this.password = password;
        this.userName = userName;
    }
//    @Override
//    public char role() {
//        char setRole='B';
//        if (){
//            setRole='C';
//        }
//        return setRole;
//    }


    public String idGenerator(char role){
        String userID="";
        long cIdNums=10000;
        long bIdNums=100;
        if (role=='C'){
            //must check existing id nums first to find if there is a user with that same id number, else:
            userID=role+String.valueOf(cIdNums);
            cIdNums++;
        }else{
            userID=role+String.valueOf(bIdNums);
            bIdNums++;
        }
        return userID;
    }


    public static void main(String[] args) {
        Scanner loginScanner= new Scanner(System.in);
        System.out.println("Welcome to ACME Bank Application");
        System.out.println("Pick a choice to proceed: e.g.1");
        System.out.println("1.Login\n2.Exit");
        int userChoice= loginScanner.nextInt();
        if (userChoice==2){
            System.out.println("Thank you for using ACME Bank Application");
            System.exit(0);
        }
        System.out.println("Enter your ID:");

    }
}
