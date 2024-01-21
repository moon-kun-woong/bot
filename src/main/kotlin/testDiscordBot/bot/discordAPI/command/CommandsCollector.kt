package testDiscordBot.bot.discordAPI.command

import testDiscordBot.bot.discordRepository.TaskRepository

interface CommandsCollector {
    fun createCommandText(taskRepository: TaskRepository): Command
}