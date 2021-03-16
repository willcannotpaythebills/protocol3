package protocol3;

import java.io.IOException;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import protocol3.backend.*;
import protocol3.commands.*;
import protocol3.events.*;
import protocol3.tasks.*;

public class Main extends JavaPlugin implements Listener {
	public static Plugin instance;
	public static OfflinePlayer Top = null;

	@Override
	public void onEnable() {

		instance = this;

		// Required files load
		System.out.println("[protocol3] Creating required files if they do not exist...");
		try {
			FileManager.setup();
		} catch (IOException e) {
			System.out.println("[protocol3] An error occured creating the necessary files.");
		}

		// Load required files
		System.out.println("[protocol3] Loading files..");
		try {
			PlayerMeta.loadDonators();
			PlayerMeta.loadMuted();
			PlayerMeta.loadLagfags();
		} catch (IOException e) {
			System.out.println("[protocol3] An error occured loading files.");
		}

		// Load timers
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new LagProcessor(), 1L, 1L);
		// TODO: cleaner solution?
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoAnnouncer(), 15000L, 15000L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new ProcessPlaytime(), 20L, 20L);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new OnTick(), 1L, 1L);

		// Load listeners
		getServer().getPluginManager().registerEvents(new Chat(), this);
		getServer().getPluginManager().registerEvents(new Connection(), this);
		getServer().getPluginManager().registerEvents(new Move(), this);
		getServer().getPluginManager().registerEvents(new ItemCheckTriggers(), this);
		getServer().getPluginManager().registerEvents(new LagPrevention(), this);
		getServer().getPluginManager().registerEvents(new SpeedLimit(), this);

		// Disable Wither spawn sound
		ProtocolLibrary.getProtocolManager()
				.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.WORLD_EVENT)
				{
					@Override
					public void onPacketSending(PacketEvent event)
					{
						PacketContainer packetContainer = event.getPacket();
						if (packetContainer.getIntegers().read(0) == 1023)
						{
							packetContainer.getBooleans().write(0, false);
						}
					}
				});

		// Enable speed limit
		SpeedLimit.scheduleSlTask();

		// Load commands
		this.getCommand("kit").setExecutor(new Kit());
		this.getCommand("mute").setExecutor(new Mute());
		this.getCommand("dupehand").setExecutor(new DupeHand());
		this.getCommand("vm").setExecutor(new VoteMute());
		this.getCommand("msg").setExecutor(new Message());
		this.getCommand("r").setExecutor(new Reply());
		this.getCommand("say").setExecutor(new Say());
		this.getCommand("discord").setExecutor(new Discord());
		this.getCommand("tps").setExecutor(new Tps());
		this.getCommand("kill").setExecutor(new Kill());
		this.getCommand("setdonator").setExecutor(new SetDonator());
		this.getCommand("about").setExecutor(new About());
		this.getCommand("vote").setExecutor(new Vote());
		this.getCommand("restart").setExecutor(new Restart());
		this.getCommand("sign").setExecutor(new Sign());
		this.getCommand("admin").setExecutor(new Admin());
		this.getCommand("stats").setExecutor(new Stats());
		this.getCommand("redeem").setExecutor(new Redeem());
		this.getCommand("lagfag").setExecutor(new Lagfag());
		this.getCommand("tjm").setExecutor(new ToggleJoinMessages());
		this.getCommand("server").setExecutor(new Server());
		this.getCommand("help").setExecutor(new Help());
	}

	@Override
	public void onDisable()
	{
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
