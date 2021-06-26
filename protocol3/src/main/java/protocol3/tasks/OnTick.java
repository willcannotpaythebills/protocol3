package protocol3.tasks;

import java.util.TimerTask;

import org.bukkit.Bukkit;

import protocol3.backend.LagProcessor;
import protocol3.backend.Scheduler;
import protocol3.commands.VoteMute;
import protocol3.events.LagPrevention;

// Tps processor
public class OnTick extends TimerTask
{
	public static double lowestTps = 20.0D;
	public static int highestPop = 0;
	public static int dayHighestPop = 0;
	
	@Override
	public void run()
	{
		double tps = LagProcessor.getTPS();
		
		if(LagProcessor.getTPS() < lowestTps) {
			lowestTps = tps;
		}
		
		if(Bukkit.getOnlinePlayers().size() > highestPop) {
			highestPop = Bukkit.getOnlinePlayers().size();
		}
		
		if(Bukkit.getOnlinePlayers().size() > dayHighestPop) {
			dayHighestPop = Bukkit.getOnlinePlayers().size();
		}
		
		VoteMute.processVoteCooldowns();

		Scheduler.setLastTaskId("tickTasks");
	}
}