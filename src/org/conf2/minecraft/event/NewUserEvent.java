package org.conf2.minecraft.event;

import org.conf2.minecraft.CraftEvent;

import java.util.regex.MatchResult;

/**
 *
 */
public class NewUserEvent implements CraftEvent {
    public String name;

    NewUserEvent(String name) {
        this.name = name;
    }

    public NewUserEvent(MatchResult r) {
        this(r.group(0));
    }
}
