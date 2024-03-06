package testDiscordBot.bot.discordapi.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import testDiscordBot.bot.task.Task

@RestController
@RequestMapping("/api/task")
class MemberApiController(@Autowired val taskService: MemberApiService) {
    @PostMapping("/create")
    fun createTask(@RequestBody task: Task) {
        taskService.createTaskService(task)
    }
}