package testDiscordBot.bot.discordAPI.command

import testDiscordBot.bot.discordRepository.TaskRepository

interface Command {
    val taskRepository: TaskRepository
    suspend fun execute(parameter: CommandParameter): CommandResult
}

interface CommandParameter

enum class CommandResultType { NONE, IGNORE, REPLY }
data class CommandResult(val type: CommandResultType, val message: String) {
    companion object {
        @JvmStatic
        fun ignore() = CommandResult(CommandResultType.IGNORE, "")

        @JvmStatic
        fun reply(message: String) = CommandResult(CommandResultType.REPLY, message)
    }
}
