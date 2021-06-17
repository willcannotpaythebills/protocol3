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
	
	public static String serverHostname = "unknown";
	public static boolean serverRestarting = false;
	
	@EventHandler
	public void onConnect(PlayerLoginEvent e) {
		
		if(serverRestarting) {
			e.setKickMessage("§6Server is restarting");
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
		
		// Custom whitelist kick
		if(Bukkit.hasWhitelist() && !Bukkit.getWhitelistedPlayers().contains(e.getPlayer())
				&& !e.getPlayer().isOp() && serverHostname.equals("test.avas.cc")) {
			e.setKickMessage("§6The test server is closed right now. Please try again later.");
			e.setResult(Result.KICK_OTHER);
			return;
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

		if (Kit.kickedFromKit.contains(e.getPlayer().getUniqueId())) {
			Kit.kickedFromKit.remove(e.getPlayer().getUniqueId());
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
			Bukkit.spigot().broadcast(new TextComponent("§6§o"+e.getPlayer().getName()+" is a verified newfag. Go get em!"));
		}
		
		if (!PlayerMeta.isMuted(e.getPlayer()) && !Kit.kickedFromKit.contains(e.getPlayer().getUniqueId())) {
			doJoinMessage(MessageType.JOIN, e.getPlayer());
		}
	}

	public enum MessageType {
		JOIN, LEAVE
	}

	public void doJoinMessage(MessageType msg, Player player) {
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

	private String[] motds = { "active doop!!!!!", "do you like my sword sword", "peaceful smp",
			"dont join this is virused", "christian mlp anarchy server", "yiff", "Time to call someone a nigger jew!",
			"the best minecraft server in the universe", "Now supporting lava dupe!", "dutch plugins lol!!!!!!",
			"\"this next patch will be very stable\"", "\"restarting restarting restarting restarting\"",
			"\"fuck it doesn't work\"", "nice", "use /kit for free starter kit", "/KIT EXPLOIT STILL WORKS!!!!" };

	private Random r = new Random();

	private List<String> allMotds = new ArrayList<String>();

	private boolean done = false;

	@EventHandler
	public void onPing(ServerListPingEvent e) {
		if (!done) {
			try {
				allMotds = new ArrayList<String>(Arrays.asList(motds));
				System.out.println("[protocol3] Loading " + motds.length + " custom MOTDs...");
				allMotds.addAll(Files.readAllLines(Paths.get("plugins/protocol3/motds.txt")));
			} catch (IOException e1) {
				allMotds = new ArrayList<String>(Arrays.asList(motds));
			}
			done = true;
			System.out.println("[protocol3] Loaded " + allMotds.size() + " MOTDs");
		}
		int rnd = r.nextInt(allMotds.size());
		String tps = new DecimalFormat("#.##").format(LagProcessor.getTPS());
		if(Config.getValue("motd.force.desc").equals("false")) {
			e.setMotd("§9"+serverHostname+" §7| §5" + allMotds.get(rnd) + " §7| §9TPS: " + tps);
		}
		else {
			e.setMotd("§9"+serverHostname+" §7| §5" + Config.getValue("motd.force.desc") + " §7| §9TPS: " + tps);
		}
		e.setMaxPlayers(1);
	}

}
