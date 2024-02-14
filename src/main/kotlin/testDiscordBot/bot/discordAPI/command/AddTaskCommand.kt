package testDiscordBot.bot.discordAPI.command

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import testDiscordBot.bot.discordEntity.Task
import testDiscordBot.bot.discordRepository.TaskRepository

@CommandAnnotation(prefix = "!ADD-TASK")
class AddTaskCommand(
    override val taskRepository: TaskRepository,
    @Autowired private val openAiAPI: OpenAiAPI
) : MessageCreateCommand() {
    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {

        val chatMessage = openAiAPI.processNlpForTask(parameter)
        val jsonGroupData = "[" + chatMessage + "]"

        val mapper = ObjectMapper().registerModules()
        val jsonNode: JsonNode = mapper.readTree(jsonGroupData)

        jsonNode.map(Task::from).forEach(taskRepository::save)

        val tasks = jsonNode.map { Task.from(it) }
        tasks.forEach { taskRepository.save(it) }


        return CommandResult.reply("Task 가 추가되었습니다.")
    }
}
