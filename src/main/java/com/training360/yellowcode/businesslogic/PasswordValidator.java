package com.training360.yellowcode.businesslogic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {

    public boolean passwordStrengthValidator(String password) {
        int lowerCaseLetterCounter = 0;
        int upperCaseLetterCounter = 0;
        for (Character character : password.toCharArray()) {
            if (Character.isLowerCase(character)) {
                lowerCaseLetterCounter++;
            }
            if (Character.isUpperCase(character)) {
                upperCaseLetterCounter++;
            }
        }
        Pattern number = Pattern.compile("[0-9]+");
        Matcher numberMatcher = number.matcher(password);
        return password.matches(".{8,}") && lowerCaseLetterCounter > 0 && upperCaseLetterCounter > 0
                && numberMatcher.find();
    }
}
