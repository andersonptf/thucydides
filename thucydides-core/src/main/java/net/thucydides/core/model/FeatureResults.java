package net.thucydides.core.model;

import com.google.common.collect.ImmutableList;
import net.thucydides.core.model.features.ApplicationFeature;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.sum;

/**
 * A set of test results related to a given feature.
 */
public class FeatureResults {
    private final ApplicationFeature feature;

    private List<StoryTestResults> storyTestResultsList;

    private ReportNamer namer;

    public FeatureResults(final ApplicationFeature feature) {
        this.feature = feature;
        this.namer = new ReportNamer(ReportNamer.ReportType.HTML);
        storyTestResultsList = new ArrayList<StoryTestResults>();
    }

    public ApplicationFeature getFeature() {
        return feature;
    }

    public void recordStoryResults(final StoryTestResults storyResults) {
        storyTestResultsList.add(storyResults);
    }

    public Integer getTotalTests() {
        return sum(extract(storyTestResultsList, on(StoryTestResults.class).getTotal())).intValue();
    }

    public Integer getPassingTests() {
        return sum(extract(storyTestResultsList, on(StoryTestResults.class).getSuccessCount())).intValue();
    }

    public Integer getFailingTests() {
        return sum(extract(storyTestResultsList, on(StoryTestResults.class).getFailureCount())).intValue();
    }

    public Integer getPendingTests() {
        return sum(extract(storyTestResultsList, on(StoryTestResults.class).getPendingCount())).intValue();
    }

    public Integer getTotalSteps() {
        return sum(extract(storyTestResultsList, on(StoryTestResults.class).getStepCount())).intValue();
    }

    public Integer getTotalStories() {
        return storyTestResultsList.size();
    }

    public List<StoryTestResults> getStoryResults() {
        return ImmutableList.copyOf(storyTestResultsList);
    }

    public String getStoryReportName() {
        return "stories_" + namer.getNormalizedTestNameFor(feature);
    }

    public int countStepsInSuccessfulTests() {
        if (storyTestResultsList.size() == 0) {
            return 0;
        }
        return sum(storyTestResultsList, on(StoryTestResults.class).countStepsInSuccessfulTests());
    }
}
