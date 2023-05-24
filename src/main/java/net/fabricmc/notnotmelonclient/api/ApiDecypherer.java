package net.fabricmc.notnotmelonclient.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * these are decypherers for various formats of rest API.
 * they convert a URL into JsonObject
 * some of these try statements are unnecessary. too bad!
 * See: CyclicApiRequest.java
 */
public class ApiDecypherer {
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