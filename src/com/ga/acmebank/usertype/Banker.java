package com.ga.acmebank.usertype;

import com.ga.acmebank.LoginHandler;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Banker extends LoginHandler implements UserRole{

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

    public static boolean createNewCustomer(){
        Scanner scanner= new Scanner(System.in);
        String name="", lastName="", fName="", password="";
        int indexComma=0;

        //here should be a userID generator

        //username
        System.out.println("Please fill in this Form to create a new Customer Account:\nEnter your name: \"lastname,firstname\"");
         name= scanner.nextLine();
        indexComma= name.indexOf(",");
        lastName= name.substring(0,indexComma);
        fName= name.substring(indexComma+1);
        System.out.println(lastName + " " + fName);

        System.out.println("Create a Password*:\n*Your password consists of no less than 8 values.\n*Your Password should also contain a combination of characters and numbers");
        password= scanner.nextLine();
        while(password.length()<8){
            System.out.println("Password Creation failed, please enter a new password that matches the requirements.");
            password= scanner.nextLine();

        }
        System.out.println("Password Created Successfully");


        //here user should add balance, will update when doing the AccountTransactionsClass

        try {
            File dir= new File("src/com/ga/acmebank/data/customer");
            if (!dir.exists()) {
                dir.mkdir();
            }
            String fixedname=fName+lastName;
            File newCustomer= new File(dir, "Customer-"+fixedname+".txt");
        if (newCustomer.createNewFile()) {
            System.out.println("Customer Added Succesfully:"+ newCustomer.getName());
            return true;
        }else
            System.out.println("Error. Please try again later");
    }   catch (IOException e){
            System.out.println("Error detected");
            e.printStackTrace();
        }
        return false;
    }


    public static void main(String[] args) {
        createNewCustomer();


    }
}
