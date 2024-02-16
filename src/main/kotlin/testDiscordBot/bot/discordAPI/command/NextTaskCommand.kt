package testDiscordBot.bot.discordAPI.command

import org.springframework.beans.factory.annotation.Autowired
import testDiscordBot.bot.discordRepository.TaskRepository

@TaskCommand(prefix = "!NEXT-TASK")
class NextTaskCommand(override val taskRepository: TaskRepository,
                        @Autowired private val aiPromptWriter: AiPromptWriter
    ) : MessageCreateCommand() {
    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {

        val userId = parameter.username

        val tasks = taskRepository.findAllByUserId(userId = userId)
        if (tasks.isEmpty()) return CommandResult.reply("아직 추가된 태스크가 없습니다.")

        val highestPriorities = tasks.maxBy { it.priority }
        val task = tasks
            .filter { it.priority == highestPriorities.priority }
            .maxBy { it.createdAt }

        val openAiProcessingData = aiPromptWriter.processFindNextTask(task)


        return CommandResult.reply(openAiProcessingData)
    }
}
