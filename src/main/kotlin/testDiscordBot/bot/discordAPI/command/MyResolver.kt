package testDiscordBot.bot.discordAPI.command

import dev.kord.core.entity.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.stereotype.Component
import testDiscordBot.bot.discordAPI.UnsupportedCommandException
import testDiscordBot.bot.discordRepository.TaskRepository

//@Component
//class MyResolver (
//    private val taskRepository: TaskRepository,
//    private val applicationContext: ApplicationContext
//){
//
//    class Foo
//
//    class Bar(private val foo: Foo)
//
//    val context = GenericApplicationContext().apply {
//        registerBean<Foo>()
//        registerBean { Bar(it.getBean()) }
//    }
//
//    fun resolve(obj: Any) {
//        obj.javaClass.declaredFields.forEach { f ->
//            if (f.isAnnotationPresent(CommandAnnotation::class.java)) {
//                if (f.equals(obj) || f.trySetAccessible())
//                    return
//            }
//        }
//    }
//}