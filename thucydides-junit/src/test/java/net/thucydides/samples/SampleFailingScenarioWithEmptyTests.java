package net.thucydides.samples;

import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.runners.ThucydidesRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(ThucydidesRunner.class)
public class SampleFailingScenarioWithEmptyTests {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleFailingScenarioWithEmptyTests.class);

    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;
    
    @Steps
    public SampleScenarioSteps steps;

    @Test
    public void happy_day_scenario() throws Throwable {
        throw new AssertionError("Failure without any steps.");
    }
}
