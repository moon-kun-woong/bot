package testDiscordBot.bot.discordapi.command

import dev.kord.core.event.message.MessageCreateEvent
import testDiscordBot.bot.discordapi.CommandAndParameterMismatchedException


data class MessageCreateParameter(
    val isBot: Boolean,
    val username: String,
    val channelName: String,
    val serverName: String,
    val content: String,
    val priority: Int,
) : CommandParameter

abstract class MessageCreateCommand : Command {
    companion object {
        @JvmStatic
        suspend fun buildParameterFrom(event: MessageCreateEvent): MessageCreateParameter {
            val message = event.message
            val author = message.author!!
            val channel = message.getChannel()
            return MessageCreateParameter(
                isBot = author.isBot,
                username = author.username,
                channelName = channel.data.name.value!!,
                serverName = message.getGuild().name,
                content = message.content,
                priority = 0
            )
        }
    }

    override suspend fun execute(parameter: CommandParameter): CommandResult {
        if (parameter !is MessageCreateParameter) throw CommandAndParameterMismatchedException(this, parameter)

        if (parameter.isBot) return CommandResult.ignore()
        return execute(parameter)
    }

    abstract suspend fun execute(parameter: MessageCreateParameter): CommandResult
}
