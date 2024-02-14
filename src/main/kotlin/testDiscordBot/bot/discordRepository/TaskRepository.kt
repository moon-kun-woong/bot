package testDiscordBot.bot.discordRepository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import testDiscordBot.bot.discordEntity.Task

@Repository
interface TaskRepository : CrudRepository<Task, Long> {
    fun findAllByUserId(userId: String): List<Task>
    override fun deleteById(taskId: Long)

}
