package ru.glavtoy.bankcardsystem.util;

public class CardUtil {
    public static String maskCardNumber(String number) {
        if (number.length() < 4) return "****";
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}