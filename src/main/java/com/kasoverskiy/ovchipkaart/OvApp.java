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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * -jar ov.jar joycollector Temp1234 01-03-2016 31-03-2016 march2016.pdf
 * -l joycollector -p Temp1234 -id 123456789 -begin 01-03-2016 -end 31-03-2016 -path march2016.pdf
 */
public class OvApp {

    @Parameter(names = {"-login", "-l"}, description = "login OV Chipkaart", required = true)
    private String username;
    @Parameter(names = {"-password", "-p"}, description = "password OV Chipkaart", required = true)
    private String password;
    @Parameter(names = "-id", description = "Card number", required = true)
    private String cardId;
    @Parameter(names = {"-begin", "-b"}, description = "initial date for report", converter = ArgsToLocalDate.class, required = true)
    private LocalDate beginPeriod;
    @Parameter(names = {"-end", "-e"}, description = "final date for report", converter = ArgsToLocalDate.class, required = true)
    private LocalDate endPeriod;
    @Parameter(names = "-path", description = "out pdf", required = true)
    private Path pathOutPdf;
    @Parameter(names = {"-station", "-s"}, variableArity = true, description = "Work station")
    private List<String> workStation = new ArrayList<>();

    public static class ArgsToLocalDate implements IStringConverter<LocalDate> {

        @Override
        public LocalDate convert(String value) {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        }
    }


    public static void main(String[] args) {
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

    private void run() {
        try (
                WebClient webClient = new WebClient();
                OutputStream os = Files.newOutputStream(pathOutPdf)
        ) {
            webClient.getOptions().setJavaScriptEnabled(true);
            // Need to disable javascript errors due to errors during CSV download.
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            OvChipkaartClient ovChipkaartClient = new OvChipkaartClient(webClient);

            if (!ovChipkaartClient.login(username, password)) {
                throw new OvException("could not connect to the server");
            }


            String mediumId = ovChipkaartClient.getCards().get(cardId);
            if (mediumId == null) {
                throw new OvException("Card " + cardId + " don't exist");
            }

            String csv = ovChipkaartClient.getTravelHistoryAsCsv(mediumId, beginPeriod, endPeriod);
            InputStream streamCsv = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

            List<Transaction> transactions = new CsvImporter().importCsv(streamCsv, workStation);

            Map<String, String> personalInfo = ovChipkaartClient.getPersonalInfo(cardId);
            ByteArrayInputStream byteArrayInStream = new HtmlGenerator().createHtml(transactions, personalInfo, beginPeriod, endPeriod);

            new PdfGenerator().createPdf(byteArrayInStream, os);

        } catch (Exception e) {
            e.printStackTrace();
            throw new OvException(pathOutPdf + " file can't be created.", e);
        }

    }
}
