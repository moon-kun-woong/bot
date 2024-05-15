package testDiscordBot.bot.discordapi.api

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import testDiscordBot.bot.discordapi.command.MessageCreateParameter
import testDiscordBot.bot.task.Task

@RestController
@RequestMapping("/api/task")
class MemberApiController(@Autowired val taskService: MemberApiService) {

    @GetMapping("/get/{username}")
    fun findMemberTask(@PathVariable username: String): List<Task> {
        return taskService.findMemberTaskService(username)
    }

    @GetMapping("/parameter/{username}")
    fun findMemberParameter(event: MessageCreateParameter): JsonNode {
        val mapper = ObjectMapper().registerModules()
        val jsonNode: JsonNode = mapper.readTree(event.toString())
        return jsonNode
    }
}