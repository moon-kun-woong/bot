package testDiscordBot.bot.discordController

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import testDiscordBot.bot.discordAPI.DiscordStartService


@Controller
class BotController {

    @Autowired
    private lateinit var botStart :DiscordStartService

    // 생각해보니 jsp나 html 이 없는데..


}