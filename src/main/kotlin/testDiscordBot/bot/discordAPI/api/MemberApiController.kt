package testDiscordBot.bot.discordapi.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import testDiscordBot.bot.task.Task

@RestController
@RequestMapping("/api/task")
class MemberApiController(@Autowired val taskService: MemberApiService) {

    @GetMapping("/get/{username}")
    fun findMemberTask(@PathVariable username: String): List<Task> {
        return taskService.findMemberTaskService(username)
    }

    @PostMapping("/create")
    fun createTask(@RequestBody task: Task) {
        taskService.createTaskService(task)
    }

    @DeleteMapping("/delete/{taskId}")
    fun deleteTask(taskId: Long) {
        taskService.deleteTaskService(taskId)
    }
}