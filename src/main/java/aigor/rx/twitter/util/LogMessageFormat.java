package aigor.rx.twitter.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class LogMessageFormat {

    public static void alterLogging(){
        Logger log = Logger.getLogger("");
        Handler[] arr = log.getHandlers();
        int len = arr.length;

        for(int i = 0; i < len; ++i) {
            Handler handler = arr[i];
            handler.setFormatter(new LogMessageFormatter());
        }
    }

    static class LogMessageFormatter extends Formatter {
        @Override
        public synchronized String format(final LogRecord record) {
            String source  = getSource(record);
            String message = formatMessage(record);
            String throwable = getThrowableMessage(record);
            String logger = record.getLoggerName();

            return (new SimpleDateFormat("HH:mm:ss.SSS")).format(new Date(record.getMillis())) + " " + String.format("[%3$7s] [T:%6$4s] %4$s%5$s%n",
                    source, logger, record.getLevel(), message, throwable, record.getThreadID());
        }

        private static String getThrowableMessage(LogRecord record) {
            String throwable = "";
            if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                pw.println();
                record.getThrown().printStackTrace(pw);
                pw.close();
                throwable = sw.toString();
            }
            return throwable;
        }

        private static String getSource(LogRecord record) {
            String source;
            if (record.getSourceClassName() != null) {
                source = record.getSourceClassName();
                if (record.getSourceMethodName() != null) {
                    source += " " + record.getSourceMethodName();
                }
            } else {
                source = record.getLoggerName();
            }
            return source;
        }
    }
}
