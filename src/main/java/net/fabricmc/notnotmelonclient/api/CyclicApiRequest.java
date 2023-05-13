package net.fabricmc.notnotmelonclient.api;

import java.lang.reflect.Method;
import java.net.URL;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.google.gson.JsonObject;

import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.util.Scheduler;

/**
 * Used to keep up-to-date rest API data. For example lowest BIN text on items.
 * Runs every "cooldown" ticks
 * See: ApiDecypherer.java
 */
public class CyclicApiRequest {
	private static final Logger LOGGER = Main.LOGGER;
	public static final int DEFAULT_COOLDOWN = 30 * 60 * 10; // 10 minutes
	private URL[] endpoints; // incase the api has mirrors, we can call all of them until there is a response. sorted by priority
	private int cooldown;
	private Method decypherer;
	private JsonObject result;

	CyclicApiRequest(URL endpoint, Method decypherer) {
		this(new URL[]{endpoint}, DEFAULT_COOLDOWN, decypherer);
	}

	CyclicApiRequest(URL[] endpoints, Method decypherer) {
		this(endpoints, DEFAULT_COOLDOWN, decypherer);
	}

	CyclicApiRequest(URL endpoint, int cooldown, Method decypherer) {
		this(new URL[]{endpoint}, cooldown, decypherer);
	}

	CyclicApiRequest(URL[] endpoints, int cooldown, Method decypherer) {
		this.endpoints = endpoints;
		this.cooldown = cooldown;
		this.decypherer = decypherer;

		Scheduler.getInstance().scheduleCyclic(this::run, cooldown);
	}

	@Nullable public JsonObject getResult() {
		return result;
	}
	
	public void run() {
		run(0);
	}

	private void run(int index) {
		if (index == endpoints.length) return;
		URL url = endpoints[index];
		try {
			result = (JsonObject) decypherer.invoke(null, new Object[]{url});
		} catch (Exception e) {
			LOGGER.warn("[nnc] API request failed. Endpoint: " + url.toString(), e);
			run(index + 1);
		}
	}
}
