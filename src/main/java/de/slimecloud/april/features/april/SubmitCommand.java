package de.slimecloud.april.features.april;

import de.mineking.discordutils.commands.ApplicationCommand;
import de.mineking.discordutils.commands.ApplicationCommandMethod;
import de.mineking.discordutils.commands.condition.IRegistrationCondition;
import de.mineking.discordutils.commands.condition.Scope;
import de.mineking.discordutils.commands.context.ICommandContext;
import de.mineking.discordutils.commands.option.Option;
import de.slimecloud.april.config.GuildConfig;
import de.slimecloud.april.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@ApplicationCommand(name = "april", scope = Scope.GUILD)
public class SubmitCommand {
	public final IRegistrationCondition<ICommandContext> condition = (manager, guild, cache) -> cache.<GuildConfig>getState("config").getApril().isPresent();

	@ApplicationCommandMethod
	public void performCommand(@NotNull Bot bot, @NotNull SlashCommandInteractionEvent event,
	                           @Option(description = "Deine Lösung") String satz
	) {
		bot.loadGuild(event.getGuild()).getLogChannel().ifPresentOrElse(
				channel -> {
					channel.sendMessageEmbeds(new EmbedBuilder()
							.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl())
							.setColor(bot.getColor(event.getGuild()))
							.setTitle("April-Event Lösungs-Einreichung")
							.setDescription(satz)
							.setTimestamp(Instant.now())
							.build()
					).queue();
					event.reply("Lösungssatz eingereicht").setEphemeral(true).queue();
				},
				() -> event.reply(":x: Lösung konnte nicht eingereicht werden").setEphemeral(true).queue()
		);
	}
}
