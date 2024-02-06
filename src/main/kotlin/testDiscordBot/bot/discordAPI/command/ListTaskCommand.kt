package testDiscordBot.bot.discordAPI.command

import org.springframework.beans.factory.annotation.Autowired
import testDiscordBot.bot.discordRepository.TaskRepository

@CommandAnnotation(prefix = "!LIST-TASK")
class ListTaskCommand(override val taskRepository: TaskRepository,
                    @Autowired private val openAiAPI: OpenAiAPI
    ) : MessageCreateCommand() {
    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {

        val userId = parameter.username
        val tasks = taskRepository.findAllByUserId(userId = userId).toString()
        val openAiProcessingData = openAiAPI.processFindTaskList(tasks)

        return CommandResult.reply(openAiProcessingData)
    }
}
