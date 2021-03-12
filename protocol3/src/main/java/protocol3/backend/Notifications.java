package protocol3.backend;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.rest.util.Color;
import org.bukkit.Bukkit;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

public class Notifications {

	private static String[] facts;

	private static final Random r = new Random();

	public Notifications() {
		if (!Config.getValue("analytics.enabled").equals("1"))
			return;

		facts = loadFacts();

		DiscordClientBuilder.create(Config.getValue("analytics.bot_id")).build().login().subscribe((client) -> {

			client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> {
				final User self = event.getSelf();
				System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
			});

			client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
					.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
				.filter(message -> message.getContent().equalsIgnoreCase("!tps")).flatMap(Message::getChannel)
				.flatMap(channel -> channel
					.createMessage(//"TPS is currently " + new DecimalFormat("#.##").format(LagProcessor.getTPS()))
						messageSpec -> messageSpec.setEmbed(embedSpec ->
							embedSpec.setTitle("AVAS Server | Server TPS")
									 .setAuthor("AVAS Bot", "https://avas.cc/","https://avas.cc/favicon.png")
									 .setColor(Color.RUBY)
									 .addField("Server Info",
											 "TPS is currently " + new DecimalFormat("#.##").format(LagProcessor.getTPS()),
											 false)
									 .addField("\u200B",  "\u200B", false)
									 .addField("Current Player Count",
											 		 new DecimalFormat("##").format(Bukkit.getOnlinePlayers().size()) + "/1",
											   true)
									 .addField("Server Up Time",  Utilities.calculateTime(ServerMeta.getUptime()), true)
					))).subscribe();

			client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
					.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
					.filter(message -> message.getContent().equalsIgnoreCase("!facts"))
					.filter(message -> Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
					.flatMap(Message::getChannel)
					.flatMap(channel -> channel
							.createMessage(//facts[r.nextInt(facts.length)])
									messageSpec -> messageSpec.setEmbed(embedSpec ->
											embedSpec.setTitle("AVAS Server | Fun Fact Friday")
													.setAuthor("AVAS Bot", "https://avas.cc/", "https://avas.cc/favicon.png")
													.setColor(Color.RUBY)
													.addField("Regular title field", facts[r.nextInt(facts.length)], false)
													.addField("\u200B", "\u200B", false)
													.addField("Current Player Count",
															new DecimalFormat("##").format(Bukkit.getOnlinePlayers().size()) + "/1",
															true)
													.addField("Server Up Time", Utilities.calculateTime(ServerMeta.getUptime()), true)
									))).subscribe();
		});
	}

	private String[] loadFacts() {
		ArrayList<String> fact = new ArrayList<>();
		Scanner s = new Scanner(Notifications.class.getClassLoader().getResourceAsStream("facts.txt"));
		while (s.hasNextLine()) {
			String line = s.nextLine();
			if (!line.trim().isEmpty()) {  //no empty or space only line
				fact.add(s.nextLine());
			}
		}
		return fact.toArray(new String[]{});
	}

}
