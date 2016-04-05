package com.kasoverskiy.ovchipkaart.csv;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by Вадим on 05.04.2016.
 */
public class HolidaysNL {
    static private Map<Integer, Set<LocalDate>> holidays = new HashMap<>();



    public static boolean isHoliday(LocalDate date) {
        if (!holidays.containsKey(date.getYear())) {
            getHolidays(date.getYear());
        }
        return holidays.get(date.getYear()).contains(date);
    }

    /**
     * Return set national NL holidays for the year from holidayapi.com
     *
     * @param year
     * @return
     * @throws IOException
     */
    private static void getHolidays(int year) {
        Set<LocalDate> result = null;
        try {
            result = new HashSet<>();

            URL url = new URL("http://holidayapi.com/v1/holidays?country=NL&year=" + year);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            JSONObject dataJsonObj = new JSONObject(reader.readLine());
            JSONObject records = dataJsonObj.getJSONObject("holidays");
            for (String each : records.keySet()) {
                LocalDate localeData = LocalDate.parse(each);
                result.add(localeData);
            }
            holidays.put(year, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
