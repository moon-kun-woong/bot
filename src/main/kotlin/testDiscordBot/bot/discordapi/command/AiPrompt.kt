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

    @Suppress("UNCHECKED_CAST")
    fun resolveChatMessageFromResource(chatMessageResource: String): List<MessageItem> {
        val yaml = Yaml()
        val inputStream: InputStream? = this.javaClass.classLoader.getResourceAsStream(chatMessageResource)
        // !Auto casting 안됨, 추후 리팩토링 필요.
        val obj = yaml.load<Map<String, Any>>(inputStream)
        val messages = obj["messages"] as List<*>
        return messages.map { MessageItem.from(it as Map<String, String>)}
        // ~Auto casting 안됨, 추후 리팩토링 필요.
    }

    data class MessageItem(val title: String, val role: String, val content: String) {
        private fun resolveContentWithEvent(event: MessageCreateParameter): String {
            return content
                .replace("{{username}}", event.username)
                .replace("{{serverName}}", event.serverName)
                .replace("{{channelName}}", event.channelName)
        }

        fun toChatMessage(event: MessageCreateParameter) =
            ChatMessage(role = Role(role), content = resolveContentWithEvent(event))

        companion object {
            @JvmStatic
            fun from(raw: Map<String, String>) = MessageItem(
                title = raw.getOrDefault("title", ""),
                role= raw.getOrDefault("role", ""),
                content = raw.getOrDefault("content", "")
            )
        }
    }

    suspend fun processAddTask(event: MessageCreateParameter): String {
        val promptResource = resolveChatMessageFromResource("convert-structured-task-prompt.yaml")
        val prompts = promptResource.map { it.toChatMessage(event) }
        println("DEBUG: AddTask Prompt\n=== ${prompts.joinToString("\n=\n")}\n===")
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model),
            messages = listOf(
                *prompts.toTypedArray(),
                ChatMessage(
                    role = ChatRole.User,
                    content = event.toString()
                )
            )
        )

        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        println("DEBUG: AddTask Response - ${completion.choices.joinToString(",\n")}")
        val aiResponse = completion.choices[0].message.content.toString()

        return aiResponse
    }

    suspend fun processFindNextTask(event: Task): String {
        val promptResource = resolveChatMessageFromResource("next-task-prompt.yaml")
        val prompts = promptResource.map { ChatMessage(role = Role(it.role), content = it.content) }
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model),
            messages = listOf(
                *prompts.toTypedArray(),
                ChatMessage(
                    role = ChatRole.User,
                    content = "$event"
                )
            )
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        println("DEBUG: AddTask Response - ${completion.choices.joinToString(",\n")}")
        val processedContent = completion.choices[0].message.content.toString().trimIndent()

        return processedContent
    }
}