package de.slimecloud.april.features.april;

import de.slimecloud.april.config.ConfigCategory;
import de.slimecloud.april.config.engine.ConfigField;
import de.slimecloud.april.config.engine.ConfigFieldType;
import lombok.Getter;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class AprilConfig extends ConfigCategory {
	@ConfigField(name = "Lösungs-Satz", command = "message", description = "Der notwendige Lösungssatz", type = ConfigFieldType.STRING, required = true)
	private String message;

	@ConfigField(name = "Kanal", command = "channel", description = "Kanal für Einreichungen", type = ConfigFieldType.MESSAGE_CHANNEL)
	private Long channel;

	@NotNull
	public Optional<GuildMessageChannel> getChannel() {
		return Optional.ofNullable(channel).map(id -> bot.getJda().getChannelById(GuildMessageChannel.class, id));
	}
}
