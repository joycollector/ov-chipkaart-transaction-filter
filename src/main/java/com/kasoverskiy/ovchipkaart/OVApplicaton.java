package com.kasoverskiy.ovchipkaart;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

/**
 * Created by joycollector on 4/3/16.
 */
public class OVApplicaton extends Application<OVConfiguration> {

    public static void main(String[] args) throws Exception {
        new OVApplicaton().run(args);
    }

    @Override
    public void run(OVConfiguration configuration, Environment environment) throws Exception {

    }
}
