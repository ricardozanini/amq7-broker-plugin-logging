package amq7.samples.broker.plugin;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.Message;
import org.apache.activemq.artemis.core.postoffice.RoutingStatus;
import org.apache.activemq.artemis.core.server.MessageReference;
import org.apache.activemq.artemis.core.server.RoutingContext;
import org.apache.activemq.artemis.core.server.ServerConsumer;
import org.apache.activemq.artemis.core.server.ServerSession;
import org.apache.activemq.artemis.core.server.plugin.ActiveMQServerPlugin;
import org.apache.activemq.artemis.core.transaction.Transaction;
import org.jboss.logging.Logger;

public class MessageLogginPlugin implements ActiveMQServerPlugin {
    
    private static final Logger LOGGER = Logger.getLogger(MessageLogginPlugin.class);

    public MessageLogginPlugin() {
        
    }
    
    @Override
    public void afterMessageRoute(Message message, RoutingContext context, boolean direct, boolean rejectDuplicates, RoutingStatus result) throws ActiveMQException {
        ActiveMQServerPlugin.super.afterMessageRoute(message, context, direct, rejectDuplicates, result);
        LOGGER.info("************* After Message Route (IN) *********************");
        LOGGER.info(message.toString());
    }
    
    @Override
    public void afterSend(ServerSession session, Transaction tx, Message message, boolean direct, boolean noAutoCreateQueue, RoutingStatus result) throws ActiveMQException {
        ActiveMQServerPlugin.super.afterSend(session, tx, message, direct, noAutoCreateQueue, result);
        LOGGER.info("************* After Send Message (OUT) *********************");
        LOGGER.info(message.toString());
    }

    @Override
    public void afterDeliver(ServerConsumer consumer, MessageReference reference) throws ActiveMQException {
        ActiveMQServerPlugin.super.afterDeliver(consumer, reference);
        LOGGER.info("************* After Deliver Message *********************");
        LOGGER.info(reference.toString());
    }
}
