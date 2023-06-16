package net.fabricmc.notnotmelonclient.util;

import java.util.HashMap;

public class PointList<T extends Number> {
	private final HashMap<T, HashMap<T, HashMap<T, Integer>>> points = new HashMap<>();
	
	public void add(T x, T y, T z, int amount) {
		if (!points.containsKey(x))
			points.put(x, new HashMap<>());

		if (!points.get(x).containsKey(y))
			points.get(x).put(y, new HashMap<>());

		if (!points.get(x).get(y).containsKey(z))
			points.get(x).get(y).put(z, 0);

		int newAmount = get(x, y, z) + amount;
		if (newAmount == 0)
			points.get(x).get(y).put(z, null);
		else
			points.get(x).get(y).put(z, newAmount);
	}

	public void add(T x, T y, T z) {
		add(x, y, z, 1);
	}

	public boolean contains(T x, T y, T z) {
		return points.containsKey(x) && points.get(x).containsKey(y) && points.get(x).get(y).containsKey(z);
	}

	public Integer get(T x, T y, T z) {
		return contains(x, y, z) ? points.get(x).get(y).get(z) : null;
	}
}
