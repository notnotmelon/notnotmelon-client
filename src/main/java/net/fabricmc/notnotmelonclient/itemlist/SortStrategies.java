package net.fabricmc.notnotmelonclient.itemlist;

import dev.isxander.yacl.api.NameableEnum;
import net.minecraft.text.Text;

import java.util.Comparator;

public enum SortStrategies implements NameableEnum {
	Alphabetical(Comparator.comparing((ItemListIcon a) -> a.skyblockID)),
	Value((ItemListIcon a, ItemListIcon b) -> a.skyblockID.compareTo(b.skyblockID)),
	Rarity((ItemListIcon a, ItemListIcon b) -> a.skyblockID.compareTo(b.skyblockID)),
	Item_Group((ItemListIcon a, ItemListIcon b) -> a.skyblockID.compareTo(b.skyblockID));

	final Comparator<ItemListIcon> sortFunction;
	SortStrategies(Comparator<ItemListIcon> sortFunction) {
		this.sortFunction = sortFunction;
	}

	@Override
	public Text getDisplayName() {
		return Text.of(name().replace('_', ' '));
	}
}
