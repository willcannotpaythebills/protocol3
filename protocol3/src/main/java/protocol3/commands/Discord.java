package protocol3.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.Config;

// Discord command

public class Discord implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		TextComponent message;
		if(Config.getValue("discord.link").equals("none")) {
			message = new TextComponent("§cUnknown command.");
		}
		else {
			message = new TextComponent("§6Click this message to join the Discord.");
			message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Config.getValue("discord.link")));
		}
		sender.spigot().sendMessage(message);
		return true;
	}

}
