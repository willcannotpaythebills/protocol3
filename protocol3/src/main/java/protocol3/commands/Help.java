package protocol3.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;

public class Help implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			displayPage(Integer.parseInt(args[0]), sender);
		} catch (Exception ex) {
			displayPage(1, sender);
		}
		return true;
	}

	private void displayPage(int page, CommandSender sender) {
		int maxPage = 2;

		page = (page > maxPage) ? maxPage : Math.max(page, 1);

		sender.spigot().sendMessage(new TextComponent("§6--- Help Page " + page + "/" + maxPage + " ---"));
		switch (page) {
			case 1:
				Arrays.asList("§6/help: §7This list of commands",
						"§6/faq: §7A list of frequently asked questions.",
						"§6/stats [(playername)/top/leaderboard]: §7Get a list of statistics about a player",
						"§6/discord: §7Join the Discord.",
						"§6/donate: §7Donate to the server.",
						"§6/kit [type]: §7Get a netherite kit with steak and more useful tools.",
						"§6/vote: §7Dupe the item in your hand. Only occurs after voting.",
						"§6/sign: §7Sign the item you are holding. Cannot undo or overwrite."
				).forEach(message -> sender.spigot().sendMessage(new TextComponent(message)));
				break;
			case 2:
				Arrays.asList("§6/server: §7Get statistics about the server.",
						"§6/vm [player]: §7Vote to mute a player.",
						"§6/ignore [player]: §7Ignore someone.",
						"§6/kill, /suicide, /kys: §7End it all.",
						"§6/msg, /w, /r: §7Message or reply to a player.",
						"§6/tdm: §7Toggle death messages.",
						"§6/tjm: §7Toggle join messages.",
						"§6/ignore [player]: §7Ignore a player. Persists until next restart."
				).forEach(message -> sender.spigot().sendMessage(new TextComponent(message)));
				break;
		}
		sender.spigot().sendMessage(new TextComponent("§6--- Help Page " + page + "/" + maxPage + " ---"));
	}
}
