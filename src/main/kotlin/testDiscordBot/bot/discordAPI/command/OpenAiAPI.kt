package testDiscordBot.bot.discordAPI.command

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import dev.kord.core.entity.Message
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.time.Duration.Companion.seconds

@Component
class OpenAiAPI(
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

    suspend fun processMakeJsonData(message: Message): Content? {
        val getMessage = ChatCompletionRequest(
            model = ModelId(model),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "받아낸 데이터를 JSON 으로 만들어줘."
                ),
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = """
                        {
                            "taskId" : "",
                            "userId" : "",
                            "serverName" : "",
                            "channelName" : "",
                            "content" : "",
                            "priority" : "" 
                        }
                    """.trimIndent()
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = message.data.content
                )
            )
        )
        return openAI.chatCompletion(getMessage).choices[0].message.messageContent
    }

    suspend fun processNlpForTask(getDiscordMessage: Message): Content? {
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
                        1. Task 를 만드는 커맨드는 '!ADD-TASK' 야. 이것을 시작으로 '--p ?'(?는 업무의 중요도이면서 레벨) 와 '내용' 이 있을거야.
                        너가 만들것을 예를 들면 사용자가 업무할당요청을 할 경우에 예를 들면 '안녕하세요 문건웅입니다. 현재 진행되고 있는 업무 중 발생된 500 에러를 잡지 못하고 있네요. 최대한 빠른 시일에 해주셨으면 하는데 아마 resolver 에서 잘못 작성된 코드가 있는 것 같으니 수정 
                        부탁드립니다. 중요도는 2.' 라고 하면 너는 '!ADD-TASK --p 2 resolver 오작동 코드에서 발생한 500에러 발원지 코드 수정 작업.' 이라고 답해주면 되. 이건 예시니까 이대로 출력하란게 아닌 사용자가 방금 보낸 내용을 바탕으로 하라는 뜻임을 명심해.
                        2. Task 를 만들어줘. 요청을 여러 Task 로 나누는게 유리한 상황 일때에는 분할을 해주고 유리한 상황은 너가 주체적으로 진행해줘.
                        예를 들면 '숙제도 해야하고 코딩도 해야하네~' 라고 사용자가 말한다면 '!ADD-TASK --p 2 숙제 및 과제 수행' , '!ADD-TASK --p 1 코딩 연습' 이런식으로 나눠서 작성해 줘야만해. 아랫칸으로 옮겨서 적어줘야해.
                        3. Task 해결에 우선순위를 0~4 까지의 레벨로 설정해줘. 숫자가 높을수록 우선순위가 높은거고 일반적인 관점에서 생각해서 우선순위를 추론하되 모를 경우에는 사용자에게 다시 한번 물어보는 쪽으로 진행해줘.
                        위의 과정이 다 완료 되었다면 '!ADD-TASK --p ?(? 는 너가 정한 레벨, 또는 사용자가 원하는 레벨) ??(?? 는 너가 요약한 업무의 내용이야.)'  이렇게 반환해줘.
                        
                        만약 사용자가 스케줄과 업무에 관한 질문을 할 경우, 예를 들면 '지금 해야되는 업무가 뭐지?','지금까지 기록된 TASK 를 리스트로 보여줘.' 처럼 말한다면 이것을 우리는
                        '업무리스트' 라고 할거야. 사용자는 다음에 무슨 일을 해야하는지 남은 Task 가 무엇인지 같은 일정 관리에 관련된 내용을 질문 할 수 없어.  
                        사용자의 발언에서 업무리스트라고 판단된 사항은 모두 '!LIST-TASK' 라고 반환해주면 될 것 같아.

                        만약 사용자가 해야할 일 중 가장 중요한 일을 물어볼 경우를 '최우선 과제' 라고 할거야. 예를 들면 '지금 해야하는 일이 뭐지?' '지금 해야하는 최우선 순위가 뭐지?', '가장 급해게 해야하는 일을 알려줘.' 라고 말할 경우 '!NEXT-TASK' 를
                        주는거야. 
                        아래의 형식을 반드시 갖출 것. 다른 방식은 하지마. 이 3가지만 해주면 되.
                        '!ADD-TASK --p 3 .....................',
                        '!LIST-TASK',
                        '!NEXT-TASK'
                    """
                ),
                ChatMessage(
                    role = ChatRole.Assistant,
                    content =
                    """
                        아래의 형식을 반드시 갖출 것.
                        '!ADD-TASK --p 3 .....................',
                        '!LIST-TASK',
                        '!NEXT-TASK'
                    """
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = "${getDiscordMessage}"
                )
            )
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        return completion.choices[0].message.messageContent
    }
}