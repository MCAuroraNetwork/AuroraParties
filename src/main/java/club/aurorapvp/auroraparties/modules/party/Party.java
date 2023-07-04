package club.aurorapvp.auroraparties.modules.party;

import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.enums.PartyStatus;
import club.aurorapvp.auroraparties.util.PlayerHeadUtil;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Party {
  private final static Set<Party> PARTIES = new HashSet<>();
  private final String name;
  private final Player partyLeader;
  private final Set<Player> partyMembers = new HashSet<>();
  private final PartyGUI partyGUI;
  private PartyStatus status;
  private final ItemStack displayItem;

  public Party(Player partyLeader) {
    this.name = partyLeader.getName() + "'s Party";
    this.partyLeader = partyLeader;
    this.partyGUI = new PartyGUI(this);

    ItemStack displayItem = PlayerHeadUtil.getPlayerSkull(partyLeader);
    ItemMeta meta = displayItem.getItemMeta();
    meta.displayName(Lang.formatComponent("party-name", partyLeader.getName()));
    displayItem.setItemMeta(meta);

    this.displayItem = displayItem;

    this.setStatus(PartyStatus.INVITE_ONLY);

    partyLeader.sendMessage(Lang.getComponent("party-created"));

    PARTIES.add(this);
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

  public static Set<Party> getParties() {
    return PARTIES;
  }

  public static Party getParty(Player p) {
    for (Party party : PARTIES) {
      if (party.contains(p)) {
        return party;
      }
    }
    return null;
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

  public PartyStatus getStatus() {
    return status;
  }

  public void setStatus(PartyStatus status) {
    this.status = status;

    ItemMeta meta = displayItem.getItemMeta();

    switch (status) {
      case PRIVATE -> {
        PublicPartyGUI.removeParty(this);

        meta.lore(List.of(Component.text("Private"),
            Component.text(this.getMembers().size() + 1 + " Members")));

        displayItem.setItemMeta(meta);
      }
      case ANYONE_CAN_JOIN -> {
        meta.lore(List.of(Component.text("Anyone can Join"),
            Component.text(this.getMembers().size() + 1 + " Members")));

        displayItem.setItemMeta(meta);

        PublicPartyGUI.addParty(this);
      }
      case INVITE_ONLY -> {
        PublicPartyGUI.removeParty(this);

        meta.lore(List.of(Component.text("Invite Only"),
            Component.text(this.getMembers().size() + 1 + " Members")));

        displayItem.setItemMeta(meta);

        PublicPartyGUI.addParty(this);
      }
    }
  }

  public ItemStack getDisplayItem() {
    return displayItem;
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
    for (Player player : this.getMembers()) {
      player.sendMessage(Lang.formatComponent("player-joined-party", p.getName()));
    }
    partyLeader.sendMessage(Lang.formatComponent("player-joined-party", p.getName()));

    partyMembers.add(p);

    ItemMeta meta = displayItem.getItemMeta();

    switch (status) {
      case PRIVATE -> meta.lore(List.of(Component.text("Private"),
          Component.text(this.getMembers().size() + 1 + "Members")));
      case ANYONE_CAN_JOIN -> meta.lore(List.of(Component.text("Anyone Can Join"),
          Component.text(this.getMembers().size() + 1 + "Members")));
      case INVITE_ONLY -> meta.lore(List.of(Component.text("Invite Only"),
          Component.text(this.getMembers().size() + 1 + "Members")));
    }

    displayItem.setItemMeta(meta);

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

    ItemMeta meta = displayItem.getItemMeta();

    switch (status) {
      case PRIVATE -> meta.lore(List.of(Component.text("Private"),
          Component.text(this.getMembers().size() + 1 + "Members")));
      case ANYONE_CAN_JOIN -> meta.lore(List.of(Component.text("Anyone Can Join"),
          Component.text(this.getMembers().size() + 1 + "Members")));
      case INVITE_ONLY -> meta.lore(List.of(Component.text("Invite Only"),
          Component.text(this.getMembers().size() + 1 + "Members")));
    }

    displayItem.setItemMeta(meta);
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
}