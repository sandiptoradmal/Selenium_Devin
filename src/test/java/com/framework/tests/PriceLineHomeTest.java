package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.pageobjects.PriceLineHomePage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PriceLineHomeTest extends BaseTest {

	@Test(description="Validate priceline home page opened")
	public void successfulLaunch()
	
		
	{
		
		PriceLineHomePage p= new PriceLineHomePage(getDriver());
		
		
	}
}
