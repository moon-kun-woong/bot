package testDiscordBot.bot.discordAPI.command

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class CommandAnnotation(val prefix :String)
