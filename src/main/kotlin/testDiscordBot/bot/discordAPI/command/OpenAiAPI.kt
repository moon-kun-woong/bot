package testDiscordBot.bot.discordAPI.command

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
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
            timeout = Timeout(socket = 60.seconds)
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
                            사용자가 업무나 해야할 일에 대한 내용 또는 문제에서 해결해야하는 일이 발생했을때, 해야만 하는 일 등을 입력하면 문자를 받고 Task를 만들텐데 이것을 '업무할당요청' 이라고 할게. 
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

    suspend fun processFindTaskList(event: String):String{
        val chatCompletionRequest = ChatCompletionRequest(
            model= ModelId(model),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content =
                        """
                            너는 스캐쥴을 관리해주는 Task 매니져야. 
                            사용자는 너에게 ListData 를 넘겨 줄거야. 너는 사용자에게서 받은 리스트 데이터를 가공하는 작업을 할텐데 이걸 "데이터프로세싱" 이라고 할게.
                            데이터프로세싱을 하기 위해서는 우선 ListData 부터 무엇을 받았는지 알 필요가 있어. 너가 넘겨받은 ListData 는 리포지토리의 검색을 통해 한개의 userId를 가지고 검색한 TaskList 야.
                            userId 로 찾아낸 각각의 값들 즉, taskId, serverName, channelName, content, priority 이 있고 userId 까지 합한다면 총 6개의 데이터가 있지. 
                            userId는 보낸 사용자의 아이디, taskId는 task의 고유 Id 이고, serverName 은 디스코드의 서버 이름, channelName 은 디스코드 서버방의 채널의 이름, 
                            Content 는 Task 의 내용이고, priority 는 이 task 의 중요도 또는 레벨 이라고 해.
                            또한 데이터를 받을 때 예를 들면
                            [Task(taskId=1, userId=moontingting, serverName=LLM-Task-ms, channelName=일반, content=테스트인데 잘되나?>, priority=0), Task(taskId=2, userId=moontingting, serverName=LLM-Task-ms, channelName=일반, content=테스트인데 잘되나?, priority=0)]
                            위와 같은 데이터로 받게 될텐데 [] 는 해당 Task의 List 이고 () 안에 있는것은 해당 Task 의 정보임을 명심해줘. 그리고 Task 의 정보에서 중요한건 userId 와 taskId,Content,priority 야.
                            중요하다 말한 userId 와 taskId,Content,priority 의 정보들을 가지고 TaskList 데이터의 데이터프로세싱을 진행해서 사용자가 보기 유리한 자연어로 바꾸어 주려고 해. 
                            그렇다면 데이터프로세싱을 할 때 다음과 같은 절차를 진행하면 사용자가 보기 편하게 될거야.
                            1. 받아낸 TaskList 데이터를 번호가 있는 리스트로 만들어 내면 편하겠어. 이것의 순서는 TaskList 안의 맨 앞에 있는 taskId 가 가장 작은 순서 부터 큰 순서대로 정렬해서 내주면 될거야.
                            2. 위에서 언급한 userId 와 taskId,content,priority 를 반드시 활용해서 데이터프로세싱을 진행했으면 좋겠어. 여기서 주의해야 할 것은 userId 는 보통 한사람의 것이며, Content는 각각 다른 업무, 과제의 내용, priority 는 해당 task의 중요도, 레벨, 우선순위 를 나타내.
                            이 4개를 활용해서 task 번호 순서대로 데이터프로세싱 된 데이터를 주면 좋겠어.
                            3. 만약 데이터프로세싱 중 같은 내용의 content 가 있다면 너가 만든 데이터를 맨아래에 '중복된 업무내용 : ' 이라고 써서 중복된 content 의 taskId 들과 content 의 내용을 간략하게 적어서 만들어줘. taskId 는 중복된 content 의 것을 전부 써주고 content 자체는 요약해서 한가지만 써주면 될거같아.
                            만약 중복된 content가 없다고 판단 된다면 중복된 업무내용은 적지 않아도 괜찮으며, 판단은 너에게 맡길게.
                            4. 너가 받아낸 TaskList 를 보고 중요도와 내용을 보고 먼저 해야할 업무를 판단하고 요약해서 사용자에게 전달해줘. 이 내용은 중복된 업무 내용의 밑에 써주면 되고 만약 충분한 양의 정보가 없다고 판단되면 이부분도 생략해도 좋아. 만약 중복된 업무 내용이 제외되서 작성된다면 너가 만든 프로세싱데이터의 맨 아래에 만들어줘.
                            5. priority 는 반드시 숫자로 표기해줘.
                        """.trimIndent()
                ),
                ChatMessage(
                    role = ChatRole.Assistant,
                    content =
                        """
                            이것은 예제이니 참고 할 것.
                            ------------------------
                            userId(= task 데이터의 userId이다.) 님의 Task 리스트입니다. 
                            
                            1. 테스크 번호 : ...
                                업무 내용 : ....
                                중요도 : priority(priority 는 task 정보의 priority 이다. 숫자로 적을것.)
                            2. 테스크 번호 : ...
                                업무 내용 : ....
                                중요도 : priority(priority 는 task 정보의 priority 이다. 숫자로 적을것.)
                            3. 테스크 번호 : ...
                                업무 내용 : ....
                                중요도 : priority(priority 는 task 정보의 priority 이다. 숫자로 적을것.)
                                
                            중복된 업무 내용:
                               - 업무 내용: ............
                                 테스크 번호: [..,.. ,.. ]
                                 
                               - 업무 내용: ............
                                 테스크 번호: [.., ..]
                                 
                               - 업무 내용: ............
                                 테스크 번호: [.., ..]
                            
                            처리해야 할 업무 요약:
                               - ............                            
                           
                            --------------------------    
                        """.trimIndent()
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = "${event}"
                )
            )
        )
        val completion : ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        val processingData = completion.choices[0].message.content.toString().trimIndent()
        return processingData
    }
}