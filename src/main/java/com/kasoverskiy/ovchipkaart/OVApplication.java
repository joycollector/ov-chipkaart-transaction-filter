package com.kasoverskiy.ovchipkaart;

import com.gargoylesoftware.htmlunit.WebClient;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

/**
 * Created by joycollector on 4/3/16.
 */
public class OVApplication extends Application<OVConfiguration> {

    public static void main(String[] args) throws Exception {
        new OVApplication().run(args);
    }

    @Override
    public void run(OVConfiguration configuration, Environment environment) throws Exception {
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(true);
        // Need to disable javascript errors due to errors during CSV download.
        webClient.getOptions().setThrowExceptionOnScriptError(false);
    }
}
