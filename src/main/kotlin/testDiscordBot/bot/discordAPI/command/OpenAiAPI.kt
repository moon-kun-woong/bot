package testDiscordBot.bot.discordAPI.command

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.yaml.snakeyaml.Yaml
import testDiscordBot.bot.discordEntity.Task
import java.io.InputStream
import kotlin.time.Duration.Companion.seconds

@Component
class OpenAiAPI (
    @Value("\${openAi.key}") private val token: String,
    @Value("\${openAi.model}") private val model: String,
    @Value("\${openAi.organization}") private val organization: String,
) {
    private var openAI: OpenAI = OpenAI(
        token=token,
        organization=organization,
        timeout = Timeout(socket = 30.seconds)
    )

    fun resolveChatMessageFromResource(tagger: String = "nlpprompting.yaml"): Map<String, Any> {
        val yaml = Yaml()
        val inputStream : InputStream? = this.javaClass.classLoader.getResourceAsStream(tagger)
        val obj : Map<String,Any> = yaml.load(inputStream)
        return obj
    }

    suspend fun processNlpForTask(event : MessageCreateParameter): String {
        val promptResource = resolveChatMessageFromResource()
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = promptResource["addCommandSystemContent"].toString()
                ),
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = promptResource["addCommandAssistant"].toString()
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = event.toString()
                )
            )
        )

        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        val aiResponse = completion.choices[0].message.messageContent.toString().trimIndent()
        val jsonContent = aiResponse.substringAfter("TextContent(content=").substringBeforeLast(")")

        return jsonContent
    }

    suspend fun processFindNextTask(event: Task):String{
        val promptResource = resolveChatMessageFromResource()
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model),
            messages =  listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = promptResource["nextCommandSystemContent"].toString()
                ),
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = promptResource["nextCommandAssistant"].toString()
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = "$event"
                )
            )
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        val processedContent = completion.choices[0].message.content.toString().trimIndent()

        return processedContent
    }
}