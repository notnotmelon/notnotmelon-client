package net.fabricmc.notnotmelonclient.util;

import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StringInjector {
	private final List<Pair<Pattern, String>> injectors = new ArrayList<>();

	public void add(String pattern, String replacement) {
		injectors.add(new Pair<>(Pattern.compile(pattern), replacement));
	}

	public String inject(String string) {
		for (Pair<Pattern, String> injector : injectors) {
			Pattern pattern = injector.getLeft();
			String replacement = injector.getRight();
			string = pattern.matcher(string).replaceAll(replacement);;
		}
		return string;
	}
}
