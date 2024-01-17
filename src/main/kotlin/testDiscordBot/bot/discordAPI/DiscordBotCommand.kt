package testDiscordBot.bot.discordAPI

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener


interface DiscordBotCommand { // Command

    val nextTaskCommand: Bot
    val listTaskCommand: Bot
    val addTaskCommand: Bot

    fun execute() {

        lateinit var kord: Kord
        lateinit var bot: Bot


        kord.on<MessageCreateEvent> {
            if (message.author?.isBot == true) return@on

            when {
                message.content.startsWith("!ADD-TASK") -> {
                    bot.addTaskCommand()
                }
                message.content == "!LIST-TASK" -> {
                    bot.listTaskCommand()
                }
                message.content == "!NEXT-TASK" -> {
                    bot.nextTaskCommand()
                }
            }
        }
    }

}

