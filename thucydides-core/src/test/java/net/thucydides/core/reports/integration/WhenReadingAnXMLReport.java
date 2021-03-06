package net.thucydides.core.reports.integration;

import net.thucydides.core.model.ConcreteTestStep;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import net.thucydides.core.model.TestStepGroup;
import net.thucydides.core.model.features.ApplicationFeature;
import net.thucydides.core.reports.xml.XMLTestOutcomeReporter;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

public class WhenReadingAnXMLReport {

    @Rule
    public TemporaryFolder temporaryDirectory = new TemporaryFolder();

    private XMLTestOutcomeReporter outcomeReporter;

    private File outputDirectory;

    @Before
    public void setupTestReporter() {
        outcomeReporter = new XMLTestOutcomeReporter();

        outputDirectory = temporaryDirectory.newFolder("target/thucydides");

        outcomeReporter.setOutputDirectory(outputDirectory);
    }

    @Test
    public void should_load_acceptance_test_report_from_xml_file() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
          + "  <test-step result='SUCCESS' screenshot='step_1.png'>\n"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        TestOutcome testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.getTitle(), is("Should do this"));
    }

    @Test
    public void should_load_test_step_details_from_xml_file() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
          + "  <test-step result='SUCCESS' screenshot='step_1.png'>\n"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        TestOutcome testOutcome = outcomeReporter.loadReportFrom(report);

        ConcreteTestStep testStep = (ConcreteTestStep) testOutcome.getTestSteps().get(0);
        assertThat(testOutcome.getTestSteps().size(), is(1));
        assertThat(testStep.getResult(), is(TestResult.SUCCESS));
        assertThat(testStep.getDescription(), is("step 1"));
        assertThat(testStep.getScreenshotPath(), is("step_1.png"));
    }


    @Test
    public void should_load_user_story_details_from_xml_file() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
          + "  <test-step result='SUCCESS' screenshot='step_1.png'>\n"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        TestOutcome testOutcome = outcomeReporter.loadReportFrom(report);
        assertThat(testOutcome.getUserStory(), is(Story.withId("net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory", "A user story")));
    }

    @Test
    public void should_load_feature_details_from_xml_file() throws Exception {
        String storedReportXML =
            "<acceptance-test-run title='Should do this' name='should_do_this' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
          + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story'>\n"
          + "    <feature id='myapp.myfeatures.SomeFeature' name='Some feature' />\n"
          + "  </user-story>"
          + "  <test-step result='SUCCESS' screenshot='step_1.png'>\n"
          + "    <description>step 1</description>\n"
          + "  </test-step>\n"
          + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        TestOutcome testOutcome = outcomeReporter.loadReportFrom(report);
        testOutcome.getFeature();

        ApplicationFeature expectedFeature = new ApplicationFeature("myapp.myfeatures.SomeFeature", "Some feature");
        assertThat(testOutcome.getFeature().getId(), is("myapp.myfeatures.SomeFeature"));
        assertThat(testOutcome.getFeature().getName(), is("Some feature"));
    }

    @Test
    public void should_load_acceptance_test_report_with_nested_groups_from_xml_file() throws Exception {
        String storedReportXML = 
            "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
            + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
            + "  <test-group name='Group 1' result='SUCCESS'>\n"
            + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
            + "      <test-group name='Group 1.1.1' result='SUCCESS'>\n"
            + "        <test-step result='SUCCESS'>\n"
            + "          <description>step 1</description>\n"
            + "        </test-step>\n"
            + "      </test-group>\n" 
            + "    </test-group>\n" 
            + "  </test-group>\n" 
            + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        TestOutcome testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.getTitle(), is("A nested test case"));
        
        TestStepGroup group1 = (TestStepGroup) testOutcome.getTestSteps().get(0);
        TestStepGroup group1_1 = (TestStepGroup) group1.getSteps().get(0);
        assertThat(testOutcome.getTestSteps().size(), is(1));
    }

    @Test
    public void should_load_acceptance_test_report_with_simple_nested_groups_from_xml_file() throws Exception {
        String storedReportXML = 
            "<acceptance-test-run title='A nested test case' name='a_nested_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
            + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
            + "  <test-group name='Group 1' result='SUCCESS'>\n"
            + "    <test-group name='Group 1.1' result='SUCCESS'>\n"
            + "      <test-step result='SUCCESS'>\n"
            + "        <description>step 1</description>\n"
            + "      </test-step>\n"
            + "    </test-group>\n" 
            + "  </test-group>\n" 
            + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        TestOutcome testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.getTitle(), is("A nested test case"));
        
        TestStepGroup group1 = (TestStepGroup) testOutcome.getTestSteps().get(0);
        TestStepGroup group1_1 = (TestStepGroup) group1.getSteps().get(0);
        assertThat(testOutcome.getTestSteps().size(), is(1));
    }


    @Test
    public void should_load_acceptance_test_report_with_multiple_test_steps_from_xml_file() throws Exception {
        String storedReportXML = "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n"
                + "  <test-step result='FAILURE'>\n"
                + "    <description>step 2</description>\n"
                + "  </test-step>\n"
                + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        TestOutcome testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.getTitle(), is("A simple test case"));
        assertThat(testOutcome.getTestSteps().size(), is(2));
        assertThat(testOutcome.getTestSteps().get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(testOutcome.getTestSteps().get(0).getDescription(), is("step 1"));
        assertThat(testOutcome.getTestSteps().get(1).getResult(), is(TestResult.FAILURE));
        assertThat(testOutcome.getTestSteps().get(1).getDescription(), is("step 2"));
    }

    @Test
    public void should_load_acceptance_test_report_with_top_level_requirement_from_xml_file()
            throws Exception {
        String storedReportXML = "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                + "  <requirements>\n"
                + "    <requirement>12</requirement>\n"
                + "  </requirements>\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n"
                + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        TestOutcome testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.getTitle(), is("A simple test case"));
        assertThat(testOutcome.getTestedRequirements().size(), is(1));
        assertThat(testOutcome.getTestedRequirements(), hasItem("12"));
        assertThat(testOutcome.getTestSteps().size(), is(1));
        assertThat(testOutcome.getTestSteps().get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(testOutcome.getTestSteps().get(0).getDescription(), is("step 1"));
    }

    @Test
    public void should_load_acceptance_test_report_with_top_level_requirements_from_xml_file()
            throws Exception {
        String storedReportXML = "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                + "  <requirements>\n"
                + "    <requirement>12</requirement>\n"
                + "    <requirement>32</requirement>\n"
                + "  </requirements>\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n" + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        TestOutcome testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.getTitle(), is("A simple test case"));
        assertThat(testOutcome.getTestedRequirements().size(), is(2));
        assertThat(testOutcome.getTestedRequirements(), hasItem("12"));
        assertThat(testOutcome.getTestedRequirements(), hasItem("32"));
        assertThat(testOutcome.getTestSteps().get(0).getResult(), is(TestResult.SUCCESS));
        assertThat(testOutcome.getTestSteps().get(0).getDescription(), is("step 1"));
    }

    @Test
    public void should_load_acceptance_test_report_with_step_level_requirements_from_xml_file()
            throws Exception {
        String storedReportXML = "<acceptance-test-run title='A simple test case' name='a_simple_test_case' steps='1' successful='1' failures='0' skipped='0' ignored='0' pending='0' result='SUCCESS'>\n"
                + "  <user-story id='net.thucydides.core.reports.integration.WhenGeneratingAnXMLReport.AUserStory' name='A user story' />\n"
                + "  <test-step result='SUCCESS'>\n"
                + "    <requirements>\n"
                + "      <requirement>12</requirement>\n"
                + "      <requirement>32</requirement>\n"
                + "    </requirements>\n"
                + "    <description>step 1</description>\n"
                + "  </test-step>\n" + "</acceptance-test-run>";

        File report = temporaryDirectory.newFile("saved-report.xml");
        FileUtils.writeStringToFile(report, storedReportXML);

        TestOutcome testOutcome = outcomeReporter.loadReportFrom(report);

        assertThat(testOutcome.getTestSteps().get(0).getTestedRequirements().size(), is(2));
        assertThat(testOutcome.getTestSteps().get(0).getTestedRequirements(), hasItem("12"));
        assertThat(testOutcome.getTestSteps().get(0).getTestedRequirements(), hasItem("32"));
    }
}
