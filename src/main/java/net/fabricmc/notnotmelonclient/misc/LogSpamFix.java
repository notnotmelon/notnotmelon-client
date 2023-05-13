package net.fabricmc.notnotmelonclient.misc;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.jetbrains.annotations.NotNull;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

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
		HashSet<LoggerConfig> existingLoggers = new HashSet<LoggerConfig>(logContext.getConfiguration().getLoggers().values());
		for (LoggerConfig logger : existingLoggers)
			logger.addFilter(logFilter);
	}

	public static boolean shouldFilterMessage(String message) {
		return message.contains("Ignoring player info update for unknown player");
	}

	private final class FilteredPrintStream extends PrintStream {
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

	private final class LogFilter extends AbstractFilter implements Filter {
		public boolean isLoggable(@NotNull LogRecord record) {
			return !shouldFilterMessage(record.getMessage());
		}
	
		public Result filter(@NotNull LogEvent event) {
			return shouldFilterMessage(event.getMessage().getFormattedMessage()) ? Result.DENY : Result.NEUTRAL;
		}
	}
}