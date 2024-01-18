package testDiscordBot.bot.discordAPI.command

import dev.kord.core.event.message.MessageCreateEvent
import testDiscordBot.bot.discordAPI.Command
import testDiscordBot.bot.discordEntity.Task
import testDiscordBot.bot.discordRepository.TaskRepository

class AddTaskCommand(override val taskRepository: TaskRepository) : Command {
    override suspend fun execute(event: MessageCreateEvent) {
        val message = event.message
        if (message.author?.isBot == true) return

        val userId = message.author!!.username
        val channelName = message.getChannel().data.name.value.toString()
        val serverName = message.getGuild().name

        val regex = Regex("!ADD-TASK(?:\\s+--p\\s+(\\d+))?\\s+(.+)")
        val matches = regex.find(message.content)

        if (matches != null) {
            val priority = matches.groupValues[1].toIntOrNull() ?: 0
            val content = matches.groupValues[2].trim()

            val task = Task(
                userId = userId,
                serverName = serverName,
                channelName = channelName,
                content = content,
                priority = priority
            )
            taskRepository.save(task)
            message.channel.createMessage("Task 가 추가 되었습니다.")
        } else {
            message.channel.createMessage("잘못된 형식.")
        }
    }
}
