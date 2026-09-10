package com.ga.acmebank;

import com.ga.acmebank.usertype.Banker;
import com.ga.acmebank.usertype.UserAccountActions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.io.*;
import java.nio.*;

//must get role from userrole somehow
public class LoginHandler{
    long userID;
    static String userName;
    static String password;
    static Scanner loginScanner = new Scanner(System.in);
    //    static Timer attemptsTimer = new Timer();
//    static Timer wrongAttemptTimer = new Timer();
    static int attemptsCount = 0;
//    static TimerTask loginProcessor;


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


    public static void setUserName(String userName) {
        LoginHandler.userName = userName;
    }

    public static String getUserName() {
        return userName;
    }

    public String idGenerator(char role) {
        String userID = "";
        long cIdNums = 10000;
        long bIdNums = 100;
        if (role == 'C') {
            //must check existing id nums first to find if there is a user with that same id number, else:
            userID = role + String.valueOf(cIdNums);
            cIdNums++;
        } else {
            userID = role + String.valueOf(bIdNums);
            bIdNums++;
        }
        return userID;
    }

    public void login() {
        System.out.println("=======================================================================");
        System.out.println("Welcome to ACME Bank Application");
        System.out.println("Pick a choice to proceed: e.g.1");
        System.out.println("1.Login");
        System.out.println("2.Exit");
        int userChoice = loginScanner.nextInt();
        loginScanner.nextLine();
        if (userChoice == 2) {
            System.out.println("Thank you for using ACME Bank Application");
            System.exit(0);
        } else if (userChoice == 1) {
            System.out.println("Enter your ID:");
            String inputID = loginScanner.nextLine().toUpperCase().strip();
            try {
                boolean fileExists = Files.walk(Path.of("C:/Users/fmabd/jdb/projects/Project1- Banking With Java/data")).filter(Files::isRegularFile).anyMatch(path -> path.getFileName().toString().contains(inputID));
                if (fileExists) {
                    System.out.println("Enter your Password:");
                    String password = loginScanner.nextLine();
                    try(Scanner fileReader= new Scanner("data")){
                        while(fileReader.hasNextLine()){
                            String data= fileReader.nextLine();
                            if (data.startsWith("====")|| data.startsWith("USERID"))
                                continue;
                            else{
                                String[] userinfoSplitter= data.split("\\s*\\|\\|\\s*");
                                if (userinfoSplitter[3].equals(password)){
                                    setUserName(userinfoSplitter[2]);
                                    accountActions();
                                } else {
                                    failedLogin();
                                }
                            }
                        }
                    }
                    }else{
                        System.out.println("There was an error finding your userID, do you wish to try again, if this is your first time using the ACME Bank Application, you should create a new account first.");
                        System.out.println("1.Try Again\n2.Create a new Account.");
                        int failedChoice = loginScanner.nextInt();
                        if (failedChoice == 1)
                            login();
                        else {
                            // should call one of the bankers
                            Banker mike = new Banker(null, null);
                            mike.createNewCustomer();
                        }
                    }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }

}
        public void failedLogin () {

            System.out.println("Wrong Username or Password. Please Try again");
            attemptsCount++;

            if (attemptsCount == 3) {
                System.out.println("Please Try again in One minute.");
                attemptsCount = 0;
                try {
                    Thread.sleep(60000);
                    login();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                login();
            }
        }
        public void accountActions () {
            System.out.println("Welcome, " + getUserName());
//                    System.out.println("Pick an Action:");
//                    System.out.println("1.Deposit\n2.Transfer\n3.Withdraw\n4.Check Your account");
//                    String action= loginScanner.nextLine();
        }

        public static void main (String[]args){
            LoginHandler newloginattempt = new LoginHandler(null, null);
            newloginattempt.login();
        }
    }





