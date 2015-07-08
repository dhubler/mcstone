package org.conf2.minecraft.event;

import org.conf2.minecraft.CraftEvent;

import java.util.regex.MatchResult;

/**
 *
 */
public class RemovedUserEvent implements CraftEvent {
    public String name;

    RemovedUserEvent(String name) {
        this.name = name;
    }

    public RemovedUserEvent(MatchResult m) {
        this(m.group(1));
    }
}
