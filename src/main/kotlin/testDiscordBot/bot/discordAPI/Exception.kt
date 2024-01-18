package testDiscordBot.bot.discordAPI

import dev.kord.core.event.message.MessageCreateEvent

class UnsupportedCommandException(
    event: MessageCreateEvent
) : Exception("Unsupported command invoked, message: ${event.message}")

