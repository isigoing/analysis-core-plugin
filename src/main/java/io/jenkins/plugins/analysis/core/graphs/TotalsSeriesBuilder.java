package io.jenkins.plugins.analysis.core.graphs;

import java.util.ArrayList;
import java.util.List;

import io.jenkins.plugins.analysis.core.steps.AnalysisResult;

/**
 * FIXME: write comment.
 *
 * @author Ullrich Hafner
 */
public class TotalsSeriesBuilder extends SeriesBuilder {
    @Override
    protected List<Integer> computeSeries(final AnalysisResult current) {
        List<Integer> series = new ArrayList<>();
        series.add(current.getNumberOfWarnings());
        return series;
    }

}