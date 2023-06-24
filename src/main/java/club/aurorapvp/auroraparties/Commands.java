package club.aurorapvp.auroraparties;

import club.aurorapvp.auroraparties.commands.PartyCommands;
import club.aurorapvp.auroraparties.commands.PluginCommands;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import java.util.List;

public class Commands {
  public static PaperCommandManager MANAGER = new PaperCommandManager(AuroraParties.INSTANCE);

  public static void init() {
    MANAGER.registerCommand(new PluginCommands());
    MANAGER.registerCommand(new PartyCommands());

    CommandCompletions<BukkitCommandCompletionContext> commandCompletions =
        MANAGER.getCommandCompletions();

    commandCompletions.registerCompletion("foo", c -> {
      return List.of("bar");
    });
  }
}
