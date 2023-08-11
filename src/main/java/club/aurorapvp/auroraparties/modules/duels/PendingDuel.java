package club.aurorapvp.auroraparties.modules.duels;

import club.aurorapvp.auroraduels.modules.duels.Duel;
import club.aurorapvp.auroraduels.modules.duels.DuelKitGui;
import club.aurorapvp.auroraduels.modules.queues.Queue;
import club.aurorapvp.auroraduels.modules.queues.QueueGUI;
import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.config.Lang;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class PendingDuel {
  protected static final Set<PendingDuel> PENDING_DUELS = new HashSet<>();
  private final Player partyLeader;
  private final Duel duel;

  public PendingDuel(Player partyLeader, Duel duel) {
    this.partyLeader = partyLeader;
    this.duel = duel;

    QueueGUI.open(partyLeader);

    PENDING_DUELS.add(this);
  }

  public static void onInventoryClick(InventoryClickEvent event) {
    Player p = (Player) event.getWhoClicked();

    if (!QueueGUI.isQueueGUI(event.getClickedInventory()) || !PendingDuel.hasPendingDuel(p)) {
      return;
    }

    final ItemStack clickedItem = event.getCurrentItem();

    if (clickedItem == null || clickedItem.getType().isAir()) {
      return;
    }

    event.setCancelled(true);

    if (event.getCurrentItem().getItemMeta().displayName() == null) {
      p.sendMessage(Lang.getComponent("queue-not-found"));
    } else {
      String itemName =
          PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().displayName());
      String queueName = itemName.substring(1, itemName.length() - 1);

      Queue queue = Queue.getQueue(queueName.toLowerCase());

      if (queue == null) {
        p.sendMessage(Lang.getComponent("queue-not-found"));
        return;
      }

      Objects.requireNonNull(Duel.getDuel(p)).setQueue(queue);

      if (AuroraParties.isAuroraKitsInstalled()) {
        new DuelKitGui(p);
        return;
      }

      Objects.requireNonNull(PendingDuel.getPendingDuel(p)).enterQueue();
    }
  }

  public static void onInventoryClose(InventoryCloseEvent event) {
    if (!(event.getPlayer() instanceof Player p)) {
      return;
    }

    if (!PendingDuel.hasPendingDuel(p)) {
      return;
    }

    if (QueueGUI.isQueueGUI(event.getInventory())
        && PendingDuel.hasPendingDuel(p)
        && Objects.requireNonNull(Duel.getDuel(p)).getQueue() == null) {
      QueueGUI.open(p);
    }

    DuelKitGui kitGui = DuelKitGui.getGui(p);

    if (kitGui == null) {
      return;
    }

    if (kitGui.isGuiInventory(event.getInventory())) {
      Objects.requireNonNull(PendingDuel.getPendingDuel(p)).enterQueue();
    }
  }

  public static PendingDuel getPendingDuel(Player p) {
    for (PendingDuel pendingDuel : PENDING_DUELS) {
      if (pendingDuel.partyLeader == p) {
        return pendingDuel;
      }
    }
    return null;
  }

  public static boolean hasPendingDuel(Player p) {
    for (PendingDuel pendingDuel : PENDING_DUELS) {
      if (pendingDuel.partyLeader == p) {
        return true;
      }
    }
    return false;
  }

  public void enterQueue() {
    duel.getQueue().enterDuelQueue(duel);
    PENDING_DUELS.remove(this);
  }
}
