package net.fabricmc.notnotmelonclient.misc;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.notnotmelonclient.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

// https://modrinth.com/mod/log-begone
// https://github.com/AzureDoom/Log-Begone/blob/1.19Fabric/src/main/java/mod/azure/logbegone/LogBegoneMod.java

// This may be overkill

public class LogSpamFix implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		FilteredPrintStream printFilter = new FilteredPrintStream(System.out);
		LogFilter logFilter = new LogFilter();

		System.setOut(printFilter);
		java.util.logging.Logger.getLogger("").setFilter(logFilter);
		((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(logFilter);
		LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
		HashSet<LoggerConfig> existingLoggers = new HashSet<>(logContext.getConfiguration().getLoggers().values());
		for (LoggerConfig logger : existingLoggers)
			logger.addFilter(logFilter);
	}

	private static final Pattern UNKNOWN_TEAM = Pattern.compile("^Received packet for unknown team .+?: team action: REMOVE, player action:");
	private static final Pattern UNKNOWN_PLAYER = Pattern.compile("^Ignoring player info update for unknown player .+?$");
	private static boolean shouldFilterMessage(String message) {
		if (!Util.isSkyblock || !CONFIG.logSpamFix) return false;
		if (message.contains("Ignoring player info update for unknown player")) return true;
		if (message.equals("Received passengers for unknown entity")) return true;
		if (UNKNOWN_TEAM.matcher(message).find()) return true;
		if (UNKNOWN_PLAYER.matcher(message).find()) return true;
		return false;
	}

	private static final class FilteredPrintStream extends PrintStream {
		public FilteredPrintStream(PrintStream stream) {
			super(stream);
		}

		@Override
		public void print(String s) {
			if (!shouldFilterMessage(s))
				super.print(s);
		}

		@Override
		public void println(String x) {
			if (!shouldFilterMessage(x))
				super.println(x);
		}
	}

	private static final class LogFilter extends AbstractFilter implements Filter {
		public boolean isLoggable(@NotNull LogRecord record) {
			return !shouldFilterMessage(record.getMessage());
		}
	
		public Result filter(@NotNull LogEvent event) {
			return shouldFilterMessage(event.getMessage().getFormattedMessage()) ? Result.DENY : Result.NEUTRAL;
		}
	}
}