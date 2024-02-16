package testDiscordBot.bot.discordAPI.command

import dev.kord.core.entity.Message
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import testDiscordBot.bot.discordAPI.UnsupportedCommandException

@Component
class CommandResolver(
    private val ac: ApplicationContext,
) {
    fun findBeanByCommandAnnotation(): Map<String?, Command> {

        val beansWithAnnotation = ac.getBeansWithAnnotation(TaskCommand::class.java)
        return beansWithAnnotation.values.mapNotNull { it as Command }
            .associateBy { it::class.java.getAnnotation(TaskCommand::class.java)?.prefix }
    }

    fun resolve(message: Message): Command {
        val writeCommand = message.content.split("\\s+".toRegex())[0]
        val commandBeans = findBeanByCommandAnnotation()

        println("writeCommand ---->>$writeCommand")
        println("commandBeans ---->>$commandBeans")

        return commandBeans[writeCommand] ?: throw UnsupportedCommandException(message)
    }
}
