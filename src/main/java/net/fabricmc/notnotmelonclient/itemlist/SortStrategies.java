package net.fabricmc.notnotmelonclient.itemlist;

import dev.isxander.yacl3.api.NameableEnum;
import net.fabricmc.notnotmelonclient.util.ItemUtil;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.Comparator;

public enum SortStrategies implements NameableEnum {
	Value(
		Comparator.comparing((ItemListIcon a) ->
			-Math.max(
				ItemUtil.getValue(a.stack),
				a.children == null
					? 0
					: Collections.max(a.children.stream().map(b -> ItemUtil.getValue(b.stack)).toList())
			)
		).thenComparing((ItemListIcon a) -> -ItemUtil.getRarity(a.stack)
		).thenComparing(ItemListIcon::simplifiedName)
	),
	Rarity(Comparator.comparing((ItemListIcon a) -> -ItemUtil.getRarity(a.stack)).thenComparing(ItemListIcon::simplifiedName)),
	Alphabetical(Comparator.comparing(ItemListIcon::simplifiedName));

	final Comparator<ItemListIcon> sortFunction;
	SortStrategies(Comparator<ItemListIcon> sortFunction) {
		this.sortFunction = sortFunction;
	}

	@Override
	public Text getDisplayName() {
		return Text.of(name().replace('_', ' '));
	}
}