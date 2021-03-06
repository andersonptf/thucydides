package net.thucydides.core.reports.xml;

import com.google.common.base.Preconditions;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestStepGroup;
import net.thucydides.core.model.features.ApplicationFeature;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

/**
 * XStream converter used to generate the XML acceptance test report.
 *
 * @author johnsmart
 */
public class TestOutcomeConverter implements Converter {

    private static final String TITLE_FIELD = "title";
    private static final String NAME_FIELD = "name";
    private static final String ID_FIELD = "id";
    private static final String STEPS_FIELD = "steps";
    private static final String SUCCESSFUL_FIELD = "successful";
    private static final String FAILURES_FIELD = "failures";
    private static final String SKIPPED_FIELD = "skipped";
    private static final String IGNORED_FIELD = "ignored";
    private static final String PENDING_FIELD = "pending";
    private static final String RESULT_FIELD = "result";
    private static final String TEST_GROUP = "test-group";
    private static final String TEST_STEP = "test-step";
    private static final String USER_STORY = "user-story";
    private static final String FEATURE = "feature";
    private static final String REQUIREMENTS = "requirements";
    private static final String REQUIREMENT = "requirement";
    private static final String EXCEPTION = "exception";
    private static final String ERROR = "error";
    private static final String SCREENSHOT_FIELD = "screenshot";
    private static final String DESCRIPTION = "description";

    private transient String qualifier;

    public TestOutcomeConverter() {
    }

    public TestOutcomeConverter(final String qualifier) {
        this();
        this.qualifier = qualifier;
    }

    /**
     * Determines which classes this converter applies to.
     */
    @SuppressWarnings("rawtypes")
    public boolean canConvert(final Class clazz) {
        return TestOutcome.class.isAssignableFrom(clazz);
    }

    /**
     * Generate an XML report given an TestOutcome object.
     */
    public void marshal(final Object value, final HierarchicalStreamWriter writer,
                        final MarshallingContext context) {
        TestOutcome testOutcome = (TestOutcome) value;
        Preconditions.checkNotNull(testOutcome, "The test run was null - WTF?");

        writer.addAttribute(TITLE_FIELD, titleFrom(testOutcome));
        writer.addAttribute(NAME_FIELD, nameFrom(testOutcome));
        writer.addAttribute(STEPS_FIELD, Integer.toString(testOutcome.countTestSteps()));
        writer.addAttribute(SUCCESSFUL_FIELD, Integer.toString(testOutcome.getSuccessCount()));
        writer.addAttribute(FAILURES_FIELD, Integer.toString(testOutcome.getFailureCount()));
        writer.addAttribute(SKIPPED_FIELD, Integer.toString(testOutcome.getSkippedCount()));
        writer.addAttribute(IGNORED_FIELD, Integer.toString(testOutcome.getIgnoredCount()));
        writer.addAttribute(PENDING_FIELD, Integer.toString(testOutcome.getPendingCount()));
        writer.addAttribute(RESULT_FIELD, testOutcome.getResult().toString());
        addUserStoryTo(writer, testOutcome.getUserStory());
        addRequirementsTo(writer, testOutcome.getTestedRequirements());

        List<TestStep> steps = testOutcome.getTestSteps();
        for (TestStep step : steps) {
            writeStepTo(writer, step);
        }
    }


    private String titleFrom(final TestOutcome testOutcome) {
        if (qualifier == null) {
            return testOutcome.getTitle();
        } else {
            return testOutcome.getTitle() + " [" + humanized(qualifier) + "]";
        }
    }

    private String humanized(final String text) {
        return text.replaceAll("_", "/");
    }

    private String nameFrom(final TestOutcome testOutcome) {
        String baseName = null;
        if (testOutcome.getMethodName() != null) {
            baseName = testOutcome.getMethodName();
        } else {
            baseName = testOutcome.getTitle();
        }
        String testRunName;
        if (qualifier == null) {
            testRunName = baseName;
        } else {
            String qualifierWithoutSpaces = qualifier.replaceAll(" ", "_");
            testRunName = baseName + "_" + qualifierWithoutSpaces;
        }
        return testRunName;
    }


    private void writeStepTo(final HierarchicalStreamWriter writer, final TestStep step) {
        if (step instanceof TestStepGroup) {
            writer.startNode(TEST_GROUP);
            writer.addAttribute(NAME_FIELD, step.getDescription());
            writeResult(writer, step);
            writeScreenshotIfPresent(writer, step);

            List<TestStep> nestedSteps = ((TestStepGroup) step).getSteps();
            for (TestStep nestedStep : nestedSteps) {
                writeStepTo(writer, nestedStep);
            }
            writer.endNode();
        } else {
            ConcreteTestStep concreteStep = (ConcreteTestStep) step;
            writer.startNode(TEST_STEP);
            writeResult(writer, concreteStep);
            writeScreenshotIfPresent(writer, concreteStep);
            addRequirementsTo(writer, step.getTestedRequirements());
            writeDescription(writer, concreteStep);
            writeErrorForFailingTest(writer, concreteStep);
            writer.endNode();
        }
    }

    private void addUserStoryTo(final HierarchicalStreamWriter writer, final Story userStory) {
        if (userStory != null) {
            writer.startNode(USER_STORY);
            writer.addAttribute(ID_FIELD, userStory.getId());
            writer.addAttribute(NAME_FIELD, userStory.getName());
            if (userStory.getFeatureClass() != null) {
                writeFeatureNode(writer, userStory);
            }
            writer.endNode();
        }
    }

    private void writeFeatureNode(HierarchicalStreamWriter writer, Story userStory) {
        ApplicationFeature feature = ApplicationFeature.from(userStory.getFeatureClass());
        writer.startNode(FEATURE);
        writer.addAttribute(ID_FIELD, feature.getId());
        writer.addAttribute(NAME_FIELD, feature.getName());
        writer.endNode();
    }

    private void addRequirementsTo(final HierarchicalStreamWriter writer, final Set<String> set) {
        if (!set.isEmpty()) {
            writer.startNode(REQUIREMENTS);
            for (String requirement : set) {
                writer.startNode(REQUIREMENT);
                writer.setValue(requirement);
                writer.endNode();
            }
            writer.endNode();
        }
    }

    private void writeErrorForFailingTest(final HierarchicalStreamWriter writer, final ConcreteTestStep step) {
        if (step.isFailure()) {
            writeErrorMessageAndException(writer, step);
        }

    }

    private void writeErrorMessageAndException(final HierarchicalStreamWriter writer,
                                               final ConcreteTestStep step) {
        if (step.getErrorMessage() != null) {
            writeErrorMessageNode(writer, step.getErrorMessage());
            if (step.getException() != null) {
                writeExceptionNode(writer, step.getException());
            }
        }
    }

    private void writeExceptionNode(final HierarchicalStreamWriter writer, final Throwable cause) {
        writer.startNode(EXCEPTION);
        StringWriter stringWriter = new StringWriter();
        cause.printStackTrace(new PrintWriter(stringWriter));
        writer.setValue(stringWriter.toString());
        writer.endNode();
    }

    private void writeErrorMessageNode(final HierarchicalStreamWriter writer,
                                       final String errorMessage) {
        writer.startNode(ERROR);
        writer.setValue(errorMessage);
        writer.endNode();
    }

    private void writeScreenshotIfPresent(final HierarchicalStreamWriter writer, final TestStep step) {
        if (step.getScreenshot() != null) {
            writer.addAttribute(SCREENSHOT_FIELD, step.getScreenshot().getName());
        }
    }

    private void writeResult(final HierarchicalStreamWriter writer, final TestStep step) {
        writer.addAttribute(RESULT_FIELD, step.getResult().toString());
    }

    private void writeDescription(final HierarchicalStreamWriter writer, final TestStep step) {
        writer.startNode(DESCRIPTION);
        writer.setValue(step.getDescription());
        writer.endNode();
    }

    /**
     * Convert XML to an TestOutcome object. Not needed for now.
     */
    public Object unmarshal(final HierarchicalStreamReader reader,
                            final UnmarshallingContext context) {

        TestOutcome testOutcome = new TestOutcome();
        testOutcome.setMethodName(reader.getAttribute(NAME_FIELD));
        testOutcome.setTitle(reader.getAttribute(TITLE_FIELD));
        readChildren(reader, testOutcome);
        return testOutcome;
    }

    private void readChildren(final HierarchicalStreamReader reader, final TestOutcome testOutcome) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(TEST_STEP)) {
                readTestStep(reader, testOutcome);
            } else if (childNode.equals(TEST_GROUP)) {
                readTestGroup(reader, testOutcome);
            } else if (childNode.equals(REQUIREMENTS)) {
                readTestRunRequirements(reader, testOutcome);
            } else if (childNode.equals(USER_STORY)) {
                readUserStory(reader, testOutcome);
            }
            reader.moveUp();
        }
    }


    private void readUserStory(final HierarchicalStreamReader reader,
                               final TestOutcome testOutcome) {

        String storyId = reader.getAttribute(ID_FIELD);
        String storyName = reader.getAttribute(NAME_FIELD);
        ApplicationFeature feature = null;

        if (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(FEATURE)) {
                feature = readFeature(reader);
            }
            reader.moveUp();
        }
        Story story = null;
        if (feature == null) {
            story = Story.withId(storyId, storyName);
        } else {
            story = Story.withId(storyId, storyName, feature.getId(), feature.getName());
        }
        testOutcome.setUserStory(story);
    }

    private ApplicationFeature readFeature(final HierarchicalStreamReader reader) {

        String featureId = reader.getAttribute(ID_FIELD);
        String featureName = reader.getAttribute(NAME_FIELD);
        return new ApplicationFeature(featureId, featureName);
    }


    private void readTestRunRequirements(final HierarchicalStreamReader reader,
                                         final TestOutcome testOutcome) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String requirement = reader.getValue();
            testOutcome.testsRequirement(requirement);
            reader.moveUp();
        }
    }

    private void readTestStepRequirements(final HierarchicalStreamReader reader, final TestStep step) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String requirement = reader.getValue();
            step.testsRequirement(requirement);
            reader.moveUp();
        }
    }

    /*
     * <test-step result="SUCCESS"> <description>The customer navigates to the
     * metro masthead site.</description>
     * <screenshot>the_customer_navigates_to_the_metro_masthead_site2
     * .png</screenshot> </test-step>
     */
    private void readTestStep(final HierarchicalStreamReader reader, final TestOutcome testOutcome) {
        ConcreteTestStep step = new ConcreteTestStep();
        String testResultValue = reader.getAttribute(RESULT_FIELD);
        TestResult result = TestResult.valueOf(testResultValue);
        step.setResult(result);
        String screenshot = reader.getAttribute(SCREENSHOT_FIELD);
        if (screenshot != null) {
            step.setScreenshotPath(screenshot);
        }
        readTestStepChildren(reader, step);

        testOutcome.recordStep(step);
    }

    private void readTestGroup(final HierarchicalStreamReader reader, final TestOutcome testOutcome) {
        String name = reader.getAttribute(NAME_FIELD);
        String screenshot = reader.getAttribute(SCREENSHOT_FIELD);
        String testResultValue = reader.getAttribute(RESULT_FIELD);
        TestResult result = TestResult.valueOf(testResultValue);
        testOutcome.startGroup(name);
        testOutcome.getCurrentGroup().setScreenshotPath(screenshot);
        testOutcome.getCurrentGroup().setResult(result);
        readChildren(reader, testOutcome);
        testOutcome.endGroup();
    }

    private void readTestStepChildren(final HierarchicalStreamReader reader, final ConcreteTestStep step) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String childNode = reader.getNodeName();
            if (childNode.equals(DESCRIPTION)) {
                step.setDescription(reader.getValue());
            } else if (childNode.equals(REQUIREMENTS)) {
                readTestStepRequirements(reader, step);
            }
            reader.moveUp();
        }
    }
}