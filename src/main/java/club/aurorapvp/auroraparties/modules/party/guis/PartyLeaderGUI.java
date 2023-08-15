package club.aurorapvp.auroraparties.modules.party.guis;

import club.aurorapvp.auroraduels.modules.duels.Duel;
import club.aurorapvp.auroraduels.modules.duels.FFADuel;
import club.aurorapvp.auroraduels.modules.duels.TeamDuel;
import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.modules.duels.PendingDuel;
import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.auroraparties.util.InventoryUtil;
import club.aurorapvp.aurorawarps.modules.WorldSpawn;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class PartyLeaderGUI extends PartyGUI {
  private final Inventory inventory;

  public PartyLeaderGUI(Party party, ItemStack leaderSkull) {
    super(party);

    inventory =
        Bukkit.createInventory(
            this,
            54,
            MiniMessage.miniMessage()
                .deserialize("<gradient:#FFAA00:#FF55FF><bold>" + party.getName()));

    inventory.setItem(10, leaderSkull);

    ItemStack disbandParty = new ItemStack(Material.RED_WOOL);
    ItemMeta meta = disbandParty.getItemMeta();
    meta.displayName(Lang.getComponent("disband-party"));
    disbandParty.setItemMeta(meta);

    inventory.setItem(53, disbandParty);

    ItemStack manageParty = new ItemStack(Material.ENDER_PEARL);
    meta = manageParty.getItemMeta();
    meta.displayName(Lang.getComponent("manage-party"));
    manageParty.setItemMeta(meta);

    inventory.setItem(45, manageParty);

    ItemStack teamDuel = new ItemStack(Material.NETHERITE_SWORD);
    meta = teamDuel.getItemMeta();
    meta.lore(List.of(Component.text("")));
    meta.displayName(Lang.getComponent("create-team-duel"));
    teamDuel.setItemMeta(meta);

    inventory.setItem(49, teamDuel);

    ItemStack partyDuel = new ItemStack(Material.DIAMOND_AXE);
    meta = partyDuel.getItemMeta();
    meta.lore(List.of(Component.text("")));
    meta.displayName(Lang.getComponent("start-random-team-duel"));
    partyDuel.setItemMeta(meta);

    inventory.setItem(47, partyDuel);

    ItemStack ffaDuel = new ItemStack(Material.DIAMOND_SWORD);
    meta = ffaDuel.getItemMeta();
    meta.lore(List.of(Component.text("")));
    meta.displayName(Lang.getComponent("start-ffa-duel"));
    ffaDuel.setItemMeta(meta);

    inventory.setItem(51, ffaDuel);
  }

  @Override
  public void addMember(int index, ItemStack memberSkull) {
    inventory.setItem(index, memberSkull);
  }

  @Override
  public void removeMember(int slot, ItemStack lastSkull) {
    inventory.clear(slot);

    InventoryUtil.moveItem(inventory, lastSkull, slot);
  }

  @Override
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!Objects.equals(event.getInventory().getHolder(), this)) {
      return;
    }

    event.setCancelled(true);

    Player p = (Player) event.getWhoClicked();

    Set<Player> players = null;
    Location loc = null;

    switch (event.getSlot()) {
      case 53 -> {
        party.disband();
        p.closeInventory();
      }

      case 45 -> p.openInventory(party.getManageGUI().getInventory());

      case 49 -> {
        if (party.getMembers().isEmpty()) {
          p.sendMessage(Lang.getComponent("not-enough-players"));
          return;
        }

        p.openInventory(party.getTeamDuelGUI().getInventory());
      }
      case 47, 51 -> {
        if (party.getMembers().isEmpty()) {
          return;
        }

        players = new HashSet<>(party.getMembers());
        players.add(party.getLeader());

        loc = Duel.getDuelLobby();

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
      }
    }

    if (event.getSlot() == 47) {
      assert players != null;
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
    }

    if (event.getSlot() == 51) {
      new PendingDuel(p, new FFADuel(players, loc, false));
    }
  }


  @Override
  public @NotNull Inventory getInventory() {
    return inventory;
  }
}
