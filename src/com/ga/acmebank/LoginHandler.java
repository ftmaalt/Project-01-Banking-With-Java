package com.ga.acmebank;

import com.ga.acmebank.usertype.Banker;


import static com.ga.acmebank.usertype.passwordHasher.verifyPasswords;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.io.*;



public class LoginHandler{

    String userID;
    static String userName;
    static Scanner loginScanner = new Scanner(System.in);
    static int attemptsCount = 0;


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public static void setUserName(String userName) {
        LoginHandler.userName = userName;
    }

    public static String getUserName() {
        return userName;
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
                boolean fileExists = Files.walk(Path.of("data")).filter(Files::isRegularFile).anyMatch(path -> path.getFileName().toString().contains(inputID));

                if (fileExists) {
                    setUserID(inputID);
                    System.out.println("Enter your Password:");
                    String password = loginScanner.nextLine();
                    //verifies if user inputted the correct password
                    try(Scanner fileReader= new Scanner(new File("data/users.txt"))){
                        while(fileReader.hasNextLine()){
                            String data= fileReader.nextLine();
                            String[] userinfoSplitter= data.split("\\s*\\|\\|\\s*");
                            if (inputID.equals(userinfoSplitter[0])){
                                if (verifyPasswords(password,userinfoSplitter[3])){
                                    setUserName(userinfoSplitter[2]);
                                    String userRole= userinfoSplitter[1].trim();
                                    if (userRole.equalsIgnoreCase("B")){
                                        Banker loggedInBanker= new Banker();
                                        loggedInBanker.setUserID(getUserID());
                                        loggedInBanker.bankerMenu();
                                    }
                                    UserAccountActions newLoginAction= new Actions();
                                    newLoginAction.accountActions(getUserID());
                                }else {
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
                            Banker loggedInBanker= new Banker();
                            loggedInBanker.createNewCustomer();
                            login();
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


        public static void main (String[]args){
            LoginHandler newloginattempt = new LoginHandler();
            newloginattempt.login();
        }
    }





