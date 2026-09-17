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

public class SavingsAccountTest {

    private static final String TEST_ID = "TESTSV999";
    private Path testFile;
    private Path usersFile;
    private SavingsAccount savings;

    @BeforeEach
    public void setUp() throws IOException {
        Path dir = Path.of("data", "customer");
        Files.createDirectories(dir);
        testFile = dir.resolve("Customer-TEST SAVINGS-" + TEST_ID + ".txt");
        List<String> lines = List.of(
                "====CHECKING ACCOUNT====",
                "ACCOUNTID: " + TEST_ID + "-CH",
                "Account Balance: 0.00",
                "-------Transaction History-------",
                "DATE|TYPE|FROM|TO|AMOUNT|POST-BALANCE",
                "====SAVINGS ACCOUNT====",
                "ACCOUNTID: " + TEST_ID + "-SV",
                "Account Balance: 500.00",
                "-------Transaction History-------",
                "DATE|TYPE|FROM|TO|AMOUNT|POST-BALANCE"
        );
        Files.write(testFile, lines);
        usersFile = Path.of("data", "users.txt");
        String userRow = TEST_ID + " || C || Test User || dummyhash || MasterCard || "
                + TEST_ID + "-CH || " + TEST_ID + "-SV";
        Files.write(usersFile, List.of(userRow),
                java.nio.file.StandardOpenOption.APPEND);

        savings = new SavingsAccount(TEST_ID);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(testFile);
    }

    @Test
    @DisplayName("When savings account ID requested then it matches in file")
    public final void whenSavingsAccountIdRequestedThenItMatchesInFile() {
        assertEquals(TEST_ID + "-SV", savings.savingAccountID(TEST_ID));
    }

    @Test
    @DisplayName("When balance is above activation minimum then withdraw succeeds")
    public final void whenBalanceIsAboveActivationMinimumThenWithdrawSucceeds() {
        String result = savings.withdrawMoney(50.00);
        assertTrue(result.contains("Withdrawal Successful"));
    }

    @Test
    @DisplayName("When balance is below activation minimum then withdraw does not succeed")
    public final void whenBalanceIsBelowActivationMinimumThenWithdrawDoesNotSucceed() throws IOException {
        AccountFileHelper.updateBalance(testFile, SavingsAccount.section_header, 50.00);
        String result = savings.withdrawMoney(10.00);
        assertTrue(result.startsWith("Please deposit a minimum of"));
    }

    @Test
    @DisplayName("When six withdrawals performed then withdrawal limit is enforced")
    public final void whenSixWithdrawalsPerformedThenWithdrawalLimitIsEnforced() {
        for (int i = 0; i < 6; i++) {
            savings.withdrawMoney(1.00);
        }
        String seventh = savings.withdrawMoney(1.00);
        assertEquals("Sorry, You have reached your withdraw/transfer limit for this month", seventh);
    }
}