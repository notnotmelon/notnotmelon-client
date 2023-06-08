package net.fabricmc.notnotmelonclient.itemlist;

import net.fabricmc.notnotmelonclient.util.ItemUtil;
import net.fabricmc.notnotmelonclient.util.Rect;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.item.ItemStack;

import java.util.List;

import static net.fabricmc.notnotmelonclient.itemlist.ItemList.*;

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
		int itemNum = childrenNum + 1; // include the parent
		int verticalDirection = gridY * 2 > gridHeight ? -1 : 1; // -1 is UP 1 is DOWN
		int verticalSpace = verticalDirection == -1 ? gridY : gridHeight - gridY - 1;

		if (verticalSpace >= childrenNum) { // case #1
			int childY = y;
			for (ItemListIcon child : children) {
				childY += verticalDirection * STEP;
				child.setLocation(x, childY);
			}
			playground = new Rect(x, y, STEP, -verticalDirection * itemNum * STEP);
		} else { // case #2
			int resultWidth = 1;
			int resultHeight = 1;
			while (resultHeight * resultWidth < itemNum && (resultWidth != gridWidth || resultHeight != gridHeight)) {
				if (resultWidth != gridWidth) resultWidth++;
				if (resultHeight * resultWidth >= itemNum) break;
				if (resultHeight != gridHeight) resultHeight++;
			}

			int horizontalDirection = gridX * 2 > gridWidth ? -1 : 1; // -1 is LEFT 1 is RIGHT
			int horizontalSpace = horizontalDirection == -1 ? gridX : gridWidth - gridX - 1;

			Util.print("itemNum: " + itemNum + " resultWidth: " + resultWidth + " resultHeight: " + resultHeight + " horizontalSpace: " + horizontalSpace + " verticalSpace: " + verticalSpace);
			playground = new Rect(x, y, resultWidth * STEP, resultHeight * STEP);
		}

		if (playground.height < 0) { // the item tooltip rendering function breaks with negative height
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
