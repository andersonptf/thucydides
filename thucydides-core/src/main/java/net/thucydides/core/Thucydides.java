package net.thucydides.core;

import net.thucydides.core.annotations.TestCaseAnnotations;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepAnnotations;
import net.thucydides.core.steps.StepFactory;
import net.thucydides.core.steps.StepListener;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.WebDriverFactory;
import net.thucydides.core.webdriver.WebdriverManager;
import org.openqa.selenium.WebDriver;

import java.io.File;

/**
 * A utility class that provides services to initialize web testing and reporting-related fields in arbitrary objects.
 * It is designed to help integrate Thucydides into other testing tools such as Cucumber.
 */
public class Thucydides {

    private static final ThreadLocal<WebDriverFactory> factoryThreadLocal = new ThreadLocal<WebDriverFactory>();
    private static final ThreadLocal<WebdriverManager> webdriverManagerThreadLocal = new ThreadLocal<WebdriverManager>();
    private static final ThreadLocal<Pages> pagesThreadLocal = new ThreadLocal<Pages>();
    private static final ThreadLocal<StepFactory> stepFactoryThreadLocal = new ThreadLocal<StepFactory>();
    private static final ThreadLocal<StepListener> stepListenerThreadLocal = new ThreadLocal<StepListener>();

    /**
     * Initialize Thucydides-related fields in the specified object.
     * This includes managed WebDriver instances,
     */
    public static void initialize(final Object testCase) {
        setupWebDriverFactory();
        setupWebdriverManager();

        initPagesObjectUsing(getDriver());
        initStepListener();
        initStepFactoryUsing(getPages(), getStepListener());

        injectDriverInto(testCase);
        injectAnnotatedPagesObjectInto(testCase);
        injectScenarioStepsInto(testCase);

    }

    private static void initStepListener() {
        File outputDirectory = Configuration.loadOutputDirectoryFromSystemProperties();
        StepListener listener  = new BaseStepListener(outputDirectory, getPages());

        listener.getTestOutcomes();

        stepListenerThreadLocal.set(listener);
    }

    private static void setupWebDriverFactory() {
        factoryThreadLocal.set(new WebDriverFactory());
    }

    private static void initPagesObjectUsing(final WebDriver driver) {
        pagesThreadLocal.set(new Pages(driver));
    }

    private static void initStepFactoryUsing(final Pages pagesObject, StepListener listener) {
        StepFactory stepFactory = new StepFactory(pagesObject);
        stepFactoryThreadLocal.set(new StepFactory(pagesObject));

        stepFactory.addListener(listener);
    }

    /**
     * Instantiate the @Managed-annotated WebDriver instance with current WebDriver.
     */
    protected static void injectDriverInto(final Object testCase) {
        TestCaseAnnotations.forTestCase(testCase).injectDriver(getDriver());
    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     */
    protected static void injectScenarioStepsInto(final Object testCase) {
        StepAnnotations.injectScenarioStepsInto(testCase, getStepFactory());

    }

    /**
     * Instantiates the @ManagedPages-annotated Pages instance using current WebDriver.
     */
    protected static void injectAnnotatedPagesObjectInto(final Object testCase) {
        getPages().notifyWhenDriverOpens();
        StepAnnotations.injectAnnotatedPagesObjectInto(testCase, getPages());
    }

    /**
     * Indicate that the test run using this object is finished, and reports can be generated.
     */
    public static void done(final Object testClass) {
        getWebdriverManager().closeDriver();

    }

    protected static WebDriver getDriver() {
        return getWebdriverManager().getWebdriver();
    }

    protected static Pages getPages() {
        return pagesThreadLocal.get();
    }

    /**
     * Use a mock driver for testing purposes
     */
    protected static void useMockDriver(final WebDriver mockDriver) {
        setupWebdriverManager(new WebdriverManager(getWebDriverFactory()) {

            @Override
            public WebDriver getWebdriver() {
                return mockDriver;
            }
        });

    }

    protected static void stopUsingMockDriver() {
        setupWebdriverManager();
    }

    private static WebdriverManager getWebdriverManager() {
        return webdriverManagerThreadLocal.get();
    }

    private static WebDriverFactory getWebDriverFactory() {
        return factoryThreadLocal.get();
    }

    private static StepFactory getStepFactory() {
        return stepFactoryThreadLocal.get();
    }

    private static void setupWebdriverManager() {
        setupWebdriverManager(new WebdriverManager(getWebDriverFactory()));
    }

    private static void setupWebdriverManager(WebdriverManager webdriverManager) {
        webdriverManagerThreadLocal.set(webdriverManager);
    }

    private static WebDriver getWebDriver() {
        return getWebdriverManager().getWebdriver();
    }

    public static StepListener getStepListener() {
        return stepListenerThreadLocal.get();
    }
}
