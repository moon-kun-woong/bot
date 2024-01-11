package testDiscordBot.bot.discordAPI

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import testDiscordBot.bot.discordEntity.Task
import testDiscordBot.bot.discordRepository.BotRepository

@Service
class DiscordStartService {

    @Autowired
    private lateinit var botRepository: BotRepository

    private lateinit var kord: Kord

    @Value("\${discord.bot.token}")
    private lateinit var discordToken: String


    suspend fun botStart() {
        kord = Kord(discordToken)

        kord.on<MessageCreateEvent> {
            if (message.author?.isBot == true) return@on

            when {
                message.content.startsWith("!ADD-TASK") -> {

                    val userId = message.author!!.username
                    val channelName = message.getChannel().data.name.value.toString()
                    val serverName = message.getGuild().name

                    val regex = Regex("!ADD-TASK(?:\\s+--p\\s+(\\d+))?\\s+(.+)")
                    val matches = regex.find(message.content)

                    if (matches != null) {
                        val priority = matches.groupValues[1].toIntOrNull() ?: 0
                        val content = matches.groupValues[2].trim()

                        addTask(userId, serverName, channelName, content, priority)
                    } else {
                        // 처리할 수 없는 형식의 명령어일 경우에 대한 처리
                        message.channel.createMessage("잘못된 형식의 명령어입니다.")
                    }
                }
                message.content == "!LIST-TASK" -> {
                    val userId = message.author!!.username
                    val tasks = botRepository.findAllByUserId(userId = userId)
                    val taskList = tasks.joinToString(", \n") { it.content }
                    message.channel.createMessage("$userId -> $taskList")
                }
            }
        }

        kord.login {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
        }
    }

    suspend fun addTask(userId: String, serverName: String, channelName: String, content: String ,priority: Int) {
        println(content)
        val task =
            Task(userId = userId, serverName = serverName, channelName = channelName, content = content, priority = priority)
        botRepository.save(task)

    }


    @EventListener(ApplicationReadyEvent::class)
    fun botStartCoroutine() {
        runBlocking {
            this.run { botStart() }
        }
    }

}

