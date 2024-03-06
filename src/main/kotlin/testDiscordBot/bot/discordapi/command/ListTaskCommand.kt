package testDiscordBot.bot.discordapi.command

import testDiscordBot.bot.repository.TaskRepository

@TaskCommand(prefix = "!LIST-TASK")
class ListTaskCommand(override val taskRepository: TaskRepository) : MessageCreateCommand() {
    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {

        val userId = parameter.username
        try {
            val tasks = taskRepository.findAllByUserId(userId = userId)
            if (tasks.isEmpty()){
                return CommandResult.reply("해당하는 테스크가 검색되지 않습니다.")
            }
            val taskList = tasks.joinToString(", \n") { it.content }
            return CommandResult.reply("$userId -> \n $taskList")
        }
        catch (e:Exception){
            return CommandResult.ignore()
        }

    }
}
