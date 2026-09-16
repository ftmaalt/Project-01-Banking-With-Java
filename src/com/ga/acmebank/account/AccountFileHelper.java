package com.ga.acmebank.account;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class AccountFileHelper {
    private static Path datapath= Path.of("data");
    static final DateTimeFormatter TransactionTime_Format= DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
    static final String Transaction_History_Header="DATE|TYPE|FROM|TO|AMOUNT|POST-BALANCE";
    static final String Transaction_History="-------Transactions-------";

    static final String CH_Section_Header="=====CHECKING ACCOUNT=====";
    static final String SV_Section_Header="=====SAVINGS ACCOUNT=====";

// finds file containing the id part of the file name, i.e: C10000 | B100
    public static Optional<Path> findFileFromId(String idPart)throws IOException {
        if (!Files.exists(datapath) || idPart == null)
            return Optional.empty();
        try (Stream<Path> stream = Files.walk(datapath)) {
            return stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName()
                            .toString().contains(idPart))
                    .findFirst();
        }
    }

    // finds file containing the id part of the file name, i.e: C10000 | B100
    public static Optional<Path> findFileFromAccountID(String account_ID)throws IOException {
        if (!Files.exists(datapath) || account_ID == null)
            return Optional.empty();
        try (Stream<Path> stream = Files.walk(datapath)) {
            List<Path> files= stream.filter(Files::isRegularFile).toList();
            for (Path p: files){
                List<String> lines = Files.readAllLines(p);
                if (lines.stream().anyMatch(line -> line.trim().equals("AccountID:"+account_ID))){
                    return Optional.of(p);
                }
            }
            return Optional.empty();
        }
    }
    public static String sectionFinderByID(String actID){
        if (actID!=null && actID.endsWith("-CH")){
            return CH_Section_Header;
        }else{
            return SV_Section_Header;
        }
    }
    private static int getSectionByHeader(List<String> lines, String header){
        for (int i =0; i< lines.size();i++){
            if (lines.get(i).trim().equals(header)) return i;
        }
        return -1;
    }

    public static double readBalance(Path file, String header) throws IOException {
        double balance=0.0;
        List<String> lines = Files.readAllLines(file);
        int index=getSectionByHeader(lines,header);
        if (index==-1){
            return balance;
        }else{
            for (int i=index; i< lines.size();i++){
                if (lines.get(i).startsWith(Transaction_History))
                    break;
                if (lines.get(i).startsWith("Balance:")){
                    balance= Double.parseDouble(lines.get(i).substring("Balance".length()).trim());

                }
            }
        }
        return balance;
    }
    public static void updateBalance(Path file, String header, double newBalance) throws IOException {
        double oldBalance =0.0;
        List<String> lines = Files.readAllLines(file);
        int index=getSectionByHeader(lines,header);
        if (index==-1){
            System.out.println("Error finding Account Type..");
            return;
        }else{
            for (int i=index; i< lines.size();i++){
                if (lines.get(i).startsWith(Transaction_History))
                    break;
                if (lines.get(i).startsWith("Balance:")){
                    lines.set(i, "Balance: "+ String.format("%.2f", newBalance));
                    Files.write(file, lines);
                    return;
                }
            }
        }
    }
    public static String formatTransaction(LocalDateTime transaction_dt, String type, String from, String to, double amt, double postBalance){
        StringBuilder builder= new StringBuilder();
        builder.append(transaction_dt.format(TransactionTime_Format)).append('|').append(type).append('|').append(from).append('|').append(to).append('|').append(String.format("%.2f",amt)).append('|').append(String.format("%.2f",postBalance)).append('|').toString();
        return builder.toString();
    }


    public static void addNewTransaction(Path file, String header, String transaction_details) throws IOException {
        double oldBalance =0.0;
        List<String> lines = new ArrayList<>(Files.readAllLines(file));
        int startFromIndex =getSectionByHeader(lines,header);
        if (startFromIndex ==-1){
            System.out.println("Error finding Account Type..");
            return;
        }else{
            int insertIndex= lines.size();
            for (int i = startFromIndex; i< lines.size(); i++) {
                String file_line= lines.get(i).trim();
                if (file_line.startsWith("=====")){
                    insertIndex=i;
                    break;
                }
            }
            lines.add(insertIndex, transaction_details);
            Files.write(file,lines);
            return;
        }
    }
}
