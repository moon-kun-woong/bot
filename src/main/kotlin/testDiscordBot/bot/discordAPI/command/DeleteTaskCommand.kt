package testDiscordBot.bot.discordapi.command

import testDiscordBot.bot.repository.TaskRepository

@TaskCommand(prefix = "!DELETE-TASK")
class DeleteTaskCommand (override val taskRepository: TaskRepository) : MessageCreateCommand() {

    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {

        val deleteTaskIdValue = parameter.content
        val regex = Regex("!DELETE-TASK?\\s+(.+)")
        val match = regex.find(deleteTaskIdValue)?: return CommandResult.reply("삭제할 TaskId 를 정확히 입력해주세요.")
        val taskId = match.groupValues[1].toLong()

        taskRepository.deleteById(taskId)

        return CommandResult.reply("TaskId = [ $taskId ] 가 삭제 되었습니다.")
    }
}