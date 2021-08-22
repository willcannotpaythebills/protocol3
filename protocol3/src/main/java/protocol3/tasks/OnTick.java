package protocol3.tasks;

import java.util.TimerTask;

import protocol3.backend.LagProcessor;
import protocol3.backend.Scheduler;
import protocol3.commands.VoteMute;

// Tps processor
public class OnTick extends TimerTask
{
	public static double lowestTps = 20.0D;
	@Override
	public void run()
	{
		double tps = LagProcessor.getTPS();
		if(LagProcessor.getTPS() < lowestTps) {
			lowestTps = tps;
		}
		
		VoteMute.processVoteCooldowns();

		Scheduler.setLastTaskId("tickTasks");
	}
}