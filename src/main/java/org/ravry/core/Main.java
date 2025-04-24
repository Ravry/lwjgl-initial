package org.ravry.core;

import org.ravry.graphics.Window;
import org.ravry.utilities.Logger;

import static org.ravry.utilities.Logger.LOG_STATE.ERROR_LOG;

public class Main {
    public static void main(String[] args) {
        final int WIDTH = 1440;
        final int HEIGHT = 810;
        try {
            Window window = new Window("subscribe!", WIDTH, HEIGHT);
            window.run();
        } catch (Exception ex) {
            Logger.LOG(ERROR_LOG, ex.getMessage());
        }
    }

}