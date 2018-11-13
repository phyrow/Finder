package services;

import org.slf4j.Logger;

import javax.inject.Singleton;
import java.text.MessageFormat;

@Singleton
public class LogService {

  public final static String HEADER_REQUEST_ID = "X-Request-ID";

  public final static String EMPTY_STRING = "";

  private final static String LOG_FORMAT = "[{0}]: {1}";

  public void error(final Logger logger, final String requestId, final String message, final Throwable e) {
    final String logMessage = MessageFormat.format(LOG_FORMAT, requestId, message);
    logger.error(logMessage, e);
  }

  public void error(final Logger logger, final String requestId, final String message) {
    final String logMessage = MessageFormat.format(LOG_FORMAT, requestId, message);
    logger.error(logMessage);
  }
}