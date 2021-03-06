package net.lobby_simulator_companion.loop;

import net.lobby_simulator_companion.loop.ui.GithubPanel;
import org.pcap4j.core.Pcaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class Sanity {
    private static final Logger logger = LoggerFactory.getLogger(Sanity.class);

    private static boolean headless = false;

    public static boolean check() {
        boolean[] checks = {
                checkGraphics(),
                checkUpdate(),
                checkJava()
//                checkPCap()
        };

        for (boolean check : checks) {
            if (!check)
                return false;
        }
        return true;
    }

    /**
     * Check for a valid graphical environment.
     */
    private static boolean checkGraphics() {
        if (GraphicsEnvironment.isHeadless()) {
            headless = true;
            message("This program requires a graphical environment to run!\nIt's weird that you even got this far.");
            return false;
        }
        return true;
    }

    /**
     * Check the current Java Version.
     */
    private static boolean checkJava() {
        String v = System.getProperty("java.version");
        logger.info("Java version: {}", v);
        if (!v.equals("9")) {
            double version = Double.parseDouble(v.substring(0, v.indexOf('.', 2)));
            if (version < 1.8) {
                message("Java version 8 or higher is required!\nYou are currently using " + version + "!\n");
                return false;
            }
        }
        return true;
    }

    /**
     * Check the WinPcap lib installation.
     */
    private static boolean checkPCap() {
        try {
            logger.info("Pcap info: {}", Pcaps.libVersion());
        } catch (Error e) {
            logger.error("Failed to initialize PCap library.", e);
            message("You MUST have NPCap or WinPCap installed to allow this program to monitor the lobby!"
                    + (Desktop.isDesktopSupported() ? "\nAn installer link will attempt to open." : "Please go to https://www.winpcap.org/ and install it."));
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URL("https://nmap.org/npcap/").toURI());
                } catch (IOException | URISyntaxException e1) {
                    logger.error("Failed to open URL on browser.", e1);
                    message("We couldn't open the URL for you, so go to https://nmap.org/npcap/ and install it!");
                }
            }
            return false;
        }
        return true;
    }

    public static boolean checkUpdate() {
        GithubPanel mp = new GithubPanel();

        if (!mp.prompt()) {
            message("At least one update located is mandatory!\nSome updates can be very important for functionality and your security.\n"
                    + "Please update LOOP before running!");
            return false;
        } else {
            logger.info("Application is up to date!");
        }
        return true;
    }

    private static void message(String out) {
        logger.error(out);
        if (!headless)
            JOptionPane.showMessageDialog(null, out, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
