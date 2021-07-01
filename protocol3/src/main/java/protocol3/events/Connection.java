package protocol3.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.*;
import protocol3.commands.Admin;
import protocol3.commands.Kit;
import protocol3.commands.ToggleJoinMessages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

// Connection Events
// protocol3. ~~DO NOT REDISTRIBUTE!~~ n/a 3/6/2021

public class Connection implements Listener {
	
	public static List<String> Motds = new ArrayList<String>();
	
	public static String serverHostname = "unknown";
	public static boolean serverRestarting = false;
	
	public static boolean doJoinAnnounce = false;
	public static String joinAnnounceText = "";
	
	public static int newfags = 0;
	
	@EventHandler
	public void onConnect(PlayerLoginEvent e) {
		
		if(serverRestarting) {
			e.setKickMessage("Disconnected");
			e.setResult(Result.KICK_OTHER);
			return;
		}
		
		// Set server name if it's forced
		if(Config.getValue("motd.force").equals("true")) {
			serverHostname = Config.getValue("motd.force.name");
		}
		
		// Get domain name, NOT ip if player is connecting from IP
		if(!Utilities.validIP(e.getHostname()) && serverHostname.equals("unknown")) {
			serverHostname = e.getHostname().split(":")[0];
		}
		
		if(e.getPlayer().isOp() && Admin.AllowedAdmins.contains(e.getPlayer().getUniqueId()) && Config.getValue("2fa").equals("true")) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(p.isOp()) {
					p.spigot().sendMessage(new TextComponent("§aOP login success - "+e.getPlayer().getName()+" - "+e.getAddress().toString().split(":")[0].replace("/", "")));
				}
			}
		}
		
		if(e.getPlayer().isOp() && !Admin.AllowedAdmins.contains(e.getPlayer().getUniqueId()) && Config.getValue("2fa").equals("true")) {
			e.setKickMessage("§6You need to authenticate via console or another admin first.\n§oTip: You can disable this option by setting §n2fa = false§r§6 in config.txt.");
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(p.isOp()) {
					p.spigot().sendMessage(new TextComponent("§cOP login failure - "+e.getPlayer().getName()+" - "+e.getAddress().toString().split(":")[0].replace("/", "")));
				}
			}
			e.setResult(Result.KICK_OTHER);
			return;
		}
		
		if (!ServerMeta.canReconnect(e.getPlayer())) {
			e.setKickMessage("§6Connection throttled. Please wait some time before reconnecting.");
			e.setResult(Result.KICK_OTHER);
			return;
		}
		
		if(!PlayerMeta.IPResolutions.containsKey(e.getPlayer().getName())) {
			PlayerMeta.IPResolutions.put(e.getPlayer().getName(), e.getAddress().toString().split(":")[0].replace("/", ""));
		}
		else {
			String value = PlayerMeta.IPResolutions.get(e.getPlayer().getName());
			if(!value.contains(e.getAddress().toString().split(":")[0].replace("/", ""))) {
				value += ","+e.getAddress().toString().split(":")[0].replace("/", "");
			}
			PlayerMeta.IPResolutions.put(e.getPlayer().getName(), value);
		}
		
		if(!PlayerMeta.isDonator(e.getPlayer())) {
			if(ProxyFilter.doBlock(e.getPlayer(), e.getAddress().toString().split(":")[0].replace("/", ""))) {
				e.setKickMessage("§6Connection blocked. If the server just started, please wait some time before reconnecting. \nIf it hasn't, ensure you're not on a VPN or proxy. You can use a VPN or proxy if you're a donator.");
				e.setResult(Result.KICK_OTHER);
				return;
			}
		}
		
		// store uuid
		if(PlayerMeta.getCachedUUID(e.getPlayer().getName()) != null) {
			if(!PlayerMeta.getCachedUUID(e.getPlayer().getName()).equals(e.getPlayer().getUniqueId())) {
				// name change
				PlayerMeta.UUIDResolutions.put(e.getPlayer().getName(), e.getPlayer().getUniqueId());
				// remove old name
				Set<String> names = PlayerMeta.UUIDResolutions.keySet();
				for(String p : names) {
					if(PlayerMeta.UUIDResolutions.get(p).equals(e.getPlayer().getUniqueId())) {
						PlayerMeta.UUIDResolutions.remove(p);
					}
				}
			}
		}
		else {
			PlayerMeta.UUIDResolutions.put(e.getPlayer().getName(), e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		
		if(!PlayerMeta.Playtimes.containsKey(e.getPlayer().getUniqueId())) {
			PlayerMeta.Playtimes.put(e.getPlayer().getUniqueId(), 0.0D);
		}

		// Full player check on initial join
		if (Config.getValue("item.illegal.onjoin").equals("true")) {
			e.getPlayer().getInventory().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "LOGON_INVENTORY_ITEM", e.getPlayer()));
			e.getPlayer().getEnderChest().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "LOGON_ENDER_CHEST_ITEM", e.getPlayer()));
			Arrays.stream(e.getPlayer().getInventory().getArmorContents()).forEach(itemStack -> ItemCheck.IllegalCheck(itemStack, "LOGON_ARMOR_ITEM", e.getPlayer()));

			ItemCheck.IllegalCheck(e.getPlayer().getInventory().getItemInMainHand(), "LOGON_MAIN_HAND", e.getPlayer());

			ItemCheck.IllegalCheck(e.getPlayer().getInventory().getItemInOffHand(), "LOGON_OFF_HAND", e.getPlayer());
		}

		// Set survival if enabled; exempt ops
		if (Config.getValue("misc.survival").equals("true") && !e.getPlayer().isOp()) {
			e.getPlayer().setGameMode(GameMode.SURVIVAL);
		}
		
		if(!e.getPlayer().hasPlayedBefore()) {
			newfags++;
			Bukkit.spigot().broadcast(new TextComponent("§6§o"+e.getPlayer().getName()+" is newfag #"+newfags+" today. Go get em!"));
		}
		
		if(doJoinAnnounce) {
			e.getPlayer().spigot().sendMessage(new TextComponent(joinAnnounceText));
		}
		
		if (!PlayerMeta.isMuted(e.getPlayer()) && !Kit.kickedFromKit.contains(e.getPlayer().getUniqueId())) {
			doJoinMessage(MessageType.JOIN, e.getPlayer());
		}
		
		if (Kit.kickedFromKit.contains(e.getPlayer().getUniqueId())) {
			Kit.kickedFromKit.remove(e.getPlayer().getUniqueId());
		}
	}

	public enum MessageType {
		JOIN, LEAVE
	}

	public void doJoinMessage(MessageType msg, Player player) {
		if(player.isOp()) return;
		String messageOut = "§7" + player.getName()
				+ ((msg.equals(MessageType.JOIN)) ? " joined the game." : " left the game.");
		Bukkit.getOnlinePlayers().forEach(player1 ->{
				if (!ToggleJoinMessages.disabledJoinMessages.contains(player1.getUniqueId())) player1.sendMessage(messageOut);});
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		if (!PlayerMeta.isMuted(e.getPlayer()) && !Kit.kickedFromKit.contains(e.getPlayer().getUniqueId())) {
			doJoinMessage(MessageType.LEAVE, e.getPlayer());
		}
		Location l = e.getPlayer().getLocation();            //store Location floored to block
		Admin.LogOutSpots.put(e.getPlayer().getName(), new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ()));
		ServerMeta.preventReconnect(e.getPlayer(), Integer.parseInt(Config.getValue("speedlimit.rc_delay_safe")));
	}

	private Random randomMotd = new Random();

	@EventHandler
	public void onPing(ServerListPingEvent e) {
		
		int rnd = randomMotd.nextInt(Motds.size());
		String tps = new DecimalFormat("#.##").format(LagProcessor.getTPS());
		String motd = "";
		
		if(Config.getValue("motd.force.desc").equals("false")) {
			motd = Motds.get(rnd).replace("&", "§");
			e.setMotd("§9"+serverHostname+" §7| §5" + motd + " §7| §9TPS: " + tps);
		}
		else {
			motd = Config.getValue("motd.force.desc").replace("&", "§");
			e.setMotd("§9"+serverHostname+" §7| §5" + motd + " §7| §9TPS: " + tps);
		}
		e.setMaxPlayers(1);
	}

}
