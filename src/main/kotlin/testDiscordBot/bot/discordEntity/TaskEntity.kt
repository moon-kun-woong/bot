package testDiscordBot.bot.discordEntity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "TASK") // 테이블 만듦...
public class TaskEntity(writerId: String , channelName: String, taskName: String, serverName: String, content: String) {

    var writerId: String = ""
    var channelName: String = ""
    var serverName: String = ""
    var content: String = ""

    // @GeneratedValue 은 식별자 값을 자동으로 생성 시켜준다!
    @Id @GeneratedValue(strategy = GenerationType.AUTO) // 기본 키 매핑이 이것! xml 을 쓰지 않고서 매핑 가능하게 해줌.
    var taskId : Long = 0  // 대신 4가지 전략이라는게 있는데 일단 나는 auto로 하기로 하자. (IDENTITY, SEQUENCE, TABLE)
    // IDENTITY 로 쓰면 ID 값을 설정하지 않고 insert 쿼리를 날리면 그때! id 값을 넣어준다.


    // 여기에서 뭔가... 뭔가 많다.. 근데 잘모르겠다 물어보자..
    // Entity 에서 읽고 만들고 가져오는거인가? 일단 서비스에 만들자..
}