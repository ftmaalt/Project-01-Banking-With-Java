package com.ga.acmebank;

import com.ga.acmebank.account.BankAccount;
import com.ga.acmebank.account.CheckingAccount;
import com.ga.acmebank.account.SavingsAccount;
import com.ga.acmebank.usertype.Banker;
import com.ga.acmebank.usertype.UserAccountActions;
import com.ga.acmebank.usertype.passwordHasher;
import static com.ga.acmebank.usertype.passwordHasher.verifyPasswords;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.io.*;
import java.nio.*;


//must get role from userrole somehow
public class LoginHandler{
    BankAccount userAccountAction;
    String userID;
    static String userName;
    static Scanner loginScanner = new Scanner(System.in);
    static int attemptsCount = 0;


//    @Override
//    public char role() {
//        char setRole='B';
//        if (){
//            setRole='C';
//        }
//        return setRole;
//    }


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
                    setUserID(inputID);
                    System.out.println("Enter your Password:");
                    String password = loginScanner.nextLine();
                    try(Scanner fileReader= new Scanner(new File("data/users.txt"))){
                        while(fileReader.hasNextLine()){
                            String data= fileReader.nextLine();
                            String[] userinfoSplitter= data.split("\\s*\\|\\|\\s*");
                            if (inputID.equals(userinfoSplitter[0])){
                                if (verifyPasswords(password,userinfoSplitter[3])){
                                    setUserName(userinfoSplitter[2]);
                                    accountActions();
                                }else {
                                    failedLogin();
                                }
                            }
                            else{
                                continue;
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
            double amount=0.0;
            System.out.println("Welcome, " + getUserName());
                    System.out.println("Pick an Action: e.g. 1 to Deposit");
                    System.out.println("1.Deposit\n2.Transfer\n3.Withdraw\n4.Check Your account Balance");
                    int action= loginScanner.nextInt();
                    if (action==1){
                            System.out.println("1.Inner Deposit || 2.Outer Deposit");
                            int deposit_action= loginScanner.nextInt();

                            if (deposit_action==1) {

                                if (userAccountAction.hasSavingsAccount()) {
                                    System.out.println("Deposit to :\n1.Checking Account || 2. Savings Account");
                                    deposit_action= loginScanner.nextInt();

                                    if (deposit_action==1) {
                                        userAccountAction = new CheckingAccount(getUserID());
                                        System.out.println("Enter the amount you wish to deposit:");
                                        amount= loginScanner.nextDouble();
                                        System.out.println(userAccountAction.depositMoney(amount));

                                    } else if (deposit_action==2) {
                                        userAccountAction= new SavingsAccount(getUserID());
                                        System.out.println("Enter the amount you wish to deposit:");
                                        amount= loginScanner.nextDouble();
                                        System.out.println(userAccountAction.depositMoney(amount));
                                    }
                                }
                                else{
                                    userAccountAction = new CheckingAccount(getUserID());
                                    System.out.println("Enter the amount you wish to deposit:");
                                    amount= loginScanner.nextDouble();
                                    System.out.println(userAccountAction.depositMoney(amount));
                                }
                            }else if (deposit_action==2){
                                userAccountAction = new CheckingAccount(getUserID());
                                System.out.println("Enter the Account ID you wish to deposit to:");
                                String action_ID= loginScanner.nextLine();
                                try(Scanner fileReader= new Scanner(new File("data/users.txt"))) {
                                    while (fileReader.hasNextLine()) {
                                        String data = fileReader.nextLine();
                                        String[] userinfoSplitter = data.split("\\s*\\|\\|\\s*");
                                        if (action_ID.equals(userinfoSplitter[0])) {
                                            System.out.println("Enter the amount you wish to deposit:");
                                            amount = loginScanner.nextDouble();
                                            System.out.println(userAccountAction.depositToAccount(amount,action_ID));

                                        }
                                    }
                                } catch (Exception e) {
                                    System.out.println("Account Not Found.");
                                    throw new RuntimeException(e);
                                }
                            }

                    }
        }

        public static void main (String[]args){
            LoginHandler newloginattempt = new LoginHandler();
            newloginattempt.login();
        }
    }





