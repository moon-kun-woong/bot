package testDiscordBot.bot.discordapi.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import testDiscordBot.bot.repository.TaskRepository
import testDiscordBot.bot.task.Task

@Service
class MemberApiService(@Autowired val repository: TaskRepository) {
    fun createTaskService(task: Task) {
        repository.save(task)
    }
}