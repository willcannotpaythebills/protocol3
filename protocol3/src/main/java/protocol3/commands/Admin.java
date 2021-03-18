package protocol3.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.Config;
import protocol3.backend.Utilities;
import protocol3.events.SpeedLimit;

// funny command haha

public class Admin implements CommandExecutor {

	public static List<UUID> Spies = new ArrayList<UUID>();
	public static List<UUID> MsgToggle = new ArrayList<UUID>();
	public static List<UUID> UseRedName = new ArrayList<UUID>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		if (args.length == 1) {
			if (!player.isOp()) {
				return true;
			}
			switch (args[0].toUpperCase()) {
				case "COLOR":
					if (UseRedName.contains(player.getUniqueId())) {
						player.spigot().sendMessage(new TextComponent("§6Disabled red name."));
						UseRedName.remove(player.getUniqueId());
					} else {
						player.spigot().sendMessage(new TextComponent("§6Enabled red name."));
						UseRedName.add(player.getUniqueId());
					}
					return true;
				case "SPY":
					if (Spies.contains(player.getUniqueId())) {
						player.spigot().sendMessage(new TextComponent("§6Disabled spying on player messages."));
						Spies.remove(player.getUniqueId());
					} else {
						player.spigot().sendMessage(new TextComponent("§6Enabled spying on player messages."));
						Spies.add(player.getUniqueId());
					}
					return true;
				case "MSGTOGGLE":
					if (MsgToggle.contains(player.getUniqueId())) {
						player.spigot().sendMessage(new TextComponent("§6Enabled recieving player messages."));
						MsgToggle.remove(player.getUniqueId());
					} else {
						player.spigot().sendMessage(new TextComponent("§6Disabled recieving player messages."));
						MsgToggle.add(player.getUniqueId());
					}
					return true;
				case "RELOAD":
					try
					{
						Config.load();
						player.spigot().sendMessage(new TextComponent("§aSuccessfully reloaded."));

					} catch (IOException e)
					{
						player.spigot().sendMessage(new TextComponent("§4Failed to reload."));
						Utilities.restart();
					}
					return true;
				case "SPEED":
					player.spigot().sendMessage(new TextComponent("§6Player speeds:"));
					List<String> speeds = SpeedLimit.getSpeeds();
					for (String speed : speeds) {
						player.spigot().sendMessage(new TextComponent(speed));
					}
					player.spigot().sendMessage(new TextComponent("§6End of speed list."));
					return true;
			}
		}
		player.spigot().sendMessage(new TextComponent("§cd2k11: §7Systems Administrator, Developer, Founder"));
		player.spigot().sendMessage(new TextComponent("§cxX_xxX6_9xx_Xx: §7Community Delegate, Financial, Oldfag"));
		player.spigot().sendMessage(new TextComponent("§chaJUNT: §7Community Manager, Oldfag"));
		return true;
	}

}
