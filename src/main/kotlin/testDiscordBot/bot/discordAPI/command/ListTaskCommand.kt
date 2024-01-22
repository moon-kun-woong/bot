package testDiscordBot.bot.discordAPI.command

import org.springframework.stereotype.Component
import testDiscordBot.bot.discordRepository.TaskRepository

@CommandAnnotation(prefix = "!LIST-TASK")
@Component
class ListTaskCommand(override val taskRepository: TaskRepository) : MessageCreateCommand() {

    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {
        val userId = parameter.username
        val tasks = taskRepository.findAllByUserId(userId = userId)
        val taskList = tasks.joinToString(", \n") { it.content }
        return CommandResult.reply("$userId -> $taskList")
    }
}
