package testDiscordBot.bot.discordapi

import dev.kord.core.entity.Message
import testDiscordBot.bot.discordapi.command.Command
import testDiscordBot.bot.discordapi.command.CommandParameter

class UnsupportedCommandException(
    message: Message
) : Exception("Unsupported command invoked, message: $message")

class CommandAndParameterMismatchedException(
    command: Command,
    parameter: CommandParameter,
) : Exception("The parameter($parameter) is not allowed this command($command)")
