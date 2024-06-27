package org.msolutions.controllers;

import org.msolutions.InformationBoard;
import org.msolutions.PageLoader;

import java.io.IOException;

public class MainController extends PageLoader {
    public void startDemonstration() throws IOException {
        application.showDemonstration();
    }
}
