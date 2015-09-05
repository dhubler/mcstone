package org.conf2.mcstone;

import org.conf2.minecraft.CommandRunner;
import org.conf2.minecraft.EventSource;
import org.conf2.yang.Module;
import org.conf2.yang.driver.Driver;
import org.conf2.restconf.Service;
import org.conf2.yang.SimpleStreamSource;

import java.io.*;
import java.util.logging.*;

/**
 *
 */
public class Main {
    private Logger logger = Logger.getLogger("org.conf2.mcstone");
    private EventSource events;
    private CommandRunner commands;
    private StoneGame game;

    public Main() {
        game = new StoneGame();
    }

    void loadRestconf() {
        Driver d = new Driver();
        Service s = new Service(d);
        String docRootProp = "mcstone.docroot";
        String docRoot = System.getProperty(docRootProp);
        if (docRoot != null) {
            File docRootPath = new File(docRoot);
            if (docRootPath.exists() && docRootPath.isDirectory()) {
                s.setDocRoot(new SimpleStreamSource(docRootPath));
            } else {
                logger.log(Level.SEVERE, "Cannot server ui,  invalid dir " + docRoot);
            }
        } else {
            logger.log(Level.SEVERE, "Cannot server ui, system property missing: " + docRootProp);
        }

        Module m = d.loadModule(new SimpleStreamSource(Main.class), "mcstone-lite.yang");
        StoneBrowser browser = new StoneBrowser(m, game);
        s.registerBrowser(browser);
        s.start();
    }

    void startGame(String[] args) throws IOException {
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
