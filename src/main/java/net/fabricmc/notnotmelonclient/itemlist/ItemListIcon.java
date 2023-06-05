package net.fabricmc.notnotmelonclient.itemlist;

import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemListIcon {
	public int x;
	public int y;
	public ItemStack stack;
	public List<ItemList> children;

	public ItemListIcon(ItemStack stack, int x, int y) {
		this.x = x;
		this.y = y;
		this.stack = stack;
	}
}
