package net.thucydides.core.reports.json;

import net.thucydides.core.model.FeatureResults;
import net.thucydides.core.model.StoryTestResults;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestResult;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static java.awt.Color.BLACK;
import static java.awt.Color.GRAY;
import static java.awt.Color.GREEN;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.awt.Color.YELLOW;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class WhenDeterminingTheColorOfAFeatureByRelativeSize extends AbstractColorSchemeTest {

    ColorScheme colorScheme;

    private static final Color PALE_YELLOW_GREEN = new Color(143,255,32);
    private static final Color PALE_YELLOW = new Color(255,255,64);
    private static final Color LIGHT_ORANGE = new Color(255,170,0);
    private static final Color LIGHT_ORANGE_YELLOW = new Color(255,184,61);
    private static final Color DARK_ORANGE = new Color(255,48,48);

    @Before
    public void createColorScheme() {
        colorScheme = new RelativeSizeColorScheme();
    }

    @Test
    public void a_feature_with_no_tests_should_be_black() {
        FeatureResults feature = mockFeatureResults(WidgetFeature.class,100, 10, 0, 0,0,0);
        Color color = colorScheme.colorFor(feature);

        assertThat(color, is(BLACK));
    }

    @Test
    public void a_completely_failing_feature_should_be_red() {
        FeatureResults feature = mockFeatureResults(WidgetFeature.class,100, 10, 20, 0,0,20);
        Color color = colorScheme.colorFor(feature);

        assertThat(color, is(RED));
    }

    @Test
    public void a_completely_passing_feature_should_be_green() {
        FeatureResults feature = mockFeatureResults(WidgetFeature.class,100, 10, 20, 20,0,0);
        Color color = colorScheme.colorFor(feature);

        assertThat(color, is(GREEN));
    }

    @Test
    public void a_mostly_pending_feature_with_a_failing_tests_should_be_orange() {
        FeatureResults feature = mockFeatureResults(WidgetFeature.class,100, 10, 20, 10,0,10);
        Color color = colorScheme.colorFor(feature);

        assertThat(color, is(LIGHT_ORANGE));
    }

    @Test
    public void a_pending_feature_with_a_failing_tests_should_be_orange_yellow() {
        FeatureResults feature = mockFeatureResults(WidgetFeature.class,100, 10, 20, 0,19,1);
        Color color = colorScheme.colorFor(feature);

        assertThat(color, is(LIGHT_ORANGE_YELLOW));
    }

    @Test
    public void a_completely_pending_feature_should_be_pale_yellow() {
        FeatureResults feature = mockFeatureResults(WidgetFeature.class,100, 10, 20, 0,20,0);
        Color color = colorScheme.colorFor(feature);

        assertThat(color, is(PALE_YELLOW));
    }


    @Test
     public void a_completely_failing_story_should_be_red() {
         StoryTestResults story = mockStory(20, 0,0,20);
         Color color = colorScheme.colorFor(story);

         assertThat(color, is(RED));
     }

     @Test
     public void a_completely_passing_story_should_be_green() {
         StoryTestResults story = mockStory(20, 20, 0, 0);
         Color color = colorScheme.colorFor(story);

         assertThat(color, is(GREEN));
     }

    @Test
    public void a_completely_pending_story_should_be_pale_yellow() {
        StoryTestResults story = mockStory(20, 0, 20, 0);
        Color color = colorScheme.colorFor(story);

        assertThat(color, is(PALE_YELLOW));
    }

    @Test
    public void a_feature_with_pending_and_passing_tests_should_be_pale_yellow_green() {
        FeatureResults feature = mockFeatureResults(WidgetFeature.class,100, 10, 20, 10,10,0);
        Color color = colorScheme.colorFor(feature);

        assertThat(color, is(PALE_YELLOW_GREEN));
    }

    @Test
    public void a_story_with_pending_and_passing_tests_should_be_pale_yellow_green() {
        StoryTestResults story = mockStory(20, 10, 10, 0);
        Color color = colorScheme.colorFor(story);

        assertThat(color, is(PALE_YELLOW_GREEN));
    }

    @Test
    public void a_successful_test_step_should_be_green() {
        Color color = colorScheme.colorFor(mockTestStep(TestResult.SUCCESS));
        assertThat(color, is(GREEN));
    }

    @Test
    public void a_failing_test_step_should_be_red() {
        Color color = colorScheme.colorFor(mockTestStep(TestResult.FAILURE));
        assertThat(color, is(RED));
    }

    @Test
    public void a_pending_test_step_should_be_yellow() {
        Color color = colorScheme.colorFor(mockTestStep(TestResult.PENDING));
        assertThat(color, is(YELLOW));
    }

    @Test
    public void a_skipped_test_step_should_be_grey() {
        Color color = colorScheme.colorFor(mockTestStep(TestResult.SKIPPED));
        assertThat(color, is(GRAY));
    }

    @Test
    public void an_ignored_test_step_should_be_orange() {
        Color color = colorScheme.colorFor(mockTestStep(TestResult.IGNORED));
        assertThat(color, is(ORANGE));
    }

    @Test
    public void a_successful_test_outcome_should_be_green() {
        TestOutcome outcome = mockTestOutcome(10, TestResult.SUCCESS);
        Color color = colorScheme.colorFor(outcome);

        assertThat(color, is(GREEN));
    }

    @Test
    public void a_failing_test_outcome_should_be_red() {
        TestOutcome outcome = mockTestOutcome(10, TestResult.FAILURE);
        Color color = colorScheme.colorFor(outcome);

        assertThat(color, is(RED));
    }


    @Test
    public void a_pending_test_outcome_should_be_yellow() {
        TestOutcome outcome = mockTestOutcome(10, TestResult.PENDING);
        Color color = colorScheme.colorFor(outcome);

        assertThat(color, is(YELLOW));
    }

    @Test
    public void a_skipped_test_outcome_should_be_grey() {
        TestOutcome outcome = mockTestOutcome(10, TestResult.SKIPPED);
        Color color = colorScheme.colorFor(outcome);

        assertThat(color, is(GRAY));
    }

    @Test
    public void an_ignored_test_outcome_should_be_orange() {
        TestOutcome outcome = mockTestOutcome(10, TestResult.IGNORED);
        Color color = colorScheme.colorFor(outcome);

        assertThat(color, is(ORANGE));
    }
}