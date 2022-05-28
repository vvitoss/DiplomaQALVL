package ru.netology.data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataGenerator {
    private DataGenerator() {
    }

    public static Faker faker = new Faker(new Locale("en"));

    public static String getFirstCardNumber() {
        return ("4444 4444 4444 4441");
    }

    public static String getSecondCardNumber() {
        return ("4444 4444 4444 4442");
    }

    public static String getInvalidCardNumber() {
        return faker.finance().creditCard();
    }

    public static String getInvalidShortCardNumber() {
        return faker.numerify("#### #### #### #");
    }

    public static String getMonthCard(int plusMonth) {
        return LocalDate.now().plusMonths(plusMonth).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String getInvalidMonthCardOneDigit() {
        return faker.numerify("#");
    }

    public static String getInvalidMonthCardInvalidPeriod() {
        return "19";
    }

    public static String getInvalidMonthCard() {
        return "00";
    }

    public static String getYearCard(int plusYears) {
        return LocalDate.now().plusYears(plusYears).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String getInvalidYearCard() {
        return faker.numerify("#");
    }

    public static String getCvc() {
        return faker.numerify("###");
    }

    public static String getInvalidCvc() {
        return faker.numerify("##");
    }

    public static String getOwnerCard() {
        return faker.name().name();
    }

    public static String getInvalidOwnerCard() {
        return faker.name().firstName();
    }

    public static String getInvalidOwnerCardCyrillic() {
        Faker faker = new Faker(new Locale("ru"));
        return faker.name().name();
    }

    public static String getInvalidOwnerCardWithNumbers() {
        return (faker.name().firstName() + faker.numerify("#######"));
    }
    public static String getInvalidOwnerCardOneLetterName() {
        return "G";
    }

}
