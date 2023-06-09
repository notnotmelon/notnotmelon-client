package net.fabricmc.notnotmelonclient.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.notnotmelonclient.itemlist.ItemList;
import net.fabricmc.notnotmelonclient.itemlist.NeuRepo;
import net.fabricmc.notnotmelonclient.itemlist.SortStrategies;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import org.jetbrains.annotations.Blocking;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import static net.fabricmc.notnotmelonclient.Main.LOGGER;
import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

public class ApiRequests {
	public static ApiRequest npcPrices;
	public static CyclicApiRequest lowestBins;
	public static CyclicApiRequest bazaarPrices;
	public static CyclicApiRequest averageBins;
	public static RepoRequest neuRepo;

	public static void init() {
		try {
			Method jsonDecypherer = ApiDecypherer.class.getMethod("json", URL.class);
			Method moulberryDecypherer = ApiDecypherer.class.getMethod("moulberry", URL.class);

			npcPrices = new ApiRequest(new URL("https://hysky.de/api/npcprice"), jsonDecypherer);
			lowestBins = new CyclicApiRequest(new URL("https://lb.tricked.pro/lowestbins"), jsonDecypherer);
			bazaarPrices = new CyclicApiRequest(new URL("https://hysky.de/api/bazaar"), jsonDecypherer);
			averageBins = new CyclicApiRequest(new URL("https://moulberry.codes/auction_averages_lbin/3day.json.gz"), moulberryDecypherer);
			neuRepo = new RepoRequest(new URL("https://github.com/KonaeAkira/NotEnoughUpdates-REPO.git"), FabricLoader.getInstance().getConfigDir().resolve("notnotmelonclient/item-repo"), NeuRepo::afterDownload);
		} catch(Exception e) {
			LOGGER.error("[nnc] API Error!", e);
		}

		Scheduler.schedule(() -> Scheduler.scheduleCyclicThreaded(() -> {
			if (CONFIG.sortStrategy == SortStrategies.Value) ItemList.sort();
		}, 20 * 60 * 29), 20 * 20);
	}

	public static class ApiDecypherer {
		@Blocking public static JsonObject json(URL url) throws IOException {
			try (InputStream stream = url.openStream()) {
				try (InputStreamReader reader = new InputStreamReader(stream)) {
					return new Gson().fromJson(reader, JsonObject.class);
				}
			}
		}

		@Blocking public static JsonObject moulberry(URL url) throws IOException {
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
