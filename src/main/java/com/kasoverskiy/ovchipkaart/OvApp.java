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

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * -jar ov.jar joycollector Temp1234 01-03-2016 31-03-2016 march2016.pdf
 * -l joycollector -p Temp1234 -id 123456789 -begin 01-03-2016 -end 31-03-2016 -path march2016.pdf
 */
public class OvApp {

    public static final Logger LOGGER = Logger.getLogger("com.kasoverskiy.ovchipkaart");
    @Parameter(names = {"-login", "-l"}, description = "login OV Chipkaart", required = true)
    private String username;
    @Parameter(names = {"-password", "-p"}, description = "password OV Chipkaart", required = true)
    private String password;
    @Parameter(names = "-id", description = "Card number", required = true)
    private String cardId;
    @Parameter(names = {"-begin", "-b"}, description = "initial date for report", converter = ArgsToLocalDate.class, required = true)
    private LocalDate beginPeriod;
    @Parameter(names = {"-end", "-e"}, description = "final date for report", converter = ArgsToLocalDate.class)
    private LocalDate endPeriod;
    @Parameter(names = "-path", description = "out pdf")
    private Path pathOutPdf;
    @Parameter(names = {"-station", "-s"}, variableArity = true, description = "Work station")
    private List<String> workStation = new ArrayList<>();

    public static class ArgsToLocalDate implements IStringConverter<LocalDate> {

        @Override
        public LocalDate convert(String value) {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        }
    }


    public static void main(String[] args) throws IOException {
        Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
        OvApp ovApp = new OvApp();
        //This replacement emulates shutdown separator "comma"
        args = Arrays.stream(args).map(s -> s.replace(",", "&separator&")).toArray(String[]::new);
        JCommander jCommander = new JCommander(ovApp);
        if (args.length == 0) {
            jCommander.usage();
        } else {
            jCommander.parse(args);
            ovApp.workStation = ovApp.workStation.stream().map(s -> s.replace("&separator&", ",")).collect(Collectors.toList());
            ovApp.run();
        }
    }

    private void run() throws IOException {
        try (
                WebClient webClient = new WebClient()
        ) {

            webClient.getOptions().setJavaScriptEnabled(true);
            // Need to disable javascript errors due to errors during CSV download.
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            OvChipkaartClient ovChipkaartClient = new OvChipkaartClient(webClient);
            LOGGER.info("Log in for user " + username);
            if (!ovChipkaartClient.login(username, password)) {
                throw new OvException("could not connect to the server");
            }
            LOGGER.info("Logged in.");

            LOGGER.info("Getting card with id " + cardId);
            String mediumId = ovChipkaartClient.getCards().get(cardId);
            if (mediumId == null) {
                throw new OvException("Card " + cardId + " don't exist");
            }
            LOGGER.info("Card found.");

            if (endPeriod == null) {
                endPeriod = beginPeriod.with(TemporalAdjusters.lastDayOfMonth());
            }

            LOGGER.info("Loading transactions for period " + beginPeriod + " - " + endPeriod);
            String csv = ovChipkaartClient.getTravelHistoryAsCsv(mediumId, beginPeriod, endPeriod);
            InputStream streamCsv = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

            List<Transaction> transactions = new CsvImporter().importCsv(streamCsv, workStation);
            LOGGER.info("Transactions loaded.");

            LOGGER.info("Getting personal info.");
            Map<String, String> personalInfo = ovChipkaartClient.getPersonalInfo(cardId);
            LOGGER.info("Personal info loaded.");
            LOGGER.info("Creating PDF.");

            if (pathOutPdf == null) {
                pathOutPdf = Paths.get("ov-chipkaart-report-" + beginPeriod.toString() + "--" + endPeriod.toString() + ".pdf");
            }

            ByteArrayInputStream byteArrayInStream = new HtmlGenerator().createHtml(transactions, personalInfo, beginPeriod, endPeriod);
            try (OutputStream os = new FileOutputStream(pathOutPdf.toFile())) {
                new PdfGenerator().createPdf(byteArrayInStream, os);
            }
            LOGGER.info("PDF created.");
            Desktop.getDesktop().open(pathOutPdf.toFile());

        }
    }
}
