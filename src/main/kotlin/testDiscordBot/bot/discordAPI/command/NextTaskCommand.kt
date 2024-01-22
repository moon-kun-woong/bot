package testDiscordBot.bot.discordAPI.command

import org.springframework.stereotype.Component
import testDiscordBot.bot.discordRepository.TaskRepository

@CommandAnnotation(prefix = "!NEXT-TASK")
@Component
class NextTaskCommand(override val taskRepository: TaskRepository) : MessageCreateCommand() {

    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {
        val userId = parameter.username

        val tasks = taskRepository.findAllByUserId(userId = userId)
        if (tasks.isEmpty()) return CommandResult.reply("아직 추가된 태스크가 없습니다.")

        val highestPriorities = tasks.maxBy { it.priority }
        val task = tasks
            .filter { it.priority == highestPriorities.priority }
            .maxBy { it.createdAt }

        return CommandResult.reply("현재 $userId 의 가장 중요한 사항: ${task.content} \n --p ${task.priority}")
    }
}
