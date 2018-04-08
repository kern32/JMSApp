package durable.subscription;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class DurableSubscriber {
	private String topicName = "default_JMS_host/JVTopic";
	private String factoryName = "default_JMS_host/CFactory";
	private TopicConnection conn = null;
	private TopicSession session = null;
	private Topic topic = null;

	private void begin() throws JMSException, NamingException, UnknownHostException {
		setupPubSub();
		subscribe();
	}
	
	private void setupPubSub() throws JMSException, NamingException, UnknownHostException {
		InitialContext iniCtx = new InitialContext();
		TopicConnectionFactory tcf = (TopicConnectionFactory) iniCtx.lookup(factoryName);
		conn = tcf.createTopicConnection("admin", "");
		conn.setClientID(InetAddress.getLocalHost().getHostName());
		topic = (Topic) iniCtx.lookup(topicName);
		session = conn.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
		conn.start();
	}

	private void subscribe() throws JMSException {
		TopicSubscriber recv = session.createDurableSubscriber(topic, "jms-ex1dtps");
		while(true){
			Message msg = recv.receive(5000);
			TextMessage txtMsg = (TextMessage) msg;
			if (txtMsg == null) {
				System.out.println("timed out waiting for message");
			} else if (txtMsg.getText().equalsIgnoreCase("exit")) {
				stop();
				System.out.println("end durable subscription");
				System.exit(0);
			} else {
				System.out.println("received message: " + txtMsg.getText());
			}
		}
	}

	private void stop() throws JMSException {
		conn.stop();
		session.close();
		conn.close();
	}

	public static void main(String args[]) throws Exception {
		System.out.println("begin durable subsription --->");
		DurableSubscriber client = new DurableSubscriber();
		client.begin();
	}
}
