package net.fabricmc.notnotmelonclient.util;

import net.minecraft.client.util.math.Rect2i;

public class Rect extends Rect2i {
	public Rect(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public boolean aabb(int x, int y) {
		return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
	}
}
