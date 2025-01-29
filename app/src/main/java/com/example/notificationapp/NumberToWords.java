package com.example.notificationapp;

public class NumberToWords {
    private static final String[] units = {"zéro", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf", "dix",
            "onze", "douze", "treize", "quatorze", "quinze", "seize", "dix-sept", "dix-huit", "dix-neuf"};

    private static final String[] tens = {"", "", "vingt", "trente", "quarante", "cinquante", "soixante", "soixante-dix", "quatre-vingt", "quatre-vingt-dix"};

    public static String convertToWords(int number) {
        if (number < 0 || number > 999999) {
            return "Nombre non pris en charge";
        }

        if (number < 20) {
            return units[number];
        }

        if (number < 100) {
            return convertTens(number);
        }

        if (number < 1000) {
            return convertHundreds(number);
        }

        if (number < 2000) {
            return convertThousands(number);
        }

        if (number < 1000000) {
            return convertMoreThanThousands(number);
        }

        return "Nombre non pris en charge";
    }

    private static String convertTens(int number) {
        if (number < 20) {
            return units[number];
        }

        int tensDigit = number / 10;
        int unitsDigit = number % 10;

        if (unitsDigit == 0) {
            return tens[tensDigit];
        }

        if (tensDigit == 7) {
            return "soixante-" + convertToWords(10 + unitsDigit);
        }

        if (tensDigit == 9) {
            return "quatre-vingt-" + convertToWords(10 + unitsDigit);
        }

        return tens[tensDigit] + "-" + units[unitsDigit];
    }

    private static String convertHundreds(int number) {
        int hundreds = number / 100;
        int remainder = number % 100;

        if (remainder == 0) return units[hundreds] + " cent";

        return units[hundreds] + " cent " + convertToWords(remainder);
    }

    private static String convertThousands(int number) {
        int remainder = number % 1000;

        if (remainder == 0) return "mille";

        return "mille " + convertToWords(remainder);
    }

    private static String convertMoreThanThousands(int number) {
        int thousands = number / 1000;
        int remainder = number % 1000;

        if (remainder == 0) return convertToWords(thousands) + " mille";

        return convertToWords(thousands) + " mille " + convertToWords(remainder);
    }
}