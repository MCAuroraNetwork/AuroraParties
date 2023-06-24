package club.aurorapvp.modules;

import club.aurorapvp.configs.Lang;
import club.aurorapvp.util.PlayerHeadUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PartyGUI {
  private final Party party;
  private final String name;
  private final Player partyLeader;
  private final Set<Player> partyMembers = new HashSet<>();
  private final HashMap<Player, ItemStack> memberSkulls = new HashMap<>();
  private final Inventory publicInventory;
  private final Inventory memberInventory;
  private final Inventory leaderInventory;

  public PartyGUI(Party party) {
    this.party = party;
    this.name = party.getName();
    this.partyLeader = party.getLeader();

    ItemStack leaderSkull = PlayerHeadUtil.getPlayerSkull(partyLeader);
    memberSkulls.put(partyLeader, leaderSkull);

    publicInventory = Bukkit.createInventory(null, 54, Component.text(name));
    memberInventory = Bukkit.createInventory(null, 54, Component.text(name));
    leaderInventory = Bukkit.createInventory(null, 54, Component.text(name));

    publicInventory.setItem(10, leaderSkull);

    this.setupMemberInventory(leaderSkull);
    this.setupLeaderInventory(leaderSkull);
  }

  public void open(Player p) {
    if (party.isLeader(p)) {
      p.openInventory(leaderInventory);
    } else if (party.isMember(p)) {
      p.openInventory(memberInventory);
    } else {
      p.openInventory(publicInventory);
    }
  }

  public void addMember(Player p) {
    partyMembers.add(p);

    memberSkulls.put(p, PlayerHeadUtil.getPlayerSkull(p));
  }

  private void setupMemberInventory(ItemStack leaderSkull) {
    memberInventory.setItem(0, leaderSkull);

    ItemStack leaveParty = new ItemStack(Material.RED_WOOL);
    ItemMeta meta = leaveParty.getItemMeta();
    meta.displayName(Lang.getComponent("leave-party"));
    leaveParty.setItemMeta(meta);

    memberInventory.setItem(53, leaveParty);
  }

  private void setupLeaderInventory(ItemStack leaderSkull) {
    leaderInventory.setItem(10, leaderSkull);

    ItemStack leaveParty = new ItemStack(Material.RED_WOOL);
    ItemMeta meta = leaveParty.getItemMeta();
    meta.displayName(Lang.getComponent("disband-party"));
    leaveParty.setItemMeta(meta);

    leaderInventory.setItem(53, leaveParty);

    ItemStack manageParty = new ItemStack(Material.ENDER_PEARL);
    meta = manageParty.getItemMeta();
    meta.displayName(Lang.getComponent("manage-party"));
    manageParty.setItemMeta(meta);

    leaderInventory.setItem(45, manageParty);
  }
}
