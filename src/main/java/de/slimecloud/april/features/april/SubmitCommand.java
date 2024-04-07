package de.slimecloud.april.features.april;

import de.mineking.discordutils.commands.AnnotatedCommand;
import de.mineking.discordutils.commands.ApplicationCommand;
import de.mineking.discordutils.commands.ApplicationCommandMethod;
import de.mineking.discordutils.commands.condition.IRegistrationCondition;
import de.mineking.discordutils.commands.condition.Scope;
import de.mineking.discordutils.commands.condition.cooldown.Cooldown;
import de.mineking.discordutils.commands.context.ICommandContext;
import de.mineking.discordutils.commands.option.Option;
import de.mineking.discordutils.events.Listener;
import de.mineking.discordutils.events.handlers.ButtonHandler;
import de.slimecloud.april.config.GuildConfig;
import de.slimecloud.april.main.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@ApplicationCommand(name = "april", description = "Reiche einen Lösungs-Satz für das April-Scherz Event ein", scope = Scope.GUILD)
public class SubmitCommand {
	public final IRegistrationCondition<ICommandContext> condition = (manager, guild, cache) -> cache.<GuildConfig>getState("config").getApril().isPresent();

	@Cooldown(interval = 1, unit = TimeUnit.HOURS, auto = false, identifier = "april")
	public void handleCooldown(@NotNull ICommandContext context) {
		context.getEvent().reply(":timer: :x: Du kannst nur 1 mal jede Stunde eine Lösung Einreichen!").setEphemeral(true).queue();
	}

	@ApplicationCommandMethod
	public void performCommand(@NotNull Bot bot, @NotNull SlashCommandInteractionEvent event,
	                           @Option(description = "Deine Lösung") String satz
	) {
		event.replyEmbeds(new EmbedBuilder()
				.setTitle("Einreichung Bestätigen")
				.setColor(bot.getColor(event.getGuild()))
				.setDescription("Möchtest du diese Lösung wirklich einreichen? Du kannst nur **jede Stunde** eine Lösung einreichen und nur die **letzte wird gewertet**!")
				.addField(
						"Aktuelle Lösung",
						satz,
						false
				)
				.build()
		).addActionRow(Button.success("april:confirm", "Bestätigen")).setEphemeral(true).queue();
	}

	@Listener(type = ButtonHandler.class, filter = "april:confirm")
	public void confirm(@NotNull Bot bot, @NotNull ButtonInteractionEvent event) {
		GuildConfig config = bot.loadGuild(event.getGuild());

		config.getApril().flatMap(AprilConfig::getChannel).or(config::getLogChannel).ifPresentOrElse(
				channel -> {
					AnnotatedCommand.cooldowns.get("april").increment(event.getUser().getIdLong());

					channel.sendMessageEmbeds(new EmbedBuilder()
							.setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl())
							.setColor(bot.getColor(event.getGuild()))
							.setTitle("April-Event Lösungs-Einreichung")
							.setDescription(event.getMessage().getEmbeds().get(0).getFields().get(0).getValue())
							.setTimestamp(Instant.now())
							.build()
					).queue();
					event.editMessage(":white_check_mark: Lösungssatz eingereicht!").setEmbeds().setComponents().queue();
				},
				() -> event.reply(":x: Lösung konnte nicht eingereicht werden").setEphemeral(true).queue()
		);
	}
}
