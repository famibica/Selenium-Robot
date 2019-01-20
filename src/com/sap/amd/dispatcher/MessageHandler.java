package com.sap.amd.dispatcher;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;

import com.sap.amd.utils.Log;
import com.sap.amd.utils.types.TMessage;

public class MessageHandler implements Serializable
{
	private static final long serialVersionUID = -6601970073554421237L;

	protected Context context;
	protected WebDriver driver;
	private int timeout;

	/**
	 * Creates a new instance of the class MessageHandler
	 * @param driver the Selenium WebDriver that contains the messages to catch
	 * @param context the Context where these messages can be found
	 */
	public MessageHandler(WebDriver driver, Context context)
	{
		this.driver = driver;
		this.context = context;
		this.timeout = 5;
	}

	/**
	 * Creates a new instance of the class MessageHandler
	 * @param driver	the Selenium WebDriver that contains the messages to catch
	 * @param context	the Context where these messages can be found
	 * @param timeout	number of tries until the catches times out (in seconds)
	 */
	public MessageHandler(WebDriver driver, Context context, int timeout)
	{
		this.driver = driver;
		this.context = context;
		this.timeout = timeout;
	}
	
	public List<Message> catchMessages()
	{
		Log.write("Catching messages...");
		
		List<Message> messages = new LinkedList<Message>();

		context.switchToThisContext(driver);

		Document document = Jsoup.parse(driver.getPageSource());
		Elements elements = document.getElementsByAttributeValueStarting("id", "CRMMessageLine");
		
		int tries = 0;

		while (elements.size() == 0)
		{
			if (tries == timeout)
			{
				return messages;
			}
			document = Jsoup.parse(driver.getPageSource());
			elements = document.getElementsByAttributeValueStarting("id", "CRMMessageLine");
			tries++;
		}

		for (int i = 0; i < elements.size(); i++)
		{
			String mestype = elements.get(i).getElementsByTag("img").get(0).attr("alt");
			
			TMessage type;
			String text = null;
			
			List<Node> nodes = elements.get(i).childNodes();

			for (int j = 0; j < nodes.size(); j++)
			{
				if (nodes.get(j).attr("Title") != null)
				{
					text = nodes.get(j).attr("Title");
				}
			}

			if (mestype.equals("Error") || mestype.equals("E") || mestype.equals("X"))
			{
				type = TMessage.Error;
			}
			else if (mestype.equals("S"))
			{
				type = TMessage.Success;
			}
			else if (mestype.equals("Warning Message") || mestype.equals("W"))
			{
				type = TMessage.Warning;
			}
			else
			{
				type = TMessage.Information;
			}

			messages.add(new Message(type, text));
		}

		Log.write(messages.size() + " message(s) caught:");
		Log.write(messages);		
		
		return messages;
	}

	public Message catchMessage(String text)
	{
		List<Message> messages = catchMessages();
		
		for (int i = 0; i < messages.size(); i++)
		{
			if (messages.get(i).getMessage().contains(text))
			{
				return messages.get(i);
			}
		}
		
		return null;
	}

	public void setTimeout(int tries)
	{
		this.timeout = tries;
	}
	
	public void changeContext(Context context)
	{
		this.context = context;
	}
}