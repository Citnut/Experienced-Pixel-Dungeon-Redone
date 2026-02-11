package com.zrp200.scrollofdebug;

/**
 * Provides inline (ghost text) command suggestions for a text input.
 * Implementations should return only the suffix to be inserted at cursor.
 */
public interface InlineSuggestionEngine {

	/**
	 * @param fullText current input text
	 * @param cursorPosition current cursor position in fullText
	 * @return suffix to show as ghost text (or empty string when no suggestion)
	 */
	String suggestSuffix(String fullText, int cursorPosition);
}
