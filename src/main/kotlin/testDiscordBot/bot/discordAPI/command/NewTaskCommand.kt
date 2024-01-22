package testDiscordBot.bot.discordAPI.command

import org.springframework.stereotype.Component
import testDiscordBot.bot.discordRepository.TaskRepository

@CommandAnnotation(prefix="!NEW-TASK")
@Component
class NewTaskCommand(override val taskRepository: TaskRepository) : MessageCreateCommand() {

    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {
        return CommandResult.reply("This is new command!")
    }
}