package net.thucydides.samples;

import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Title;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.UserStoryCode;
import net.thucydides.core.annotations.Managed;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@RunWith(ThucydidesRunner.class)
public class SampleTestScenario {
    
    @Managed
    public WebDriver webdriver;

    @ManagedPages(defaultUrl = "http://www.google.com")
    public Pages pages;
    
    @Steps
    public SampleScenarioSteps steps;

    @Title("Happy day scenario - fixes issues #123 and #456")
    @Test
    public void happy_day_scenario() {
        steps.anotherGroupOfSteps();
        steps.stepThree("e");
        steps.stepFour("f");
    }    

    @Test
    public void failing_scenario() {
        steps.groupOfStepsContainingAFailure();
        steps.anotherGroupOfSteps();
        steps.stepThatSucceeds();
    }    
	
}
