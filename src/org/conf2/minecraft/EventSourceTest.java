package org.conf2.minecraft;

import org.conf2.minecraft.event.NewUserEvent;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class EventSourceTest {
    @Test
    public void testRead() {
        String in = "[ header ]: nothing\n" +
                "[ header ]: glorb joined the game\n";
        EventSource rdr = new EventSource(new ByteArrayInputStream(in.getBytes()), null);
        final boolean fired[] = new boolean[] { false };
        final String name[] = new String[1];
        rdr.addListener(NewUserEvent.class, new CraftListener() {
            @Override
            public void onCraftEvent(Object e) {
                fired[0] = true;
                name[0] = ((NewUserEvent) e).name;
            }
        });
        rdr.run();
        assertTrue(fired[0]);
        assertEquals("glorb", name[0]);
    }
}
