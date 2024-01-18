package testDiscordBot.bot.discordAPI

import dev.kord.core.entity.Message

class UnsupportedCommandException(
    message: Message
) : Exception("Unsupported command invoked, message: $message")

