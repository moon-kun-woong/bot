package testDiscordBot.bot.discordAPI.command

import dev.kord.core.entity.Message
import org.springframework.stereotype.Component
import testDiscordBot.bot.discordAPI.Command
import testDiscordBot.bot.discordAPI.UnsupportedCommandException
import testDiscordBot.bot.discordRepository.TaskRepository

@Component
class CommandResolver(
    private val taskRepository: TaskRepository,
) {
    fun resolve(message: Message): Command {
        return when {
            message.content.startsWith("!ADD-TASK") -> AddTaskCommand(taskRepository)
            message.content == "!LIST-TASK" -> ListTaskCommand(taskRepository)
            message.content == "!NEXT-TASK" -> NextTaskCommand(taskRepository)
            else -> throw UnsupportedCommandException(message)
        }
    }
}
