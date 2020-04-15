/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */

package org.jire.discordmentionall

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

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