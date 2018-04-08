package queue;

import javax.jms.*;
import javax.naming.*;

import java.io.*;

public class Publisher {
	private QueueConnectionFactory cf;
	private QueueSession session;
	private QueueConnection quc;
	private String queueName = "default_JMS_host/JVQueue";
	private String factoryName = "default_JMS_host/CFactory";
	private Queue que;

	public static void main(String[] args) throws NamingException, IOException, JMSException {
		System.out.println("begin publish messages --->");
		Publisher pub = new Publisher();
		pub.sendMessage("admin", "");
	}

	private void sendMessage(String uname, String pwd) throws NamingException, JMSException {
		InitialContext ctx = new InitialContext();
		cf = (QueueConnectionFactory) ctx.lookup(factoryName);
		que = (Queue) ctx.lookup(queueName);
		quc = cf.createQueueConnection(uname, pwd);
		session = quc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

		MessageProducer producer = session.createProducer(que);
		for(int i = 0; i < 5; i++){
			Message msg = session.createTextMessage("text body message: " + i);
			producer.send(msg);			
		}
		System.out.println("sended 5 messages");
	}
}
