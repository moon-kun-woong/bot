package testDiscordBot.bot.discordapi.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class LangChainApiController() {
    fun requestLangChain(userId:String): String? {
        val restTemplate = RestTemplate()
        val response = restTemplate.getForObject("http://localhost:5000/api/listTask/$userId", String::class.java)
        print(response)

        val objectMapper = ObjectMapper()
        val responseMap: Map<*, *>? = objectMapper.readValue(response, Map::class.java)

        val text: String? = responseMap?.get("text") as? String

        println(text)
        return text
    }
}