package testDiscordBot.bot.discordAPI.command

import testDiscordBot.bot.discordRepository.TaskRepository

class ListTaskCommand(override val taskRepository: TaskRepository) : MessageCreateCommand() {
    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {
        val userId = parameter.username
        val tasks = taskRepository.findAllByUserId(userId = userId)
        val taskList = tasks.joinToString(", \n") { it.content }
        return CommandResult.reply("$userId -> $taskList")
    }
}
