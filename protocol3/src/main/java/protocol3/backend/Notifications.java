package protocol3.backend;

import java.text.DecimalFormat;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

public class Notifications
{

	public Notifications()
	{
		if (Config.getValue("analytics.enabled") != "1")
			return;

		GatewayDiscordClient client = DiscordClientBuilder.create(Config.getValue("analytics.bot_id")).build().login()
				.block();

		client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> {
			final User self = event.getSelf();
			System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
		});

		client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
				.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
				.filter(message -> message.getContent().equalsIgnoreCase("!tps")).flatMap(Message::getChannel)
				.flatMap(channel -> channel
						.createMessage("TPS is currently " + new DecimalFormat("#.##").format(LagProcessor.getTPS())))
				.subscribe();

		client.onDisconnect().block();
	}

}
