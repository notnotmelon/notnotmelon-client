package net.fabricmc.notnotmelonclient.api;

import com.google.gson.JsonObject;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.net.URL;

import static net.fabricmc.notnotmelonclient.Main.LOGGER;

/**
 * Used to periodically run API calls until a successful response.
 * Useful for static resources such as NPC prices.
 * See: ApiDecypherer.java
 */
public class ApiRequest {
	public static final int DEFAULT_COOLDOWN = 30 * 60 * 10; // 10 minutes
	protected URL[] endpoints; // in case the api has mirrors, we can call all of them until there is a response. sorted by priority
	protected int cooldown;
	protected Method decypherer;
	protected JsonObject result;

	protected ApiRequest() {}

	ApiRequest(URL endpoint, Method decypherer) {
		this(new URL[]{endpoint}, DEFAULT_COOLDOWN, decypherer);
	}

	ApiRequest(URL[] endpoints, Method decypherer) {
		this(endpoints, DEFAULT_COOLDOWN, decypherer);
	}

	ApiRequest(URL endpoint, int cooldown, Method decypherer) {
		this(new URL[]{endpoint}, cooldown, decypherer);
	}

	ApiRequest(URL[] endpoints, int cooldown, Method decypherer) {
		this.endpoints = endpoints;
		this.cooldown = cooldown;
		this.decypherer = decypherer;
		new Thread(this::run).start();
	}

	@Nullable public JsonObject getJSON() {
		return result;
	}
	
	public void run() {
		run(0);
	}

	protected void run(int index) {
		if (index == endpoints.length) {
			Scheduler.scheduleThreaded(this::run, cooldown);
			return;
		}
		URL url = endpoints[index];
		try {
			doRequest(url);
		} catch (Exception e) {
			LOGGER.warn("[nnc] API request failed. Endpoint: " + url.toString(), e);
			run(index + 1);
		}
	}

	protected void doRequest(URL url) throws Exception {
		LOGGER.info("[nnc] Attempting API request. Endpoint: " + url.toString());
		result = (JsonObject) decypherer.invoke(null, new Object[]{url});
	}
}
