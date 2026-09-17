package com.ga.acmebank.account;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class AccountFileHelper {
    private static Path datapath = Path.of("data");
    static final DateTimeFormatter TransactionTime_Format = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
    static final String Transaction_History_Header = "DATE|TYPE|FROM|TO|AMOUNT|POST-BALANCE";
    static final String Transaction_History = "-------Transaction History-------";

    static final String CH_Section_Header = "====CHECKING ACCOUNT====";
    static final String SV_Section_Header = "====SAVINGS ACCOUNT====";

    // finds file containing the id part of the file name, i.e: C10000 | B100
    public static Optional<Path> findFileFromId(String idPart) throws IOException {
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
    public static Optional<Path> findFileFromAccountID(String account_ID) throws IOException {
        if (!Files.exists(datapath) || account_ID == null)
            return Optional.empty();
        try (Stream<Path> stream = Files.walk(datapath)) {
            List<Path> files = stream.filter(Files::isRegularFile).toList();
            for (Path p : files) {
                List<String> lines = Files.readAllLines(p);
                if (lines.stream().anyMatch(line -> line.trim().equals("ACCOUNTID: " + account_ID))) {
                    return Optional.of(p);
                }
            }
            return Optional.empty();
        }
    }

    public static String sectionFinderByID(String actID) {
        if (actID != null && actID.endsWith("-CH")) {
            return CH_Section_Header;
        } else {
            return SV_Section_Header;
        }
    }

    private static int getSectionByHeader(List<String> lines, String header) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).trim().equals(header)) return i;
        }
        return -1;
    }

    public static double readBalance(Path file, String header) throws IOException {
        double balance = 0.0;
        List<String> lines = Files.readAllLines(file);
        int index = getSectionByHeader(lines, header);
        if (index == -1) {
            return balance;
        } else {
            for (int i = index; i < lines.size(); i++) {
                if (lines.get(i).startsWith(Transaction_History))
                    break;
                if (lines.get(i).startsWith("Account Balance:")) {
                    balance = Double.parseDouble(lines.get(i).substring("Account Balance:".length()).trim());

                }
            }
        }
        return balance;
    }

    public static void updateBalance(Path file, String header, double newBalance) throws IOException {
        double oldBalance = 0.0;
        List<String> lines = Files.readAllLines(file);
        int index = getSectionByHeader(lines, header);
        if (index == -1) {
            System.out.println("Error finding Account Type..");
            return;
        } else {
            for (int i = index; i < lines.size(); i++) {
                if (lines.get(i).startsWith(Transaction_History))
                    break;
                if (lines.get(i).startsWith("Account Balance: ")) {
                    lines.set(i, "Account Balance: " + String.format("%.2f", newBalance));
                    Files.write(file, lines);
                    return;
                }
            }
        }
    }
// to apply the cards daily account actions limit
    public static double accumulatedDailyDeposit(Path file, String header, Set<String> type) throws IOException {
        List<String> lines = Files.readAllLines(file);
        int index = getSectionByHeader(lines, header);
        if (index == -1) return 0;
        LocalDate today= LocalDate.now();
        double sum = 0.0;
        boolean inTransactions = false;

        for (int i = index + 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("====")) {
                break;
            }
            if (line.equals(Transaction_History)) {
                inTransactions = true;
                continue;
            }
            if (!inTransactions || line.equals(Transaction_History_Header) || line.isEmpty())
                continue;

            String[] details = line.split("\\|", -1);
            if (details.length < 5)
                continue;
            try {
                LocalDateTime dateTime = LocalDateTime.parse(details[0], TransactionTime_Format);
                if (!LocalDate.from(dateTime).equals(today))
                    continue;
                if (type != null && !type.isEmpty() && !type.contains(details[1]))
                    continue;
                sum += Double.parseDouble(details[4]);
            } catch (Exception e) {
            }

        }
        return sum;
    }

// to update card type
    public static double accumulatedMonthlyDeposit(Path file, String header, Set<String> type) throws IOException {
        List<String> lines = Files.readAllLines(file);
        int index = getSectionByHeader(lines, header);
        if (index == -1) return 0;
        YearMonth thisMonth = YearMonth.now();
        double sum = 0.0;
        boolean inTransactions = false;

        for (int i = index + 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("====")) {
                break;
            }
            if (line.equals(Transaction_History)) {
                inTransactions = true;
                continue;
            }
            if (!inTransactions || line.equals(Transaction_History_Header) || line.isEmpty())
                continue;

            String[] details = line.split("\\|", -1);
            if (details.length < 5)
                continue;
            try {
                LocalDateTime dateTime = LocalDateTime.parse(details[0], TransactionTime_Format);
                if (!YearMonth.from(dateTime).equals(thisMonth))
                    continue;
                if (type != null && !type.isEmpty() && !type.contains(details[1]))
                    continue;
                sum += Double.parseDouble(details[4]);
            } catch (Exception e) {
            }

        }
        return sum;
    }

    public static void updateCardType(String userID, String newCardType) throws IOException {
        Path userFile = Path.of("data/users.txt");
        List<String> lines = new ArrayList<>(Files.readAllLines(userFile));
        for (int i = 0; i < lines.size(); i++) {
            String[] userInfoSplitter = lines.get(i).split("\\s*\\|\\|\\s*");
            if (userInfoSplitter.length > 4 && userID.equals(userInfoSplitter[0])) {
                userInfoSplitter[4] = newCardType;
                lines.set(i, String.join(" || ", userInfoSplitter));
                Files.write(userFile, lines);

            }
        }
    }

    public static String formatTransaction(LocalDateTime transaction_dt, String type, String from, String to, double amt, double postBalance) {
        StringBuilder builder = new StringBuilder();
        builder.append(transaction_dt.format(TransactionTime_Format)).append('|').append(type).append('|').append(from).append('|').append(to).append('|').append(String.format("%.2f", amt)).append('|').append(String.format("%.2f", postBalance));
        return builder.toString();
    }


    public static void addNewTransaction(Path file, String header, String transaction_details) throws IOException {
        double oldBalance = 0.0;
        List<String> lines = new ArrayList<>(Files.readAllLines(file));
        int startFromIndex = getSectionByHeader(lines, header);
        if (startFromIndex == -1) {
            System.out.println("Error finding Account Type..");
            return;
        } else {
            int insertIndex = lines.size();
            for (int i = startFromIndex + 1; i < lines.size(); i++) {
                String file_line = lines.get(i).trim();
                if (file_line.startsWith("====")) {
                    insertIndex = i;
                    break;
                }
            }
            lines.add(insertIndex, transaction_details);
            Files.write(file, lines);
            return;
        }
    }
    public static List<String> getAllTransactions(Path file, String header) throws IOException {
        List<String> lines = Files.readAllLines(file);
        int index = getSectionByHeader(lines, header);
        List<String> matched = new ArrayList<>();
        if (index == -1) {
            return matched;
        }
        boolean inTransactions = false;

        for (int i = index + 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("====")) {
                break;
            }
            if (line.equals(Transaction_History)) {
                inTransactions = true;
                continue;
            }
            if (!inTransactions || line.equals(Transaction_History_Header) || line.isEmpty())
                continue;
            matched.add(line);
        }
        return matched;

    }

    public static List<String> getTransactionsDuringPeriod(Path file, String header, LocalDate start, LocalDate end) throws IOException {
        List<String> lines = Files.readAllLines(file);
        int index = getSectionByHeader(lines, header);
        List<String> matched = new ArrayList<>();
        if (index == -1) {
            return matched;
        }
        boolean inTransactions = false;

        for (int i = index + 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("====")) {
                break;
            }
            if (line.equals(Transaction_History)) {
                inTransactions = true;
                continue;
            }
            if (!inTransactions || line.equals(Transaction_History_Header) || line.isEmpty())
                continue;

            String[] details = line.split("\\|", -1);
            if (details.length < 5)
                continue;
            try {
                LocalDate date = LocalDateTime.parse(details[0], TransactionTime_Format).toLocalDate();
                if (!date.isBefore(start) && !date.isAfter(end)) {
                    matched.add(line);
                }

            } catch (Exception e) {
            }

        }
        return matched;

    }

    public static List<String> filterByToday(Path file, String header) throws IOException {
        LocalDate today = LocalDate.now();
        return getTransactionsDuringPeriod(file, header, today, today);
    }

    public static List<String> filterByYesterday(Path file, String header) throws IOException {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return getTransactionsDuringPeriod(file, header, yesterday, yesterday);
    }

    public static List<String> filterByLast7Days(Path file, String header) throws IOException {
        LocalDate startFrom = LocalDate.now().minusDays(6);
        return getTransactionsDuringPeriod(file, header, startFrom, LocalDate.now());
    }

    public static List<String> filterByLast30Days(Path file, String header) throws IOException {
        LocalDate startFrom = LocalDate.now().minusDays(29);
        return getTransactionsDuringPeriod(file, header, startFrom, LocalDate.now());
    }

    public static List<String> filterByLastMonth(Path file, String header) throws IOException {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        return getTransactionsDuringPeriod(file, header, lastMonth.atDay(1), lastMonth.atEndOfMonth());
    }

    public static List<String> filterByLastWeek(Path file, String header) throws IOException {
        LocalDate startOfThisWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDate endOfLastWeek = startOfThisWeek.minusDays(1);
        return getTransactionsDuringPeriod(file, header, startOfLastWeek, endOfLastWeek);
    }

    public static int getOverDraftCount(Path file, String secHeader) throws IOException {
        List<String> lines = Files.readAllLines(file);
        int index = getSectionByHeader(lines, secHeader);
        if (index == -1) {
            return 0;
        }
            for (int i = index; i < lines.size(); i++) {
                if (lines.get(i).startsWith(Transaction_History)) break;
                if (lines.get(i).startsWith("OverDrafts:")) {
                    int OverDraftcount = Integer.parseInt(lines.get(i).substring("OverDrafts:".length()).trim());
                    return OverDraftcount;
                }
            }
        return 0;
    }


    public static void updateOverDraftCount(Path file, String secHeader, int count) throws IOException {
        List<String> lines = Files.readAllLines(file);
        int index = getSectionByHeader(lines, secHeader);
        if (index == -1) {
            return;
        }
        for (int i = index; i < lines.size(); i++) {
            if (lines.get(i).startsWith("OverDrafts:")) {
                lines.set(i,"OverDrafts: "+count);
                Files.write(file,lines);
                return;
            }
            if (lines.get(i).startsWith(Transaction_History)){
                lines.add(i, "OverDrafts: "+count);
                Files.write(file,lines);
                return;
            }

        }

    }

    public static void accountStatus(Path file, String secHeader, String status) throws IOException {
        List<String> lines = Files.readAllLines(file);
        int index = getSectionByHeader(lines, secHeader);
        if (index == -1) {
            throw new IOException("Error");
            }
            for (int i = index; i < lines.size(); i++) {
                if (lines.get(i).startsWith("Status:")) {
                    lines.set(i, "Status: " + status);
                    Files.write(file, lines);
                }
                if (lines.get(i).startsWith(Transaction_History)) {
                    lines.add(i, "Status: " + status);
                    Files.write(file, lines);
                    return;
                }
            }

        }

        public static boolean isDeactivated (Path file, String sectionHeader) throws IOException {
            List<String> lines = Files.readAllLines(file);
            int index = getSectionByHeader(lines, sectionHeader);
            if (index == -1) {
                return false;
            }
            for (int i = index; i < lines.size(); i++) {
                if (lines.get(i).startsWith(Transaction_History)) {
                    break;
                }
                if(lines.get(i).trim().equals("Status: DEACTIVATED"))
                    return true;
                }
            return false;
                }

        }

