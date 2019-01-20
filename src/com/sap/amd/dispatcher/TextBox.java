package com.sap.amd.dispatcher;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.sap.amd.utils.Log;

public class TextBox extends Element
{
	private static final long serialVersionUID = 7794972079873178944L;
	
	public TextBox(String id, Context context, WebDriver driver)
	{
		super(id, context, driver);
	}

	public void focus()
	{
		context.switchToThisContext(driver);

		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		driver.findElement(By.id(id)).click();
	}

	public void clear(boolean submit)
	{
		Log.write("Clearing textbox " + id + "...");
		
		context.switchToThisContext(driver);

		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		
		WebElement element = driver.findElement(By.id(id));
		
		element.clear();
		
		if (submit)
		{
			element.submit();
		}
	}

	public void write(String input, boolean submit)
	{		
		Log.write("Writing '" + input + "' to textbox '" + id + "'...");
		
		context.switchToThisContext(driver);
			
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		WebElement TextBox = driver.findElement(By.id(id));
		TextBox.sendKeys(input);

		if (submit)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e) { }
			
			//TextBox.submit();
			TextBox.sendKeys("\n");
		}
	}

	public String getText()
	{
		context.switchToThisContext(driver, true);
		
		try
		{
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		}
		catch (TimeoutException e)
		{
			return "";
		}
			
		return driver.findElement(By.id(id)).getAttribute("value");
	}
}
