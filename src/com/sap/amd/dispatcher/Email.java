package com.sap.amd.dispatcher;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.sap.amd.Program;
import com.sap.amd.utils.Log;

public class Email implements Serializable
{
	private static final long serialVersionUID = 685869044968959538L;

	private File file;
	private BodyPart calendarPart = null;
	
	public Email() { }
	
	public Email(File attachment)
	{
		this.file = attachment;
	}
	
	public void createICalendarAppointment (Date date) {
		try {
			String result = "";		
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmm'00'");
	
	        String timeZone = TimeZone.getDefault().getDisplayName();
	        Date create = new Date();        
	        Date start = date;
	        Date end = new Date();
	        end.setTime(start.getTime() + 300000);	        
	     
	        long lrawOffSet = TimeZone.getDefault().getRawOffset();
	        String srawOffSet = "";
	        if (lrawOffSet < 0) {
	            lrawOffSet *= (-1);
	            srawOffSet = "-";
	        }        
	        long minute = (lrawOffSet / (1000 * 60)) % 60;
	        long hour = (lrawOffSet / (1000 * 60 * 60)) % 24;
	        srawOffSet += String.format("%02d%02d", hour, minute);        
	        
	        lrawOffSet = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
	        String ssaveOffSet = "";
	        if (lrawOffSet < 0) {
	            lrawOffSet *= (-1);
	            ssaveOffSet = "-";
	        }
	        else {
	        	ssaveOffSet = "+";
	        }
	        minute = (lrawOffSet / (1000 * 60)) % 60;
	        hour = (lrawOffSet / (1000 * 60 * 60)) % 24;
	        ssaveOffSet += String.format("%02d%02d", hour, minute);
	        	        	       
	        String TzDstStartDate = "20150329T000000";
	        String TzDstEndDate = "20151025T000000";
	
	        result =
	                "BEGIN:VCALENDAR\n"
	                + "VERSION:2.0\n"
	                + "PRODID:QUEUE BUSTER\n"
	                + "METHOD:REQUEST\n"
	                + "BEGIN:VTIMEZONE\n"
	                + "TZID:" + timeZone + "\n"
	                + "BEGIN:STANDARD\n"
	                + "DTSTART:" + TzDstEndDate + "\n"
	                + "TZOFFSETFROM:" + ssaveOffSet + "\n"
	                + "TZOFFSETTO:" + srawOffSet + "\n"
	                + "END:STANDARD\n"
	                + "BEGIN:DAYLIGHT\n"
	                + "DTSTART:" + TzDstStartDate + "\n"
	                + "TZOFFSETFROM:" + srawOffSet + "\n"
	                + "TZOFFSETTO:" + ssaveOffSet + "\n"
	                + "END:DAYLIGHT\n"
	                + "END:VTIMEZONE\n"
	                + "BEGIN:VEVENT\n"
	                + "DTSTAMP;TZID=\"" + timeZone + "\":" + simpleDateFormat.format(create) + "\n"
	                + "DTSTART;TZID=\"" + timeZone + "\":" + simpleDateFormat.format(start) + "\n"
	                + "DTEND;TZID=\"" + timeZone + "\":" + simpleDateFormat.format(end) + "\n"
	                + "UID:324567548245636767834564768564\n"
	                + "BEGIN:VALARM\n"
	                + "ACTION:DISPLAY\n"
	                + "DESCRIPTION:Alarm\n"
	                + "TRIGGER;RELATED=START:-PT00H30M00S\n"
	                + "END:VALARM\n"
	                + "END:VEVENT \n"
	                + "END:VCALENDAR";
	        
	        this.calendarPart = new MimeBodyPart();
	        this.calendarPart.addHeader("Content-Class", "url:content-classes:calendarmessage");
	        this.calendarPart.setContent(result, "text/calendar;method=CANCEL");
	        
	        Log.write(result);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	
	}	
	
	public InternetAddress[] parseRecipients(String recipients)
	{
		try
		{
			return InternetAddress.parse(recipients);
		}
		catch (AddressException e)
		{
			Log.write(e);
			return new InternetAddress[]{};
		}
	}
	
	
	private File getDefaultProfileFolder(File[] folders)
	{
		for (File file: folders)
		{
            if (file.getName().endsWith(".default"))
            {
                return file;
            }
        }
		
		return null;
	}
	
	public String getProcessorEmail(String inumber)
	{
			Log.write("Retrieving email from user " + inumber + "...");
			File profileFolder = new File(System.getenv().get("APPDATA") + "\\Mozilla\\Firefox\\Profiles");
			profileFolder = getDefaultProfileFolder(profileFolder.listFiles());
	
	try{
		if (profileFolder != null)
		{
			FirefoxProfile profile = new FirefoxProfile(profileFolder);
			profile.setPreference("security.default_personal_cert", "Select Automatically"); // Select certificate automatically
			Log.write("Creating a WebDriver");
			WebDriver driver = new FirefoxDriver(profile);
						
			driver.get("https://people.wdf.sap.corp/profiles/" + inumber);
			
			WebElement element = driver.findElement(By.className("email_link"));
			WebElement aTagged = element.findElement(By.tagName("a"));
			String email = aTagged.getText();
			
			driver.quit();
			
			Log.write("Email returned :" + email);
			return email;
		
		}
		else
		{	
			Log.write("Email returned null");
			return null;
			}
		}
	
	catch(Exception e){
		Log.write("Email returned null by exception");
		return null;
	}
}
	
	public String getProcessorName(String inumber)
	{
			Log.write("Retrieving name from user " + inumber + "...");
			File profileFolder = new File(System.getenv().get("APPDATA") + "\\Mozilla\\Firefox\\Profiles");
			profileFolder = getDefaultProfileFolder(profileFolder.listFiles());
	
	try{
		if (profileFolder != null)
		{
			FirefoxProfile profile = new FirefoxProfile(profileFolder);
			profile.setPreference("security.default_personal_cert", "Select Automatically"); // Select certificate automatically
			Log.write("Creating a WebDriver");
			WebDriver driver = new FirefoxDriver(profile);
			
						
			driver.get("https://people.wdf.sap.corp/profiles/" + inumber);

			WebElement element = driver.findElement(By.className("info"));
			WebElement aTagged = element.findElement(By.tagName("header"));

			
			String name = aTagged.getText();
			driver.quit();
			
			Log.write("Name returned :" + name);
			return name;
		
		}
		else
		{	
			Log.write("Name returned null");
			return null;
			}
		}
	
	catch(Exception e){
		Log.write("Name returned null by exception");
		return null;
	}
}

	public void send(String content, String mimeContentType, String subject, String sender, Address[] recipients)
	{		
		Log.write("Email sending process started...");
		
		if (recipients.length > 0)
		{
			Log.write("Setting email properties...");
			
			Properties props = new Properties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.host", "mail.sap.corp");
			props.put("mail.smtp.port", "25");

			Log.write("Setting up session...");
			
			final String processorINumber = sender;
			String processorEmail = getProcessorEmail(processorINumber);
			
			Session session = Session.getInstance(props, new javax.mail.Authenticator()
			{
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(processorINumber, "");
				}
			});
			
			try
			{
				javax.mail.Message message = new MimeMessage(session);
				
				Log.write("Setting up email elements...");
				
				InternetAddress address;

				try
				{
					address = new InternetAddress(processorEmail);
				}
				catch (Exception e)
				{
					address = new InternetAddress("vitor.lemberck@sap.com");
				}
				
				message.setFrom(address);
				message.setRecipients(javax.mail.Message.RecipientType.TO, recipients);
				message.setSubject(subject);

				MimeMultipart multipart = new MimeMultipart();
				
				MimeBodyPart contentBodyPart = new MimeBodyPart();
				contentBodyPart.setContent(content, mimeContentType);
				contentBodyPart.setDisposition(Part.INLINE);
				multipart.addBodyPart(contentBodyPart);
				
				if (file != null)
				{
					MimeBodyPart attachment = new MimeBodyPart();
			        attachment.attachFile(file);
			        multipart.addBodyPart(attachment);
				}
				
				if (this.calendarPart != null) {
					multipart.addBodyPart(this.calendarPart);
				}
				
				message.setContent(multipart);

				Log.write("Setting up transport and trying to connect...");
				
				Transport transport = session.getTransport();
				transport.connect();

				Log.write("Sending email...");
				transport.sendMessage(message, message.getAllRecipients());

				Log.write("Process finished successfully.");
			}
			catch (MessagingException | IOException e)
			{
				Log.write("Process finished unexpectedly by an exception:");
				Log.write(e);
			}
		}
	}
	
	public void attach(File file)
	{
		this.file = file;
	}
}
