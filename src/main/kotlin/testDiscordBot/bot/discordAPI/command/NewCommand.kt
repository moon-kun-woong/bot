package testDiscordBot.bot.discordAPI.command

import testDiscordBot.bot.discordRepository.TaskRepository

@CommandAnnotation(prefix="!NEW-TASK")
class NewCommand(override val taskRepository: TaskRepository) : MessageCreateCommand() {
    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {
        return CommandResult.reply("This is new command!")
    }
}