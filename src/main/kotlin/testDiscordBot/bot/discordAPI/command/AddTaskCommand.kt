package testDiscordBot.bot.discordAPI.command

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import testDiscordBot.bot.discordEntity.Task
import testDiscordBot.bot.discordRepository.TaskRepository

@TaskCommand(prefix = "!ADD-TASK")
class AddTaskCommand(override val taskRepository: TaskRepository,
                    @Autowired private val aiPromptWriter: AiPromptWriter
    ) : MessageCreateCommand() {
    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {

        val chatMessage = aiPromptWriter.processNlpForTask(parameter)
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
