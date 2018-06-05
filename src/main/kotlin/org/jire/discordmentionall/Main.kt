package org.jire.discordmentionall

import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder

/**
 * @author Thomas G. P. Nappo (Jire)
 */
object Main {
	
	@JvmStatic
	fun main(args: Array<String>) {
		val jda = JDABuilder(AccountType.CLIENT)
				.setToken(Settings.TOKEN)
				.buildAsync()
		jda.addEventListener(DiscordListener)
	}
	
}