import kotlinx.coroutines.runBlocking
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import testDiscordBot.bot.discordAPI.DiscordStart

@Component // service 에 같이 집어 넣어도 괜찮을지도..? 물어보자.
class BotRunner @Autowired constructor(private val discordBotService: DiscordStart) : CommandLineRunner {
    // CommandLineRunner = 인터페이스 -> 어플을 시작할때 run 메서드가 실행된다.
    override fun run(vararg args: String?) { // "vararg" 가변인자
        runBlocking {
            discordBotService.botStart()
        }
    }
}