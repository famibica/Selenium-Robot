package com.sap.amd.dispatcher;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.text.WordUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.sap.amd.Program;
import com.sap.amd.parsers.SpreadsheetParser;
import com.sap.amd.utils.Log;
import com.sap.amd.utils.html.Border;
import com.sap.amd.utils.html.Cell;
import com.sap.amd.utils.html.HtmlColor;
import com.sap.amd.utils.html.Row;

public class CrashEmail extends Email
{
	private static final long serialVersionUID = 721764227793042436L;
	
	private Exception exception;
	private String message;
	private String processor;
	private String session;
	List<String> interestedParties;
	
	public CrashEmail(Exception exception, String message, List<String> interestedParties, String processor, String session)
	{
		super();
		this.exception = (exception == null ? new Exception() : exception);
		this.message = (message == null ? "" : message);
		this.processor = (processor == null ? "" : processor);
		this.session = (session == null ? "" : session);
			if (interestedParties != null)
				this.interestedParties = interestedParties;
			else {
				Log.write("interestedParties is null (Crash Email), Add the User and Vitor Lemberck in list");
				this.interestedParties.add("vitor.lemberck@gmail.com");
				this.interestedParties.add(processor);
			}
	}
	
	
	public Address[] getArrayInterestedParties(List<String> interestedParties){

		List<InternetAddress> addresses = new LinkedList<InternetAddress>();
		
		for (int i = 0; i < interestedParties.size(); i++)
		{
			try
			{
				InternetAddress address = new InternetAddress(interestedParties.get(i), true);
				addresses.add(address);
			}
			catch (AddressException e)
			{
				Log.write("The provided email '" + interestedParties.get(i) + "' is not a supported email.");
			}
		}
	
		InternetAddress[] addressesArray = new InternetAddress[addresses.size()];
		
		for (int i = 0; i < addresses.size(); i++)
		{
			addressesArray[i] = addresses.get(i);
		}
	
		return addressesArray;
	}
	

	
	public void send()
	{
		Log.write("Composing email...");
		Date now = new Date();
		
		String text;
		String headerImage = "http://oi61.tinypic.com/21cw9p4.jpg";
		String footerImage = "http://oi60.tinypic.com/2hrgy88.jpg";
		String bgImage = "http://oi57.tinypic.com/2qby81e.jpg";
		
		String header = "<body background='" + bgImage + "'><CENTER><img src='" + headerImage+ "'><br/><br/></CENTER>" +
				"<font color='"+ "#333333" + "'face='" + "Arial" + "'size='" + 2 + "'>";
		
		text =	 "<CENTER><b>This is an error report sent from the Crash Dialog.<br/></b>" + 
		         "<br/><br/><b>User: </b>" + processor +
		         "<br/><b>Sent on: </b>" + new SimpleDateFormat("dd/MM/YYYY HH:mm:ss (zXXX)").format(now) +
		         "<br/><b>Log path:</b> .\\logs\\" + processor + "\\log" + session + ".txt" +
		         "<br/><br/><b>Error:</b> " + exception.toString() +
		         "<br/><br/>Message:<br/>" + WordUtils.wrap(message.replaceAll("\n", "<br/> "), 100) +
		         "<br/><br/>Best regards,</CENTER>";
		
		String footer =   "<br/><img src='" + footerImage+ "'></CENTER></html>";
		
		int espaceLines = (message.split("<br/>").length - 1) * 30;
		String textHtml = header + boxWhiteText(text, 100, 450 + espaceLines )+ footer;
		
		Log.write("Send process started...");
		
		super.send(textHtml, "text/html; charset=UTF-8", "QueueBuster Error Report", processor, getArrayInterestedParties(interestedParties));
		
		Log.write("Email sent.");
	}

	public void sendBugEmail(){
		Log.write("Composing email...");
		Date now = new Date();
		
		String text;
		String headerImage = "http://oi59.tinypic.com/3ehbc.jpg";
		String footerImage = "http://oi60.tinypic.com/2hrgy88.jpg";
		String bgImage = "http://oi61.tinypic.com/sq5etf.jpg";
		
		String header = "<body background='" + bgImage + "'><CENTER><img src='" + headerImage+ "'><br/><br/></CENTER>" +
		"<font color='"+ "#333333" + "'face='" + "Arial" + "'size='" + 2 + "'>";
		
		text = 	"<CENTER><b>Sent from user:</b> " + processor +
				"<br/><b>Sent on:</b> " + new SimpleDateFormat("dd/MM/YYYY HH:mm:ss (zXXX)").format(now) + 
			    "<br/><br/><b>Description :</b><br/>" + message.replaceAll("\n", "<br/>") + 
			    "<br/><br/>Best regards,</CENTER>";
		
		String footer =   "<br/><img src='" + footerImage+ "'></CENTER></html>";
		
		int espaceLines = (message.split("<br/>").length - 1) * 30;

		String textHtml = header + boxWhiteText(text, 100, 450 + espaceLines )+ footer;
		
		Log.write("Send process started...");
		super.send(textHtml, "text/html; charset=UTF-8", "QueueBuster Bug Report", processor, getArrayInterestedParties(interestedParties));
		Log.write("Email sent.");
		}
	
	
	
	public String boxWhiteText(String text, int width, int height){
			return  "<p><CENTER><div style='" + "background-color: white; height:" + height + "px; width:" 
					+ width + "px; float: center" + "'><br/>" + text + "<br/></div></p>";
	}
}
