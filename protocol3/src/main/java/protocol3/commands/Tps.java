package protocol3.commands;

import java.text.DecimalFormat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.LagProcessor;

// TPS check

public class Tps implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		double tps = LagProcessor.getTPS();
		if (tps > 20)
			tps = 20;
		if (tps == 0)
		{
			TextComponent component = new TextComponent(
					"TPS is either extremely low or still processing. Try again later.");
			sender.spigot().sendMessage(component);
		} else
		{
			String tpss = new DecimalFormat("#.##").format(tps);
			double lag = Math.round(100 - ((tps / 20.0D) * 100.0D));
			String lags = new DecimalFormat("#.##").format(lag);
			TextComponent component = new TextComponent(
					"TPS is " + tpss + ", which is " + lags + "% slower than normal.");

			if (lag <= 9)
				component.setColor(ChatColor.GREEN);
			else if (lag <= 25 && lag > 9)
				component.setColor(ChatColor.YELLOW);
			else if (lag <= 50 && lag > 25)
				component.setColor(ChatColor.GOLD);
			else if (lag <= 100 && lag > 50)
				component.setColor(ChatColor.RED);
			else
				component.setColor(ChatColor.LIGHT_PURPLE);

			sender.spigot().sendMessage(component);
		}
		return true;
	}

}
