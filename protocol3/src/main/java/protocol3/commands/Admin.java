package protocol3.commands;

import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import protocol3.backend.Config;
import protocol3.backend.Pair;
import protocol3.backend.PlayerMeta;
import protocol3.backend.ProxyFilter;
import protocol3.backend.Utilities;
import protocol3.events.SpeedLimit;

import java.io.IOException;
import java.util.*;

// funny command haha

public class Admin implements CommandExecutor {

	public static List<UUID> Spies = new ArrayList<UUID>();
	public static List<UUID> MsgToggle = new ArrayList<UUID>();
	public static List<UUID> UseRedName = new ArrayList<UUID>();
	public static Map<String, Location> LogOutSpots = new HashMap<>();
	public static boolean disableWarnings = false;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		if(args.length != 0) {
			if (!PlayerMeta.isOp(sender)) {
				sender.sendMessage(new TextComponent("§cYou can't use this."));
				return true;
			}
		}
		if (args.length == 1) {
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
					try {
						Config.load();
						sender.spigot().sendMessage(new TextComponent("§aSuccessfully reloaded."));

					} catch (IOException e) {
						sender.spigot().sendMessage(new TextComponent("§4Failed to reload."));
						Utilities.restart();
					}
					return true;
				case "SPEED":
					player.spigot().sendMessage(new TextComponent("§6Player speeds:"));
					List< Pair<Double, String> > speeds = SpeedLimit.getSpeeds();
					for (Pair<Double, String> speedEntry : speeds) {
						double speed = speedEntry.getLeft();
						if(speed == 0) continue;
						String playerName = speedEntry.getRight();
						String color = "§";
						if (speed >= 64.0)
							color += "c"; // red
						else if (speed >= 48.0)
							color += "e"; // yellow
						else
							color += "a"; // green
						player.spigot().sendMessage(new TextComponent(color
								+ String.format("%4.1f: %s", speed, playerName)));
					}
					player.spigot().sendMessage(new TextComponent("§6End of speed list."));
					return true;
				case "AGRO":
					disableWarnings = !disableWarnings;
					if(disableWarnings) {
						sender.spigot().sendMessage(new TextComponent("§6Enabled aggressive speed limit."));
					}
					else {
						sender.spigot().sendMessage(new TextComponent("§6Disabled aggressive speed limit."));
					}
					return true;
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("spot")) {
				Location l = LogOutSpots.get(args[1]);
				if (l == null) {
					sender.sendMessage(new TextComponent("§6No logout spot logged for " + args[1]));
				} else {
					sender.sendMessage(new TextComponent("§6"+args[1] + " logged out at " + l.getX() + " " + l.getY() + " " + l.getZ()));
				}
				return true;
			}
			else if(args[0].equalsIgnoreCase("whois")) {
				Player p = Bukkit.getPlayer(args[1]);
				String ip = PlayerMeta.getIp(p);
				boolean muted = PlayerMeta.isMuted(p);
				int rank = PlayerMeta.getRank(p);
				sender.spigot().sendMessage(new TextComponent("§6"+p.getName()+": [IP/MUTED/RANK]: "+ip+"/"+muted+"/"+rank));
				return true;
			}
			else if(args[0].equalsIgnoreCase("filter")) {
				if(Config.getValue("filter.enabled").equals("false") || Config.getValue("filter.email").equals("example@example.com")) {
					sender.sendMessage(new TextComponent("§cThe proxy filter is disabled or is not properly set up."));
					return true;
				}
				boolean isArgInt = false;
				try { Integer.parseInt(args[1]); isArgInt = true; } catch(Exception ex) { }
				if(isArgInt) {
					int arg = Integer.parseInt(args[1]);
					if(arg > 4 || arg < 0) {
						sender.sendMessage(new TextComponent("§cInvalid filter tier. Pick a number 0 (disabled) through 4 (strict)."));
						return true;
					}
					ProxyFilter.setTier(arg);
					sender.sendMessage(new TextComponent("§6Security tier was set to "+arg));
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(!p.isOp()) {
							p.kickPlayer("§6A server security change was made. Please rejoin.");
						}
					}
					return true;
				}
				else {
					if(args[1].equals("notify")) {
						boolean status = ProxyFilter.toggleNotifyAdmin((Player)sender);
						if(status) {
							sender.sendMessage(new TextComponent("§6Enabled proxy notifications."));
						}
						else {
							sender.sendMessage(new TextComponent("§6Disabled proxy notifications."));
						}
						return true;
					}
				}
			}
		}
		player.spigot().sendMessage(new TextComponent("§cd2k11: §7Systems Administrator, Developer, Founder"));
		player.spigot().sendMessage(new TextComponent("§cxX_xxX6_9xx_Xx: §7Community Delegate, Finance, Oldfag"));
		player.spigot().sendMessage(new TextComponent("§chaJUNT: §7Community Manager, Oldfag"));
		player.spigot().sendMessage(new TextComponent("§cxCondii: §7Community, <insert something else idk>"));
		
		return true;
	}

}
