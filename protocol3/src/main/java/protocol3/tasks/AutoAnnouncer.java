package protocol3.tasks;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.TimerTask;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.LagProcessor;
import protocol3.backend.Scheduler;

//Auto announcer
public class AutoAnnouncer extends TimerTask {
	Random r = new Random();

	@Override
	public void run() {
		int rnd = r.nextInt(14);

		switch (rnd) {
			case 0:
				String tps = new DecimalFormat("#.##").format(LagProcessor.getTPS());
				Bukkit.spigot().broadcast(new TextComponent("§6You are playing on AVAS Survival. TPS is " + tps + "."));
				break;
			case 1:
				Bukkit.spigot().broadcast(new TextComponent("§6You can vote to mute a player by doing §l/vm [playername]."));
				break;
			case 2:
				Bukkit.spigot().broadcast(new TextComponent("§6You can dupe the item in your hand by holding the item you want to dupe and then voting using /vote. (not a troll)"));
				break;
			case 3:
				Bukkit.spigot().broadcast(new TextComponent("§6Dont forget to get your /kit starter, including §lsteak§6, basic §ldiamond armor§6, and a §lnetherite sword."));
				break;
			case 4:
				Bukkit.spigot().broadcast(new TextComponent("§6You can sign items to show them as uniquely yours by doing §l/sign."));
				break;
			case 5:
				Bukkit.spigot().broadcast(new TextComponent("§6You can buy donor for life for $20 at https://avas.cc/donate"));
				break;
			case 6:
				Bukkit.spigot().broadcast(new TextComponent("§6You can buy an MOTD for life for $10 at https://avas.cc/donate"));
				break;
			case 7:
				Bukkit.spigot().broadcast(new TextComponent("§6You can toggle death messages with /tdm, and join messages with /tjm."));
				break;
			case 8:
				Bukkit.spigot().broadcast(new TextComponent("§6Lagging the server will result in §lsevere consequences."));
				break;
			case 9:
				Bukkit.spigot().broadcast(new TextComponent("§6Do /help to see the commands available to you."));
				break;
			case 10:
				Bukkit.spigot().broadcast(new TextComponent("§6AVAS has four admins. Each perform a unique task essential to the server's survival. You can see them by running /admin."));
				break;
			case 11:
				Bukkit.spigot().broadcast(new TextComponent("§6Book dupe §6§ldoes not work §6on AVAS. It has been patched by Mojang and Spigot as of 1.16.5. Please stop asking."));
				break;
			case 12:
				Bukkit.spigot().broadcast(new TextComponent("§6The source code for AVAS is available at https://avas.cc/github."));
				break;
		}
		Scheduler.setLastTaskId("autoAnnounce");
	}
}
