package protocol3.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

// funny command haha

public class About implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		player.spigot().sendMessage(new TextComponent("§7protocol3.3.0 by d2k11 & AVAS Community contributors. Written for avas.cc.")); // :)
		TextComponent message = new TextComponent("protocol3 is now open source. You can access the GitHub by clicking this message.");
		message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://avas.cc/github"));
		return true;
	}

}
