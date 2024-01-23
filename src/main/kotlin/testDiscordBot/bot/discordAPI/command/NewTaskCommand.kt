package testDiscordBot.bot.discordAPI.command

import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import testDiscordBot.bot.discordRepository.TaskRepository

@CommandAnnotation(prefix="!NEW-TASK")
class NewTaskCommand(override val taskRepository: TaskRepository) : MessageCreateCommand() {
    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {
        return CommandResult.reply("This is new command!")
    }
}


