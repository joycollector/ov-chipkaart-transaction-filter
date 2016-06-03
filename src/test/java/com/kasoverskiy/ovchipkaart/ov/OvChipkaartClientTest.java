package com.kasoverskiy.ovchipkaart.ov;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by joycollector on 4/3/16.
 */
public class OvChipkaartClientTest {

    private WebClient webClient;
    private OvChipkaartClient ovChipkaartClient;

    @Before
    public void setUp() throws Exception {
        webClient = spy(new WebClient());
        ovChipkaartClient = new OvChipkaartClient(webClient);
    }

    @After
    public void tearDown() throws Exception {
        ovChipkaartClient.close();
    }

    @Test
    public void testLogin() throws Exception {
        final HtmlPage inloggenPage = (HtmlPage) getPageFromFile("/inloggen.htm");
        final HtmlSubmitInput spyLoginButton = spyHtmlElement((HtmlSubmitInput)
                inloggenPage.getElementById("btn-login"));

        doReturn(inloggenPage).when(webClient).getPage(OvChipkaartClient.INLOGGEN);
        doReturn(inloggenPage).when(spyLoginButton).click();
        assertTrue(ovChipkaartClient.login("username", "password"));
    }

    @Test
    public void testGetCards() throws Exception {
        final HtmlPage myTravelHistory = (HtmlPage) getPageFromFile("/my-travel-history.htm");

        doReturn(myTravelHistory).when(webClient).getPage(OvChipkaartClient.MY_TRAVEL_HISTORY);

        final HashMap<String, String> cards = ovChipkaartClient.getCards();
        assertEquals(2, cards.size());
        assertEquals("0987654321", cards.get("1234567890"));
    }

    @Test
    public void testGetTravelHistoryAsCsv() throws Exception {
        final HtmlPage travelHistoryDeclaration = (HtmlPage) getPageFromFile("/travel-history-declaration.htm");
        final TextPage csv = mock(TextPage.class);
        final HtmlButton downloadCSV = spyHtmlElement((HtmlButton)
                travelHistoryDeclaration.getDocumentElement().getOneHtmlElementByAttribute("button", "value", "CSV"));

        final LocalDate startDate = LocalDate.of(2016, 1, 1);
        final LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        final String travelDeclarationUrl = ovChipkaartClient.getTravelDeclarationUrl("1234567890", startDate, endDate);

        doReturn(travelHistoryDeclaration).when(webClient).getPage(travelDeclarationUrl);
        doReturn(csv).when(downloadCSV).click();
        doReturn("test").when(csv).getContent();

        final String travelHistoryAsCsv = ovChipkaartClient.getTravelHistoryAsCsv("1234567890", startDate, endDate);
        assertEquals("test", travelHistoryAsCsv);
    }

    private Page getPageFromFile(String file) throws URISyntaxException, IOException {
        final URI uri = OvChipkaartClientTest.class.getResource(file).toURI();
        final byte[] encoded = Files.readAllBytes(Paths.get(uri));
        final StringWebResponse webResponse = new StringWebResponse(new String(encoded, "UTF-8"),
                new URL("http://localhost"));
        return HTMLParser.parseHtml(webResponse, webClient.getCurrentWindow());
    }

    private <T extends HtmlElement> T spyHtmlElement(T origHtmlElement) {
        final T spyDownloadCsvButton = spy(origHtmlElement);
        origHtmlElement.replace(spyDownloadCsvButton);
        return spyDownloadCsvButton;
    }
}