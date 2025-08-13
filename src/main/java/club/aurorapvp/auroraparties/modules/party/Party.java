package club.aurorapvp.auroraparties.modules.party;

import club.aurorapvp.aurorachat.modules.ChatGroup;
import club.aurorapvp.auroraduels.modules.DuelRequest;
import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.modules.guis.PartyLeaderGUI;
import club.aurorapvp.auroraparties.modules.guis.PartyManageGUI;
import club.aurorapvp.auroraparties.modules.guis.PartyMemberGUI;
import club.aurorapvp.auroraparties.modules.guis.PartySelectGUI;
import club.aurorapvp.auroraparties.modules.guis.PublicPartyGUI;
import club.aurorapvp.auroraparties.modules.duels.TeamDuelGUI;
import club.aurorapvp.auroraparties.util.InventoryUtil;
import club.aurorapvp.auroraparties.util.PlayerHeadUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Party implements ChatGroup.ChatTarget {

  private static final Set<Party> PARTIES = new HashSet<>();
  private static final Set<Player> PARTY_LEADERS = new HashSet<>();
  private final HashMap<UUID, ItemStack> memberSkulls = new HashMap<>();
  private final Player partyLeader;
  private final Set<Player> partyMembers = new HashSet<>();
  private final PartyLeaderGUI partyLeaderGUI;
  private final PartyManageGUI partyManageGUI;
  private final PartyMemberGUI partyMemberGUI;
  private final PublicPartyGUI publicPartyGUI;
  private final TeamDuelGUI teamDuelGUI;
  private int index = 10;
  private int maxSize;
  private final ItemStack displayItem;
  private Status status;
  private boolean inDuel;

  public Party(Player partyLeader, Status status) {
    this.partyLeader = partyLeader;
    this.status = status;

    PARTY_LEADERS.add(partyLeader);

    maxSize = 2;

    RegisteredServiceProvider<LuckPerms> provider =
            Bukkit.getServicesManager().getRegistration(LuckPerms.class);

    if (provider != null && AuroraParties.getInstance().isLuckPermsInstalled()) {
      LuckPerms api = provider.getProvider();
      User user = api.getPlayerAdapter(Player.class).getUser(partyLeader);

      QueryOptions queryOptions = api.getContextManager().getQueryOptions(user).orElse(null);

      if (queryOptions != null) {
        CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);
        String meta = metaData.getMetaValue("party-size");

        if (meta != null) {
          maxSize = Integer.parseInt(meta);
        }
      }
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
    meta.displayName(
            AuroraParties.getInstance().getLang().formatComponent("party-name", partyLeader.getName()));
    meta.lore(
            List.of(
                    getStatusDisplayName(),
                    Component.text(this.getSize() + "/" + this.getMaxSize() + " Members")));
    displayItem.setItemMeta(meta);

    this.displayItem = displayItem;

    partyLeader.sendMessage(
            AuroraParties.getInstance()
                    .getLang()
                    .getComponentCommand("party-created", "Click to open Party", "/party"));

    PARTIES.add(this);

    // Enable party chat by default for leader
    ChatGroup leaderGroup = ChatGroup.getChatGroup(partyLeader);
    leaderGroup.setChatTarget(this);
    partyLeader.sendMessage(AuroraParties.getInstance().getLang().getComponent("party-chat-enabled"));
  }

  public static boolean inParty(Player player) {
    for (Party party : PARTIES) {
      if (party.getLeader().equals(player)) {
        return true;
      }
      for (Player member : party.getMembers()) {
        if (member.equals(player)) {
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

  public int getSize() {
    return partyMembers.size() + 1;
  }

  public boolean isInDuel() {
    return inDuel;
  }

  public int getMaxSize() {
    return maxSize;
  }

  public void setInDuel(boolean inDuel) {
    this.inDuel = inDuel;
  }

  public void setStatus(Status status) {
    this.status = status;
    updateDisplayItem();
    PartySelectGUI.reload();
  }

  private Component getStatusDisplayName() {
    return switch (status) {
      case PRIVATE -> AuroraParties.getInstance().getLang().getComponent("private-party");
      case ANYONE_CAN_JOIN -> AuroraParties.getInstance().getLang().getComponent("public-party");
      case INVITE_ONLY -> AuroraParties.getInstance().getLang().getComponent("invite-only-party");
    };
  }

  private void updateDisplayItem() {
    ItemMeta meta = displayItem.getItemMeta();
    meta.lore(
            List.of(
                    getStatusDisplayName(),
                    Component.text(this.getSize() + "/" + this.getMaxSize() + " Members")));
    displayItem.setItemMeta(meta);
  }

  public ItemStack getDisplayItem() {
    return displayItem;
  }

  public boolean isLeader(Player player) {
    return partyLeader.equals(player);
  }

  public static boolean isPartyLeader(Player player) {
    return PARTY_LEADERS.contains(player);
  }

  public boolean contains(Player player) {
    if (this.getLeader().equals(player)) {
      return true;
    }
    for (Player member : this.getMembers()) {
      if (member.equals(player)) {
        return true;
      }
    }
    return false;
  }

  public void addMember(Player player) {
    if (partyMembers.contains(player)) {
      return;
    }
    if (DuelRequest.hasPendingRequest(player)) {
      for (DuelRequest request : DuelRequest.getRequests(player)) {
        request.deny();
      }
    }
    player.sendMessage(
            AuroraParties.getInstance()
                    .getLang()
                    .formatComponent("player-joined-party", player.getName()));
    player.openInventory(this.getMemberGUI().getInventory());
    for (Player partyMember : this.getMembers()) {
      partyMember.sendMessage(
              AuroraParties.getInstance()
                      .getLang()
                      .formatComponent("player-joined-party", player.getName()));
    }
    partyLeader.sendMessage(
            AuroraParties.getInstance()
                    .getLang()
                    .formatComponent("player-joined-party", player.getName()));
    partyMembers.add(player);
    updateDisplayItem();
    incrementIndex();
    ItemStack memberSkull = PlayerHeadUtil.getPlayerSkull(player);
    memberSkulls.put(player.getUniqueId(), memberSkull);
    partyLeaderGUI.addMember(index, memberSkull);
    partyManageGUI.addMember(index, memberSkull);
    partyMemberGUI.addMember(index, memberSkull);
    publicPartyGUI.addMember(index, memberSkull);
    teamDuelGUI.addMember(memberSkull, player);
    PartySelectGUI.reload();

    // Enable party chat by default for new member
    ChatGroup memberGroup = ChatGroup.getChatGroup(player);
    memberGroup.setChatTarget(this);
    player.sendMessage(AuroraParties.getInstance().getLang().getComponent("party-chat-enabled"));
  }

  public void leave(Player player) {
    if (player.equals(partyLeader)) {
      this.disband();
      return;
    }
    this.removeMember(player);
    ChatGroup cg = ChatGroup.getChatGroup(player);
    if (cg.getMode() == ChatGroup.ChatMode.SELECT && cg.getAllowedGroup() == this) {
      cg.setChatTarget(null);
      player.sendMessage(AuroraParties.getInstance().getLang().getComponent("party-chat-reverted"));
    }
    player.sendMessage(AuroraParties.getInstance().getLang().getComponent("left-party"));
    partyLeader.sendMessage(
            AuroraParties.getInstance()
                    .getLang()
                    .formatComponent("player-left-party", player.getName()));
    for (Player partyMember : this.getMembers()) {
      partyMember.sendMessage(
              AuroraParties.getInstance()
                      .getLang()
                      .formatComponent("player-left-party", player.getName()));
    }
    updateDisplayItem();
  }

  public void kick(Player player) {
    this.removeMember(player);
    ChatGroup cg = ChatGroup.getChatGroup(player);
    if (cg.getMode() == ChatGroup.ChatMode.SELECT && cg.getAllowedGroup() == this) {
      cg.setChatTarget(null);
      player.sendMessage(AuroraParties.getInstance().getLang().getComponent("party-chat-reverted"));
    }
    player.sendMessage(AuroraParties.getInstance().getLang().getComponent("removed-from-party"));
    partyLeader.sendMessage(
            AuroraParties.getInstance()
                    .getLang()
                    .formatComponent("player-removed-from-party", player.getName()));
    for (Player partyMember : this.getMembers()) {
      partyMember.sendMessage(
              AuroraParties.getInstance()
                      .getLang()
                      .formatComponent("player-removed-from-party", player.getName()));
    }
    updateDisplayItem();
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
    PartySelectGUI.reload();
  }

  public void disband() {
    for (Player player : getChatMembers()) {
      player.sendMessage(
              AuroraParties.getInstance()
                      .getLang()
                      .formatComponent("party-disbanded", partyLeader.getName()));
      ChatGroup cg = ChatGroup.getChatGroup(player);
      if (cg.getMode() == ChatGroup.ChatMode.SELECT && cg.getAllowedGroup() == this) {
        cg.setChatTarget(null);
        player.sendMessage(AuroraParties.getInstance().getLang().getComponent("party-chat-reverted"));
      }
    }
    PartySelectGUI.reload();

    PARTY_LEADERS.remove(partyLeader);
    PARTIES.remove(this);
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

  public Set<Player> getChatMembers() {
    Set<Player> all = new HashSet<>(partyMembers);
    all.add(partyLeader);
    return all;
  }

  public enum Status {
    ANYONE_CAN_JOIN,
    INVITE_ONLY,
    PRIVATE
  }
}