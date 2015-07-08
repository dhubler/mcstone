package org.conf2.mcstone;

import org.conf2.minecraft.CommandRunner;
import org.conf2.minecraft.EventSource;
import org.conf2.restconf.Driver;

import java.io.*;
import java.util.logging.*;

/**
 *
 */
public class Main {
    private Logger logger = Logger.getLogger("org.conf2.mcstone");
    private EventSource events;
    private CommandRunner commands;

    void loadRestconf() {
        Driver d = new Driver();
        d.start();
        String docRootProp = "mcstone.docroot";
        String docRoot = System.getProperty(docRootProp);
        if (docRoot != null) {
            File docRootPath = new File(docRoot);
            if (docRootPath.exists() && docRootPath.isDirectory()) {
                d.setDocRoot(docRootPath);
            } else {
                logger.log(Level.SEVERE, "Cannot server ui,  invalid dir " + docRoot);
            }
        } else {
            logger.log(Level.SEVERE, "Cannot server ui, system property missing: " + docRootProp);
        }
        
        InputStream yang = getClass().getResourceAsStream("mcstone.yang");
        try {
            d.loadModule(yang);
            logger.log(Level.WARNING, "plugin got message that server is starting");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not load yang", e);
        }
    }

    void startGame(String[] args) throws IOException {
        StoneGame game = new StoneGame();
        game.init();
        Thread t = new Thread() {
            @Override
            public void run() {
                net.minecraft.server.MinecraftServer.main(args);
            }
        };
        t.start();
    }

    public static void main(String[] args) throws IOException {
        Main m = new Main();
        m.loadRestconf();
        m.startGame(args);
    }
}
