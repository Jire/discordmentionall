package org.jire.discordmentionall

/**
 * @author Thomas G. P. Nappo (Jire)
 */
enum class OnlineStatus(vararg val strings: String) {
	OFFLINE("offline"),
	IDLE("idle"),
	DO_NOT_DISTURB("dnd", "do_not_disturb"),
	ONLINE("online");
	
	fun matches(string: String) = strings.map { it.toLowerCase() }.contains(string.toLowerCase())
	
	companion object {
		fun getStatusFor(string: String) = values().firstOrNull { it.matches(string) } ?: OFFLINE
		
		fun isAtLeast(string: String, onlineStatus: OnlineStatus)
				= getStatusFor(string).ordinal >= onlineStatus.ordinal
	}
	
}