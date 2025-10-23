package ru.glavtoy.bankcardsystem.util;

public class CardUtil {
    public static String maskCardNumber(String number) {
        if (number == null) return "****";
        String digits = number.replaceAll("\\s+", "");
        if (digits.length() <= 4) return "****";
        String last4 = digits.substring(digits.length() - 4);
        return "**** **** **** " + last4;
    }
}