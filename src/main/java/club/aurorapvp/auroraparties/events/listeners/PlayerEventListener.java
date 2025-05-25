package club.aurorapvp.auroraparties.events.listeners;

import club.aurorapvp.auroraparties.modules.party.Party;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (!Party.inParty(event.getPlayer())) {
      return;
    }

    Party party = Party.getParty(event.getPlayer());
    assert party != null;

    party.leave(event.getPlayer());
  }
}
