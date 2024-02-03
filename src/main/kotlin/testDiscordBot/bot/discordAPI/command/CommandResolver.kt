package testDiscordBot.bot.discordAPI.command

import dev.kord.core.entity.Message
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import testDiscordBot.bot.discordAPI.UnsupportedCommandException

@Component
class CommandResolver(
    private val ac: ApplicationContext,
    private val ai: OpenAiAPI
) {
    fun findBeanByCommandAnnotation(): Map<String?, Command> {

        val beansWithAnnotation = ac.getBeansWithAnnotation(CommandAnnotation::class.java)
        return beansWithAnnotation.values.mapNotNull { it as Command }
            .associateBy { it::class.java.getAnnotation(CommandAnnotation::class.java)?.prefix }
    }

    suspend fun aiTextFilter(message: Message): String? {
        val aiText = ai.processNlpForTask(message).toString()
        val regex = Regex("TextContent\\(content=(.*?)\\)")
        val aiResponse = regex.find(aiText)?.groupValues?.get(1)

        println("aiResponse ---->>$aiResponse")

        return aiResponse
    }

    suspend fun resolve(message: Message): Command {
        val writeCommand = aiTextFilter(message).toString().split("\\s+".toRegex())[0]
        val commandBeans = findBeanByCommandAnnotation()

        println("writeCommand ---->>$writeCommand")
        println("commandBeans ---->>$commandBeans")

        return commandBeans[writeCommand] ?: throw UnsupportedCommandException(message)
    }
}
