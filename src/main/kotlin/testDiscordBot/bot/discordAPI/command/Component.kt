//package testDiscordBot.bot.discordAPI.command
//
//import org.springframework.context.ApplicationContext
//import org.springframework.context.annotation.AnnotationConfigApplicationContext
//import org.springframework.context.annotation.ComponentScan
//import org.springframework.context.annotation.ComponentScan.Filter
//
//
//@ComponentScan(
//    includeFilters = (F)
//)
//class Component (private val includeFilter: Filter, private val commandAnnotation: CommandAnnotation) {
//
//    fun joinFilter(){
//        val filter = includeFilter.type.name.plus(CommandAnnotation::class.java)
//    }
//
//
//    fun autoWiredBean() {
//        val ac: ApplicationContext = AnnotationConfigApplicationContext(
//            NewTaskCommand::class.java
//        )
//        return ac.autowireCapableBeanFactory.autowireBean(joinFilter())
//    }
//}
//
//
