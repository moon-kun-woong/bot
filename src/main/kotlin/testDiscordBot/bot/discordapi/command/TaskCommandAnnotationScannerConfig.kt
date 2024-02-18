package testDiscordBot.bot.discordapi.command

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(TaskCommandAnnotationScanner::class)
class TaskCommandAnnotationScannerConfig