package org.conf2.mcstone;

/**
 *
 */
public enum TeamColor {
    RED, BLUE, GREEN, YELLOW;

    public static TeamColor decodeColor(String color) {
        if ("red".equals(color)) {
            return RED;
        }
        if ("blue".equals(color)) {
            return BLUE;
        }
        if ("green".equals(color)) {
            return GREEN;
        }
        if ("yellow".equals(color)) {
            return YELLOW;
        }
        return null;
    }
}
