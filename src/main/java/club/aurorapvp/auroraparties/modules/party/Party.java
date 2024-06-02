package club.aurorapvp.auroraparties.modules.party;

import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.modules.guis.PartyLeaderGUI;
import club.aurorapvp.auroraparties.modules.guis.PartyManageGUI;
import club.aurorapvp.auroraparties.modules.guis.PartyMemberGUI;
import club.aurorapvp.auroraparties.modules.guis.PartySelectGUI;
import club.aurorapvp.auroraparties.modules.guis.PublicPartyGUI;
import club.aurorapvp.auroraparties.modules.guis.TeamDuelGUI;
import club.aurorapvp.auroraparties.util.InventoryUtil;
import club.aurorapvp.auroraparties.util.PlayerHeadUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Party {

  private static final Set<Party> PARTIES = new HashSet<>();
  private final int maxSize;
  private final HashMap<UUID, ItemStack> memberSkulls = new HashMap<>();
  private final Player partyLeader;
  private final Set<Player> partyMembers = new HashSet<>();
  private final PartyLeaderGUI partyLeaderGUI;
  private final PartyManageGUI partyManageGUI;
  private final PartyMemberGUI partyMemberGUI;
  private final PublicPartyGUI publicPartyGUI;
  private final TeamDuelGUI teamDuelGUI;
  private int index = 10;
  private Status status;
  private final ItemStack displayItem;

  public Party(Player partyLeader) {
    this.partyLeader = partyLeader;

    if (AuroraParties.getInstance().isLuckPermsInstalled()) {
      LuckPerms luckPerms = Objects.requireNonNull(
          Bukkit.getServicesManager().getRegistration(LuckPerms.class)).getProvider();

      User user = luckPerms.getPlayerAdapter(Player.class).getUser(partyLeader);

      user.get
    } else {
      maxSize = Integer.MAX_VALUE;
    }

    ItemStack leaderSkull = PlayerHeadUtil.getPlayerSkull(partyLeader);
    memberSkulls.put(partyLeader.getUniqueId(), leaderSkull);

    this.teamDuelGUI = new TeamDuelGUI(this, leaderSkull.clone());
    this.partyManageGUI = new PartyManageGUI(this, leaderSkull.clone());
    this.partyLeaderGUI = new PartyLeaderGUI(this, leaderSkull.clone());
    this.partyMemberGUI = new PartyMemberGUI(this, leaderSkull.clone());
    this.publicPartyGUI = new PublicPartyGUI(this, leaderSkull.clone());

    ItemStack displayItem = PlayerHeadUtil.getPlayerSkull(partyLeader);
    ItemMeta meta = displayItem.getItemMeta();
    meta.displayName(AuroraParties.getInstance().getLang().formatComponent("party-name", partyLeader.getName()));
    displayItem.setItemMeta(meta);

    this.displayItem = displayItem;

    this.setStatus(Status.INVITE_ONLY);

    partyLeader.sendMessage(AuroraParties.getInstance().getLang().getComponent("party-created"));

    PARTIES.add(this);
  }

  public static boolean inParty(Player player) {
    for (Party party : PARTIES) {
      if (party.getLeader() == player) {
        return true;
      }

      for (Player member : party.getMembers()) {
        if (member == player) {
          return true;
        }
      }
    }
    return false;
  }

  public static Set<Party> getParties() {
    return PARTIES;
  }

  public static Party getParty(Player player) {
    for (Party party : PARTIES) {
      if (party.contains(player)) {
        return party;
      }
    }
    return null;
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

  public TeamDuelGUI getTeamDuelGUI() {
    return teamDuelGUI;
  }

  public Player getLeader() {
    return partyLeader;
  }

  public Set<Player> getMembers() {
    return partyMembers;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;

    ItemMeta meta = displayItem.getItemMeta();

    switch (status) {
      case PRIVATE -> {
        meta.lore(
            List.of(
                Component.text("Private"),
                Component.text(this.getMembers().size() + 1 + " Members")));

        displayItem.setItemMeta(meta);

        PartySelectGUI.refresh();
      }
      case ANYONE_CAN_JOIN -> {
        meta.lore(
            List.of(
                Component.text("Anyone can Join"),
                Component.text(this.getMembers().size() + 1 + " Members")));

        displayItem.setItemMeta(meta);

        PartySelectGUI.refresh();
      }
      case INVITE_ONLY -> {
        meta.lore(
            List.of(
                Component.text("Invite Only"),
                Component.text(this.getMembers().size() + 1 + " Members")));

        displayItem.setItemMeta(meta);

        PartySelectGUI.refresh();
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

  public boolean isLeader(Player player) {
    return partyLeader == player;
  }

  public boolean contains(Player player) {
    if (this.getLeader() == player) {
      return true;
    }

    for (Player member : this.getMembers()) {
      if (member == player) {
        return true;
      }
    }
    return false;
  }

  public void addMember(Player player) {
    if (partyMembers.contains(player)) {
      return;
    }

    player.sendMessage(AuroraParties.getInstance().getLang().formatComponent("player-joined-party", player.getName()));

    player.openInventory(this.getMemberGUI().getInventory());

    for (Player partyMember : this.getMembers()) {
      partyMember.sendMessage(AuroraParties.getInstance().getLang().formatComponent("player-joined-party", player.getName()));
    }

    partyLeader.sendMessage(AuroraParties.getInstance().getLang().formatComponent("player-joined-party", player.getName()));

    partyMembers.add(player);

    ItemMeta meta = displayItem.getItemMeta();

    switch (status) {
      case PRIVATE -> meta.lore(
          List.of(
              Component.text("Private"), Component.text(this.getMembers().size() + 1 + " Members")));
      case ANYONE_CAN_JOIN -> meta.lore(
          List.of(
              Component.text("Anyone Can Join"),
              Component.text(this.getMembers().size() + 1 + " Members")));
      case INVITE_ONLY -> meta.lore(
          List.of(
              Component.text("Invite Only"),
              Component.text(this.getMembers().size() + 1 + " Members")));
    }

    displayItem.setItemMeta(meta);

    incrementIndex();
    ItemStack memberSkull = PlayerHeadUtil.getPlayerSkull(player);

    memberSkulls.put(player.getUniqueId(), memberSkull);

    partyLeaderGUI.addMember(index, memberSkull);
    partyManageGUI.addMember(index, memberSkull);
    partyMemberGUI.addMember(index, memberSkull);
    publicPartyGUI.addMember(index, memberSkull);
    teamDuelGUI.addMember(memberSkull, player);
  }

  public void leave(Player player) {
    this.removeMember(player);

    player.sendMessage(AuroraParties.getInstance().getLang().getComponent("left-party"));

    partyLeader.sendMessage(AuroraParties.getInstance().getLang().formatComponent("player-left-party", player.getName()));

    for (Player partyMember : this.getMembers()) {
      partyMember.sendMessage(AuroraParties.getInstance().getLang().formatComponent("player-left-party", player.getName()));
    }
  }

  public void kick(Player player) {
    this.removeMember(player);

    player.sendMessage(AuroraParties.getInstance().getLang().getComponent("removed-from-party"));

    partyLeader.sendMessage(AuroraParties.getInstance().getLang().formatComponent("player-removed-from-party", player.getName()));

    for (Player partyMember : this.getMembers()) {
      partyMember.sendMessage(AuroraParties.getInstance().getLang().formatComponent("player-removed-from-party", player.getName()));
    }

    ItemMeta meta = displayItem.getItemMeta();

    switch (status) {
      case PRIVATE -> meta.lore(
          List.of(
              Component.text("Private"), Component.text(this.getMembers().size() + 1 + " Members")));
      case ANYONE_CAN_JOIN -> meta.lore(
          List.of(
              Component.text("Anyone Can Join"),
              Component.text(this.getMembers().size() + 1 + " Members")));
      case INVITE_ONLY -> meta.lore(
          List.of(
              Component.text("Invite Only"),
              Component.text(this.getMembers().size() + 1 + " Members")));
    }

    displayItem.setItemMeta(meta);
  }

  public void removeMember(Player player) {
    partyMembers.remove(player);

    decrementIndex();

    ItemStack memberSkull = memberSkulls.get(player.getUniqueId());

    int slot = InventoryUtil.findSkullSlot(memberSkull, publicPartyGUI.getInventory());
    ItemStack lastSkull = InventoryUtil.getLastSkull(publicPartyGUI.getInventory());

    partyLeaderGUI.removeMember(slot, memberSkull, lastSkull);
    partyManageGUI.removeMember(slot, memberSkull, lastSkull);
    partyMemberGUI.removeMember(slot, memberSkull, lastSkull);
    publicPartyGUI.removeMember(slot, memberSkull, lastSkull);
    teamDuelGUI.removeMember(memberSkull);
  }

  public void disband() {
    partyLeader.sendMessage(
        AuroraParties.getInstance().getLang().formatComponent("party-disbanded", partyLeader.getName()));

    for (Player player : partyMembers) {
      player.sendMessage(
          AuroraParties.getInstance().getLang().formatComponent("party-disbanded", partyLeader.getName()));
    }

    PartySelectGUI.reload();

    PARTIES.remove(this);
  }

  public enum Status {
    ANYONE_CAN_JOIN,
    INVITE_ONLY,
    PRIVATE
  }
}
