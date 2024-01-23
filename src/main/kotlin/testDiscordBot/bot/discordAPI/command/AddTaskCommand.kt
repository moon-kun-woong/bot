package testDiscordBot.bot.discordAPI.command

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import testDiscordBot.bot.discordEntity.Task
import testDiscordBot.bot.discordRepository.TaskRepository

@CommandAnnotation(prefix = "!ADD-TASK")
class AddTaskCommand(override val taskRepository: TaskRepository) : MessageCreateCommand() {
    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {

        val userId = parameter.username
        val channelName = parameter.channelName
        val serverName = parameter.serverName

        val regex = Regex("!ADD-TASK(?:\\s+--p\\s+(\\d+))?\\s+(.+)")
        val matches = regex.find(parameter.content) ?: return CommandResult.reply("잘못된 형식")

        val (_, priority, content) = matches.groupValues
        val task = Task(
            userId = userId,
            serverName = serverName,
            channelName = channelName,
            content = content.trim(),
            priority = priority.toIntOrNull() ?: 0
        )
        taskRepository.save(task)

        return CommandResult.reply("Task 가 추가되었습니다.")
    }
}
