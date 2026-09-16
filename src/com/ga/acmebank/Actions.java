package com.ga.acmebank;

import com.ga.acmebank.account.BankAccount;
import com.ga.acmebank.account.CheckingAccount;
import com.ga.acmebank.account.SavingsAccount;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import static com.ga.acmebank.LoginHandler.getUserName;

public class Actions implements UserAccountActions {
//    extends LoginHandler
    Scanner actions_Scanner= new Scanner(System.in);

    double amount=0.0;
//    @Override
//    public String userID {
//        return super.userID;
//    }
//
//    @Override
//    public void setUserID(String userID) {
//        super.setUserID(userID);
//    }

    @Override
    public boolean createSavingsAccount() {
        return false;
    }

    public void accountActions (String userID) throws IOException {
        BankAccount newSavings= new SavingsAccount(userID);
        BankAccount newChecking= new CheckingAccount(userID);
        for (String s : Arrays.asList("Welcome, " + getUserName(), "Pick an Action: e.g. 1 to Deposit", "1.Deposit\n2.Transfer\n3.Withdraw\n4.Check Your account Balance")) {
            System.out.println(s);

        int action= actions_Scanner.nextInt();
        actions_Scanner.nextLine();
        // deposit actions
        if (action==1){
            System.out.println("1.Inner Deposit || 2.Outer Deposit");
            int deposit_action= actions_Scanner.nextInt();
            actions_Scanner.nextLine();
            if (deposit_action==1) {
                // deposit to own account
                //checks if user has both savings account
                if (newSavings.getActBalance(userID,"Saving")!=0) {
                    System.out.println("Deposit to :\n1.Checking Account || 2. Savings Account");
                    deposit_action= actions_Scanner.nextInt();
                    actions_Scanner.nextLine();
                    //deposit to checking account
                    if (deposit_action==1) {
                        System.out.println("Enter the amount you wish to deposit:");
                        amount= actions_Scanner.nextDouble();
                        System.out.println(newChecking.depositMoney(amount, newChecking.checkingAccountID(userID)));
                        // deposit to savings account
                    } else if (deposit_action==2) {
                        System.out.println("Enter the amount you wish to deposit:");
                        amount= actions_Scanner.nextDouble();
                        System.out.println(newSavings.depositMoney(amount, userID));
                    }
                }
                //if the user doesn't have a savings account immediately deposit to the checking account
                else{
                    System.out.println("Enter the amount you wish to deposit:");
                    amount= actions_Scanner.nextDouble();
                    System.out.println(newChecking.depositMoney(amount,userID));
                }

            }else if (deposit_action==2){
                System.out.println("Enter the Account ID you wish to deposit to:");
                String action_ID= actions_Scanner.nextLine();
                try(Scanner fileReader= new Scanner(new File("data/users.txt"))) {
                    while (fileReader.hasNextLine()) {
                        String data = fileReader.nextLine();
                        String[] userinfoSplitter = data.split("\\s*\\|\\|\\s*");
                        if (action_ID.equals(userinfoSplitter[0])) {
                            System.out.println("Enter the amount you wish to deposit:");
                            amount = actions_Scanner.nextDouble();
                            System.out.println(newChecking.depositMoney(amount,action_ID));
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Account Not Found.");
                    throw new RuntimeException(e);
                }
            }else{
                System.out.println("Action not found, please try again.");

            }
//Transfer Actions
        }else if(action==2) {
            System.out.println("Enter the Account ID you wish to transfer to:");
            String action2_ID = actions_Scanner.nextLine();

            if (userID.equals(action2_ID)) {
                if(newSavings.getActBalance(userID,"Saving")!=0){
                    System.out.println("Pick an Action to Transfer: 1.Checking -> Saving || 2.Saving -> Checking");
                    int transfer_action = actions_Scanner.nextInt();
                    actions_Scanner.nextLine();
                    if (transfer_action == 1) {
                        System.out.println("Enter the amount you wish to transfer into your Savings Account:");
                        amount = actions_Scanner.nextDouble();
                        newChecking.transferMoney(amount, newSavings.savingAccountID(userID));

                    } else if (transfer_action==2) {
                        System.out.println("Enter the amount you wish to transfer into your Checking Account:");
                        amount = actions_Scanner.nextDouble();
                        newSavings.transferMoney(amount, newChecking.checkingAccountID(userID));


                }
            }else {
                    System.out.println("Transferring from Checking -> Savings Account");
                    System.out.println("Enter the amount you wish to transfer into your Savings Account:");
                    amount = actions_Scanner.nextDouble();
                    newChecking.transferMoney(amount, newSavings.savingAccountID(userID));
                }
            }else{
            System.out.println("Action not found, please try again.");

        }
            //Withdraw actions
        } else if (action==3) {
            System.out.println("Enter the Account ID you wish to withdraw from:");
            String action2_ID = actions_Scanner.nextLine();

            if (userID.equals(action2_ID) && (newSavings.savingAccountID(userID) != null)) {
                System.out.println("Pick an Action to Transfer: 1.Checking -> Saving || 2.Saving -> Checking");
                int transfer_action = actions_Scanner.nextInt();
                actions_Scanner.nextLine();
                if (transfer_action == 1) {
                    System.out.println("Enter the amount you wish to transfer into your Savings Account:");
                    amount = actions_Scanner.nextDouble();
                    newChecking.transferMoney(amount, newSavings.savingAccountID(userID));

                } else if (transfer_action==2) {
                    System.out.println("Enter the amount you wish to transfer into your Checking Account:");
                    amount = actions_Scanner.nextDouble();
                    newSavings.transferMoney(amount, newChecking.checkingAccountID(userID));

                }else{
                    System.out.println("Action not found, please try again.");

                }
            }
        }
    }
    }
}