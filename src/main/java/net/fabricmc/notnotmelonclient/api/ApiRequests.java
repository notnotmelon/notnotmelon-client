package net.fabricmc.notnotmelonclient.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class ApiRequests {
	public static ApiRequest npcPrices;
	public static ApiRequest lowestBins;
	public static ApiRequest bazaarPrices;
	public static ApiRequest averageBins;

	public static void init() {
		try {
			Method jsonDecypherer = ApiDecypherer.class.getMethod("json", URL.class);
			Method moulberryDecypherer = ApiDecypherer.class.getMethod("moulberry", URL.class);

			npcPrices = new ApiRequest(new URL("https://hysky.de/api/npcprice"), jsonDecypherer);
			lowestBins = new CyclicApiRequest(new URL[]{new URL("https://lb.tricked.pro/lowestbins"), new URL("https://lb2.tricked.pro/lowestbins")}, jsonDecypherer);
			bazaarPrices = new CyclicApiRequest(new URL("https://hysky.de/api/bazaar"), jsonDecypherer);
			averageBins = new CyclicApiRequest(new URL("https://moulberry.codes/auction_averages_lbin/3day.json.gz"), moulberryDecypherer);
		} catch(MalformedURLException | NoSuchMethodException ignored) {

		}
	}

	public static class ApiDecypherer {
		public static JsonObject json(URL url) throws IOException {
			try (InputStream stream = url.openStream()) {
				try (InputStreamReader reader = new InputStreamReader(stream)) {
					return new Gson().fromJson(reader, JsonObject.class);
				}
			}
		}

		public static JsonObject moulberry(URL url) throws IOException {
			try (InputStream stream = url.openStream()) {
				try (GZIPInputStream gzip = new GZIPInputStream(stream)) {
					try (InputStreamReader reader = new InputStreamReader(gzip)) {
						return new Gson().fromJson(reader, JsonObject.class);
					}
				}
			}
		}
	}
}
