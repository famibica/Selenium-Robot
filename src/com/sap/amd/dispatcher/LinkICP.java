package com.sap.amd.dispatcher;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sap.amd.utils.Log;

public class LinkICP extends Element
{
	private static final long serialVersionUID = 2989211033735014840L;
	
	public LinkICP(String id, Context context, WebDriver driver)
	{
		super(id, context, driver);
	}

	public void click(boolean waitForSubmit)
	{
		Log.write("Clicking link " + id + "...");
		
		context.switchToThisContext(driver);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("C3_W12_V23_SavedSearches-btn")));
		
		//change to ICP needs with an IF(isBCP == false)
		driver.findElement(By.id("C3_W12_V23_SavedSearches-btn")).click();
		
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div#C3_W12_V23_SavedSearches__items ul li:nth-child(1)")));
		
		driver.findElement(By.cssSelector("div#C3_W12_V23_SavedSearches__items ul li:nth-child(1)")).click();
		driver.findElement(By.id("C3_W12_V23_QUICKSEARCH")).click();
		
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
