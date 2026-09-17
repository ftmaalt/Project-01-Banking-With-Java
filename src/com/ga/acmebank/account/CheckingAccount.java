package com.ga.acmebank.account;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class CheckingAccount extends Account {

    public static String section_header=AccountFileHelper.CH_Section_Header;

    // constructor
    public CheckingAccount(String userID) {
        super(userID);
    }

    @Override
    protected boolean matchesActType(String actType) {
        if (actType.equalsIgnoreCase("Checking")|| actType.equalsIgnoreCase("Checkings")){
            return true;
        }
        return false;
    }

    private int cardRank(String cardName){
        return switch (cardName){
            case "MasterCardPlatinum"-> 2;
            case "MasterCardTitanium"->1;
            default -> 0;
        };
    }


    @Override
    public String UpdateCardType() {
        try{
            Path file= AccountFileHelper.findFileFromId(userID).orElseThrow(()-> new IOException("User file not found."));
            double accumulatedForThisMonth = AccountFileHelper.accumulatedMonthlyDeposit(
                    file, section(), Set.of("Deposit", "Transfer In"));

            String currentCard= checkCardName();
            String updatedCard;
            if (accumulatedForThisMonth> 50_000)
                updatedCard="MasterCardPlatinum";
            else if (accumulatedForThisMonth>10_000) {
                updatedCard="MasterCardTitanium";
            }
            else{
                updatedCard= "MasterCard";
            }
            if (cardRank(updatedCard)<= cardRank(currentCard)){
                return "You are not Eligible to update your card. Current Card:"+currentCard;
            }
            AccountFileHelper.updateCardType(userID, updatedCard);
            return "Congratulations! Your card has been successfully upgraded! Upgraded from: "+currentCard+ " to: "+updatedCard;

        } catch (IOException e) {
            return "There was an error while checking for card updates, please try again...";        }
    }

    @Override
    protected String ownActID() {
        return checkingAccountID(userID);
    }

    @Override
    protected String section() {
        return section_header;
    }


    public String checkingAccountID(String userID) {
        String userCHID = null;
        try (Scanner fileReader = new Scanner(new File("data/users.txt"))) {
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine();
                String[] userinfoSplitter = data.split("\\s*\\|\\|\\s*");
                if (userID.equals(userinfoSplitter[0])) {
                    userCHID = userinfoSplitter[5];
                    return userCHID;
                }
            }
        } catch (Exception e) {
            System.out.println("Account Not Found.");
            throw new RuntimeException(e);
        }

        return userCHID;
    }


}
