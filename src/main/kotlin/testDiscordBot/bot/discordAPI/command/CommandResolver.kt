package testDiscordBot.bot.discordAPI.command

import dev.kord.core.entity.Message
import org.springframework.stereotype.Component
import testDiscordBot.bot.discordAPI.UnsupportedCommandException
import testDiscordBot.bot.discordRepository.TaskRepository

@Component
class CommandResolver(
    private val taskRepository: TaskRepository,
    private val commandsText: List<CommandsCollector>
) {
    fun resolve(message: Message): Command {
        val writeCommand = message.content.split("\\s+".toRegex())[0]
        // prefixes : null 을 계속 뱉음. 이게 아닌건가. 이젠 진짜 모르겠다.
        val prefixes =  commandsText.firstOrNull{Command::class.java.getAnnotation(CommandAnnotation::class.java)?.prefix == writeCommand}
        println("writeCommand이다: $writeCommand, prefixes이다: $prefixes")
        return when {
            writeCommand.equals(prefixes) -> prefixes.createCommandText(taskRepository)
            else -> throw UnsupportedCommandException(message)
        }
    }
}
