package amq7.samples.broker.plugin;

import org.apache.activemq.artemis.utils.StringEscapeUtils;
import org.jboss.logging.Logger;

final class LogUtil {

    private static final String MSG_FORMAT = "\"type\":\"%s\", \"message-id\":\"%s\", \"correlation-id\":\"%s\", \"destination\":\"%s\", \"payload\":\"%s\", \"clientID\":\"%s\", \"jmsMessageID\":\"%s\", \"amqRouteTo\":\"%s\", \"properties\": \"%s\" ";

    private LogUtil() {

    }

    static void toLog(final Logger log, final String... data) {
        String _payload = StringEscapeUtils.escapeString(data[4]).trim();
        String msg = String.format(MSG_FORMAT, data[0], data[1], data[2], data[3], _payload, data[5], data[6], data[7], data[8]);
        log.info(msg);
        //System.out.println(msg);
    }

}
