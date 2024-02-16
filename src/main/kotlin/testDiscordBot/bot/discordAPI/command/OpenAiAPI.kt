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

    fun resolveChatMessageFromResource(): Map<String, Any> {
        val yaml = Yaml()
        val inputStream : InputStream? = this.javaClass.classLoader.getResourceAsStream("nlpprompting.yaml")
        val obj : Map<String,Any> = yaml.load(inputStream)
        return obj
    }

    suspend fun processNlpForTask(event : MessageCreateParameter): String {
        val promptData = resolveChatMessageFromResource()
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = promptData["addCommandSystemContent"].toString()
                ),
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = promptData["addCommandAssistant"].toString()
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = event.content
                )
            )
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        val jsonStringData = completion.choices[0].message.messageContent.toString().trimIndent()
        val jsonContent = jsonStringData.substringAfter("{").substringBeforeLast("}")
        val realJsonData = "{$jsonContent}"

        return realJsonData
    }

    suspend fun processFindNextTask(event: Task):String{
        val promptData = resolveChatMessageFromResource()
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model),
            messages =  listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = promptData["nextCommandSystemContent"].toString()
                ),
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = promptData["nextCommandAssistant"].toString()
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = "$event"
                )
            )
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        val processingData = completion.choices[0].message.content.toString().trimIndent()

        return processingData
    }
}