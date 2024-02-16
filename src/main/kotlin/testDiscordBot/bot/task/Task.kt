package testDiscordBot.bot.task

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val taskId : Long = 0,

    @Column(length = 500)
    val userId: String,
    @Column(length = 500)
    val serverName: String,
    @Column(length = 500)
    val channelName: String,
    @Column(length = 500)
    val content: String,
    @Column
    val priority: Int
){

    @CreationTimestamp
    lateinit var createdAt: LocalDateTime

    @UpdateTimestamp
    lateinit var updatedAt: LocalDateTime
}
