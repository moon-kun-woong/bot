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
class AddTaskCommand(override val taskRepository: TaskRepository,
                    @Autowired private val openAiAPI: OpenAiAPI
    ) : MessageCreateCommand() {
    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {

        val chatMessage = openAiAPI.processNlpForTask(parameter)
        val jsonGroupData = "[" + chatMessage + "]"

        val mapper = ObjectMapper().registerModules()
        val jsonNode : JsonNode  = mapper.readTree(jsonGroupData)

        jsonNode.forEach { jsonData ->

            val userId = jsonData["userId"].asText()
            val channelName = jsonData["channelName"].asText()
            val serverName = jsonData["serverName"].asText()
            val content = jsonData["content"].asText()
            val priority = jsonData["priority"].asInt()

            val task = Task(
                userId = userId,
                serverName = serverName,
                channelName = channelName,
                content = content,
                priority = priority
            )

            taskRepository.save(task)
        }

        return CommandResult.reply("Task 가 추가되었습니다.")
    }
}
