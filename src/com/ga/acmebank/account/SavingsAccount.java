package com.ga.acmebank.account;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.io.*;
import java.nio.*;
import java.util.stream.Stream;

// needs to have a method that saves the amount in the user file
public class SavingsAccount implements BankAccount{
    double acct_balance=0.0;
    int withdrawLimit=6;
    public SavingsAccount(String userID) {
    }

    public double getAcct_balance() {
        return acct_balance;
    }

    public void setAcct_balance(double acct_balance) {
        this.acct_balance = acct_balance;
    }

    @Override
    public double getActBalance(String userID, String actType) throws IOException {
        double doubleBalance=0.0;
        Path datapath= Path.of("data");
        Path userFilePath= null;
        if (Files.exists(datapath)){
            String line;
            try(Stream<Path> stream=Files.walk(datapath)){
                userFilePath=stream.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName()
                                .toString().contains(userID))
                                .findFirst()
                                .orElse(null);
                try (BufferedReader readCustomerFile = new BufferedReader(new FileReader(userFilePath.toFile()))) {
                    if ((actType.equalsIgnoreCase("Saving")||actType.equalsIgnoreCase("Savings"))){
                        while((line= readCustomerFile.readLine())!=null){
                            if (line.equals("=====SAVINGS ACCOUNT=====")){
                                if (line.startsWith("Balance:")){
                                    String balance= line.substring("Balance:".length()).trim();
                                    doubleBalance= Double.parseDouble(balance);
                                    setAcct_balance(doubleBalance);
                                }else{
                                    continue;
                                }
                                }else {
                                    continue;
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new IOException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return getAcct_balance();
    }

    @Override
    public double setActBalance(String userID, String actType) {
        return 0;
    }

    @Override
    public String checkingAccountID(String userID) {
        return "";
    }

    public String savingAccountID(String userID){
        // only returns true if savings account has balance in it/ otherwise the account does not exist even if the ID is there.
        String userSVID=null;
        try(Scanner fileReader= new Scanner(new File("data/users.txt"))) {
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine();
                String[] userinfoSplitter = data.split("\\s*\\|\\|\\s*");
                if (userID.equals(userinfoSplitter[0])) {
                    if (!userinfoSplitter[6].equals(null)) { //should check if account has balance
                        userSVID = userinfoSplitter[6];
                        return userSVID;
                    } else {
                        System.out.println("You don't have a savings account, would you like to create one?");
                        return userSVID;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Account Not Found.");
            throw new RuntimeException(e);
        }

        return userSVID;
    }
//    @Override
//    public boolean hasSavingsAccount() {
//        return true;
//    }
    @Override
    public String UpdateCardType() {
        return "";
    }

    @Override
    public String depositMoney(double balance) {
        return "";
    }

    @Override
    public String depositToAccount(double balance, String userID) {
        return "";
    }

    @Override
    public String withdrawMoney(double balance) {
        if (withdrawLimit==0){
            System.out.println("Sorry, you have reached your withdraw limit for this month.");
            return null;
        }else{
            //saving_balance greater > 0 &&
            // acme overdraft action checker

        }
        return "";
    }

    @Override
    public String transferMoney(double balance, String otherAccountID) {
        return "";
    }

    @Override
    public String transferToAccount(double balance, String userID) {
        return "";
    }

    public int savingWithdrawLimit(String ID){



        return 0;
    }
}
