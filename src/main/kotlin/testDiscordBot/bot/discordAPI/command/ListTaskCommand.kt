package testDiscordBot.bot.discordAPI.command

import dev.kord.core.event.message.MessageCreateEvent
import testDiscordBot.bot.discordAPI.Command
import testDiscordBot.bot.discordRepository.TaskRepository

class ListTaskCommand(override val taskRepository: TaskRepository) : Command {
    override suspend fun execute(event: MessageCreateEvent) {
        val message = event.message
        if (message.author?.isBot == true) return

        val userId = message.author!!.username
        val tasks = taskRepository.findAllByUserId(userId = userId)
        val taskList = tasks.joinToString(", \n") { it.content }
        message.channel.createMessage("$userId -> $taskList")
    }
}
