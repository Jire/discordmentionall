package org.jire.discordmentionall

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import net.dv8tion.jda.core.entities.*

/**
 * @author Thomas G. P. Nappo (Jire)
 */
object DiscordListener : ListenerAdapter() {
	
	override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
		val self = event.jda.selfUser
		if (self == event.author) {
			val message = event.message
			if (Settings.SIGNAL_MESSAGE == message.contentRaw) {
				val textChannel = message.textChannel
				val guildMembers = event.guild.members
				
				message.delete().submit().thenAcceptAsync {
					val membersToMention = HashSet<Member>(guildMembers.size)
					for (member in guildMembers) {
						if (self != member.user && member.meetsRequirements()) {
							membersToMention.add(member)
						}
					}
					
					val textToSend = StringBuilder()
					for (memberToMention in membersToMention) {
						textToSend.append("<@").append(memberToMention.user.id).append(">")
						if (textToSend.length > 1900) {
							val tts = textToSend.toString()
							textToSend.setLength(0)
							textChannel.sendMessageAndDelete(tts)
						}
					}
					if (textToSend.isNotEmpty()) {
						textChannel.sendMessageAndDelete(textToSend)
					}
				}
			}
		}
	}
	
	private fun Member.meetsRequirements() =
			OnlineStatus.isAtLeast(onlineStatus.name, Settings.MINIMUM_ONLINE_STATUS)
					&& (Settings.ROLE_WHITELIST.isEmpty() || hasRoles(Settings.ROLE_WHITELIST))
					&& (Settings.ROLE_BLACKLIST.isEmpty() || !hasRoles(Settings.ROLE_BLACKLIST))
	
	private fun Member.hasRoles(roles: String) =
			this.roles.firstOrNull {
				!roles.splitToSequence(ROLE_DELIMITER).contains(it.name)
			} != null
	
	private const val ROLE_DELIMITER = ","
	
	private fun TextChannel.sendMessageAndDelete(message: CharSequence) {
		sendMessage(message).submit().thenAcceptAsync {
			it.delete().submit()
		}
	}
	
}