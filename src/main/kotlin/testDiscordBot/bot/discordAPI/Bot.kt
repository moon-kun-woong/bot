package testDiscordBot.bot.discordAPI

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import testDiscordBot.bot.discordEntity.Task
import testDiscordBot.bot.discordRepository.BotRepository

@Service
class Bot { // Invoker

    @Autowired
    private lateinit var botRepository: BotRepository

    private lateinit var kord: Kord

    @Value("\${discord.bot.token}")
    private lateinit var discordToken: String



    suspend fun botStart(){
        kord = Kord(discordToken)

        kord.login {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
        }
    }

    fun addTaskCommand() {

        kord.on<MessageCreateEvent> {
            if (message.author?.isBot == true) return@on

            val userId = message.author!!.username
            val channelName = message.getChannel().data.name.value.toString()
            val serverName = message.getGuild().name

            val regex = Regex("!ADD-TASK(?:\\s+--p\\s+(\\d+))?\\s+(.+)")
            val matches = regex.find(message.content)

            if (matches != null) {
                val priority = matches.groupValues[1].toIntOrNull() ?: 0
                val content = matches.groupValues[2].trim()

                addTask(userId, serverName, channelName, content, priority)
                message.channel.createMessage("Task 가 추가 되었습니다.")
            } else {
                message.channel.createMessage("잘못된 형식.")
            }
        }
    }

    fun addTask(userId: String, serverName: String, channelName: String, content: String ,priority: Int) {
        println(content)
        val task =
            Task(userId = userId, serverName = serverName, channelName = channelName, content = content, priority = priority)
        botRepository.save(task)

    }

    fun listTaskCommand(){
        kord.on<MessageCreateEvent> {
            if (message.author?.isBot == true) return@on

            val userId = message.author!!.username
            val tasks = botRepository.findAllByUserId(userId = userId)
            val taskList = tasks.joinToString(", \n") { it.content }
            message.channel.createMessage("$userId -> $taskList")
        }
    }


    fun nextTaskCommand() {
        kord.on<MessageCreateEvent> {
            if (message.author?.isBot == true) return@on

            val userId = message.author!!.username
            val tasks = botRepository.findAllByUserId(userId = userId)

            val highestPriorities = tasks.maxByOrNull { it.priority }

            val bestImportentTask = if (highestPriorities != null) {
                tasks.filter { it.priority == highestPriorities.priority }
                    .maxByOrNull { it.createdAt }
            } else {
                null
            }

            if (bestImportentTask != null) {
                message.channel.createMessage("현재 $userId 의 가장 중요한 사항: ${bestImportentTask.content} \n --p ${bestImportentTask.priority}")
            } else {
                message.channel.createMessage("아직 추가된 중요사항이 없습니다.")
            }
        }
    }
}