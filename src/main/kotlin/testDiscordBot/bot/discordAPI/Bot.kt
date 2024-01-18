package testDiscordBot.bot.discordAPI

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import testDiscordBot.bot.discordAPI.command.AddTaskCommand
import testDiscordBot.bot.discordAPI.command.ListTaskCommand
import testDiscordBot.bot.discordAPI.command.NextTaskCommand
import testDiscordBot.bot.discordRepository.TaskRepository

@Service
class Bot(
    private val taskRepository: TaskRepository,

    @Value("\${discord.bot.token}")
    private val discordToken: String
) {

    @EventListener(ApplicationReadyEvent::class)
    fun botStartCoroutine() {
        runBlocking {
            this.run { botStart() }
        }
    }

    suspend fun botStart() {
        val kord = Kord(discordToken)

        kord.on<MessageCreateEvent> {
            if (message.author?.isBot == true) return@on
            val command = when {
                message.content.startsWith("!ADD-TASK") -> AddTaskCommand(taskRepository)
                message.content == "!LIST-TASK" -> ListTaskCommand(taskRepository)
                message.content == "!NEXT-TASK" -> NextTaskCommand(taskRepository)
                else -> throw UnsupportedCommandException(this)
            }

            command.execute(this)
        }

        kord.login {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
        }
    }
}
