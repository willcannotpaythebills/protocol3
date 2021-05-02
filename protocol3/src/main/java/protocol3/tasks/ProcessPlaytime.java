package protocol3.tasks;

import java.io.IOException;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.LagProcessor;
import protocol3.backend.PlayerMeta;
import protocol3.backend.Scheduler;
import protocol3.backend.ServerMeta;
import protocol3.backend.Utilities;
import protocol3.commands.Admin;
import protocol3.commands.VoteMute;
import protocol3.events.Chat;

// Playtime processor
public class ProcessPlaytime extends TimerTask {
	private static long lastTime = 0;
	private static long lastHour = 0;

	public static long lowTpsCounter = 0;
	private static long timeTillReset = 3600000;

	private static boolean withersLoaded = false;

	@Override
	public void run() {
		if (lastTime == 0) {
			lastTime = System.currentTimeMillis();
			lastHour = System.currentTimeMillis();
			return;
		}

		long sinceLast = System.currentTimeMillis() - lastTime;

		// Tick playtime
		Bukkit.getOnlinePlayers().forEach(p -> PlayerMeta.tickPlaytime(p, sinceLast));

		if (!withersLoaded) {
			// Check current withers
			// LagPrevention.currentWithers = LagPrevention.getWithers();
			withersLoaded = true;
		}

		// Tick temporary mutes
		PlayerMeta.tickTempMutes(sinceLast);

		// Tick server uptime
		ServerMeta.tickUptime(sinceLast);
		// Tick reconnect delays
		ServerMeta.tickRcDelays(sinceLast);

		if (System.currentTimeMillis() - lastHour >= 3600000) {
			lastHour = System.currentTimeMillis();
			Chat.violationLevels.clear();
			VoteMute.clear();
			Admin.AllowedAdmins.clear();
			
			if(Bukkit.getOnlinePlayers().size() < 10) {
				System.out.println("[protocol3] Saving files...");
				try
				{
					PlayerMeta.saveDonators();
					PlayerMeta.saveMuted();
					PlayerMeta.saveLagfags();
					PlayerMeta.writePlaytime();
				} catch (IOException ex)
				{
					System.out.println("[protocol3] Failed to save one or more files.");
				}
			}
		}

		// Check if we need a restart
		if (LagProcessor.getTPS() < 9) {
			lowTpsCounter += sinceLast;
			if (lowTpsCounter >= 600000) {
				Utilities.restart(true);
			}
		}

		timeTillReset = timeTillReset - sinceLast;

		if (timeTillReset <= 0) {
			lowTpsCounter = 0;
			timeTillReset = 3600000;
			withersLoaded = false;
		}

		lastTime = System.currentTimeMillis();

		// Log this
		Scheduler.setLastTaskId("oneSecondTasks");
	}
}