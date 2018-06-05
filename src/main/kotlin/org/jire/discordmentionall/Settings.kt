package org.jire.discordmentionall

/**
 * @author Thomas G. P. Nappo (Jire)
 */
object Settings {
	
	/**
	 * Your Discord auth token.
	 * (Visit the Discord web client and view the SQLite tables in Chrome Developer Options.)
	 */
	const val TOKEN = ""
	
	/**
	 * The contents of the message to signify mention all.
	 */
	const val SIGNAL_MESSAGE = "]"
	
	/**
	 * The minimum online status to mention.
	 */
	val MINIMUM_ONLINE_STATUS = OnlineStatus.IDLE
	
	/**
	 * The allowed roles to mention, separated by commas.
	 */
	const val ROLE_WHITELIST = ""
	
	/**
	 * The roles to never mention, separated by commas.
	 */
	const val ROLE_BLACKLIST = ""
	
}