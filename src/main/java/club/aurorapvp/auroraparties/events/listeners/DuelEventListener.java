package club.aurorapvp.auroraparties.events.listeners;

import club.aurorapvp.auroraduels.events.custom.QueueJoinEvent;
import club.aurorapvp.auroraparties.modules.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DuelEventListener implements Listener {
  @EventHandler
  public static void onDuelQueue(QueueJoinEvent event) {
    for (Player p : event.getDuel().getPlayers()) {
      if (Party.inParty(p)) {
        Party party = Party.getParty(p);

        assert party != null;
        party.leave(p);
      }
    }
  }
}
