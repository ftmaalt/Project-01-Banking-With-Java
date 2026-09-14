package com.ga.acmebank.usertype;

import com.ga.acmebank.LoginHandler;
import com.ga.acmebank.account.BankAccount;
import com.ga.acmebank.usertype.passwordHasher;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import static com.ga.acmebank.account.BankAccount.defaultcard;

public class Banker extends LoginHandler implements UserRole{
    static long cIdNums=10000;
    static long bIdNums;
    static String userID="";
    static String userInputpassword="";
//    BufferedWriter writer=null;

//    public Banker( String password, String userName) {
//        super();
//    }

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
//        bIdNums = 100;
        if (Character.toUpperCase(role) == 'C') {

            Path filesPath= Paths.get("data/customer");

            try {
                if (!Files.exists(filesPath)) {
                    Files.createDirectories(filesPath);
                }
                long highestID=9999;
            try(Stream<Path> newstream= Files.list(filesPath)) {
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
//        } else {
////            userID = role + String.valueOf(bIdNums);
////            bIdNums++;
        }
        return userID;
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
        String passwordReqs="^(?=.*[a-z](?=.*[A-Z])(?=.*[0-9])$)";
        password= scanner.nextLine();
        while((password.length() < 8) && !password.matches(passwordReqs)){
            System.out.println("Password Creation failed, please enter a new password that matches the requirements.");
            password= scanner.nextLine();
        }System.out.println("Confirm your Password:");
        String confirmedPassword= scanner.nextLine();
        if (!password.equals(confirmedPassword)){
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
    public void initCustomerFile(File customerFile, String custID){
        try(BufferedWriter writeCustomerFile = new BufferedWriter(new FileWriter(customerFile))){
            List<String> acctInfo = Arrays.asList(
                    "====CHECKING ACCOUNT====\n",
                    "ACCOUNTID: "+ custID +"-CH\n",
                    "Account Balance: 0.00\n-------Transaction History-------\nDATE|TYPE|AMOUNT|POST-BALANCE\n",
                    "====SAVINGS ACCOUNT====\n",
                    "ACCOUNTID: "+ custID +"-SV\n",
                    "Account Balance: 0.00\n-------Transaction History-------\nDATE|TYPE|AMOUNT|POST-BALANCE\n"
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
//        Banker mike= new Banker(null, null);
//        mike.createNewCustomer();

    }
}
