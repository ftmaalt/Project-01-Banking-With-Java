package com.ga.acmebank.usertype;

import com.ga.acmebank.Actions;
import com.ga.acmebank.LoginHandler;
import com.ga.acmebank.account.AccountFileHelper;
import com.ga.acmebank.account.CheckingAccount;
import com.ga.acmebank.account.SavingsAccount;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import static com.ga.acmebank.account.BankAccount.defaultcard;

public class Banker extends LoginHandler implements UserRole{
    static long cIdNums=10000;
    static long bIdNums;
    static String userID="";
    static String userInputpassword="";


    @Override
    public char role() {
        char setRole='B';
        if (createNewCustomer()){
            setRole='C';
        }
        return setRole;
    }

    public String customerIDGenerator(char role){
        userID = "";
        if (Character.toUpperCase(role) == 'C') {

            Path filesPath = Paths.get("data/customer");

            try {
                if (!Files.exists(filesPath)) {
                    Files.createDirectories(filesPath);
                }
                long highestID = 9999;
                try (Stream<Path> newstream = Files.list(filesPath)) {
                    highestID = newstream
                            .filter(Files::isRegularFile).map(path -> path.getFileName().toString())
                            .filter(fileName -> fileName.contains("-C"))
                            .map(filename -> {
                                int start = filename.lastIndexOf("-C") + 2;
                                int end = filename.lastIndexOf(".txt");

                                return Long.parseLong(filename.substring(start, end)
                                );
                            })
                            .max(Long::compare).orElse(9999L);
                    cIdNums = highestID + 1;
                    userID = role + String.valueOf(cIdNums);
                }
                userID = role + String.valueOf(cIdNums);
                cIdNums++;
            } catch (Exception e) {

                throw new RuntimeException(e);
            }
        }
        return userID;
    }
    public String bankerIDGenerator(char role){
        userID = "";
//        bIdNums = 100;
        if (Character.toUpperCase(role) == 'B') {

            Path filesPath= Paths.get("data/bankers");

            try {
                if (!Files.exists(filesPath)) {
                    Files.createDirectories(filesPath);
                }
                long highestID=99;
                try(Stream<Path> newstream= Files.list(filesPath)) {
                    highestID = newstream
                            .filter(Files::isRegularFile).map(path -> path.getFileName().toString())
                            .filter(fileName -> fileName.contains("-B"))
                            .map(filename -> {
                                int start = filename.lastIndexOf("-B") + 2;
                                int end = filename.lastIndexOf(".txt");

                                return Long.parseLong(filename.substring(start, end)
                                );
                            })
                            .max(Long::compare).orElse(99L);
                    bIdNums = highestID + 1;
                    userID = role + String.valueOf(bIdNums);
                }
                userID = role + String.valueOf(bIdNums);
                bIdNums++;
            } catch (Exception e) {

                throw new RuntimeException(e);
            }
        }
        return userID;
    }

    public boolean createDefaultBanker(){
        Scanner scanner= new Scanner(System.in);
        String name="", lastName="", fName="", password="";
        String fixedname=fName+lastName;
        int indexComma=0;

        System.out.println("Please fill in this Form to Add a new Banker Account:\nEnter Banker Name: (lastName,firstname)\"");
        name= scanner.nextLine().strip();
        indexComma= name.indexOf(",");
        lastName= name.substring(0,indexComma).strip();
        fName= name.substring(indexComma+1).strip();
        fixedname=fName+" "+lastName;

        // add banker role

        String newBankerID= bankerIDGenerator('B');

        // password creation and confirmation

        System.out.println("Create a Password*:\n*Your password consists of no less than 8 values.\n*Your Password should also contain a combination of characters and numbers");
        String passwordReqs="^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}$";
        password= scanner.nextLine();
        while(!password.matches(passwordReqs)){
            System.out.println("Password Creation failed, please enter a new password that matches the requirements.");
            password= scanner.nextLine();
        }
        String confirmedPassword;
        System.out.println("Confirm your Password:");
        confirmedPassword= scanner.nextLine();
        while (!password.equals(confirmedPassword)){
            System.out.println("Your Passwords do not match. Please re-type in your password:");
            password= scanner.nextLine();
        }
        System.out.println("Password Created Successfully");
        String hashedPass= passwordHasher.hash(password);

// Creating the banker file:
        try {
            File dir= new File("data/bankers");
            if (!dir.exists()) {
                dir.mkdir();
            }

            File addBanker = new File(dir, "Banker-"+fixedname.toUpperCase()+"-"+newBankerID+".txt");
            if (addBanker.createNewFile()) {
                System.out.println("Customer Added Successfully: "+ addBanker.getName());
                initCustomerFile(addBanker, newBankerID);
                System.out.println("Please Log in again to continue using the app.");
                // Adding User info to the main User File:
                try(BufferedWriter writeAddedBanker = new BufferedWriter(new FileWriter("data/users.txt",true))) {

                    List<String> details = Arrays.asList(
                            newBankerID+" || ", "B || ", fixedname+" || ",hashedPass+" || ",defaultcard+" || ", newBankerID+"-CH || ",newBankerID +"-SV");
                    for (String detail:details){
                        writeAddedBanker.write(detail);
                    }
                    writeAddedBanker.newLine();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                super.login();
                return true;
            }else
                System.out.println("Error. Please try again later");
        }   catch (IOException e){
            System.out.println("Error detected");
            e.printStackTrace();
        }

        return false;
    }




    public boolean createNewCustomer(){
        Scanner scanner= new Scanner(System.in);
        String name="", lastName="", fName="", password="";
        String fixedname=fName+lastName;
        int indexComma=0;

        //username

        System.out.println("Please fill in this Form to create a new Customer Account:\nEnter your name: \"lastname,firstname\"");
         name= scanner.nextLine().strip();
        indexComma= name.indexOf(",");
        lastName= name.substring(0,indexComma).strip();
        fName= name.substring(indexComma+1).strip();
        fixedname=fName+" "+lastName;

        // add customer role

        String newCustomerID= customerIDGenerator('C');

        // password creation and confirmation

        System.out.println("Create a Password*:\n*Your password consists of no less than 8 values.\n*Your Password should also contain a combination of characters and numbers");
        String passwordReqs="^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}$";
        password= scanner.nextLine();
        while(!password.matches(passwordReqs)){
            System.out.println("Password Creation failed, please enter a new password that matches the requirements.");
            password= scanner.nextLine();
        }
        String confirmedPassword;
        System.out.println("Confirm your Password:");
        confirmedPassword= scanner.nextLine();
        while (!password.equals(confirmedPassword)){
            System.out.println("Your Passwords do not match. Please re-type in your password:");
            password= scanner.nextLine();
        }
        System.out.println("Password Created Successfully");
        String hashedPass= passwordHasher.hash(password);

// Creating the customer file:
        try {
            File dir= new File("data/customer");
            if (!dir.exists()) {
                dir.mkdir();
            }

            File newCustomer= new File(dir, "Customer-"+fixedname.toUpperCase()+"-"+newCustomerID+".txt");
        if (newCustomer.createNewFile()) {
            System.out.println("Customer Added Successfully: "+ newCustomer.getName());
            initCustomerFile(newCustomer, newCustomerID);
            System.out.println("Please Log in again to continue using the app.");
            // Adding User info to the main User File:
            try(BufferedWriter writeNewCustomer= new BufferedWriter(new FileWriter("data/users.txt",true))) {
//                writeNewCustomer.newLine();
                List<String> details = Arrays.asList(
                        newCustomerID+" || ", "C || ", fixedname+" || ",hashedPass+" || ",defaultcard+" || ", newCustomerID+"-CH || ",newCustomerID +"-SV");
                for (String detail:details){
                    writeNewCustomer.write(detail);
                }
                writeNewCustomer.newLine();

            } catch (IOException e) {
                e.printStackTrace();
            }

            super.login();
            return true;
        }else
            System.out.println("Error. Please try again later");
    }   catch (IOException e){
            System.out.println("Error detected");
            e.printStackTrace();
        }

        return false;
    }
    public void bankerMenu(){
        Scanner bankerActionScanner = new Scanner(System.in);
        System.out.println("Welcome Banker,"+getUserName());
        boolean logout=false;
        while (!logout){
            System.out.println("\nBanker Menu - Pick an Action:");
            System.out.println("1.Create New Customer\n2.Create New Banker\n3.View All Customers\n4.Look Up a Customer's Statement\n5.Reactivate a Deactivated Account\n6.Perform Own Account Actions\n7.Logout");

            if (!bankerActionScanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                bankerActionScanner.nextLine();
                continue;
            }
            int choice = bankerActionScanner.nextInt();
            bankerActionScanner.nextLine();

            switch (choice) {
                case 1 -> createNewCustomer();
                case 2 -> createDefaultBanker();
                case 3 -> viewAllCustomers();
                case 4 -> lookUpCustomerStatement(bankerActionScanner);
                case 5 -> reactivateAccount(bankerActionScanner);
                case 6 -> {
                    try {
                        Actions userActions = new Actions();
                        userActions.accountActions(this.getUserID());
                    } catch (IOException e) {
                        System.out.println("Error accessing personal account: " + e.getMessage());
                    }
                }
                case 7 -> logout = true;
                default -> System.out.println("Action not found, please try again.");
            }
        }
    }

    private void reactivateAccount(Scanner bankerActionScanner) {
        System.out.println("Enter customer ID:");
        String custID= bankerActionScanner.nextLine().toUpperCase().strip();
        String section= CheckingAccount.section_header;
        try{
            Path file = AccountFileHelper.findFileFromId(custID).orElse(null);
            if (file == null) {
                System.out.println("No customer found with that ID.");
                return;
            }
            AccountFileHelper.accountStatus(file, section, "ACTIVE");
            AccountFileHelper.updateOverDraftCount(file, section, 0);
            System.out.println("Account " + custID + " has been reactivated.");
        } catch (IOException e) {
            System.out.println("There was an error reactivating that account.");
        }
    }


    private void lookUpCustomerStatement(Scanner bankerActionScanner) {
        System.out.println("Enter customer ID:");
        String custID= bankerActionScanner.nextLine().toUpperCase().strip();
        try{
            if (AccountFileHelper.findFileFromId(custID).isEmpty()){
                System.out.println("No customer exists with this ID.");
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(new CheckingAccount(custID).detailedStatement());
        System.out.println(new SavingsAccount(custID).detailedStatement());

    }

    private void viewAllCustomers() {
        Path userFile = Path.of("data/users.txt");
        try (Scanner fileReader=new Scanner(new File(userFile.toString()))) {
            System.out.println("---ACME Customers---");
            boolean found=false;
            while (fileReader.hasNextLine()){
                String custData= fileReader.nextLine();
                String[] userInfo=custData.split("\\s*\\|\\|\\s*");
                if (userInfo.length > 2 ) { // to print all bankers and customers
                    System.out.println(userInfo[0].trim() + " - " + userInfo[2].trim());
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No customers found.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not find the users database.");
        }

    }



    public void initCustomerFile(File customerFile, String custID){
        try(BufferedWriter writeCustomerFile = new BufferedWriter(new FileWriter(customerFile))){
            List<String> acctInfo = Arrays.asList(
                    "====CHECKING ACCOUNT====\n",
                    "ACCOUNTID: "+ custID +"-CH\n",
                    "Account Balance: 0.00\n-------Transaction History-------\nDATE|TYPE|FROM|TO|AMOUNT|POST-BALANCE\n",
                    "====SAVINGS ACCOUNT====\n",
                    "ACCOUNTID: "+ custID +"-SV\n",
                    "Account Balance: 0.00\n-------Transaction History-------\nDATE|TYPE|FROM|TO|AMOUNT|POST-BALANCE\n"
            );
            for(String info:acctInfo){
                writeCustomerFile.write(info);
            }
            writeCustomerFile.newLine();
        } catch (IOException r) {
            r.printStackTrace();
        }


    }


    public static void main(String[] args) {
        Banker newBanker= new Banker();
        newBanker.createDefaultBanker();
    }
}
