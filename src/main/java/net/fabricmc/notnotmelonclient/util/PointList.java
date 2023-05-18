package net.fabricmc.notnotmelonclient.util;

import java.util.HashMap;

public class PointList<T extends Number> {
	private HashMap<T, HashMap<T, HashMap<T, Boolean>>> points = new HashMap<T, HashMap<T, HashMap<T, Boolean>>>();
	
	public void add(T x, T y, T z) {
		if (!points.containsKey(x))
			points.put(x, new HashMap<T, HashMap<T, Boolean>>());

		if (!points.get(x).containsKey(y))
			points.get(x).put(y, new HashMap<T, Boolean>());

		points.get(x).get(y).put(z, true);
	}

	public boolean contains(T x, T y, T z) {
		return points.containsKey(x) && points.get(x).containsKey(y) && points.get(x).get(y).containsKey(z);
	}
}
