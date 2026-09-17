package com.ga.acmebank.account;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// Uses a throwaway test customer file under data/customer/ (cleaned up after every test)
// so it exercises the exact same file format the real application reads and writes.
public class AccountFileHelperTest {

    private static final String TEST_ID = "TESTID999";
    private Path testFile;

    @BeforeEach
    public void setUp() throws IOException {
        Path dir = Path.of("data", "customer");
        Files.createDirectories(dir);
        testFile = dir.resolve("Customer-TEST USER-" + TEST_ID + ".txt");
        List<String> lines = List.of(
                "====CHECKING ACCOUNT====",
                "ACCOUNTID: " + TEST_ID + "-CH",
                "Account Balance: 100.00",
                "-------Transaction History-------",
                "DATE|TYPE|FROM|TO|AMOUNT|POST-BALANCE",
                "====SAVINGS ACCOUNT====",
                "ACCOUNTID: " + TEST_ID + "-SV",
                "Account Balance: 50.00",
                "-------Transaction History-------",
                "DATE|TYPE|FROM|TO|AMOUNT|POST-BALANCE"
        );
        Files.write(testFile, lines);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(testFile);
    }

    @Test
    @DisplayName("When read balance called then stored balance is returned")
    public final void whenReadBalanceCalledThenStoredBalanceIsReturned() throws IOException {
        double balance = AccountFileHelper.readBalance(testFile, AccountFileHelper.CH_Section_Header);
        assertEquals(100.00, balance, 0.001);
    }


    @Test
    @DisplayName("When daily deposit calculated then sums only matching type for today")
    public final void whenDailyDepositCalculatedThenSumsOnlyMatchingTypeForToday() throws IOException {
        String deposit = AccountFileHelper.formatTransaction(
                LocalDateTime.now(), "Deposit", TEST_ID + "-CH", TEST_ID + "-CH", 30.00, 130.00);
        String withdraw = AccountFileHelper.formatTransaction(
                LocalDateTime.now(), "Withdraw", TEST_ID + "-CH", TEST_ID + "-CH", 10.00, 120.00);
        AccountFileHelper.addNewTransaction(testFile, AccountFileHelper.CH_Section_Header, deposit);
        AccountFileHelper.addNewTransaction(testFile, AccountFileHelper.CH_Section_Header, withdraw);

        double sum = AccountFileHelper.accumulatedDailyDeposit(testFile, AccountFileHelper.CH_Section_Header, Set.of("Deposit"));
        assertEquals(30.00, sum, 0.001);
    }

    @Test
    @DisplayName("When overdraft count initialized then defaults to zero and tracks updates")
    public final void whenOverdraftCountInitializedThenDefaultsToZeroAndTracksUpdates() throws IOException {
        assertEquals(0, AccountFileHelper.getOverDraftCount(testFile, AccountFileHelper.CH_Section_Header));
        AccountFileHelper.updateOverDraftCount(testFile, AccountFileHelper.CH_Section_Header, 2);
        assertEquals(2, AccountFileHelper.getOverDraftCount(testFile, AccountFileHelper.CH_Section_Header));
    }

    @Test
    @DisplayName("When account created then starts active and can be marked deactivated")
    public final void whenAccountCreatedThenStartsActiveAndCanBeMarkedDeactivated() throws IOException {
        assertFalse(AccountFileHelper.isDeactivated(testFile, AccountFileHelper.CH_Section_Header));
        AccountFileHelper.accountStatus(testFile, AccountFileHelper.CH_Section_Header, "DEACTIVATED");
        assertTrue(AccountFileHelper.isDeactivated(testFile, AccountFileHelper.CH_Section_Header));
    }
}