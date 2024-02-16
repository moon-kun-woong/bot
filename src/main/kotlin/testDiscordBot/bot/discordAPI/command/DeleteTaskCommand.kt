package testDiscordBot.bot.discordAPI.command

import testDiscordBot.bot.discordRepository.TaskRepository

@TaskCommand(prefix = "!DELETE-TASK")
class DeleteTaskCommand (override val taskRepository: TaskRepository) : MessageCreateCommand() {

    override suspend fun execute(parameter: MessageCreateParameter): CommandResult {

        val userId = parameter.username
        val deleteTaskIdValue = parameter.content
        val regex = Regex("!DELETE-TASK?\\s+(.+)")
        val match = regex.find(deleteTaskIdValue)?: return CommandResult.reply("삭제할 TaskId 를 정확히 입력해주세요.")
        val taskId = match.groupValues[1]

        val tasks = taskRepository.findAllByUserId(userId = userId)

        tasks.forEach { task ->
            if (task.taskId.toInt() == taskId.toInt()){
                taskRepository.delete(task)
            }
            else {
                return CommandResult.reply("해당 Task 는 없는 테스크 입니다.")
            }
        }

        return CommandResult.reply("TaskId = [ ${taskId} ] 가 삭제 되었습니다.")
    }
}