package testDiscordBot.bot.discordAPI.command

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType

@Configuration
@ComponentScan(
    basePackages = ["testDiscordBot.bot"],
    includeFilters = [ComponentScan.Filter(type = FilterType.ANNOTATION, classes = [CommandAnnotation::class])]
    )
class ComponentMy {}