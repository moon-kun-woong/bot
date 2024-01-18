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
import testDiscordBot.bot.discordAPI.command.CommandResolver

@Service
class Bot(
    @Value("\${discord.bot.token}")
    private val discordToken: String,
    private val commandResolver: CommandResolver,
) {

    private suspend fun handleMessageCreateEvent(event: MessageCreateEvent) {
        val command = commandResolver.resolve(event.message)
        command.execute(event)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun botStartCoroutine() {
        runBlocking {
            this.run { botStart() }
        }
    }

    private suspend fun botStart() {
        val kord = Kord(discordToken)
        kord.on<MessageCreateEvent>(consumer = ::handleMessageCreateEvent)
        kord.login {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
        }
    }
}
