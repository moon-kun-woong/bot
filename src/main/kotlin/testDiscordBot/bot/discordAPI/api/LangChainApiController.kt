package testDiscordBot.bot.discordapi.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import testDiscordBot.bot.discordapi.command.MessageCreateParameter

@Component
class LangChainApiController() {
    fun requestListCommand(userId: String): String? {
        val restTemplate = RestTemplate()
        val response = restTemplate.getForObject("http://localhost:5000/api/listTask/$userId", String::class.java)

        val objectMapper = ObjectMapper()
        val responseMap: Map<*, *>? = objectMapper.readValue(response, Map::class.java)

        val listCommandResponse: String? = responseMap?.get("text") as? String

        return listCommandResponse
    }

    fun requestAddCommand(parameter: MessageCreateParameter): String? {
        val restTemplate = RestTemplate()

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val objectMapper = ObjectMapper()
        val jsonParameter = objectMapper.writeValueAsString(parameter)

        val request = HttpEntity(jsonParameter, headers)

        val response = restTemplate.postForObject("http://localhost:5000/api/addTask",request , String::class.java)
        val responseMap: Map<*, *>? = objectMapper.readValue(response, Map::class.java)
        val addCommandResponse: String? = responseMap?.get("response") as? String

        return addCommandResponse
    }
}