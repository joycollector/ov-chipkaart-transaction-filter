package com.kasoverskiy.ovchipkaart;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Created by joycollector on 4/3/16.
 */
 class OvApplication {

    public static void main(String[] args) throws Exception {
        new OvApplication().run();
    }


    public void run() throws Exception {
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(true);
        // Need to disable javascript errors due to errors during CSV download.
        webClient.getOptions().setThrowExceptionOnScriptError(false);
    }

}
