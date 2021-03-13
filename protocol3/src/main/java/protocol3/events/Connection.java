package protocol3.events;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;

import protocol3.backend.Config;
import protocol3.backend.ItemCheck;
import protocol3.backend.LagProcessor;
import protocol3.backend.PlayerMeta;
import protocol3.backend.ServerMeta;
import protocol3.commands.Kit;
import protocol3.commands.ToggleJoinMessages;

// Connection Events
// protocol3. ~~DO NOT REDISTRIBUTE!~~ n/a 3/6/2021

public class Connection implements Listener {
	@EventHandler
	public void onConnect(PlayerLoginEvent e) {
		if (!ServerMeta.canReconnect(e.getPlayer())) {
			e.setKickMessage("§6Connection throttled. Please wait some time before reconnecting.");
			e.setResult(Result.KICK_OTHER);
			return;
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		if (!PlayerMeta.isMuted(e.getPlayer()) && !Kit.kickedFromKit.contains(e.getPlayer().getUniqueId())) {
			doJoinMessage(MessageType.JOIN, e.getPlayer());
		}

		if (Kit.kickedFromKit.contains(e.getPlayer().getUniqueId())) {
			Kit.kickedFromKit.remove(e.getPlayer().getUniqueId());
		}

		// Full player check on initial join
		if (Config.getValue("item.illegal.onjoin").equals("true")) {
			e.getPlayer().getInventory().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack));
			e.getPlayer().getEnderChest().forEach(itemStack -> ItemCheck.IllegalCheck(itemStack));
			Arrays.stream(e.getPlayer().getInventory().getArmorContents()).forEach(itemStack -> ItemCheck.IllegalCheck(itemStack));

			ItemCheck.IllegalCheck(e.getPlayer().getInventory().getItemInMainHand());

			ItemCheck.IllegalCheck(e.getPlayer().getInventory().getItemInOffHand());
		}

		// Set survival if enabled; exempt ops
		if (Config.getValue("misc.survival").equals("true") && !e.getPlayer().isOp()) {
			e.getPlayer().setGameMode(GameMode.SURVIVAL);
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
		e.setMotd("§9avas.cc §7| §5" + allMotds.get(rnd) + " §7| §9TPS: " + tps);
		e.setMaxPlayers(1);
	}

}
