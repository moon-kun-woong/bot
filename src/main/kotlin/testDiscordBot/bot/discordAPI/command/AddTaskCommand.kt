package testDiscordBot.bot.discordapi.command

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import testDiscordBot.bot.discordapi.api.LangChainApiController
import testDiscordBot.bot.task.Task
import testDiscordBot.bot.repository.TaskRepository

@TaskCommand(prefix = "!ADD-TASK")
class AddTaskCommand(
    override val taskRepository: TaskRepository,
    private val langChainResponse:LangChainApiController
) : MessageCreateCommand() {
    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {

        val chatMessage = langChainResponse.requestAddCommand(parameter)

        val mapper = ObjectMapper().registerModules()
        val jsonNode: JsonNode = mapper.readTree(chatMessage)

        val tasksMapper = jsonNode.get("tasks").map(Task::from)
        tasksMapper.forEach(taskRepository::save)

        return CommandResult.reply("Task 추가 : \n${tasksMapper.map { it.content }.joinToString(" ,\n ")} \n 입니다.")
    }
}
