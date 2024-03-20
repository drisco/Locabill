package com.example.notificationapp;

import java.text.DecimalFormat;

public class NumberToWords {

    // Tableau des mots pour les chiffres de 0 à 19
    private static final String[] units = {"zéro", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf", "dix",
            "onze", "douze", "treize", "quatorze", "quinze", "seize", "dix-sept", "dix-huit", "dix-neuf"};

    // Tableau des mots pour les dizaines
// Tableau des mots pour les dizaines
    private static final String[] tens = {"", "", "vingt", "trente", "quarante", "cinquante", "soixante", "soixante-dix", "quatre-vingt", "quatre-vingt-dix"};

    // Méthode pour convertir un nombre en lettres
    public static String convertToWords(int number) {
        if (number < 0 || number > 999999) {
            return "Nombre non pris en charge";
        }

        if (number < 20) {
            return units[number];
        }

        if (number < 100) {
            return tens[number / 10] + ((number % 10 != 0) ? "-" + convertToWords(number % 10) : "");
        }

        if (number < 1000) {
            // Vérifier si le reste de la division par 100 est nul
            String space = (number % 100 != 0) ? " " : "";
            return units[number / 100] + " cent" + space + ((number % 100 != 0) ? " " + convertToWords(number % 100) : "");
        }

        if (number < 2000) {
            return "mille" + ((number % 1000 != 0) ? " " + convertToWords(number % 1000) : "");
        }

        if (number < 1000000) {
            return convertToWords(number / 1000) + " mille" + ((number % 1000 != 0) ? " " + convertToWords(number % 1000) : "");
        }

        return "Nombre non pris en charge";
    }

}

