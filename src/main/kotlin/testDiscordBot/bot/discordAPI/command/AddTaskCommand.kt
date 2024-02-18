package testDiscordBot.bot.discordapi.command

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import testDiscordBot.bot.task.Task
import testDiscordBot.bot.repository.TaskRepository

@TaskCommand(prefix = "!ADD-TASK")
class AddTaskCommand(
    override val taskRepository: TaskRepository,
    @Autowired private val aiPrompt: AiPrompt
) : MessageCreateCommand() {
    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {

        val chatMessage = aiPrompt.processAddTask(parameter)

        val mapper = ObjectMapper().registerModules()
        val jsonNode: JsonNode = mapper.readTree(chatMessage)

        jsonNode.map(Task::from).forEach(taskRepository::save)

        return CommandResult.reply("Task 가 추가되었습니다.")
    }
}
