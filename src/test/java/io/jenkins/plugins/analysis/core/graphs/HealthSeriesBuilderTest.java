package io.jenkins.plugins.analysis.core.graphs;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.analysis.Priority;
import io.jenkins.plugins.analysis.core.quality.HealthDescriptor;
import io.jenkins.plugins.analysis.core.quality.StaticAnalysisRun;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link HealthSeriesBuilder}.
 *
 * @author Mark Tripolt
 */
class HealthSeriesBuilderTest {

    // healthy und unhealthy sind die grenzen
    // array spiegelt die höhe der einzelnen zonen wieder
    // grenzen drüber drunter

    // 0 1 2 builds
    // x achse per datum statt per builds


    @Test
    void testTotalLargerThanUnhealthy() {
        StaticAnalysisRun staticAnalysisRun = mock(StaticAnalysisRun.class);
        when(staticAnalysisRun.getTotalSize()).thenReturn(20);

        HealthDescriptor healthDescriptor = new HealthDescriptor("4", "6", Priority.NORMAL);
        HealthSeriesBuilder healthSeriesBuilder = new HealthSeriesBuilder(healthDescriptor);

        assertThat(healthSeriesBuilder.computeSeries(staticAnalysisRun)).containsExactly(4, 2, 14);
    }

    @Test
    void testHealthySameAsTotal() {
        StaticAnalysisRun staticAnalysisRun = mock(StaticAnalysisRun.class);
        when(staticAnalysisRun.getTotalSize()).thenReturn(3);

        HealthDescriptor healthDescriptor = new HealthDescriptor("3", "5", Priority.NORMAL);
        HealthSeriesBuilder healthSeriesBuilder = new HealthSeriesBuilder(healthDescriptor);

        assertThat(healthSeriesBuilder.computeSeries(staticAnalysisRun)).containsExactly(3, 0, 0);
    }

    @Test
    void testDisabledHealthReport() {
        StaticAnalysisRun staticAnalysisRun = mock(StaticAnalysisRun.class);
        when(staticAnalysisRun.getTotalSize()).thenReturn(0);

        HealthDescriptor healthDescriptor = new HealthDescriptor("4", "0", Priority.NORMAL);
        HealthSeriesBuilder healthSeriesBuilder = new HealthSeriesBuilder(healthDescriptor);

        assertThat(healthSeriesBuilder.computeSeries(staticAnalysisRun)).containsExactly(0);
    }

}
