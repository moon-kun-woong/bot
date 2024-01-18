package testDiscordBot.bot.discordAPI

import dev.kord.core.event.message.MessageCreateEvent
import testDiscordBot.bot.discordRepository.TaskRepository

interface Command {
    val taskRepository: TaskRepository
    suspend fun execute(event: MessageCreateEvent)
}

