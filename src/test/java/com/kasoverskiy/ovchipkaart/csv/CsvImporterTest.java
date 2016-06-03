package com.kasoverskiy.ovchipkaart.csv;

import com.kasoverskiy.ovchipkaart.model.Transaction;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by joycollector on 4/5/16.
 */
public class CsvImporterTest {
    static final String CSV_TEST_FILE = "src/test/resources/transactions_example.csv";
    static final String CSV_TEST_FILE_6 = "src/test/resources/transactions_example6.csv";

    @Test
    public void testGetCsvRecords() throws Exception {
        CsvImporter csvImporter = new CsvImporter();
        List<CSVRecord> csvRecords = csvImporter.getCsvRecords(new FileInputStream(CSV_TEST_FILE));
        assertEquals(97, csvRecords.size());
    }

    @Test
    public void testConvertToTransaction() throws Exception {

        CsvImporter csvImporter = new CsvImporter();
        List<CSVRecord> csvRecords = csvImporter.getCsvRecords(new FileInputStream(CSV_TEST_FILE));
        Transaction transaction = csvImporter.convertToTransaction(csvRecords.get(0));

        for (Field f : transaction.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            System.out.println(f.getName() + " \"" + f.get(transaction) + "\"");
        }

        assertEquals("Pres. Kennedylaan", csvRecords.get(0).get(2));
        assertEquals("06:27", csvRecords.get(0).get(3));
        assertEquals("Station RAI", csvRecords.get(0).get(4));
        assertEquals("1.07", csvRecords.get(0).get(5));
    }

    @Test
    public void testImportCsv() throws Exception {
        List<String> list = new ArrayList<>();
//        list.add("Schiphol, Airport");
        list.add("Schiphol-Rijk");

        List<Transaction> result = new CsvImporter().importCsv(new FileInputStream(CSV_TEST_FILE), list);
        assertEquals(69, result.size());


    }
}