package testDiscordBot.bot.discordapi.command

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.yaml.snakeyaml.Yaml
import testDiscordBot.bot.task.Task
import java.io.InputStream
import kotlin.time.Duration.Companion.seconds

@Component
class AiPrompt(
    @Value("\${openAi.key}") private val token: String,
    @Value("\${openAi.model}") private val model: String,
    @Value("\${openAi.organization}") private val organization: String,
) {
    private var openAI: OpenAI = OpenAI(
        token = token,
        organization = organization,
        timeout = Timeout(socket = 30.seconds)
    )

    suspend fun processAddTask(event: MessageCreateParameter): String {
        return process("convert-structured-task-prompt.yaml", Parameter.from(event))
    }

    suspend fun processFindNextTask(event: Task): String {
        return process("next-task-prompt.yaml", Parameter.from(event))
    }


    @Suppress("UNCHECKED_CAST")
    private fun resolveChatMessageFromResource(chatMessageResource: String): List<MessageItem> {
        val yaml = Yaml()
        val inputStream: InputStream? = this.javaClass.classLoader.getResourceAsStream(chatMessageResource)
        // !Auto casting 안됨, 추후 리팩토링 필요.
        val obj = yaml.load<Map<String, Any>>(inputStream)
        val messages = obj["messages"] as List<*>
        return messages.map { MessageItem.from(it as Map<String, String>) }
        // ~Auto casting 안됨, 추후 리팩토링 필요.
    }

    suspend fun process(file: String, parameter: Parameter): String {
        val promptResource = resolveChatMessageFromResource(file)
        val prompts = promptResource.map { it.toChatMessage(parameter) }
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model),
            messages = listOf(
                *prompts.toTypedArray(),
                ChatMessage(
                    role = ChatRole.User,
                    content = parameter.content
                )
            )
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        println("DEBUG: Response - ${completion.choices.joinToString(",\n")}")
        val processedContent = completion.choices[0].message.content.toString().trimIndent()

        return processedContent
    }

    data class MessageItem(val title: String, val role: String, val content: String) {
        private fun resolveContentWithEvent(parameter: Parameter): String {
            return content
                .replace("{{username}}", parameter.username)
                .replace("{{serverName}}", parameter.serverName)
                .replace("{{channelName}}", parameter.channelName)
                .replace("{{priority}}", parameter.priority.toString())
        }

        fun toChatMessage(parameter: Parameter) =
            ChatMessage(role = Role(role), content = resolveContentWithEvent(parameter))

        companion object {
            @JvmStatic
            fun from(raw: Map<String, String>) = MessageItem(
                title = raw.getOrDefault("title", ""),
                role = raw.getOrDefault("role", ""),
                content = raw.getOrDefault("content", "")
            )
        }
    }

    data class Parameter(
        val username: String,
        val channelName: String,
        val serverName: String,
        val content: String,
        val priority: Int
    ) {
        companion object {
            fun from(event: MessageCreateParameter): Parameter {
                return Parameter(
                    username = event.username,
                    channelName = event.channelName,
                    serverName = event.serverName,
                    content = event.content,
                    priority = event.priority
                )
            }

            fun from(task: Task): Parameter {
                return Parameter(
                    username = task.userId,
                    channelName = task.channelName,
                    serverName = task.serverName,
                    content = task.content,
                    priority = task.priority
                )
            }
        }
    }
}