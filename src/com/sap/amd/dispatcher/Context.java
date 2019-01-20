package com.sap.amd.dispatcher;

import java.io.Serializable;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sap.amd.utils.Log;
import com.sap.amd.utils.StringUtils;

public class Context implements Serializable
{
	private static final long serialVersionUID = 2348415021574225197L;
	
	private String[] path;
	private String window;

	public Context(String window)
	{
		this.window = window;
		path = new String[]{};
	}
	
	public Context(String[] frames, String window)
	{
		this.path = frames;
		this.window = window;
	}
	
	public void switchToThisContext(WebDriver driver, boolean waitForFrames, boolean waitForMainFrame, int timeout, int mainTimeout)
	{
		Log.write("Switching to another context...");
		
		try
		{
			if (!driver.getWindowHandle().equals(window))
				driver.switchTo().window(window);
		}
		catch (Exception e)
		{
			driver.switchTo().window(window);
		}

		driver.switchTo().defaultContent();
		
		for (int i = 0; i < path.length; i++)
		{
			try
			{
				if (path[i].equals("@parentFrame"))
				{
					driver.switchTo().parentFrame();
				}
				else
				{	
					if (i == 0 && waitForMainFrame)
					{
						new WebDriverWait(driver, mainTimeout).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(path[i]));
					}
					else if (waitForFrames && i > 0)
					{
						new WebDriverWait(driver, timeout).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(path[i]));
					}
					else
					{
						driver.switchTo().frame(path[i]);
					}
				}
				
				Log.write("Switched to '" + path[i] + "'.");
			}
			catch (Exception e)
			{
				Log.write("Could not switch to frame '" + path[i] + "' due to the following exception:");
				Log.write(e);
			}
		}
	}
	
	public void switchToThisContext(WebDriver driver, boolean waitForFrames, boolean waitForMainFrame)
	{
		switchToThisContext(driver, waitForFrames, waitForMainFrame, 120, 120);
	}
	
	public void switchToThisContext(WebDriver driver, boolean waitForFrames)
	{
		switchToThisContext(driver, waitForFrames, false, 120, 120);
	}
	
	public void switchToThisContext(WebDriver driver)
	{
		switchToThisContext(driver, false, false, 120, 120);
	}
	
	public String toString()
	{
		return StringUtils.join("/", path);
	}
}
