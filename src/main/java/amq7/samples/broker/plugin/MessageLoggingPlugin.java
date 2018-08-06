package amq7.samples.broker.plugin;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.Message;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.postoffice.RoutingStatus;
import org.apache.activemq.artemis.core.server.MessageReference;
import org.apache.activemq.artemis.core.server.RoutingContext;
import org.apache.activemq.artemis.core.server.ServerConsumer;
import org.apache.activemq.artemis.core.server.plugin.ActiveMQServerPlugin;
import org.apache.activemq.artemis.reader.TextMessageUtil;
import org.jboss.logging.Logger;

public class MessageLoggingPlugin implements ActiveMQServerPlugin {
    
    private static final Logger LOGGER = Logger.getLogger(MessageLoggingPlugin.class);

    public MessageLoggingPlugin() {
        
    }
    
    @Override
    public void afterMessageRoute(Message message, RoutingContext context, boolean direct, boolean rejectDuplicates, RoutingStatus result) throws ActiveMQException {
        ActiveMQServerPlugin.super.afterMessageRoute(message, context, direct, rejectDuplicates, result);
        LOGGER.info("************* After Message Route (IN) *********************");
        //LOGGER.info(message.toString());
        
        LogUtil.toLog(LOGGER, 
            "IN",
            String.valueOf(message.getMessageID()),
            message.getStringProperty("JMSCorrelationID"), 
            message.getAddress(),
            TextMessageUtil.readBodyText(message.toCore().getBodyBuffer()).toString(),
            message.getConnectionID(),
            String.valueOf(message.getUserID()),
            message.getStringProperty("_AMQ_ROUTE_TO"),
            this.extractAllProps(message)
            );
    }
    

    @Override
    public void afterDeliver(ServerConsumer consumer, MessageReference reference) throws ActiveMQException {
        ActiveMQServerPlugin.super.afterDeliver(consumer, reference);
        LOGGER.info("************* After Deliver Message (OUT) *********************");
        //LOGGER.info(reference.toString());
        final Message message = reference.getMessage();
        LogUtil.toLog(LOGGER, 
                      "OUT",
                      String.valueOf(message.getMessageID()),
                      message.getStringProperty("JMSCorrelationID"), 
                      message.getAddress(),
                      TextMessageUtil.readBodyText(message.toCore().getBodyBuffer()).toString(),
                      message.getConnectionID(),
                      String.valueOf(message.getUserID()),
                      message.getStringProperty("_AMQ_ROUTE_TO"),
                      this.extractAllProps(message)
                      );
    }
    
    private String extractAllProps(final Message message) {
        final StringBuffer sb = new StringBuffer();

        for (SimpleString item : message.getPropertyNames()) {
            sb.append(item.toString());
            sb.append("=");
            sb.append(message.getStringProperty(item));
            sb.append(", ");
        }

        return sb.toString();
    }
}
