package com.kasoverskiy.ovchipkaart.csv;

import com.kasoverskiy.ovchipkaart.model.Transaction;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Вадим on 03.04.2016.
 */
public class CsvImporter {
    public List<Transaction> importCsv(InputStream is) throws IOException {
        List<Transaction> result = new ArrayList<Transaction>();
        Calendar checkIn;
        Calendar checkOut;
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
                amounth = Double.valueOf(record[5]);
                result.add(new Transaction(
                        checkIn, record[2], checkOut, record[4],
                        amounth, record[6], record[7], record[8], record[9]));

            } catch (NumberFormatException e) {
                //wrong record
            } catch (ArrayIndexOutOfBoundsException e) {
                //wrong record
            }
        }
        //Adding holidays for all years for which there is a transaction
        //На тот случай, если отчётный период захватывает 2 и более смежных годов
        Comparator<Transaction> yearTransaction = new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                return o1.getCheckOut().get(Calendar.YEAR) - o2.getCheckOut().get(Calendar.YEAR);
            }
        };
        final Set<Calendar> holidays = new HashSet<>();
        int beginYear = Collections.min(result, yearTransaction).getCheckOut().get(Calendar.YEAR);
        int endYear = Collections.max(result, yearTransaction).getCheckOut().get(Calendar.YEAR);
        for (int i = beginYear; i <= endYear; i++) {
            holidays.addAll(getHolidays(i));
        }
        //Remove holidays and other unpaid date
        result.removeIf(new Predicate<Transaction>() {
            @Override
            public boolean test(Transaction transaction) {
                if (transaction.getCheckOut().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        transaction.getCheckOut().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ||
                        transaction.getCheckOut().get(Calendar.HOUR_OF_DAY) >= 18 ||
                        holidays.contains(transaction.getCheckIn())) {
//                    System.out.println("del "+transaction);
                    return true;
                }
                return false;
            }
        });

        return result;
    }

    /**
     * Return set national NL holidays for the year from holidayapi.com
     *Тут, наверняка, надо использовать JSON но я просто распарсил
     * @param currentYear
     * @return
     * @throws IOException
     */
    public Set<Calendar> getHolidays(int currentYear) throws IOException {
        URL url = new URL("http://holidayapi.com/v1/holidays?country=NL&year=" + currentYear);
        URLConnection connection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(reader.readLine());

        Set<Calendar> result = new HashSet<Calendar>();
        while (matcher.find()) {
            int y = Integer.parseInt(matcher.group().split("-")[0]);
            int m = Integer.parseInt(matcher.group().split("-")[1]) - 1;
            int d = Integer.parseInt(matcher.group().split("-")[2]);
            result.add(new GregorianCalendar(y, m, d));
        }
        return result;
    }


//    public static void main(String[] args) {
//        try {
//            List<Transaction> a = new CsvImporter().importCsv(new FileInputStream(new File("src/test/resources/transactions_example.csv")));
//            for (Transaction transaction : a) {
//                System.out.println(transaction);
//            }
//            System.out.println(a.size());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
