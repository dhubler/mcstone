package org.conf2.mcstone;

import java.util.*;

/**
 *
 */
public class Team {
    private TeamColor color;
    private Set<String> members = new HashSet<String>();
    private Map<String,ActivePlayer> activeMembers = new HashMap<String,ActivePlayer>();

    public Team(TeamColor color) {
        this.color = color;
    }

    public TeamColor getColor() {
        return color;
    }

    public Set<String> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public void addMember(String member) {
        members.add(member);
    }

    public void removeMember(String member) {
        members.remove(member);
    }

    public void removePlayer(String name) {
        activeMembers.remove(name);
    }

    public void addPlayer(ActivePlayer activePlayer) {
        activeMembers.put(activePlayer.getName(), activePlayer);
    }

    public Map<String, ActivePlayer> getActivePlayers() {
        return Collections.unmodifiableMap(activeMembers);
    }
}
