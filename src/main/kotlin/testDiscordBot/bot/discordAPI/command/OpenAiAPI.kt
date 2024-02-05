package testDiscordBot.bot.discordAPI.command

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.serialization.json.JsonObject
import org.json.simple.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.time.Duration.Companion.seconds

@Component
class OpenAiAPI (
    @Value("\${openAi.key}") private val token: String,
    @Value("\${openAi.model}") private val model: String,
    @Value("\${openAi.organization}") private val organization: String,
) {
    private lateinit var openAI: OpenAI

    init {
        openAI = OpenAI(
            token=token,
            organization=organization,
            timeout = Timeout(socket = 30.seconds)
        )
    }

    suspend fun processNlpForTask(event : MessageCreateParameter): String {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content =
                        """
                            너는 스캐쥴을 관리해주는 Task 매니져야. 
                            사용자가 업무나 해야할 일에 대한 내용 또는 문제에서 해결해야하는 일이 발생했을때 해야만 하는 일이 입력하면 문자를 받고 Task를 만들텐데 이것을 '업무할당요청' 이라고 할게. 
                            업무할당요청이 올 경우 다음과 같은 절차를 진행해줘. 
                            1. Task 를 만들때 JSON 데이터로 만들거야. 이것을 시작으로 '--p ?'(?는 업무의 중요도이면서 레벨) 와 '내용', 'userId'등이 있을거야. 자세한건 Assistant를 확인해줘.
                            너가 만들 것을 예를 들면 사용자가 업무할당요청을 할 경우에 예를 들면 '안녕하세요 문건웅입니다. 현재 진행되고 있는 업무 중 발생된 500 에러를 잡지 못하고 있네요. 최대한 빠른 시일에 해주셨으면 하는데 아마 resolver 에서 잘못 작성된 코드가 있는 것 같으니 수정 
                            부탁드립니다. 중요도는 2.' 라고 하면 
                            너는 
                            
                            '{
                            "userId":"${event.username}",
                            "serverName":"${event.serverName}",
                            "channelName":"${event.channelName}",
                            "content":"resolver 오작동 코드에서 발생한 500에러 발원지 코드 수정 작업.",
                            "priority":2
                            }'
                            
                            와 같은 JSON 데이터를 넘겨주면 되.                            
                            이건 예시니까 이대로 출력하란게 아닌 사용자가 방금 보낸 자연어의 내용을 바탕으로 하라는 뜻임을 명심해.
                            2. Task 를 만들어줘. 요청을 여러 Task 로 나누는게 유리한 상황 일때에는 분할을 해주고 유리한 상황은 너가 주체적으로 진행해줘.
                            예를 들면 '숙제도 해야하고 코딩도 해야하네~' 라고 사용자가 말한다면 
                            '{
                            "userId":"${event.username}",
                            "serverName":"${event.serverName}",
                            "channelName":"${event.channelName}",
                            "content":"숙제 완료하기.",
                            "priority":2
                            } , 
                            {
                            "userId":"${event.username}",
                            "serverName":"${event.serverName}",
                            "channelName":"${event.channelName}",
                            "content":"코딩 작업 실행.",
                            "priority":1
                            }' 
                            이런식으로 나눠서 작성해 줘야만해. 아랫칸으로 옮겨서 적어줘야해. 이때 JSON 데이터 사이에 ,(작은 따옴표) 를 무조건 적어줘.
                            3. Task 해결에 우선순위를 0~4 까지의 레벨로 설정해줘. 숫자가 높을수록 우선순위가 높은거고 일반적인 관점에서 생각해서 우선순위를 추론하되 모를 경우에는 사용자에게 다시 한번 물어보는 쪽으로 진행해줘.
                            위의 과정이 다 완료 되었다면 예시에서 보여줬던 JSON 방식으로 데이터를 만들어서 반환해줘.
                        """
                ),
                ChatMessage(
                    role = ChatRole.Assistant,
                    content =
                        """
                            {
                            "userId":"${event.username}",
                            "serverName":"${event.serverName}",
                            "channelName":"${event.channelName}",
                            "content":".......",
                            "priority":"......"
                            }
                        """
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = "${event.content}"
                )
            )
        )

        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        val jsonStringdata = completion.choices[0].message.messageContent.toString().trimIndent()
        val jsonContent = jsonStringdata.substringAfter("{").substringBeforeLast("}")
        val realJsonData = "{" + jsonContent + "}"

        return realJsonData
    }
}