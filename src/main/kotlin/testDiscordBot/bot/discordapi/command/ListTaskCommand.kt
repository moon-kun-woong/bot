package testDiscordBot.bot.discordapi.command

import testDiscordBot.bot.discordapi.api.LangChainApiController
import testDiscordBot.bot.repository.TaskRepository

@TaskCommand(prefix = "!LIST-TASK")
class ListTaskCommand(override val taskRepository: TaskRepository, private val langChainData : LangChainApiController) : MessageCreateCommand() {
    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {
        val userId = parameter.username
        try {
            val tasks = taskRepository.findAllByUserId(userId = userId)
            if (tasks.isEmpty()) {
                return CommandResult.reply("해당하는 태스크가 검색되지 않습니다.")
            }
            val taskList = tasks.joinToString(", \n") { it.content }
            return CommandResult.reply("${langChainData.requestListCommand(userId)}")
        } catch (e: Exception) {
            return CommandResult.ignore()
        }
    }
}
