package com.kasoverskiy.ovchipkaart.csv;

import com.kasoverskiy.ovchipkaart.model.Transaction;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Вадим on 03.04.2016.
 */
public class CsvImporter {
    private  Set<Date> holidays = new HashSet<Date>();

    public  List<Transaction> importCsv(InputStream is) throws IOException {
        List<Transaction> result = new ArrayList<Transaction>();

        getHolidays();

        GregorianCalendar checkIn;
        GregorianCalendar checkOut;
        String[] record = new String[10]; //one record in CSV
        int y, m, d, h, min;
        double amounth;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        while (reader.ready()) {

            try {
                record = Arrays.copyOf(reader.readLine().replace("\"", "").split(";"), 10);
                y = Integer.parseInt(record[0].split("-")[2]);
                m = Integer.parseInt(record[0].split("-")[1]) - 1;
                d = Integer.parseInt(record[0].split("-")[0]);
                checkIn = new GregorianCalendar(y, m, d, 0, 0);
                h = Integer.parseInt(record[3].split(":")[0]);
                min = Integer.parseInt(record[3].split(":")[1]);
                checkOut = new GregorianCalendar(y, m, d, h, min);

                if (!isValid(checkOut))
                    continue;

                amounth = Double.valueOf(record[5]);
                result.add(new Transaction(
                        checkIn.getTime(), record[2],
                        checkOut.getTime(), record[4],
                        amounth, record[6], record[7], record[8], record[9]));

            } catch (NumberFormatException e) {
                //wrong record
            } catch (ArrayIndexOutOfBoundsException e) {
                //wrong record
            }
        }

        return result;
    }

    private  void getHolidays() throws IOException {
        int currentYear = new GregorianCalendar().get(Calendar.YEAR);
        URL url = new URL("http://holidayapi.com/v1/holidays?country=NL&year=" + currentYear);
        URLConnection connection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String data = reader.readLine();
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            int y = Integer.parseInt(matcher.group().split("-")[0]);
            int m = Integer.parseInt(matcher.group().split("-")[1]) - 1;
            int d = Integer.parseInt(matcher.group().split("-")[2]);
            holidays.add(new GregorianCalendar(y, m, d).getTime());
        }
//        for (Date date : holidays) {
//            System.out.println(date);
//        }
    }

    private  boolean isValid(GregorianCalendar date) {
        Date contain = new GregorianCalendar(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE)).getTime();
        if (
                date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                        date.get(Calendar.HOUR_OF_DAY) >= 18 ||
                        holidays.contains(contain)) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            List<Transaction> a = new CsvImporter().importCsv(new FileInputStream(new File("src/test/resources/transactions_example.csv")));
            for (Transaction transaction : a) {
                System.out.println(transaction);
            }
            System.out.println(a.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
