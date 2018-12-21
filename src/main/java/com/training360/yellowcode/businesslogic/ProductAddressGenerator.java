package com.training360.yellowcode.businesslogic;

import com.training360.yellowcode.dbTables.Product;

import java.text.Normalizer;

public class ProductAddressGenerator {

    public static String generateUserFriendlyAddress(Product product) {
        String address;
        if (product.getAddress() == null) {
            address = product.getName().trim().replaceAll(" ", "-").replaceAll(":", "").toLowerCase();
        } else {
            address = product.getAddress().trim().replaceAll(" ", "-").replaceAll(":", "").toLowerCase();
        }
        return Normalizer.normalize(address, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

}
