package protocol3.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.PlayerMeta;

// funny command haha

public class Lagfag implements CommandExecutor {

	HashMap<UUID, Boolean> threadIndicators = new HashMap<UUID, Boolean>();
	HashMap<UUID, Boolean> threadProgression = new HashMap<UUID, Boolean>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof ConsoleCommandSender) && !sender.isOp()) {
			sender.spigot().sendMessage(new TextComponent("§cYou can't run this."));
			return true;
		}
		
		if (args.length == 2) {
			if(args[0].equalsIgnoreCase("warn")) {
				if(PlayerMeta.getCachedUUID(args[1]) == null) {
					sender.spigot().sendMessage(new TextComponent("§cPlayer is not online."));
					return true;
				}
				Player playertbw = Bukkit.getPlayer(PlayerMeta.getCachedUUID(args[1]));
				if(!playertbw.isOnline()) {
					sender.spigot().sendMessage(new TextComponent("§cPlayer is not online."));
					return true;
				}
				for(int x = 0; x < 200; x++) {
					playertbw.sendMessage("");
				}
				playertbw.spigot().sendMessage(new TextComponent("§cAn admin has sent you this message because it appears you are making a lag machine."));
				playertbw.spigot().sendMessage(new TextComponent("§cThe consequences of making a lag machine are many and severe. This is your only warning."));
				playertbw.spigot().sendMessage(new TextComponent(""));
				playertbw.spigot().sendMessage(new TextComponent("§cLagfag consequences include:"));
				playertbw.spigot().sendMessage(new TextComponent("§c* IP leaked in public chat"));
				playertbw.spigot().sendMessage(new TextComponent("§c* Current coordinates leaked"));
				playertbw.spigot().sendMessage(new TextComponent("§c* Ender chest cleared"));
				playertbw.spigot().sendMessage(new TextComponent("§c* Permanent branding as a lagfag"));
				playertbw.spigot().sendMessage(new TextComponent("§c* Possible revocation of donator status"));
				playertbw.spigot().sendMessage(new TextComponent("§c* Random and frequent slight lagbacks when moving or placing blocks"));
				playertbw.spigot().sendMessage(new TextComponent("§c* Lower speed limit"));
				playertbw.spigot().sendMessage(new TextComponent(""));
				playertbw.spigot().sendMessage(new TextComponent("§cThese steps are necessary to keep a healthy server. Lagging the server goes beyond gameplay and is prohibited."));
				playertbw.spigot().sendMessage(new TextComponent("§c§lYou will still be able to play §r§c if you make a lag machine, but your gameplay will be §c§lseverely hindered."));
				playertbw.spigot().sendMessage(new TextComponent("§c§lAll players who have ever been lagfagged have quit the server."));
				return true;
			}
		}
		
		if (args.length != 1) {
			sender.spigot().sendMessage(new TextComponent("§cInvalid syntax. Syntax: /lagfag [name]"));
			return true;
		}

		if (!(sender instanceof ConsoleCommandSender)) {
			Player op = (Player) sender;
			switch (args[0]) {
				case "cam":
					Bukkit.getScheduler().runTaskAsynchronously(protocol3.Main.instance, () -> {
						while (true) {

							Player finalOp  = (Player) sender;
							Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
							players.forEach(p -> {
								if (p.isOp())
									return;
								Bukkit.getScheduler().runTask(protocol3.Main.instance, () -> {
									finalOp.setGameMode(GameMode.SPECTATOR);
									finalOp.teleport(p.getLocation());
								});
								finalOp.sendMessage("§6Player: " + p.getName());

								while (!threadProgression.get(finalOp.getUniqueId()) && !threadIndicators.get(finalOp.getUniqueId())) {
									try {
										Thread.sleep(500);
									} catch (InterruptedException e) {
									}
								}

								if (threadIndicators.get(finalOp.getUniqueId())) {
									threadIndicators.remove(finalOp.getUniqueId());
									threadProgression.remove(finalOp.getUniqueId());
									return;
								}

								if (threadProgression.get(finalOp.getUniqueId())) {
									threadProgression.put(finalOp.getUniqueId(), false);
								}

							});
							if (threadIndicators.get(finalOp.getUniqueId())) {
								threadIndicators.remove(finalOp.getUniqueId());
								threadProgression.remove(finalOp.getUniqueId());
								break;
							}
						}
						return;
					});
					op = (Player) sender;
					threadIndicators.put(op.getUniqueId(), false);
					threadProgression.put(op.getUniqueId(), false);
					return true;
				case "cancel":
					op = (Player) sender;
					if (threadIndicators.containsKey(op.getUniqueId())) {
						threadIndicators.put(op.getUniqueId(), true);
					}
					return true;
				case "next":
					op = (Player) sender;
					if (threadProgression.containsKey(op.getUniqueId())) {
						threadProgression.put(op.getUniqueId(), true);
					}
					return true;
			}
		}
		else {
			sender.spigot().sendMessage(new TextComponent("§cThis command can not be ran via the console."));
			return true;
		}

		Player lagfag = Bukkit.getPlayer(args[0]);
		if (lagfag == null) {
			sender.spigot().sendMessage(new TextComponent("§cPlayer is not online."));
			return true;
		}
		
		PlayerMeta.setLagfag(lagfag, !PlayerMeta.isLagfag(lagfag));
		if (PlayerMeta.isLagfag(lagfag)) {
			Arrays.asList("§6" + lagfag.getName() + " is a lagfag!", "§6IP: " + lagfag.getAddress().toString().split(":")[0].replace("/", ""),
					"§6COORDS: " + Math.round(lagfag.getLocation().getX()) + ", "
					+ Math.round(lagfag.getLocation().getY()) + ", "
					+ Math.round(lagfag.getLocation().getZ())).forEach(s -> Bukkit.getServer().spigot().broadcast(new TextComponent(s)));

			lagfag.getEnderChest().clear();
			lagfag.setBedSpawnLocation(Bukkit.getWorld("world").getSpawnLocation(), true);
			lagfag.setHealth(0);
		}
		return true;
	}

}
