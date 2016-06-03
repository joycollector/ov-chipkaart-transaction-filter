package com.kasoverskiy.ovchipkaart.csv;

import com.kasoverskiy.ovchipkaart.OvException;
import com.kasoverskiy.ovchipkaart.model.Transaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vadelic on 03.04.2016.
 */
public class CsvImporter {
    /**
     *
     * @param is
     * @param workStation
     * @return
     */
    public List<Transaction> importCsv(InputStream is, List<String> workStation) {
        List<CSVRecord> listCsvRecords = getCsvRecords(is);

        List<Transaction> transactionList = listCsvRecords.stream()
                .map(this::convertToTransaction)
                .filter(tr -> tr.getDateCheckIn().getDayOfWeek() != DayOfWeek.SATURDAY)
                .filter(tr -> tr.getDateCheckIn().getDayOfWeek() != DayOfWeek.SUNDAY)
                .filter(tr -> tr.getCheckOut() == null || tr.getCheckOut().getHour() < 20)
                .filter(tr -> !HolidaysNL.isHoliday(tr.getDateCheckIn()))
                .collect(Collectors.toList());

        List<LocalDate> daysOnWorkStation = transactionList.stream()
                .filter(tr -> workStation.contains(tr.getDestination()) || workStation.contains(tr.getDeparture()))
                .map(Transaction::getDateCheckIn)
                .distinct()
                .collect(Collectors.toList());

        return transactionList.stream()
                .filter(tr -> daysOnWorkStation.contains(tr.getDateCheckIn()))
                .collect(Collectors.toList());
    }

    protected Transaction convertToTransaction(CSVRecord csvRecord) {
        return new Transaction.Builder()
                .dateCheckIn(csvRecord.get(0).length() == 0 ? null :
                        LocalDate.parse(csvRecord.get(0), DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .checkIn(csvRecord.get(1).length() == 0 ? null :
                        LocalTime.parse(csvRecord.get(1), DateTimeFormatter.ofPattern("HH:mm")))
                .departure(csvRecord.get(2))
                .checkOut(csvRecord.get(3).length() == 0 ? null :
                        LocalTime.parse(csvRecord.get(3), DateTimeFormatter.ofPattern("HH:mm")))
                .destination(csvRecord.get(4))
                .amount(Double.valueOf(csvRecord.get(5)))
                .transaction(csvRecord.get(6))
                .classTrans(csvRecord.get(7))
                .product(csvRecord.get(8))
                .comments(csvRecord.get(9))
                .build();
    }

    protected List<CSVRecord> getCsvRecords(InputStream is) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            CSVParser csvParser = new CSVParser(reader, CSVFormat
                    .newFormat(';')
                    .withHeader("\"Date\";\"Check-in\";\"Departure\";\"Check-out\";\"Destination\";\"Amount\";" +
                            "\"Transaction\";\"Class\";\"Product\";\"Comments\"")
                    .withSkipHeaderRecord(true)
                    .withQuote('"'));
            return csvParser.getRecords();

        } catch (IOException e) {
            throw new OvException(e);
        }
    }
}
