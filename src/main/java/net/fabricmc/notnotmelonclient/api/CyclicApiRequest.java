package net.fabricmc.notnotmelonclient.api;

import com.google.gson.JsonObject;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import net.fabricmc.notnotmelonclient.util.Util;

import java.lang.reflect.Method;
import java.net.URL;

import static net.fabricmc.notnotmelonclient.Main.LOGGER;

/**
 * Used to keep up-to-date rest API data. For example lowest BIN text on items.
 * Runs every "cooldown" ticks
 */
public class CyclicApiRequest extends ApiRequest {
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

		Scheduler.scheduleCyclicThreaded(this::run, cooldown);
	}

	@Override protected void run(int index) {
		if (!Util.isSkyblock && this.result != null) return;
		if (index == endpoints.length) return;
		URL url = endpoints[index];
		try {
			doRequest(url);
		} catch (Exception e) {
			LOGGER.warn("[nnc] API request failed. Endpoint: " + url.toString(), e);
			run(index + 1);
		}
	}

	@Override protected void doRequest(URL url) throws Exception {
		LOGGER.info("[nnc] Attempting cyclic API request. Endpoint: " + url.toString());
		result = (JsonObject) decypherer.invoke(null, new Object[]{url});
	}
}
