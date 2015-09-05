package org.conf2.mcstone;

import org.conf2.mcstone.ActivePlayer;
import org.conf2.mcstone.Team;
import org.conf2.mcstone.TeamColor;
import org.conf2.yang.browse.*;
import org.conf2.mcstone.StoneGame;
import org.conf2.yang.Module;
import org.conf2.yang.driver.DriverError;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 */
public class StoneBrowser implements Browser {
    private Module module;
    private StoneGame game;

    public StoneBrowser(Module module, StoneGame game) {
        this.module = module;
        this.game = game;
    }

    @Override
    public Module getModule() {
        return module;
    }

    @Override
    public Selection getRootSelector() {
        Selection s = new Selection();
        s.meta = module;
        s.Enter = () -> {
            String ident = s.position.getIdent();
            if ("active-players".equals(ident)) {
                Collection<ActivePlayer> players = game.getActivePlayers();
                s.found = players.size() > 0;
                return enterPlayers(players);
            } else if ("game".equals(ident)) {
                s.found = (game != null);
                if (s.found) {
                    return enterGame();
                }
            }
            return null;
        };
        s.Edit = (EditOperation op, BrowseValue v) -> {
            switch (op) {
                case CREATE_CHILD:
                    game = new StoneGame();
                    break;
                case UPDATE_VALUE:
                    BrowseUtil.setterMethod(s.position, game, v);
                    break;
            }
        };
        return s;
    }

    public Selection enterPlayers(Collection<ActivePlayer> players) {
        final ActivePlayer[] player = new ActivePlayer[1];
        Selection s = new Selection();
        final Iterator[] i = new Iterator[1];
        s.Iterate = (String[] keys, boolean isFirst) -> {
            if (keys.length > 0) {
                String name = keys[0];
                for (ActivePlayer candidate  : players) {
                    if (name.equals(candidate.getName())) {
                        player[0] = candidate;
                        return true;
                    }
                }
            } else {
                if (isFirst) {
                    i[0] = players.iterator();
                }
                if (i[0].hasNext()) {
                    player[0] = (ActivePlayer)i[0].next();
                    return true;
                }
            }
            return false;
        };
        s.Read = (BrowseValue v) -> BrowseUtil.getterMethod(s.position, player[0], v);
        return s;
    }

    public Selection enterGame() {
        Selection s = new Selection();
        s.Enter = () -> {
            String ident = s.position.getIdent();
            if ("teams".equals(ident)) {
                s.found = (game.getTeams().size() > 0);
                return  enterTeams(game, game.getTeams());
            }
            return null;
        };
        s.Edit = (EditOperation op, BrowseValue v) -> {
            switch (op) {
                case CREATE_CHILD:
                    game = new StoneGame();
                    break;
                case UPDATE_VALUE:
                    BrowseUtil.setterMethod(s.position, game, v);
                    break;
            }
        };
        s.Read = (BrowseValue v) -> BrowseUtil.getterMethod(s.position, game, v);
        return s;
    }

    public Selection enterTeams(final StoneGame game, final Collection<Team> teams) {
        final Team[] team = new Team[1];
        final TeamColor[] color = new TeamColor[1];
        final Iterator[] i = new Iterator[1];
        Selection s = new Selection();
        s.Iterate = (String[] keys, boolean isFirst) -> {
            if (keys.length > 0) {
                color[0] = TeamColor.decodeColor(keys[0]);
                if (color[0] == null) {
                    throw new DriverError("Invalid color " + keys[0]);
                }
                for (Team t : teams) {
                    if (t.getColor() == color[0]) {
                        team[0] = t;
                        return true;
                    }
                }
            } else {
                if (isFirst) {
                    i[0] = teams.iterator();
                }
                if (i[0].hasNext()) {
                    team[0] = (Team) i[0].next();
                    return true;
                }
            }
            return false;
        };
        s.Enter = () -> {
            String ident = s.position.getIdent();
            if ("spawn-point".equals(ident)) {
                s.found = true;
                return enterPosition(team[0].getSpawnPoint());
            } else if ("base-position".equals(ident)) {
                s.found = true;
                return enterPosition(team[0].getBasePosition());
            }
            return null;
        };
        s.Edit = (EditOperation op, BrowseValue v) -> {
            switch (op) {
                case CREATE_LIST_ITEM:
                    team[0] = new Team(color[0]);
                    break;
                case POST_CREATE_LIST_ITEM:
                    game.addTeam(color[0], team[0]);
                    break;
                case UPDATE_VALUE:
                    String ident = s.position.getIdent();
                    if (!"color".equals(ident)) {
                        BrowseUtil.setterMethod(s.position, team[0], v);
                    }
                    break;
            }
        };
        s.Read = (BrowseValue v) -> {
            String ident = s.position.getIdent();
            if ("color".equals(ident)) {
                String colorEnum = team[0].getColor().name().toLowerCase();
                v.setEnum(s.position, colorEnum);
            } else {
                BrowseUtil.getterMethod(s.position, team[0], v);
            }
        };
        return s;
    }

    Selection enterPosition(Position p) {
        Selection s = new Selection();
        s.Read = (BrowseValue v) -> BrowseUtil.readField(s.position, p, v);
        return s;
    }
}
