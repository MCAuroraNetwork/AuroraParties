package club.aurorapvp.auroraparties.modules.party;

import club.aurorapvp.auroraduels.modules.duels.Duel;
import club.aurorapvp.auroraduels.modules.duels.FFADuel;
import club.aurorapvp.auroraduels.modules.duels.TeamDuel;
import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.modules.duels.PendingDuel;
import club.aurorapvp.auroraparties.modules.party.guis.PartyGUI;
import club.aurorapvp.auroraparties.modules.party.guis.PartyLeaderGUI;
import club.aurorapvp.auroraparties.modules.party.guis.PartyManageGUI;
import club.aurorapvp.auroraparties.modules.party.guis.PartyMemberGUI;
import club.aurorapvp.auroraparties.modules.party.guis.PublicPartyGUI;
import club.aurorapvp.auroraparties.modules.party.guis.TeamDuelGUI;
import club.aurorapvp.auroraparties.util.InventoryUtil;
import club.aurorapvp.auroraparties.util.PlayerHeadUtil;
import club.aurorapvp.aurorawarps.modules.WorldSpawn;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Party {
  private static final Set<Party> PARTIES = new HashSet<>();
  private final HashMap<Player, ItemStack> memberSkulls = new HashMap<>();
  private final String name;
  private final Player partyLeader;
  private final Set<Player> partyMembers = new HashSet<>();
  private final PartyLeaderGUI partyLeaderGUI;
  private final PartyManageGUI partyManageGUI;
  private final PartyMemberGUI partyMemberGUI;
  private final PublicPartyGUI publicPartyGUI;
  private final TeamDuelGUI teamDuelGUI;
  private int index = 10;
  private PartyStatus status;
  private final ItemStack displayItem;

  public Party(Player partyLeader) {
    this.name = partyLeader.getName() + "'s Party";
    this.partyLeader = partyLeader;

    ItemStack leaderSkull = PlayerHeadUtil.getPlayerSkull(partyLeader);
    memberSkulls.put(partyLeader, leaderSkull);

    this.teamDuelGUI = new TeamDuelGUI(this, leaderSkull);
    this.partyManageGUI = new PartyManageGUI(this, leaderSkull);
    this.partyLeaderGUI = new PartyLeaderGUI(this, leaderSkull);
    this.partyMemberGUI = new PartyMemberGUI(this, leaderSkull);
    this.publicPartyGUI = new PublicPartyGUI(this, leaderSkull);

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

  public ItemStack getSkull(Player p) {
    return memberSkulls.get(p);
  }

  public PartyLeaderGUI getLeaderGUI() {
    return partyLeaderGUI;
  }

  public PartyManageGUI getManageGUI() {
    return partyManageGUI;
  }

  public PartyMemberGUI getMemberGUI() {
    return partyMemberGUI;
  }

  public PublicPartyGUI getPublicGUI() {
    return publicPartyGUI;
  }

  public TeamDuelGUI getTeamDuelGUI() {
    return teamDuelGUI;
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

  public PartyStatus getStatus() {
    return status;
  }

  public void setStatus(PartyStatus status) {
    this.status = status;

    ItemMeta meta = displayItem.getItemMeta();

    switch (status) {
      case PRIVATE -> {
        PartyIndexGUI.removeParty(this);

        meta.lore(
            List.of(
                Component.text("Private"),
                Component.text(this.getMembers().size() + 1 + " Members")));

        displayItem.setItemMeta(meta);
      }
      case ANYONE_CAN_JOIN -> {
        meta.lore(
            List.of(
                Component.text("Anyone can Join"),
                Component.text(this.getMembers().size() + 1 + " Members")));

        displayItem.setItemMeta(meta);

        PartyIndexGUI.addParty(this);
      }
      case INVITE_ONLY -> {
        PartyIndexGUI.removeParty(this);

        meta.lore(
            List.of(
                Component.text("Invite Only"),
                Component.text(this.getMembers().size() + 1 + " Members")));

        displayItem.setItemMeta(meta);

        PartyIndexGUI.addParty(this);
      }
    }
  }

  private void decrementIndex() {
    if (index <= 10) {
      throw new IndexOutOfBoundsException("Cannot decrement below 10");
    }

    index--;

    if (index % 9 == 0) {
      index -= 2;
    }
  }

  private void incrementIndex() {
    if (index >= 43) {
      throw new IndexOutOfBoundsException("Cannot increment over 43");
    }

    index++;

    if (index % 9 == 8) {
      index += 2;
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
    p.sendMessage(Lang.formatComponent("player-joined-party", p.getName()));

    for (Player player : this.getMembers()) {
      player.sendMessage(Lang.formatComponent("player-joined-party", p.getName()));
    }

    partyLeader.sendMessage(Lang.formatComponent("player-joined-party", p.getName()));

    partyMembers.add(p);

    ItemMeta meta = displayItem.getItemMeta();

    switch (status) {
      case PRIVATE -> meta.lore(
          List.of(
              Component.text("Private"), Component.text(this.getMembers().size() + 1 + "Members")));
      case ANYONE_CAN_JOIN -> meta.lore(
          List.of(
              Component.text("Anyone Can Join"),
              Component.text(this.getMembers().size() + 1 + "Members")));
      case INVITE_ONLY -> meta.lore(
          List.of(
              Component.text("Invite Only"),
              Component.text(this.getMembers().size() + 1 + "Members")));
    }

    displayItem.setItemMeta(meta);

    incrementIndex();
    ItemStack memberSkull = PlayerHeadUtil.getPlayerSkull(p);

    memberSkulls.put(p, memberSkull);

    partyLeaderGUI.addMember(index, memberSkull);
    partyManageGUI.addMember(index, memberSkull);
    partyMemberGUI.addMember(index, memberSkull);
    publicPartyGUI.addMember(index, memberSkull);
    teamDuelGUI.addMember(memberSkull);
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
      case PRIVATE -> meta.lore(
          List.of(
              Component.text("Private"), Component.text(this.getMembers().size() + 1 + "Members")));
      case ANYONE_CAN_JOIN -> meta.lore(
          List.of(
              Component.text("Anyone Can Join"),
              Component.text(this.getMembers().size() + 1 + "Members")));
      case INVITE_ONLY -> meta.lore(
          List.of(
              Component.text("Invite Only"),
              Component.text(this.getMembers().size() + 1 + "Members")));
    }

    displayItem.setItemMeta(meta);
  }

  public void removeMember(Player p) {
    partyMembers.remove(p);

    decrementIndex();

    ItemStack memberSkull = memberSkulls.get(p);

    int slot = InventoryUtil.findSkullSlot(memberSkull, publicPartyGUI.getInventory());
    ItemStack lastSkull = InventoryUtil.getLastSkull(publicPartyGUI.getInventory());


    partyLeaderGUI.removeMember(slot, lastSkull);
    partyManageGUI.removeMember(slot, lastSkull);
    partyMemberGUI.removeMember(slot, lastSkull);
    publicPartyGUI.removeMember(slot, lastSkull);
    teamDuelGUI.removeMember(memberSkull);
  }

  public static void onInventoryClick(InventoryClickEvent event) {
    Player p = (Player) event.getWhoClicked();

    if (!Party.inParty(p)) {
      return;
    }

    if (!(event.getInventory().getHolder() instanceof PartyGUI partyGUI)) {
      return;
    }

    Party party = Party.getParty(p);
    assert party != null;
    event.setCancelled(true);

    int slot = event.getSlot();

    if (!party.isLeader(p)) {
      if (slot == 53) {
        party.leave(p);
        p.closeInventory();
      }
      return;
    }

    if (slot == 53) {
      party.disband();
      p.closeInventory();
    } else if (slot == 45) {
      if (partyGUI instanceof PartyManageGUI) {
        p.openInventory(party.getLeaderGUI().getInventory());
      } else {
        p.openInventory(party.getManageGUI().getInventory());
      }
    } else if (partyGUI instanceof PartyManageGUI partyManageGUI) {
      partyManageGUI.handleManageClick(event);
    }

    if (party.getMembers().isEmpty()) {
      return;
    }

    Set<Player> players = new HashSet<>(party.getMembers());
    players.add(party.getLeader());

    Location loc = Duel.getDuelLobby();

    if (AuroraParties.isAuroraWarpsInstalled() && loc == null) {

      WorldSpawn worldSpawn = WorldSpawn.getWorldSpawn(party.getLeader().getWorld());

      if (worldSpawn != null) {
        loc = worldSpawn.getSpawn();
      }

      if (loc == null) {
        loc = WorldSpawn.getGlobalSpawn();
      }
    }

    if (loc == null) {
      loc = Bukkit.getWorlds().get(0).getSpawnLocation();
    }

    if (slot == 49) {
      p.openInventory(party.getTeamDuelGUI().getInventory());
    } else if (slot == 47) {
      int size = players.size();
      Player extra = null;

      if (size % 2 == 1) {
        Iterator<Player> iterator = players.iterator();
        for (int i = 0; i < size - 1; i++) {
          iterator.next();
        }
        extra = iterator.next();
        iterator.remove();
        size--;
      }

      Iterable<List<Player>> partitions = Iterables.partition(players, (size / 2));

      Player[] temp = new HashSet<>(partitions.iterator().next()).toArray(new Player[0]);

      if (extra != null) {
        temp = Arrays.copyOf(temp, temp.length + 1);
        temp[temp.length - 1] = extra;
      }

      Player[] team1 = temp;
      Player[] team2 = new HashSet<>(Iterables.getLast(partitions)).toArray(new Player[0]);

      new PendingDuel(p, new TeamDuel(team1, team2, loc, false));
    } else if (slot == 51) {
      new PendingDuel(p, new FFADuel(players, loc, false));
    }
  }

  public void disband() {
    partyLeader.sendMessage(
        Lang.formatComponent("party-disbanded", this.getName(), partyLeader.getName()));

    for (Player p : partyMembers) {
      p.sendMessage(Lang.formatComponent("party-disbanded", this.getName(), partyLeader.getName()));
    }

    PARTIES.remove(this);
  }

  public enum PartyStatus {
    ANYONE_CAN_JOIN,
    INVITE_ONLY,
    PRIVATE
  }
}
