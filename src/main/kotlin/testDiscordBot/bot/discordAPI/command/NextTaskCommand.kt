package testDiscordBot.bot.discordAPI.command

import dev.kord.core.event.message.MessageCreateEvent
import testDiscordBot.bot.discordAPI.Command
import testDiscordBot.bot.discordRepository.TaskRepository

class NextTaskCommand(override val taskRepository: TaskRepository) : Command {
    override suspend fun execute(event: MessageCreateEvent) {
        val message = event.message
        if (message.author?.isBot == true) return

        val userId = message.author!!.username
        val tasks = taskRepository.findAllByUserId(userId = userId)

        val highestPriorities = tasks.maxByOrNull { it.priority }

        val bestImportantTask = if (highestPriorities != null) {
            tasks.filter { it.priority == highestPriorities.priority }
                .maxByOrNull { it.createdAt }
        } else {
            null
        }

        if (bestImportantTask != null) {
            message.channel.createMessage("현재 $userId 의 가장 중요한 사항: ${bestImportantTask.content} \n --p ${bestImportantTask.priority}")
        } else {
            message.channel.createMessage("아직 추가된 중요사항이 없습니다.")
        }
    }
}
