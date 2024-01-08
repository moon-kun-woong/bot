package testDiscordBot.bot.discordAPI

import dev.kord.core.Kord
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import testDiscordBot.bot.discordEntity.TaskEntity
import testDiscordBot.bot.discordRepository.BotRepository

@Service
class DiscordStart {

    @Autowired
    private lateinit var botRepository: BotRepository


    // 초기화를 진행해야함.
    // 이때 코틀린에는 lateinit 와 lazy 가 있는데 스터디 ㄱㄱ..
    // lateinit는 지연 초기화...? botStart 가 호출 될 때 초기화 함. var는 값이 변경 될 수 있다는 변수 선언. val 은 바꿀 값 선언.
    private lateinit var kord: Kord

    // suspend 가 무엇인가?
    // suspend 는 비동기 함수로서 코루틴 내에서 실행
    // 코루틴이란? 비동기적으로 실행되는 코드를 간소화 시키는 동시 실행 설계 패턴.(Jetpack ??)
    suspend fun botStart() {

        // 이 코드의 val 는 불필요. // val kord = Kord("MTE5MjgwMzczNjI1NTQ3OTgwOA.GXufRJ.96IHaByUfJzhfJEycNxyGtqzAFlqhjX_FPZ-jc")
        kord = Kord("MTE5MjgwMzczNjI1NTQ3OTgwOA.GXufRJ.96IHaByUfJzhfJEycNxyGtqzAFlqhjX_FPZ-jc")

        // 초기화? 인것인가?
        kord.on<MessageCreateEvent> {
            if (message.author?.isBot == true) return@on
            if (message.content == "!대답") message.channel.createMessage("응애~")
        }

        kord.login {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
        }
    }
    
    // task를 추가하는 작업 개시. // Entity 에서 만드는건지 여기에 만드는 건지 몰라서 일단 여기 만듦
    // 일단 들어갈 값들을 적어준다. 그 후 만들어둔 인터페이스Repository에 SAVE!
    suspend fun addTask(writerId:String, taskName: String, channelName: String, serverName: String, content: String) {
        val task = TaskEntity(
            writerId = writerId,
            taskName = taskName,
            channelName = channelName,
            serverName = serverName,
            content = content
        )
        botRepository.save(task)
    }

    // 이제 리스트로 만들것.
    suspend fun listTasks(writerId: String): String {
        val tasks = botRepository.findAllByWriterId(writerId)
        return tasks.joinToString(",") { it.taskId.toString() }
    }


    // 코루틴 스코프에서 실행되야함
    // 어플이 준비되면, botStartCoroutine을 자동으로 호출! runBlocking 은 코루틴을 시작하는 방법.
    @EventListener(ApplicationReadyEvent::class)
    fun botStartCoroutin() {
        runBlocking {
            this.run { botStart() }
        }
    }


}