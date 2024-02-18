package testDiscordBot.bot.discordapi.command

import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.filter.AnnotationTypeFilter

class TaskCommandAnnotationScanner: ImportBeanDefinitionRegistrar {
    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry
    ) {
        val scanner = ClassPathBeanDefinitionScanner(registry, false)
        scanner.addIncludeFilter(AnnotationTypeFilter(TaskCommand::class.java))
        scanner.scan("testDiscordBot.bot")
    }
}

