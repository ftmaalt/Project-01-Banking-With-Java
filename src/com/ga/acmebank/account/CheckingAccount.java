package com.ga.acmebank.account;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.stream.Stream;

import java.time.*;

public class CheckingAccount implements BankAccount {
    double acct_balance=0.0;
    LocalDateTime transactionTime;

    public CheckingAccount(String userID) {
    }
//setters and getters for the acct_balance
    public double getAcct_balance() {
        return acct_balance;
    }

    public void setAcct_balance(double acct_balance) {
        this.acct_balance = acct_balance;
    }

    @Override
    public String savingAccountID(String userID) {
        return "";
    }

    @Override
    public void updateCurrentBalance(String userID, String actType) {
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
                try (BufferedReader readCustomerFile = new BufferedReader(new FileReader(userFilePath.toFile()))) { // has to be one for checking and another for savings in the same method
                    if ((actType.equalsIgnoreCase("Checking")||actType.equalsIgnoreCase("Checkings"))){
                        while((line= readCustomerFile.readLine())!=null){
                            if (line.equals("=====CHECKING ACCOUNT=====")){
                                if (line.startsWith("Balance:")){
                                    String oldBalance= line.substring("Balance:".length()).trim();
                                    try(){

                                    }
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
    }

    @Override
    public double getActBalance(String userID, String actType) throws IOException {
        Path datapath= Path.of("data");
        double doubleBalance=0.0;
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
                    if ((actType.equalsIgnoreCase("Checking")||actType.equalsIgnoreCase("Checkings"))){
                        while((line= readCustomerFile.readLine())!=null){
                            if (line.equals("=====CHECKING ACCOUNT=====")){
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
    public String checkingAccountID(String userID){
        // only returns true if savings account has balance in it/ otherwise the account does not exist even if the ID is there.
        String userSVID=null;
        try(Scanner fileReader= new Scanner(new File("data/users.txt"))) {
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine();
                String[] userinfoSplitter = data.split("\\s*\\|\\|\\s*");
                if (userID.equals(userinfoSplitter[0])) {
                    if (!userinfoSplitter[5].equals(null)) { //should check if account has balance
                        userSVID = userinfoSplitter[5];
                        return userSVID;
                    } else {
                        System.out.println("Your Checking account has no balance. Please deposit first to be able to transfer");
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



    @Override
    public String UpdateCardType() {

        //gets user balance, if balance is greater than 10000, then upgrade card to titanium,
        // if card is titanium and balance is greater than 50-000 and user has deposited more than 700000 accumalted balance in account then upgrade to platinum
        return "";
    }

    @Override
    public String depositMoney(double balance) {
        //gets balance from somewhere

        return "Sorry Try Again";
    }



    @Override
    public String transferToAccount(double balance, String userID) {
        return "";
    }

    @Override
    public String transferMoney(double balance, String otherAccountID) {
        return "";
    }

    @Override
    public String depositToAccount(double balance, String userID) {
        return "";
    }

    @Override
    public String withdrawMoney(double balance) {
        return "";
    }
}
