package org.conf2.mcstone;

import org.conf2.minecraft.*;
import org.conf2.minecraft.event.NewUserEvent;
import org.conf2.minecraft.event.RemovedUserEvent;

import java.io.*;
import java.util.*;

/**
 *
 */
public class StoneGame {
    private Map<TeamColor,Team> teams = new HashMap<TeamColor, Team>();
    private Map<String,ActivePlayer> players = new HashMap<String,ActivePlayer>();
    private EventSource events;
    private CommandRunner commands;
    private int timeLimit;
    private int baseRadius;

    public StoneGame() {
        teams.put(TeamColor.BLUE, new Team(TeamColor.BLUE));
        teams.put(TeamColor.RED, new Team(TeamColor.RED));
    }

    public void init() throws IOException {
        events = EventSource.replaceStdOut();
        commands = CommandRunner.replaceStdIn();
        addListenerers();
        new Thread(commands).start();
        new Thread(events).start();
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void startGame() {
        // make world editable
        // max users health
        // allow users to take damage
        // reset world blocks
        // transport each player to start point
        // equip them with appropriate armor, weapons and tools
    }

    public void endGame() {
        // announce winners
        // make players invincible
    }

    public ActivePlayer registerPlayer(String name) {
        Team team = pickTeam(name);
        ActivePlayer p = new ActivePlayer(name, team);
        players.put(name, p);
        p.getTeam().addPlayer(p);
        return p;
    }

    void addListenerers() {
        this.events.addListener(NewUserEvent.class, new CraftListener<NewUserEvent>() {
            @Override
            public void onCraftEvent(NewUserEvent e) {
                registerPlayer(e.name);
            }
        });
        this.events.addListener(RemovedUserEvent.class, new CraftListener<RemovedUserEvent>() {
            @Override
            public void onCraftEvent(RemovedUserEvent e) {
                unregisterPlayer(e.name);
            }
        });
    }

    Team pickTeam(String name) {
        int lowestCount = Integer.MAX_VALUE;
        Team teamColorWithLowestCount = null;
        for (Team t : teams.values()) {
            if (t.getMembers().contains(name)) {
                return t;
            }
            int count = t.getActivePlayers().size();
            if (count < lowestCount) {
                lowestCount = count;
                teamColorWithLowestCount = t;
            }
        }
        return teamColorWithLowestCount;
    }

    public void unregisterPlayer(String name) {
        ActivePlayer player = players.get(name);
        if (player != null) {
            player.getTeam().removePlayer(name);
            players.remove(name);
        }
    }

    public void addTeam(TeamColor color, Team t) {
        teams.put(color, t);
    }

    public Collection<Team> getTeams() {
        return teams.values();
    }

    public Collection<ActivePlayer> getActivePlayers() {
        return players.values();
    }

    public void setBaseRadius(int baseRadius) {
        this.baseRadius = baseRadius;
    }

    public int getBaseRadius() {
        return baseRadius;
    }

    public int getTimeLimit() {
        return timeLimit;
    }
}
