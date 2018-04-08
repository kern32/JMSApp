package nondurable.subscription;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Subscriber implements MessageListener {
	private TopicConnectionFactory tCF;
	private TopicConnection tCon;
	private TopicSession session;
	private String topicName = "default_JMS_host/JVTopic";
	private String factoryName = "default_JMS_host/CFactory";
	private Topic top;
	private TopicSubscriber tSub;

	private void begin(String uname, String pwd) throws NamingException, JMSException {
		InitialContext ctx = new InitialContext();

		// Step1: Lookup the Connection Factory and the Topic
		tCF = (TopicConnectionFactory) ctx.lookup(factoryName);
		top = (Topic) ctx.lookup(topicName);

		// Step2: Create a connection using the Factory
		tCon = tCF.createTopicConnection(uname, pwd);

		// Step3: Create Topic Sessions using the connection
		session = tCon.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

		// Step4: Create TopicPublisher
		tSub = session.createSubscriber(top);

		// Associating a MessageListener to this subscriber
		tSub.setMessageListener(this);

		tCon.start();
	}
	
	public void onMessage(Message msg) {
		try {
			TextMessage txtMsg = (TextMessage) msg;
			String text = txtMsg.getText();
			if (!text.equalsIgnoreCase("exit")) {
				System.out.println("received message: " + text);
			} else {
				System.out.println("exit");
			}
		} catch (JMSException je) {
			je.printStackTrace();
		}
	}

	public static void main(String[] args) throws NamingException, JMSException, IOException {
		System.out.println("begin subsription --->");
		Subscriber sub = new Subscriber();
		sub.begin("admin", "");
	}
}
