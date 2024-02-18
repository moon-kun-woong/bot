package testDiscordBot.bot.discordapi.command

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class TaskCommand(val prefix :String)
