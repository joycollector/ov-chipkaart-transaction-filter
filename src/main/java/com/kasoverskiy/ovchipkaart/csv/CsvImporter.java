package com.kasoverskiy.ovchipkaart.csv;

import com.kasoverskiy.ovchipkaart.model.Transaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Predicate;

/**
 * Created by ����� on 03.04.2016.
 */
public class CsvImporter {
    public List<Transaction> importCsv(InputStream is) throws IOException {
        List<Transaction> result = new ArrayList<>();
        List<CSVRecord> list = getCsvRecords(is);

        for (CSVRecord csvRecord : list) {
            try {
                Transaction transaction = convertToTransaction(csvRecord);
                result.add(transaction);
            } catch (DateTimeParseException e) {
                //skip header
                //because method CSVParser.withSkipHeaderRecord isn't worked
            }
        }

        result.removeIf(new Predicate<Transaction>() {
            @Override
            public boolean test(Transaction transaction) {
                if (transaction.getCheckOut().getDayOfWeek() == DayOfWeek.SATURDAY ||
                        transaction.getCheckOut().getDayOfWeek() == DayOfWeek.SUNDAY ||
                        transaction.getCheckOut().getHour() >= 18 ||
                        HolidaysNL.isHoliday(LocalDate.from(transaction.getCheckIn()))) {
                    System.out.println("del "+transaction);
                    return true;
                }
                return false;
            }
        });
        return result;
    }

    Transaction convertToTransaction(CSVRecord csvRecord) {
        LocalDateTime checkIn;
        LocalDateTime checkOut;
        double amounth;DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        checkIn = LocalDateTime.parse(csvRecord.get(0).concat(" 00:00"), formatter);
        checkOut = LocalDateTime.parse(csvRecord.get(0).concat(" ").concat(csvRecord.get(3)), formatter);
        amounth = Double.valueOf(csvRecord.get(5));
        return new Transaction.Builder()
                .checkIn(checkIn)
                .departure(csvRecord.get(2))
                .checkOut(checkOut)
                .destination(csvRecord.get(4))
                .amount(amounth)
                .transaction(csvRecord.get(6))
                .classTrans(csvRecord.get(7))
                .product(csvRecord.get(8))
                .comments(csvRecord.get(9))
                .build();
    }

    List<CSVRecord> getCsvRecords(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        CSVParser csvParser = new CSVParser(reader, CSVFormat.newFormat(';').withSkipHeaderRecord(true).withQuote('"'));
        return csvParser.getRecords();
    }

    public static void main(String[] args) throws IOException {
        List<Transaction> a = new CsvImporter().importCsv(new FileInputStream(new File("src/test/resources/transactions_example.csv")));
        for (Transaction transaction : a) {
            System.out.println(transaction);
        }
        System.out.println(a.size());

    }
}
