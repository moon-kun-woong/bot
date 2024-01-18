package testDiscordBot.bot.discordAPI

import dev.kord.core.entity.Message
import testDiscordBot.bot.discordAPI.command.Command
import testDiscordBot.bot.discordAPI.command.CommandParameter

class UnsupportedCommandException(
    message: Message
) : Exception("Unsupported command invoked, message: $message")

class CommandAndParameterMismatchedException(
    command: Command,
    parameter: CommandParameter,
) : Exception("The parameter($parameter) is not allowed this command($command)")
