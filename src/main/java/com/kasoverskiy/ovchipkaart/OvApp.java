package com.kasoverskiy.ovchipkaart;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.gargoylesoftware.htmlunit.WebClient;
import com.kasoverskiy.ovchipkaart.csv.CsvImporter;
import com.kasoverskiy.ovchipkaart.model.Transaction;
import com.kasoverskiy.ovchipkaart.ov.OvChipkaartClient;
import com.kasoverskiy.ovchipkaart.pdf.HtmlGenerator;
import com.kasoverskiy.ovchipkaart.pdf.PdfGenerator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * -jar ov.jar joycollector Temp1234 01-03-2016 31-03-2016 march2016.pdf
 * -l joycollector -p Temp1234 -id 123456789 -begin 01-03-2016 -end 31-03-2016 -path march2016.pdf
 */
public class OvApp {
    @Parameter(names = {"-login", "-l"}, description = "login OV Chipkaart")
    String username;
    @Parameter(names = {"-password", "-p"}, description = "password OV Chipkaart")
    String password;
    @Parameter(names = "-id", description = "ID card")
    String cardId;
    @Parameter(names = {"-begin", "-b"}, description = "initial date for report", converter = ArgsToLocalDate.class)
    LocalDate beginPeriod;
    @Parameter(names = {"-end", "-e"}, description = "final date for report", converter = ArgsToLocalDate.class)
    LocalDate endPeriod;
    @Parameter(names = "-path", description = "destination path and file name")
    Path path;


    public static class ArgsToLocalDate implements IStringConverter<LocalDate> {

        @Override
        public LocalDate convert(String value) {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        }
    }


    public static void main(String[] args) {
        OvApp ovApp = new OvApp();
        args = "-l joycollector -p Temp1234 -id 3528020089725993 -begin 04-04-2016 -end 05-04-2016 -path jan_fev2016.pdf".split(" ");
        new JCommander(ovApp, args);
        ovApp.run();
    }

    private void run() {
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(true);
        // Need to disable javascript errors due to errors during CSV download.
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        OvChipkaartClient ovChipkaartClient = new OvChipkaartClient(webClient);

        if (!ovChipkaartClient.login(username, password)) {
            throw new OvException("could not connect to the server");
        }

        try (OutputStream os = Files.newOutputStream(path)
        ) {
            String mediumId = ovChipkaartClient.getCards().get(cardId);
            String csv = ovChipkaartClient.getTravelHistoryAsCsv(mediumId, beginPeriod, endPeriod);
            InputStream stream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
            List<Transaction> transactions = new CsvImporter().importCsv(stream);
            ByteArrayInputStream byteArrayInStream = new HtmlGenerator().createHtml(transactions);
            new PdfGenerator().createPdf(byteArrayInStream, os);

        } catch (Exception e) {
            throw new OvException(path + " file can't be created.", e);
        }

    }
}
