package protocol3.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.Config;

// Discord command

public class Donate implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		TextComponent message;
		if(Config.getValue("donate.link").equals("none")) {
			message = new TextComponent("§cUnknown command.");
		}
		else {
			message = new TextComponent("§6§lClick this message to donate. §r§6Please notify an admin once you have donated so you can get your benefits: a gold name in game, a gold name in Discord, and VPN access on the server.");
			message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Config.getValue("donate.link")));
		}
		sender.spigot().sendMessage(message);
		return true;
	}

}
