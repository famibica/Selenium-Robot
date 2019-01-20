package com.sap.amd.dispatcher;

import java.io.Serializable;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sap.amd.utils.Log;

public abstract class Element implements Serializable
{
	private static final long serialVersionUID = -6754601972848268175L;
	
	protected Context context;
	protected WebDriver driver;
	protected String id;
	protected WebDriverWait wait;
	
	public Element(String id, Context context, WebDriver driver)
	{
		this.driver = driver;
		this.context = context;
		this.id = id;
		this.wait = new WebDriverWait(driver, 120);
	}
	
	public Element(String id, Context context, WebDriver driver, int timeoutInSeconds)
	{
		this.driver = driver;
		this.context = context;
		this.id = id;
		this.wait = new WebDriverWait(driver, timeoutInSeconds);
	}
	
	public void changeContext(Context context)
	{
		this.context = context;
	}
	
	public WebDriver getDriver()
	{
		return driver;
	}
	
	public String getID()
	{
		return id;
	}
	
	public boolean updateContext(Context[] contexts)
	{
		Log.write("Updating context for " + id + "...");
		
		WebDriverWait wait = new WebDriverWait(driver, 1);

		for (int i = 0; i < contexts.length; i++)
		{
			contexts[i].switchToThisContext(driver, true, true, 10, 120);
			
			try
			{
				wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
				
				context = contexts[i];
				
				Log.write("Context updated to '" + contexts[i] + "'.");
				
				return true;
			}
			catch (Exception e) { }
		}
		
		Log.write("Last context was kept.");
		return false;
	}
	
	public void changeTimeout(int seconds)
	{
		this.wait = new WebDriverWait(driver, seconds);
	}
	
	protected void wait(int timeInSeconds)
	{
		WebDriverWait wait = new WebDriverWait(driver, timeInSeconds);
		
		try
		{
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("Таймоут")));
		}
		catch (Exception e) { }
	}
}
