package testDiscordBot.bot.discordAPI.command

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(BeanRegistScanner::class)
class BeanRegistScannerConfig