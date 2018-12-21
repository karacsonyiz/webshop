package com.training360.yellowcode;

import com.training360.yellowcode.businesslogic.ProductAddressGenerator;
import com.training360.yellowcode.dbTables.Product;
import com.training360.yellowcode.dbTables.ProductStatusType;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class UserFriendlyAddressGeneratorTest {

    @Test
    public void userFriendlyAddressGeneratorTest() {
        Product product = new Product(2, "80 nap alatt a Java körül", null,
                "Jules Doe", 3000, ProductStatusType.ACTIVE);
        assertEquals(ProductAddressGenerator.generateUserFriendlyAddress(product), "80-nap-alatt-a-java-korul");
    }

    @Test
    public void userFriendlyAddressGeneratorTestWithColon() {
        Product product = new Product(2, "A java ura: Doe", null,
                "Jules Doe", 9000, ProductStatusType.ACTIVE);
        assertEquals(ProductAddressGenerator.generateUserFriendlyAddress(product), "a-java-ura-doe");
    }
}
