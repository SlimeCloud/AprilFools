package de.slimecloud.april.features.april;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import de.slimecloud.april.main.Bot;
import de.slimecloud.april.main.SlimeEmoji;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AprilListener extends ListenerAdapter {
	private final Bot bot;

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if(!event.isFromGuild() || event.getAuthor().isBot() || !event.getMessage().getAttachments().isEmpty() || !event.getMessage().getEmbeds().isEmpty()) return;

		bot.loadGuild(event.getGuild()).getApril().ifPresent(config -> {
			if(config.getMessage() == null) return;
			if(!config.getChannels().isEmpty() && !config.getChannels().contains(event.getChannel().getIdLong())) return;

			String content = event.getMessage().getContentRaw();

			List<String> message = Arrays.asList(content.split("\\W+"));
			List<String> words = Arrays.asList(config.getMessage().toLowerCase().split("\\W+"));

			if(message.stream().map(String::toLowerCase).noneMatch(words::contains)) return;
			event.getMessage().delete().queue();

			getWebhook(event.getChannel() instanceof ThreadChannel ch ? (IWebhookContainer) ch.getParentChannel() : (IWebhookContainer) event.getChannel()).queue(webhook -> {
				if(event.getChannel() instanceof ThreadChannel tc) webhook = webhook.onThread(tc.getIdLong());

				webhook.send(new WebhookMessageBuilder()
						.setUsername(event.getMember().getEffectiveName())
						.setAvatarUrl(event.getMember().getEffectiveAvatarUrl())
						.setContent(StringUtils.abbreviate(
								content.replaceAll(
										"(?i)(?<=\\W|_|^)(?<word>" + words.stream().map(Pattern::quote).collect(Collectors.joining("|")) + ")(?=\\W|_|$)",
										Matcher.quoteReplacement(SlimeEmoji.SUS.getEmoji(event.getGuild()).getFormatted())
								), Message.MAX_CONTENT_LENGTH
						))
						.build()
				);
			});
		});
	}

	@NotNull
	private RestAction<JDAWebhookClient> getWebhook(@NotNull IWebhookContainer channel) {
		return channel.retrieveWebhooks().flatMap(hooks -> hooks.stream()
				.filter(w -> w.getName().equals("1. April " + channel.getJDA().getSelfUser().getIdLong()))
				.findAny()
				.map(bot::wrap)
				.orElse(channel.createWebhook("1. April " + channel.getJDA().getSelfUser().getIdLong()))
		).map(w -> JDAWebhookClient.withUrl(w.getUrl()));
	}
}
