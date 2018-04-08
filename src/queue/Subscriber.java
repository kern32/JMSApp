package queue;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Subscriber implements MessageListener{
	private QueueConnectionFactory cf;
	private QueueSession session;
	private QueueConnection quc;
	private String queueName = "default_JMS_host/JVQueue";
	private String factoryName = "default_JMS_host/CFactory";
	private Queue que;

	public static void main(String[] args) throws NamingException, IOException, JMSException {
		System.out.println("begin subsription --->");
		Subscriber sub = new Subscriber();
		sub.begin("admin", "");
	} 

	private void begin(String uname, String pwd) throws NamingException, JMSException {
		InitialContext ctx = new InitialContext();
		cf = (QueueConnectionFactory) ctx.lookup(factoryName);
		que = (Queue)ctx.lookup(queueName);
		quc = cf.createQueueConnection(uname, pwd);
		session = quc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		
		MessageConsumer consumer = session.createConsumer(que);
		consumer.setMessageListener(this);
		quc.start();
	}

	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			System.out.println("received message: " + textMessage.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
