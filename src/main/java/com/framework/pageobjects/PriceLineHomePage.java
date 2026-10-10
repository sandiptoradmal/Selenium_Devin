package com.framework.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class PriceLineHomePage extends BasePage {

	public PriceLineHomePage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	private final By homepagelink =By.xpath("//*[@id='go-to-priceline-homepage']");
	
	public void openPricelineandvalidate()
	{
		
		
	}
}
