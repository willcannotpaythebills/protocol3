package protocol3.commands;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

// funny command haha

public class Faq implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Arrays.asList("§71: How long has AVAS been running?",
				"§7A: AVAS has had two resets (no more). The server was created in June 2020, and the current map was created in October 2020.",
				"",
				"§72: What dupes can I use on here?",
				"§7A: We offer the vote dupe (hold item and vote using /vote). As it stands right now (6/5/2021), there are no active dupes that work on AVAS. We do not patch dupes if they come out.",
				"",
				"§73: Why don't some dupes work on here but work on vanilla 1.16.5?",
				"§7A: The server runs on Paper, a fork of Spigot. Our version of Paper has all dupes discovered prior to Feburary 2020. We don't plan on updating Paper until the next version of Minecraft.",
				"",
				"§74: Do you have a Discord?",
				"§7A: /discord",
				"",
				"§75: Can I have (item)?",
				"§7A: No."
		).forEach(message -> sender.spigot().sendMessage(new TextComponent(message)));
		return true;
	}

}
