package org.conf2.mcstone;

/**
 *
 */
public class ActivePlayer {
    private String name;
    private Team team;

    public ActivePlayer(String name, Team team) {
        this.name = name;
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }
}
