package org.conf2.mcstone;

import org.conf2.yang.browse.BrowsePath;
import org.conf2.yang.browse.Selection;
import org.conf2.yang.browse.Walk;
import org.conf2.yang.Module;
import org.conf2.yang.SimpleStreamSource;
import org.conf2.yang.browse.ExhaustiveWalk;
import org.conf2.yang.driver.Driver;
import org.junit.Test;

import java.util.Map;

/**
 *
 */
public class StoneBrowserTest {

    @Test
    public void testRead() {
        Driver d = new Driver();
        Module m = d.loadModule(new SimpleStreamSource(Main.class), "mcstone-lite.yang");
        StoneGame game = new StoneGame();
        StoneBrowser browser = new StoneBrowser(m, game);
        Walk.walk(browser.getRootSelector(), new ExhaustiveWalk());
    }
}
