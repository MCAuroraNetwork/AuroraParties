package club.aurorapvp.auroraparties.modules;

import club.aurorapvp.auroraparties.configs.Lang;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.entity.Player;

public class Party {
  private final static Set<Party> PARTIES = new HashSet<>();
  private final String name;
  private final Player partyLeader;
  private final Map<Player, Map<String, Boolean>> partyMembers = new HashMap<>();
  private final Map<String, Boolean> defaultPermissions = new HashMap<>();
  private final PartyGUI partyGUI;

  public Party(Player partyLeader) {
    this.name = partyLeader.getName() + "'s Party";
    this.partyLeader = partyLeader;
    this.partyGUI = new PartyGUI(this);

    defaultPermissions.put("can-invite", false);
    defaultPermissions.put("can-disband", false);

    partyLeader.sendMessage(Lang.getComponent("party-created"));

    PARTIES.add(this);
  }

  public String getName() {
    return name;
  }

  public Player getLeader() {
    return partyLeader;
  }

  public Set<Player> getMembers() {
    return partyMembers.keySet();
  }

  public PartyGUI getGUI() {
    return partyGUI;
  }

  public boolean isLeader(Player p) {
    return partyLeader == p;
  }

  public boolean isMember(Player p) {
    for (Player member : this.getMembers()) {
      if (member == p) {
        return true;
      }
    }
    return false;
  }

  public boolean hasPermission(Player p, String permission) {
    if (isLeader(p)) {
      return true;
    }

    return partyMembers.get(p).get(permission);
  }

  public boolean contains(Player p) {
    if (this.getLeader() == p) {
      return true;
    }

    for (Player member : this.getMembers()) {
      if (member == p) {
        return true;
      }
    }
    return false;
  }

  public void addMember(Player p) {
    partyMembers.put(p, defaultPermissions);
    this.getGUI().addMember(p);
  }

  public void leave(Player p) {
    this.removeMember(p);

    p.sendMessage(Lang.getComponent("left-party"));

    partyLeader.sendMessage(Lang.formatComponent("player-left-party", p.getName()));

    for (Player player : this.getMembers()) {
      player.sendMessage(Lang.formatComponent("player-left-party", p.getName()));
    }
  }

  public void kick(Player p) {
    this.removeMember(p);

    p.sendMessage(Lang.getComponent("removed-from-party"));

    partyLeader.sendMessage(Lang.formatComponent("player-removed-from-party", p.getName()));

    for (Player player : this.getMembers()) {
      player.sendMessage(Lang.formatComponent("player-removed-from-party", p.getName()));
    }
  }

  public void removeMember(Player p) {
    partyMembers.remove(p);
    this.getGUI().removeMember(p);
  }

  public void setPermission(Player p, String permission, boolean value) {
    partyMembers.get(p).put(permission, value);
  }

  public void disband() {
    partyLeader.sendMessage(
        Lang.formatComponent("party-disbanded", this.getName(), partyLeader.getName()));

    for (Player p : partyMembers.keySet()) {
      p.sendMessage(Lang.formatComponent("party-disbanded", this.getName(), partyLeader.getName()));
    }

    PARTIES.remove(this);
  }

  public static boolean inParty(Player p) {
    for (Party party : PARTIES) {
      if (party.getLeader() == p) {
        return true;
      }

      for (Player member : party.getMembers()) {
        if (member == p) {
          return true;
        }
      }
    }
    return false;
  }


  public static Party getParty(Player p) {
    for (Party party : PARTIES) {
      if (party.contains(p)) {
        return party;
      }
    }
    return null;
  }
}