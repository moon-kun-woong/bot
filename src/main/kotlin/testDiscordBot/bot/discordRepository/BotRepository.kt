package testDiscordBot.bot.discordRepository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import testDiscordBot.bot.discordEntity.TaskEntity

@Repository // 이건 맞는것 같음. check v
interface BotRepository : CrudRepository<TaskEntity, Long> {
    fun findAllByWriterId(writerId: String): List<TaskEntity>

}