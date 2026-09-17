package com.ga.acmebank.usertype;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class BankerTest {


    @Test
    @DisplayName("When customer ID generator runs then a customer ID is produced")
    public final void whenCustomerIdGeneratorRunsThenCACustomerIdIsProduced() {
        Banker banker= new Banker();
        String id = banker.customerIDGenerator('C');
        assertTrue(id.startsWith("C"));
        assertTrue(Long.parseLong(id.substring(1)) >= 10000);
    }

    @Test
    @DisplayName("When banker ID generator runs then a banker ID is produced")
    public final void whenBankerIdGeneratorRunsThenABankerIdIsProduced() {
        Banker banker= new Banker();
        String id = banker.bankerIDGenerator('B');
        assertTrue(id.startsWith("B"));
        assertTrue(Long.parseLong(id.substring(1)) >= 100);
    }
}