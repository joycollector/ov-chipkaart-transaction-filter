package com.kasoverskiy.ovchipkaart.csv;

import com.kasoverskiy.ovchipkaart.model.Transaction;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by joycollector on 4/5/16.
 */
public class CsvImporterTest {

    @Test
    public void testGetCsvRecords() throws Exception {
        CsvImporter csvImporter = new CsvImporter();
        String testData = "\"02-03-2016\";\"\";\"Schiphol Airport\";\"18:51\";\"Amsterdam RAI\";\"3.00\";\"Check-out\";\"\";\"Reizen op Saldo NS Vol tarief (2nd class)\";\"\"\n" +
                "\"02-03-2016\";\"\";\"Station RAI\";\"18:58\";\"Victorieplein\";\"0.25\";\"Check-out\";\"\";\"\";\"\"";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(testData.getBytes("UTF-8"));
        List<CSVRecord> csvRecords = csvImporter.getCsvRecords(byteArrayInputStream);
        assertEquals(2, csvRecords.size());
        assertEquals("02-03-2016", csvRecords.get(0).get(0));
    }

    @Test
    public void testConvertToTransaction() throws Exception {
        CsvImporter csvImporter = new CsvImporter();
        String testData = "\"02-03-2016\";\"\";\"Schiphol Airport\";\"18:51\";\"Amsterdam RAI\";\"3.00\";\"Check-out\";\"\";\"Reizen op Saldo NS Vol tarief (2nd class)\";\"\"";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(testData.getBytes("UTF-8"));
        List<CSVRecord> csvRecords = csvImporter.getCsvRecords(byteArrayInputStream);
        Transaction transaction = csvImporter.convertToTransaction(csvRecords.get(0));
        assertEquals(3.0d, transaction.getAmount(), 0d);
        assertEquals("Schiphol Airport", transaction.getDeparture());
    }
}