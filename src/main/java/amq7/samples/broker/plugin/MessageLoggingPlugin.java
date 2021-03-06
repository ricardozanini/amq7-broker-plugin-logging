package amq7.samples.broker.plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.Message;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.postoffice.RoutingStatus;
import org.apache.activemq.artemis.core.server.MessageReference;
import org.apache.activemq.artemis.core.server.RoutingContext;
import org.apache.activemq.artemis.core.server.ServerConsumer;
import org.apache.activemq.artemis.core.server.ServerSession;
import org.apache.activemq.artemis.core.server.plugin.ActiveMQServerPlugin;
import org.apache.activemq.artemis.core.transaction.Transaction;
import org.apache.activemq.artemis.reader.TextMessageUtil;
import org.jboss.logging.Logger;

public class MessageLoggingPlugin implements ActiveMQServerPlugin {

    private static final Logger LOGGER = Logger.getLogger(MessageLoggingPlugin.class);

    // messages from queues we don't want to log.
    private Set<String> blackList = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(new String[] { "activemq.notifications"})));

    public MessageLoggingPlugin() {

    }

    /**
     * Logs information when message is delivered to a consumer and when a message is acknowledged by a consumer.
     */
    @Override
    public void afterDeliver(ServerConsumer consumer, MessageReference reference) throws ActiveMQException {
        if (reference == null) {
            return;
        }

        if (reference.getMessage() == null) {
            return;
        }

        final Message message = reference.getMessage();
        if (isFromBlacklistAddress(message)) {
            return;
        }

        if (consumer == null) {
            return; // no consumer, message not out yet
        }

        if (reference.isAlreadyAcked()) {
            return; // we're not interested in ACK
        }

        LOGGER.info("****** OUT DETAILS ******");
        LOGGER.info("Redelivery #" + reference.getDeliveryCount());

        LogUtil.toLog(LOGGER, "OUT", String.valueOf(message.getMessageID()), message.getStringProperty("JMSCorrelationID"), message.getAddress(),
                      TextMessageUtil.readBodyText(message.toCore().getBodyBuffer()).toString(), message.getConnectionID(), String.valueOf(message.getUserID()),
                      message.getStringProperty("_AMQ_ROUTE_TO"), this.extractAllProps(message));

    }

    @Override
    public void afterMessageRoute(Message message, RoutingContext context, boolean direct, boolean rejectDuplicates, RoutingStatus result) throws ActiveMQException {
        if (message == null) {
            return;
        }

        if (isFromBlacklistAddress(message)) {
            return;
        }
        LOGGER.info("****** ROUTE DETAILS ******");
        LogUtil.toLog(LOGGER, "ROUTE", String.valueOf(message.getMessageID()), message.getStringProperty("JMSCorrelationID"), message.getAddress(),
                      TextMessageUtil.readBodyText(message.toCore().getBodyBuffer()).toString(), message.getConnectionID(), String.valueOf(message.getUserID()),
                      message.getStringProperty("_AMQ_ROUTE_TO"), this.extractAllProps(message));

    }

    /**
     * Logs information when a message has been sent to an address and when a message has been routed within the broker.
     */
    @Override
    public void afterSend(ServerSession session, Transaction tx, Message message, boolean direct, boolean noAutoCreateQueue, RoutingStatus result) throws ActiveMQException {
        if (message == null) {
            return;
        }

        if (isFromBlacklistAddress(message)) {
            return;
        }

        LOGGER.info("****** IN DETAILS ******");
        LOGGER.info("Connection details RemoteAddress: " + session.getRemotingConnection().getRemoteAddress());
        LOGGER.info("Connection details ClientID: " + session.getRemotingConnection().getClientID());
        
        LogUtil.toLog(LOGGER, "IN", String.valueOf(message.getMessageID()), message.getStringProperty("JMSCorrelationID"), message.getAddress(),
                      TextMessageUtil.readBodyText(message.toCore().getBodyBuffer()).toString(), message.getConnectionID(), String.valueOf(message.getUserID()),
                      message.getStringProperty("_AMQ_ROUTE_TO"), this.extractAllProps(message));

    }

    private String extractAllProps(final Message message) {
        final StringBuffer sb = new StringBuffer();

        if (message.getPropertyNames() != null) {
            for (SimpleString item : message.getPropertyNames()) {
                sb.append(item.toString());
                sb.append("=");
                sb.append(message.getObjectProperty(item));
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    /**
     * Test if message address is in blacklist.
     * 
     * @param message the message
     * @return true if it is in the blacklist or false if not
     */
    private boolean isFromBlacklistAddress(Message message) {
        // skip the messages that it is in black list, like activemq.notifications
        if (blackList.stream().anyMatch(address -> message.getAddress().startsWith(address)))
            return true;
        return false;
    }
}
