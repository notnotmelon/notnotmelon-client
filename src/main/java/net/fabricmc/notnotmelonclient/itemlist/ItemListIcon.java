package net.fabricmc.notnotmelonclient.itemlist;

import net.fabricmc.notnotmelonclient.util.ItemUtil;
import net.fabricmc.notnotmelonclient.util.Rect;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.item.ItemStack;

import java.util.List;

import static net.fabricmc.notnotmelonclient.itemlist.ItemList.STEP;
import static net.fabricmc.notnotmelonclient.itemlist.ItemList.gridHeight;

public class ItemListIcon {
	public int x = -1;
	public int y = -1;
	public ItemStack stack;
	public List<ItemListIcon> children; // it breaks after 1 deep
	public Rect playground;
	public String skyblockID;
	public String searchableText;
	int gridX;
	int gridY;

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

	public void setChildren(List<ItemListIcon> children) {
		this.children = children;
	}

	/**
	 This method finds positions for the children stacks on the grid.
	 Case #2 is only if we have no space for case #1.
	 
	 legend:
	 O > parent
	 X > child
	 . > other item
	 _ > dead

	 Case #1 (ideal): Draw directly down/up
	 ......
	 ...O..
	 ...X..
	 ...X..

	 Case #2: Draw rectangle. Aim to the corner with the most available space. Place dead tiles if child count is prime.
	 ......
	 XXXXO.
	 XXXXX.
	 XXXX_.

	 Case #3 (worst): Use all available tiles. Some children will be lost. Too bad!
	 XXXXXX
	 XXXXOX
	 XXXXXX
	 XXXXXX
	 */
	public void calculateChildrenPositions() {
		int childrenNum = children.size();
		int goalDirection = gridY * 2 > gridHeight ? -1 : 1; // -1 is UP 1 is DOWN
		int verticalSpace = goalDirection == -1 ? gridY : gridHeight - gridY - 1;

		if (verticalSpace >= childrenNum) { // case #1
			int childY = y;
			for (ItemListIcon child : children) {
				childY += goalDirection * STEP;
				child.setLocation(x, childY);
			}
			playground = new Rect(x, y, STEP, -goalDirection * (childrenNum + 1) * STEP);
		} else {
			Util.print(verticalSpace);
			playground = new Rect(x, y, STEP, STEP);
		}

		if (playground.height < 0) {
			playground.height *= -1;
		} else {
			playground.y -= playground.height - STEP;
		}
	}

	public void setGridLocation(int gridX, int gridY) {
		this.gridX = gridX;
		this.gridY = gridY;
	}
}
