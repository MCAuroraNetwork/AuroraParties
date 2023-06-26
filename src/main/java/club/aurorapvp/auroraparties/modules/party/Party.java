package club.aurorapvp.auroraparties.modules.party;

import club.aurorapvp.auroraparties.config.Lang;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;

public class Party {
  private final static Set<Party> PARTIES = new HashSet<>();
  private final String name;
  private final Player partyLeader;
  private final Set<Player> partyMembers = new HashSet<>();
  private final PartyGUI partyGUI;

  public Party(Player partyLeader) {
    this.name = partyLeader.getName() + "'s Party";
    this.partyLeader = partyLeader;
    this.partyGUI = new PartyGUI(this);


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
    return partyMembers;
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
    partyMembers.add(p);
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

  public void disband() {
    partyLeader.sendMessage(
        Lang.formatComponent("party-disbanded", this.getName(), partyLeader.getName()));

    for (Player p : partyMembers) {
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