package testDiscordBot.bot.discordapi.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import testDiscordBot.bot.repository.TaskRepository
import testDiscordBot.bot.task.Task

@Service
class MemberApiService(@Autowired val repository: TaskRepository) {

    fun findMemberTaskService(username: String): List<Task> {
        return repository.findAllByUserId(userId = username)
    }

    fun createTaskService(task: Task) {
        repository.save(task)
    }
}