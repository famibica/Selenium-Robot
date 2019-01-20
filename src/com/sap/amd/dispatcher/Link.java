package com.sap.amd.dispatcher;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sap.amd.utils.Log;

public class Link extends Element
{
	private static final long serialVersionUID = 2989211033735014840L;
	
	public Link(String id, Context context, WebDriver driver)
	{
		super(id, context, driver);
	}

	public void click(boolean waitForSubmit)
	{
		Log.write("Clicking link " + id + "...");
		
		context.switchToThisContext(driver);
		
		wait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(id)));
		
		//change to ICP needs with an IF(isBCP == false)
		driver.findElement(By.partialLinkText(id)).click();
		
		if (waitForSubmit)
		{
			try
			{
				new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOfElementLocated(By.id("submitInProgress")));
			}
			catch (TimeoutException e) { }
			
			new WebDriverWait(driver, 90).until(ExpectedConditions.invisibilityOfElementLocated(By.id("submitInProgress")));
		}
	}
}
