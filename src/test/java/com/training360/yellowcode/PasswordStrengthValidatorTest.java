package com.training360.yellowcode;

import com.training360.yellowcode.businesslogic.PasswordValidator;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class PasswordStrengthValidatorTest {

    @Test
    public void passwordStrengthValidatorTestValid() {
        assertTrue(new PasswordValidator().passwordStrengthValidator("JohnDoe550$"));
    }

    @Test
    public void passwordStrengthValidatorTestValidHungarianLetter() {
        assertTrue(new PasswordValidator().passwordStrengthValidator("ÉóhnDoe550$"));
    }

    @Test
    public void passwordStrengthValidatorTestValidHungarianCapitalLetter() {
        assertTrue(new PasswordValidator().passwordStrengthValidator("Éóhnooe550$"));
    }

    @Test
    public void passwordStrengthValidatorTestSufferingLevel() {
        assertTrue(new PasswordValidator().passwordStrengthValidator("@UO80pJmWsN&U#O^!UCB^i!IOTNO57"));
    }

    @Test
    public void passwordStrengthValidatorTestWithoutNumber() {
        assertFalse(new PasswordValidator().passwordStrengthValidator("JohnDoee"));
    }

    @Test
    public void passwordStrengthValidatorTestWithoutCapitalLetter() {
        assertFalse(new PasswordValidator().passwordStrengthValidator("johndoe220"));
    }

    @Test
    public void passwordStrengthValidatorTestTooShort() {
        assertFalse(new PasswordValidator().passwordStrengthValidator("Valami1"));
    }

    @Test
    public void passwordStrengthValidatorTestWithoutLowercaseLetter() {
        assertFalse(new PasswordValidator().passwordStrengthValidator("JOHNDOE2018"));
    }
}
