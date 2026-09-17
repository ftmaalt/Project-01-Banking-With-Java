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

public class OverdraftActionsTest {

    private static final String TEST_ID = "TESTOD999";
    private Path testFile;
    private final OverdraftActions overdraft = new OverdraftActions();

    @BeforeEach
    public void setUp() throws IOException {
        Path dir = Path.of("data", "customer");
        Files.createDirectories(dir);
        testFile = dir.resolve("Customer-TEST OVERDRAFT-" + TEST_ID + ".txt");
        List<String> lines = List.of(
                "====CHECKING ACCOUNT====",
                "ACCOUNTID: " + TEST_ID + "-CH",
                "Account Balance: 0.00",
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
    @DisplayName("When overdraft fee applied then fee is subtracted and count incremented")
    public final void whenOverdraftFeeAppliedThenFeeIsSubtractedAndCountIncremented() throws IOException {
        double resultBalance = overdraft.applyOverDraftFree(TEST_ID, AccountFileHelper.CH_Section_Header, -10.00);

        assertEquals(-45.00, resultBalance, 0.001); // -10.00 balance minus the $35 fee
        assertEquals(1, AccountFileHelper.getOverDraftCount(testFile, AccountFileHelper.CH_Section_Header));
        assertFalse(overdraft.isDeactivated(TEST_ID, AccountFileHelper.CH_Section_Header));
    }

    @Test
    @DisplayName("When max allowed overdrafts reached then account deactivates")
    public final void whenMaxAllowedOverdraftsReachedThenAccountDeactivates() throws IOException {
        overdraft.applyOverDraftFree(TEST_ID, AccountFileHelper.CH_Section_Header, -10.00);
        overdraft.applyOverDraftFree(TEST_ID, AccountFileHelper.CH_Section_Header, -5.00);

        assertEquals(2, AccountFileHelper.getOverDraftCount(testFile, AccountFileHelper.CH_Section_Header));
        assertTrue(overdraft.isDeactivated(TEST_ID, AccountFileHelper.CH_Section_Header));
    }

    @Test
    @DisplayName("When balance becomes positive then reactivate resolved clears deactivation")
    public final void whenBalanceBecomesPositiveThenReactivateResolvedClearsDeactivation() throws IOException {
        overdraft.applyOverDraftFree(TEST_ID, AccountFileHelper.CH_Section_Header, -10.00);
        overdraft.applyOverDraftFree(TEST_ID, AccountFileHelper.CH_Section_Header, -5.00);
        assertTrue(overdraft.isDeactivated(TEST_ID, AccountFileHelper.CH_Section_Header));

        boolean resolved = overdraft.reactivateResolved(TEST_ID, AccountFileHelper.CH_Section_Header, 20.00);

        assertTrue(resolved);
        assertFalse(overdraft.isDeactivated(TEST_ID, AccountFileHelper.CH_Section_Header));
        assertEquals(0, AccountFileHelper.getOverDraftCount(testFile, AccountFileHelper.CH_Section_Header));
    }

}