package io.jenkins.plugins.analysis.core.graphs;

import java.util.ArrayList;
import java.util.List;

import io.jenkins.plugins.analysis.core.quality.HealthDescriptor;
import io.jenkins.plugins.analysis.core.steps.AnalysisResult;

/**
 * FIXME: write comment.
 *
 * @author Ullrich Hafner
 */
public class HealthSeriesBuilder extends SeriesBuilder {
    private HealthDescriptor healthDescriptor;

    public HealthSeriesBuilder(final HealthDescriptor healthDescriptor) {
        this.healthDescriptor = healthDescriptor;
    }

    @Override
    protected List<Integer> computeSeries(final AnalysisResult current) {
        List<Integer> series = new ArrayList<>();
        int remainder = current.getNumberOfAnnotations();

        if (healthDescriptor.isEnabled()) {
            series.add(Math.min(remainder, healthDescriptor.getHealthy()));

            int range = healthDescriptor.getUnHealthy() - healthDescriptor.getHealthy();
            remainder -= healthDescriptor.getHealthy();
            if (remainder > 0) {
                series.add(Math.min(remainder, range));
            }
            else {
                series.add(0);
            }

            remainder -= range;
            if (remainder > 0) {
                series.add(remainder);
            }
            else {
                series.add(0);
            }
        }
        else { // at least a graph should be shown if the health reporting has been disabled in the meantime
            series.add(remainder);
        }

        return series;
    }
}