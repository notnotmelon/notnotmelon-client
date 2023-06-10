package net.fabricmc.notnotmelonclient.itemlist;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class ItemSearchPattern {
	ArrayList<ArrayList<String>> inner;
	public static final ItemSearchPattern EMPTY = new ItemSearchPattern(new ArrayList<ArrayList<String>>());

	public ItemSearchPattern(ArrayList<ArrayList<String>> pattern) {
		inner = pattern;
	}

	public boolean matches(ItemStack stack) {
		if (this == EMPTY) return true;
		for (ArrayList<String> or : inner)
			if (or.stream().allMatch(query -> {
				if (textContains(stack.getName(), query)) return true;
				if (textContains(stack.getItem().getName(), query)) return true;
				for (Text lore : stack.getTooltip(null, TooltipContext.BASIC))
					if (textContains(lore, query)) return true;
				return false;
			})) return true;
		return false;
	}

	public boolean matches(ItemListIcon itemListIcon) {
		if (this == EMPTY) return true;
		if (matches(itemListIcon.stack)) return true;
		if (itemListIcon.children != null)
			for (ItemListIcon child : itemListIcon.children)
				if (matches(child.stack)) return true;
		return false;
	}

	private static boolean textContains(Text text, String s) {
		return text.getString().replaceAll("§.", "").toLowerCase().contains(s);
	}
}
