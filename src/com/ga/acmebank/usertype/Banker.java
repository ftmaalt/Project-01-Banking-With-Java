package com.ga.acmebank.usertype;

import com.ga.acmebank.LoginHandler;
import com.ga.acmebank.account.BankAccount;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class Banker extends LoginHandler implements UserRole, BankAccount {
    static long cIdNums=10000;
    static long bIdNums;
    static String userID="";
//    BufferedWriter writer=null;

    public Banker( String password, String userName) {
        super(password, userName);
    }

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

// Creating the customer file:
        try {
            File dir= new File("data/customer");
            if (!dir.exists()) {
                dir.mkdir();
            }

            File newCustomer= new File(dir, "Customer-"+fixedname.toUpperCase()+"-"+newCustomerID+".txt");
        if (newCustomer.createNewFile()) {
            System.out.println("Customer Added Successfully: "+ newCustomer.getName());
            System.out.println("Please Log in again to continue using the app.");
            super.login();
            return true;
        }else
            System.out.println("Error. Please try again later");
    }   catch (IOException e){
            System.out.println("Error detected");
            e.printStackTrace();
        }
        // Adding User info to the main User File:
        try(BufferedWriter writeNewCustomer= new BufferedWriter(new FileWriter("data/users.txt",true))) {
            writeNewCustomer.write(newCustomerID+" || ");
            writeNewCustomer.write(role()+" || ");
            writeNewCustomer.write(fixedname+" || ");
            writeNewCustomer.write(password+" || "); // should be hashed
            writeNewCustomer.write(defaultcard);
            writeNewCustomer.newLine();

            if (writeNewCustomer != null) {
                try {
                    writeNewCustomer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("data/users.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static void main(String[] args) {
//        Banker mike= new Banker(null, null);
//        mike.createNewCustomer();

    }
}
