package testDiscordBot.bot.discordAPI

import dev.kord.core.Kord
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import testDiscordBot.bot.discordRepository.BotRepository

class SendMessage( // ConcreteCommand

    override val nextTaskCommand: Bot,
    override val listTaskCommand: Bot,
    override val addTaskCommand: Bot
) :DiscordBotCommand {

    @Autowired
    private lateinit var botRepository: BotRepository

    private lateinit var kord: Kord

    @Value("\${discord.bot.token}")
    private lateinit var discordToken: String

    override fun execute() {
        suspend fun botStart(){
            kord = Kord(discordToken)

            kord.login {
                @OptIn(PrivilegedIntent::class)
                intents += Intent.MessageContent
            }
        }
    }

    @EventListener(ApplicationReadyEvent::class)
    fun botStartCoroutine() {
        runBlocking {
            this.run { execute() }
        }
    }


}