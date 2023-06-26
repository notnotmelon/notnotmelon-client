package net.fabricmc.notnotmelonclient.util;

import java.util.regex.Pattern;

public class StringInjector {
	private final Pattern pattern;
	private final String replacement;

	public StringInjector(String pattern, String replacement) {
		this.pattern = Pattern.compile(pattern);
		this.replacement = replacement;
	}

	public String inject(String string) {
		return pattern.matcher(string).replaceAll(replacement);
	}
}
