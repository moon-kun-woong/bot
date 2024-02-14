package testDiscordBot.bot.discordrepository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import testDiscordBot.bot.discordtask.Task

@Repository
interface TaskRepository : CrudRepository<Task, Long> {
    fun findAllByUserId(userId: String): List<Task>

}
