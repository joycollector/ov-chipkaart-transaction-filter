package com.kasoverskiy.ovchipkaart.ov;

import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.kasoverskiy.ovchipkaart.OvException;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joycollector on 4/3/16.
 */
public class OvChipkaartClient implements Closeable {
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static final String WWW_OVCHIPKAART_NL = "https://www.ov-chipkaart.nl";
    public final static String INLOGGEN = WWW_OVCHIPKAART_NL + "/inloggen.htm";
    public static final String MY_OVCHIPKAART = WWW_OVCHIPKAART_NL + "/my-ovchipkaart";
    public final static String MY_TRAVEL_HISTORY = MY_OVCHIPKAART + "/my-travel-history/my-travel-history.htm";
    public final static String TRAVEL_HISTORY_DECLARATION_PATTERN = MY_OVCHIPKAART + "/my-travel-history/travel-history-declaration.htm?mediumid=%s&begindate=%s&enddate=%s&type=#make-declaration";

    private WebClient webClient;

    public OvChipkaartClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public void close() throws IOException {
        webClient.close();
    }

    public boolean login(String pUserName, String pPassword) {
        try {
            final HtmlPage inloggen = webClient.getPage(INLOGGEN);

            final HtmlSubmitInput loginButton = (HtmlSubmitInput) inloggen.getElementById("btn-login");
            final HtmlTextInput username = (HtmlTextInput) inloggen.getElementById("username");
            final HtmlPasswordInput password = (HtmlPasswordInput) inloggen.getElementById("password");

            username.setValueAttribute(pUserName);
            password.setValueAttribute(pPassword);

            final HtmlPage mainPage = loginButton.click();
            return !mainPage.asText().contains("De combinatie van gebruikersnaam en wachtwoord is niet bekend.");
        } catch (IOException e) {
            throw new OvException("Unable to login", e);
        }
    }

    public HashMap<String, String> getCards() {
        try {
            HashMap<String, String> cardsMap = new HashMap<>();
            HtmlPage travelHistory = webClient.getPage(MY_TRAVEL_HISTORY);
            List<HtmlElement> cards = travelHistory.getDocumentElement().getElementsByAttribute("span", "class", "cs-card-number");
            for (HtmlElement cardElement : cards) {
                String cardNumber = cardElement.getTextContent();
                String mediumId = cardElement.getAttribute("data-hashed");
                cardsMap.put(cardNumber, mediumId);
            }
            return cardsMap;
        } catch (IOException e) {
            throw new OvException("Unable to get cards list", e);
        }
    }

    public String getTravelHistoryAsCsv(String cardMediumId, LocalDate startDate, LocalDate endDate) {
        try {
            String declarationUrl = getTravelDeclarationUrl(cardMediumId, startDate, endDate);
            HtmlPage travelHistoryDeclaration = webClient.getPage(declarationUrl);
            HtmlButton downloadCSV = travelHistoryDeclaration.getDocumentElement().getOneHtmlElementByAttribute("button", "value", "CSV");
            TextPage csvResponse = downloadCSV.click();
            return csvResponse.getContent();
        } catch (IOException e) {
            throw new OvException("Unable to get travel history", e);
        }
    }

    public String getTravelDeclarationUrl(String cardMediumId, LocalDate startDate, LocalDate endDate) {
        return String.format(TRAVEL_HISTORY_DECLARATION_PATTERN, cardMediumId, FORMATTER.format(startDate), FORMATTER.format(endDate));
    }
}
