package de.fraunhofer.iosb.messaginghelpers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Properties;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;


public class MhTest {

	@Test
	public void testInitConnectionTableState() {
		Properties props = new Properties();
		PropertyBasedClientSetup jmsHelper = null;
		jmsHelper = new PropertyBasedClientSetup(props);

		try
		{
			boolean b = jmsHelper.initConnection();
			fail("initConnection call in wrong state was not detected");
		}
		catch (IllegalStateException expected) { /* do nothing */ }

	}

	@Test
	public void testDisconnectTableState() {
		Properties props = new Properties();
		PropertyBasedClientSetup jmsHelper = null;
		jmsHelper = new PropertyBasedClientSetup(props);

		  try
		  {
			  boolean b = jmsHelper.disconnect();
				fail("disconnect call in wrong state was not detected");
		  }
		  catch (IllegalStateException expected) { /* do nothing */ }

	}

	@Test
	public void testInitSessionTableState() {
		Properties props = new Properties();
		PropertyBasedClientSetup jmsHelper = null;
		jmsHelper = new PropertyBasedClientSetup(props);

		  try
		  {
			  boolean b = jmsHelper.initSession();
				fail("initSession call in wrong state was not detected");
		  }
		  catch (IllegalStateException expected) { /* do nothing */ }

	}

	@Test
	public void testSetupQueueListenerTableState() {
		Properties props = new Properties();
		PropertyBasedClientSetup jmsHelper = null;
		jmsHelper = new PropertyBasedClientSetup(props);

		  try
		  {
			  MessageListener listener = null;
			  boolean b = jmsHelper.setupQueueListener("dummy", listener);
				fail("setupQueueListener call in wrong state was not detected");
		  }
		  catch (IllegalStateException expected) { /* do nothing */ }

	}

	@Test
	public void testSetupTopicListenerTableState() {
		Properties props = new Properties();
		PropertyBasedClientSetup jmsHelper = null;
		jmsHelper = new PropertyBasedClientSetup(props);

		  try
		  {
			  MessageListener listener = null;
			  boolean b = jmsHelper.setupTopicListener("dummy", listener);
				fail("setupTopicListener call in wrong state was not detected");
		  }
		  catch (IllegalStateException expected) { /* do nothing */ }

	}

	@Test
	public void testSetupQueueProducerTableState() {
		Properties props = new Properties();
		PropertyBasedClientSetup jmsHelper = null;
		jmsHelper = new PropertyBasedClientSetup(props);

		  try
		  {
			  MessageProducer mp = jmsHelper.setupQueueProducer("dummy");
				fail("setupQueueProducer call in wrong state was not detected");
		  }
		  catch (IllegalStateException expected) { /* do nothing */ }

	}

	@Test
	public void testSetupTopicProducerTableState() {
		Properties props = new Properties();
		PropertyBasedClientSetup jmsHelper = null;
		jmsHelper = new PropertyBasedClientSetup(props);

		  try
		  {
			  MessageProducer mp = jmsHelper.setupTopicProducer("dummy");
				fail("setupTopicProducer call in wrong state was not detected");
		  }
		  catch (IllegalStateException expected) { /* do nothing */ }

	}

	@Test
	public void testCreateTextMessage() {
		Properties props = new Properties();
		PropertyBasedClientSetup jmsHelper = null;
		jmsHelper = new PropertyBasedClientSetup(props);

		  try
		  {
			  Message mp = jmsHelper.createTextMessage("dummy");
				fail("setupTopicProducer call in wrong state was not detected");
		  }
		  catch (IllegalStateException expected) { /* do nothing */ }

	}

}
