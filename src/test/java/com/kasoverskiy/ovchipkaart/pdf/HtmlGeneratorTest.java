package com.kasoverskiy.ovchipkaart.pdf;

import com.kasoverskiy.ovchipkaart.csv.CsvImporter;
import com.kasoverskiy.ovchipkaart.model.Transaction;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by joycollector on 4/7/16.
 */
public class HtmlGeneratorTest {

    @Test
    public void testCreateHtml() throws Exception {
        List<Transaction> result = new CsvImporter().importCsv(new FileInputStream("src/test/resources/transactions_example6.csv"),null);

        LocalDate beginPeriod = LocalDate.parse("04-02-2016", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate endPeriod = LocalDate.parse("05-03-2016",DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        ByteArrayInputStream bStream = new HtmlGenerator().createHtml(result, "134567890", beginPeriod, endPeriod);
        try (OutputStream os = new FileOutputStream("src/test/resources/testHtmlGenerator.html")) {
            IOUtils.copy(bStream, os);
        }


    }
}