package com.ga.acmebank.account;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CheckingAccountTest {
    private static final String TEST_ID = "TESTCH999";
    private Path testFile;
    private Path usersFile;

    private CheckingAccount checking;
// create a test user for checking account actions
    @BeforeEach
    public void setUp() throws IOException {
        Path dir = Path.of("data", "customer");
        Files.createDirectories(dir);
        testFile = dir.resolve("Customer-TEST CHECKING-" + TEST_ID + ".txt");
        List<String> lines = List.of(
                "====CHECKING ACCOUNT====",
                "ACCOUNTID: " + TEST_ID + "-CH",
                "Account Balance: 200.00",
                "-------Transaction History-------",
                "DATE|TYPE|FROM|TO|AMOUNT|POST-BALANCE",
                "====SAVINGS ACCOUNT====",
                "ACCOUNTID: " + TEST_ID + "-SV",
                "Account Balance: 0.00",
                "-------Transaction History-------",
                "DATE|TYPE|FROM|TO|AMOUNT|POST-BALANCE"
        );
        Files.write(testFile, lines);
        usersFile = Path.of("data", "users.txt");
        String userRow = TEST_ID + " || C || Test User || dummyhash || MasterCard || "
                + TEST_ID + "-CH || " + TEST_ID + "-SV";
        Files.write(usersFile, List.of(userRow),
                java.nio.file.StandardOpenOption.APPEND);

        checking = new CheckingAccount(TEST_ID);
    }
    //deletes the file after the test is done.
    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(testFile);
    }

    @Test
    @DisplayName("When checking account ID requested then it matches in file ID")
    public final void whenCheckingAccountIdRequestedThenItMatchesInFileID() {
        assertEquals(TEST_ID + "-CH", checking.checkingAccountID(TEST_ID));
    }

    @Test
    @DisplayName("When account type is wrong then get act balance ignores it")
    public final void whenAccountTypeIsWrongThenGetActBalanceIgnoresIt() throws IOException {
        assertEquals(0, checking.getActBalance(TEST_ID, "Saving"));
    }

    @Test
    @DisplayName("When deposit made then balance updates and transaction records")
    public final void whenDepositMadeThenBalanceUpdatesAndTransactionRecords() throws IOException {
        String result = checking.depositMoney(50.00, TEST_ID + "-CH");
        assertTrue(result.contains("Deposit Successful"));
        assertEquals(250.00, AccountFileHelper.readBalance(testFile, CheckingAccount.section_header), 0.001);
    }

    @Test
    @DisplayName("When checking type specified then stored balance is returned")
    public final void whenCheckingTypeSpecifiedThenStoredBalanceIsReturned() throws IOException {
        assertEquals(200.00, checking.getActBalance(TEST_ID, "Checking"), 0.001);
    }

    @Test
    @DisplayName("When withdrawal amount negative then request rejected")
    public final void whenWithdrawalAmountNegativeThenRequestRejected() {
        assertEquals("Withdrawal amount must be positive.", checking.withdrawMoney(-5.00));
    }
}