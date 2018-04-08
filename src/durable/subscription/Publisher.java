package durable.subscription;

import javax.jms.*;
import javax.naming.*;
import java.io.*;

public class Publisher {
	private String topicName = "default_JMS_host/JVTopic";
	private String factoryName = "default_JMS_host/CFactory";
	private TopicConnectionFactory tCF;
	private TopicConnection tCon;
	private TopicSession pubSession;
	private Topic top;
	private TopicPublisher tPub;

	// Constructor to initialise the required entities
	public Publisher(String uname, String pwd) throws NamingException, JMSException {
		InitialContext ctx = new InitialContext();

		// Step1: Lookup the Connection Factory and the Topic
		tCF = (TopicConnectionFactory) ctx.lookup(factoryName);
		top = (Topic) ctx.lookup(topicName);

		// Step2: Create a connection using the Factory
		tCon = tCF.createTopicConnection(uname, pwd);

		// Step3: Create Topic Sessions using the connection
		pubSession = tCon.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

		// Step4: Create TopicPublisher
		tPub = pubSession.createPublisher(top);

		tCon.start();
	}

	public static void main(String[] args) throws NamingException, IOException, JMSException {
		Publisher pub = new Publisher("admin", "");
		publish(pub);
	}

	private static void publish(Publisher pub) throws IOException, JMSException {
		System.out.println("enter text message");
		// Declaring a Reader for reading the message from user
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// Read input to be published till the users enters 'exit'
		while (true) {
			String msg = br.readLine();
			if (msg.equalsIgnoreCase("exit")) {
				pub.writeMsg(msg);
				pub.close();
				System.exit(0);
			} else {
				pub.writeMsg(msg);
			}
		}
	}

	public void close() throws JMSException {
		tCon.stop();
	}

	// Method to publish the message to the Topic
	public void writeMsg(String msg) throws JMSException {
		// Creating a Text Message with the String object
		TextMessage txtMsg = pubSession.createTextMessage(msg);

		// Publishing the message object to the Topic
		tPub.publish(txtMsg);
	}
}
