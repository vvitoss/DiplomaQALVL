package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Feature;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.CardInfo;
import ru.netology.data.DbInteractionDbUtils;
import ru.netology.page.StartPage;


import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataGenerator.*;

@Feature("Тестируем покупку тура в кредит по данным банковской карты")
public class CreditPageTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void setUp() {
       // Configuration.headless = true;
        open("http://localhost:8080");
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
        DbInteractionDbUtils.deleteTables();
    }

    @DisplayName("Проверяем карту со статусом APPROVED")
    @Test
    void shouldPaymentWithApprovedCard() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getMonthCard(1), getYearCard(2), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.successfulPaymentCreditCard();
        String actual = DbInteractionDbUtils.getStatusCredit();
        assertEquals("APPROVED", actual);
    }

    @DisplayName("Срок окончания действия карты: текущий месяц текущего года, статус APPROVED")
    @Test
    void shouldPaymentWithApprovedCardExpires() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getMonthCard(0), getYearCard(0), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.successfulPaymentCreditCard();
        String actual = DbInteractionDbUtils.getStatusCredit();
        assertEquals("APPROVED", actual);
    }

    @DisplayName("Проверяем карту со статусом DECLINED")
    @Test
    void shouldPaymentWithDeclinedCard() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getSecondCardNumber(), getMonthCard(0), getYearCard(1), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.invalidPaymentCreditCard();
        String actual = DbInteractionDbUtils.getStatusCredit();
        assertEquals("DECLINED", actual);
    }

    @DisplayName("Срок окончания действия карты: текущий месяц текущего года, статус DECLINED")
    @Test
    void shouldPaymentWithDeclinedCardExpires() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getSecondCardNumber(), getMonthCard(0), getYearCard(0), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.invalidPaymentCreditCard();
        String actual = DbInteractionDbUtils.getStatusCredit();
        assertEquals("DECLINED", actual);
    }

    @DisplayName("Невалидный номер банковской карты: несуществующая карта")
    @Test
    void shouldPaymentWithInvalidCardNumber() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getInvalidCardNumber(), getMonthCard(2), getYearCard(1), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.invalidPaymentCreditCard();
    }

    @DisplayName("Невалидный номер банковской карты: 13 цифр")
    @Test
    void shouldPaymentWithInvalidCardNumberShort() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getInvalidShortCardNumber(), getMonthCard(2), getYearCard(1), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkInvalidFormat();
    }

    @DisplayName("Невалидный период действия карты: срок окончания карты - год, предшесвующий текущему")
    @Test
    void shouldPaymentExpiredCard() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getMonthCard(0), getYearCard(-1), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkCardExpired();
    }

    @DisplayName("Невалидный период действия карты: месяц предшествующий текущему, год текущий")
    @Test
    void shouldPaymentIncorrectCardExpirationDate() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getMonthCard(-1), getYearCard(0), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkInvalidCardValidityPeriod();
    }

    @DisplayName("Невалидный период действия карты: платежная карта действительна более 5 лет")
    @Test
    void shouldPaymentCardValidMoreThanFiveYears() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getMonthCard(1), getYearCard(6), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkInvalidCardValidityPeriod();
    }

    @DisplayName("Невалидный год: ввод менее 2 цифр")
    @Test
    void shouldPaymentCardInvalidYearOneDigit() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getMonthCard(2), getInvalidYearCard(), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkInvalidFormat();
    }

    @DisplayName("Данные о владельце карты указаны неверно: введено только Имя")
    @Test
    void shouldPaymentInvalidOwnerCard() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getMonthCard(1), getYearCard(3), getInvalidOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkInvalidOwner();
    }

    @DisplayName("Данные о владельце карты указаны неверно: имя и фамилия на кириллице")
    @Test
    void shouldPaymentInvalidOwnerCardInCyrillic() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getMonthCard(1), getYearCard(3),
                getInvalidOwnerCardCyrillic(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.incorrectOwner();
    }

    @DisplayName("Данные о владельце карты указаны неверно: цифры в имени")
    @Test
    void shouldPaymentInvalidOwnerCardWithNumbers() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getMonthCard(1), getYearCard(3),
                getInvalidOwnerCardWithNumbers(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.incorrectOwner();
    }

    @DisplayName("Данные о владельце карты указаны неверно: имя, состоящее из 1 буквы")
    @Test
    void shouldPaymentInvalidOwnerCardOneLetterName() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getMonthCard(1), getYearCard(3),
                getInvalidOwnerCardOneLetterName(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.incorrectOwner();
    }


    @DisplayName("Невалидный код CVC: ввод менее 3 цифр")
    @Test
    void shouldPaymentCardInvalidCvc() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getMonthCard(1), getYearCard(2), getOwnerCard(), getInvalidCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkInvalidFormat();
    }

    @DisplayName("Невалидный месяц: ввод менее 2 цифр")
    @Test
    void shouldPaymentCardInvalidMonthOneDigit() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getInvalidMonthCardOneDigit(), getYearCard(2), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkInvalidFormat();
    }

    @DisplayName("Невалидный месяц: не входит в валидный интервал 1-12")
    @Test
    void shouldPaymentCardInvalidMonthInvalidPeriod() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getInvalidMonthCardInvalidPeriod(), getYearCard(2), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkInvalidCardValidityPeriod();
    }

    @DisplayName("Невалидный месяц: 00")
    @Test
    void shouldPaymentCardInvalidMonth() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getInvalidMonthCard(), getYearCard(2), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkInvalidCardValidityPeriod();
    }

    @DisplayName("Невалидные данные карты: поле Номер карты - не заполнено")
    @Test
    void shouldPaymentEmptyFieldNumberCard() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                null, getMonthCard(1), getYearCard(2), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkInvalidFormat();
    }

    @DisplayName("Невалидные данные карты: поле месяц - не заполнено")
    @Test
    void shouldPaymentEmptyFieldMonth() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), null, getYearCard(2), getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkInvalidFormat();
    }

    @DisplayName("Невалидные данные карты: поле год - не заполнено")
    @Test
    void shouldPaymentEmptyFieldYears() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getMonthCard(2), null, getOwnerCard(), getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkInvalidFormat();
    }

    @DisplayName("Невалидные данные карты: поле владелец - не заполнено")
    @Test
    void shouldPaymentEmptyFieldOwner() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getMonthCard(2), getYearCard(3), null, getCvc());
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkEmptyField();
    }

    @DisplayName("Невалидные данные карты: поле CVC - не заполнено")
    @Test
    void shouldPaymentEmptyFieldCvc() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                getFirstCardNumber(), getMonthCard(2), getYearCard(3), getOwnerCard(), null);
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkEmptyField();
    }

    @DisplayName("Отправка пустой формы покупки тура")
    @Test
    void shouldPaymentEmptyAllField() {
        var startPage = new StartPage();
        CardInfo card = new CardInfo(
                null, null, null, null, null);
        var creditPage = startPage.paymentOnCredit();
        creditPage.getFillCardDetails(card);
        creditPage.checkAllFieldsAreRequired();
    }


}