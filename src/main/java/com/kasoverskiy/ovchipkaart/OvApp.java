package com.kasoverskiy.ovchipkaart;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.gargoylesoftware.htmlunit.WebClient;
import com.kasoverskiy.ovchipkaart.ov.OvChipkaartClient;
import sun.util.resources.LocaleData;

import java.nio.file.Path;

/**
 * -jar ov.jar joycollector Temp1234 01-03-2016 31-03-2016 march2016.pdf
 * -l joycollector -p Temp1234 -begin 01-03-2016 -end 31-03-2016 -path march2016.pdf
 */
public class OvApp {
    @Parameter(names = {"-login", "-l"}, description = "login OV Chipkaart")
    String username;
    @Parameter(names = {"-password", "-p"}, description = "password OV Chipkaart")
    String password;
//    @Parameter(names = "-id", description = "ID card")
//    String cardId;
    @Parameter(names = {"-begin", "-b"}, description = "initial date for report")
    LocaleData beginPeriod;
    @Parameter(names = {"-end", "-e"}, description = "final date for report")
    LocaleData endPeriod;
    @Parameter(names = "-path", description = "destination path and file name")
    Path path;


    public static void main(String[] args) {
        OvApp ovApp = new OvApp();
        new JCommander(ovApp, args);
        ovApp.run();


    }

    private void run() {

        WebClient webClient = new WebClient();
        OvChipkaartClient ovChipkaartClient = new OvChipkaartClient(webClient);
    }
}
