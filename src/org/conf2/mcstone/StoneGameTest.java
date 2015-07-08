package org.conf2.mcstone;

import org.junit.Test;

import java.io.InputStream;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class StoneGameTest {

    @Test
    public void testYang() {
        InputStream yang = getClass().getResourceAsStream("mcstone.yang");
        assertNotNull(yang);
    }

    @Test
    public void testRegisterPlayer() {
        StoneGame game = new StoneGame();
        game.registerPlayer("robin");
        game.registerPlayer("canary");
        ActivePlayer[] players = game.getActivePlayers().toArray(new ActivePlayer[0]);
        assertEquals(2, players.length);
        assertEquals(players[0].getName(), "robin");
        assertEquals(players[1].getName(), "canary");
    }
}
