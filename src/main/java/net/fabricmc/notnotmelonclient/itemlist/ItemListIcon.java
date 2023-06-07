package net.fabricmc.notnotmelonclient.itemlist;

import net.fabricmc.notnotmelonclient.util.ItemUtil;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemListIcon {
	public int x = -1;
	public int y = -1;
	public ItemStack stack;
	public List<ItemStack> children;
	public String skyblockID;
	public String searchableText;

	public ItemListIcon(ItemStack stack) {
		this.stack = stack;
		skyblockID = ItemUtil.getFullItemID(stack);
		calculateSearchableText();
	}

	protected void calculateSearchableText() {
		searchableText = "";
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setChildren(List<ItemStack> children) {
		this.children = children;
	}
}
