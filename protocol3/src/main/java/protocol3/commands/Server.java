package protocol3.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.LagProcessor;
import protocol3.backend.PlayerMeta;
import protocol3.backend.ProxyFilter;
import protocol3.backend.ServerMeta;
import protocol3.backend.Utilities;
import protocol3.events.Connection;
import protocol3.events.LagPrevention;
import protocol3.events.SpeedLimit;
import protocol3.tasks.OnTick;
import protocol3.tasks.ProcessPlaytime;

public class Server implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		boolean verbose = false;
		if(args.length != 0) verbose = true;
		List<String> strings = new ArrayList<String>();
		
		// GENERAL //
		
	    strings.add("§c========== GENERAL ==========");
	    strings.add("§cServer Uptime:§7 " + Utilities.calculateTime(ServerMeta.getUptime()));
	    strings.add("§cCurrent Population:§7 " + Bukkit.getOnlinePlayers().size());
	    strings.add("§cCurrent TPS:§7 " + new DecimalFormat("#.##").format(LagProcessor.getTPS()));
	    strings.add("§cCurrent Speed Limit:§7 " + (SpeedLimit.getReducedSpeed() ? "36" : "48") + " blocks per second");
	    if(verbose) strings.add("§cAnti-Cheat Enabled: §7" + (LagProcessor.getTPS() <= 10 ? "True" : "False"));
	    
	    // PLAYER DATA //
	    
	    strings.add("§c========== PLAYER ==========");
	    strings.add("§cUnique Joins (§eSince Map Creation§c):§7 " + Bukkit.getOfflinePlayers().length);
	    if(verbose) strings.add("§cUnique Joins (§eSince Stats Update§c):§7 " + PlayerMeta.Playtimes.keySet().size());
	    if(verbose) strings.add("§cUnique Joins (§eSince UUID Resolution Update§c):§7 " + PlayerMeta.UUIDResolutions.keySet().size());
	    if(verbose) strings.add("§cTop Population (§e1h§c):§7 " + OnTick.highestPop);
	    strings.add("§cNewfags (§e24h§c):§7 " + Connection.newfags);
	    strings.add("§cTop Population (§e24h§c):§7 " + OnTick.dayHighestPop);
	    strings.add("§cDonators:§7 " + PlayerMeta._donatorList.size());
	    if(verbose)strings.add("§cLagfags:§7 " + PlayerMeta._lagfagList.size());
	    strings.add("§cPermanent Mutes:§7 " + PlayerMeta._permanentMutes.size());
	    if(verbose)strings.add("§cOP Accounts:§7 " + Bukkit.getOperators().size());
	    
	    // DEBUG //
	    
	    strings.add("§c=========== DEBUG ===========");
	    if(verbose) strings.add("§cServer Restarting: §7" + (Utilities.restarting ? "True" : "False"));
	    if(verbose) strings.add("§cTime below acceptable TPS:§7 " + ProcessPlaytime.lowTpsCounter + "ms (600000ms required to restart)");
	    strings.add("§cLowest TPS (1h):§7 " + new DecimalFormat("#.##").format(OnTick.lowestTps));
	    if(verbose) strings.add("§cSecurity tier: §7" + ProxyFilter.getTier());
	    if(verbose) strings.add("§cSecurity scans (§e24h§c): §7" + ProxyFilter.getTotalScans());
	    
	    for(String s : strings) {
	    	sender.spigot().sendMessage(new TextComponent(s));
	    }
	    
		return true;

	}
}
