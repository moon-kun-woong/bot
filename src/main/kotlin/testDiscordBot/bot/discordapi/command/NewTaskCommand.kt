package testDiscordBot.bot.discordapi.command

import testDiscordBot.bot.discordrepository.TaskRepository

@CommandAnnotation(prefix="!NEW-TASK")
class NewTaskCommand(override val taskRepository: TaskRepository) : MessageCreateCommand() {
    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {
        return CommandResult.reply("This is new command!")
    }
}


