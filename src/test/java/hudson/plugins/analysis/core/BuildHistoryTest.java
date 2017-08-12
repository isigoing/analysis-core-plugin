package hudson.plugins.analysis.core;

import java.util.NoSuchElementException;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import hudson.model.Result;
import hudson.model.Run;
import hudson.plugins.analysis.util.model.AnnotationContainer;

/**
 * Tests the class {@link BuildHistory}.
 *
 * @author Ulli Hafner
 */
@SuppressWarnings("rawtypes")
public class BuildHistoryTest {
    /**
     * Verifies that we have no results for the first build.
     */
    @Test(expected = NoSuchElementException.class)
    public void testNoPreviousResult() {
        HistoryProvider history = createHistoryProvider(mockBuild());
        assertFalse("Build has a previous result", history.hasPreviousResult());

        ReferenceProvider referenceProvider = createReferenceProvider(mockBuild());
        assertEquals("Build has wrong reference annotations", 0,
                referenceProvider.getIssues().getNumberOfAnnotations());

        history.getPreviousResult();
    }

    /**
     * Verifies that we find the correct results for the following constellation.
     * <ol>
     * <li>Build with result</li>
     * <li>Build with no result</li>
     * <li>Baseline</li>
     * </ol>
     */
    @Test
    public void testHasPreviousResult() {
        Run withResult = mockBuild();
        Run noResult = mockBuild();
        Run baseline = mockBuild();

        when(baseline.getPreviousBuild()).thenReturn(noResult);
        when(noResult.getPreviousBuild()).thenReturn(withResult);

        TestResultAction action = mockAction(withResult);
        when(withResult.getAction(TestResultAction.class)).thenReturn(action);
        BuildResult result = mock(BuildResult.class);
        when(action.getResult()).thenReturn(result);
        AnnotationContainer container = mock(AnnotationContainer.class);
        when(result.getContainer()).thenReturn(container);

        HistoryProvider history = createHistoryProvider(baseline);
        assertTrue("Build has no previous result", history.hasPreviousResult());
        assertSame("Build has wrong previous result", result, history.getPreviousResult());
        // FIXME: check this
        /*
        ReferenceProvider referenceProvider = new StablePluginReference(mockBuild(), TestResultAction.class, false);
        assertSame("Build has wrong reference result", container, referenceProvider.getIssues());
        */
    }

    /**
     * Verifies that we find the correct results for the following constellation.
     * <ol>
     * <li>Build with result and build result ABORTED</li>
     * <li>Build with no result</li>
     * <li>Baseline</li>
     * </ol>
     * If the plug-in caused the failure then there will be a result available, otherwise not.
     */
    @Test
    public void testHasPreviousResultDueToFailure() {
        verifyHasResult(false, Result.SUCCESS);
        verifyHasResult(true, Result.FAILURE);
    }

    private void verifyHasResult(final boolean expectedResult, final Result pluginResult) {
        Run withResult = mockBuild(Result.ABORTED);
        Run noResult = mockBuild();
        Run baseline = mockBuild();

        when(baseline.getPreviousBuild()).thenReturn(noResult);
        when(noResult.getPreviousBuild()).thenReturn(withResult);

        TestResultAction action = mockAction(withResult);
        when(withResult.getAction(TestResultAction.class)).thenReturn(action);
        BuildResult result = mock(BuildResult.class);
        when(action.getResult()).thenReturn(result);
        AnnotationContainer container = mock(AnnotationContainer.class);
        when(result.getContainer()).thenReturn(container);
        when(result.getPluginResult()).thenReturn(pluginResult);
        HistoryProvider history = createHistoryProvider(baseline);

        assertEquals("Build has previous result", expectedResult, history.hasPreviousResult());
    }

    private Run mockBuild() {
        return mockBuild(Result.SUCCESS);
    }

    private Run mockBuild(final Result result) {
        Run build = mock(Run.class);
        when(build.getResult()).thenReturn(result);
        return build;
    }

    /**
     * Verifies that we find the correct results for the following constellation.
     * <ol>
     * <li>Build with result, build result = SUCCESS</li>
     * <li>Build with no result</li>
     * <li>Build with result, build result = FAILURE</li>
     * <li>Build with no result</li>
     * <li>Baseline</li>
     * </ol>
     */
    @Test
    public void testHasReferenceResult() {
        Run withSuccessResult = mockBuild();
        Run noResult2 = mockBuild();
        Run withFailureResult = mockBuild();
        Run noResult1 = mockBuild();
        Run baseline = mockBuild();

        when(baseline.getPreviousBuild()).thenReturn(noResult1);
        when(noResult1.getPreviousBuild()).thenReturn(withFailureResult);
        when(withFailureResult.getPreviousBuild()).thenReturn(noResult2);
        when(noResult2.getPreviousBuild()).thenReturn(withSuccessResult);

        BuildResult failureResult = createFailureResult(withFailureResult);

        AnnotationContainer container = createSuccessfulResult(withSuccessResult);

        HistoryProvider history = createHistoryProvider(baseline);

        assertTrue("Build has no previous result", history.hasPreviousResult());
        assertSame("Build has wrong previous result", failureResult, history.getPreviousResult());

        ReferenceProvider referenceProvider = createReferenceProvider(baseline);
        assertTrue("Build has no reference build", referenceProvider.hasReference());
        assertSame("Build has wrong reference result", withSuccessResult, referenceProvider.getReference());
        assertSame("Build has wrong reference result", container, referenceProvider.getIssues());
    }

    /**
     * Verifies that we find the correct results for the following constellation.
     * <ol>
     * <li>Build with result, build result = SUCCESS and build result ABORTED</li>
     * <li>Build with no result</li>
     * <li>Build with result, build result = FAILURE</li>
     * <li>Build with no result</li>
     * <li>Baseline</li>
     * </ol>
     */
    @Test
    public void testHasNoReferenceResult() {
        Run withSuccessResultAndSuccessfulBuild = mockBuild();
        Run withSuccessResult = mockBuild(Result.ABORTED);
        Run noResult2 = mockBuild();
        Run withFailureResult = mockBuild();
        Run noResult1 = mockBuild();
        Run baseline = mockBuild();

        when(baseline.getPreviousBuild()).thenReturn(noResult1);
        when(noResult1.getPreviousBuild()).thenReturn(withFailureResult);
        when(withFailureResult.getPreviousBuild()).thenReturn(noResult2);
        when(noResult2.getPreviousBuild()).thenReturn(withSuccessResult);
        when(withSuccessResult.getPreviousBuild()).thenReturn(withSuccessResultAndSuccessfulBuild);

        BuildResult failureResult = createFailureResult(withFailureResult);

        createSuccessfulResult(withSuccessResult);
        AnnotationContainer used = createSuccessfulResult(withSuccessResultAndSuccessfulBuild);

        HistoryProvider history = createHistoryProvider(baseline);

        assertTrue("Build has no previous result", history.hasPreviousResult());
        assertSame("Build has wrong previous result", failureResult, history.getPreviousResult());

        ReferenceProvider referenceProvider = createReferenceProvider(baseline);
        assertTrue("Build has no reference build", referenceProvider.hasReference());
        assertSame("Build has wrong reference result", withSuccessResultAndSuccessfulBuild, referenceProvider.getReference());
        assertSame("Build has wrong reference result", used, referenceProvider.getIssues());
    }

    /**
     * Verifies that the previous build is used as reference build when it's unstable
     * and history is NOT configured to use only stable builds as reference builds.
     */
    @Test
    public void testUsesUnstableBuildAsReferenceBuildWhenConfigured() {
        Run unstableBuild = mockBuild(Result.UNSTABLE);
        Run stableBuild = mockBuild(Result.SUCCESS);
        Run baseline = mockBuild();

        when(baseline.getPreviousBuild()).thenReturn(unstableBuild);
        when(unstableBuild.getPreviousBuild()).thenReturn(stableBuild);

        createSuccessfulResult(unstableBuild);
        createSuccessfulResult(stableBuild);

        ReferenceProvider history = new StablePluginReference(baseline, createSelector(), false);
        assertSame("Unstable build is not reference build", unstableBuild, history.getReference());
    }

    /**
     * Verifies that the most recent STABLE build is used as reference build when the previous
     * build is unstable and history IS configured to use only stable builds as reference builds.
     */
    @Test
    public void testUsesStableBuildAsReferenceBuildWhenConfigured() {
        Run unstableBuild = mockBuild(Result.UNSTABLE);
        Run stableBuild = mockBuild(Result.SUCCESS);
        Run baseline = mockBuild();

        when(baseline.getPreviousBuild()).thenReturn(unstableBuild);
        when(unstableBuild.getPreviousBuild()).thenReturn(stableBuild);

        createSuccessfulResult(unstableBuild);
        createSuccessfulResult(stableBuild);

        ReferenceProvider history = createReferenceProvider(baseline);
        assertSame("Stable build is not reference build", stableBuild, history.getReference());
    }

    /**
     * Verifies that the most recent STABLE build is used as reference build when the previous
     * build is unstable and history IS configured to use only stable builds as reference builds.
     *
     * @see <a href="http://issues.jenkins-ci.org/browse/JENKINS-13458">Issue 13458</a>
     */
    @Test
    public void testUsesPreviousBuildAsReferenceBuildWhenConfigured() {
        Run referenceBuild = mockBuild();
        Run previous = mockBuild();
        Run baseline = mockBuild();

        when(baseline.getPreviousBuild()).thenReturn(previous);
        when(previous.getPreviousBuild()).thenReturn(referenceBuild);

        createSuccessfulResult(referenceBuild);
        createFailureResult(previous);

        ReferenceProvider referenceHistory = new StablePluginReference(baseline, createSelector(), false);
        assertSame("First build is not reference build", referenceBuild, referenceHistory.getReference());
        ReferenceProvider previousHistory = new PreviousBuildReference(baseline, createSelector(), false);
        assertSame("Previous build is not reference build", previous, previousHistory.getReference());
    }

    private BuildResult createFailureResult(final Run withFailureResult) {
        TestResultAction failureAction = mockAction(withFailureResult);
        when(withFailureResult.getAction(TestResultAction.class)).thenReturn(failureAction);
        when(failureAction.isSuccessful()).thenReturn(false);
        BuildResult failureResult = mock(BuildResult.class);
        when(failureAction.getResult()).thenReturn(failureResult);
        when(failureResult.getPluginResult()).thenReturn(Result.UNSTABLE);

        return failureResult;
    }

    private AnnotationContainer createSuccessfulResult(final Run withSuccessResult) {
        TestResultAction successAction = mockAction(withSuccessResult);
        when(withSuccessResult.getAction(TestResultAction.class)).thenReturn(successAction);
        when(successAction.isSuccessful()).thenReturn(true);
        BuildResult successResult = mock(BuildResult.class);
        AnnotationContainer container = mock(AnnotationContainer.class);
        when(successResult.getContainer()).thenReturn(container);
        when(successAction.getResult()).thenReturn(successResult);
        when(successResult.getPluginResult()).thenReturn(Result.SUCCESS);
        return container;
    }

    @SuppressWarnings("unchecked")
    private TestResultAction mockAction(final Run build) {
        TestResultAction action = mock(TestResultAction.class);
        when(action.getBuild()).thenReturn(build);
        return action;
    }

    /**
     * Factory method to create a build history under test.
     *
     * @param baseline
     *            the build to start with
     * @return the build history under test
     */
    private HistoryProvider createHistoryProvider(final Run<?, ?> baseline) {
        return new BuildHistory(baseline, createSelector());
    }

    private ReferenceProvider createReferenceProvider(final Run<?, ?> baseline) {
        return new StablePluginReference(baseline, createSelector(), true);
    }

    private DefaultResultSelector createSelector() {
        return new DefaultResultSelector(TestResultAction.class);
    }

    /**
     * Action used in tests.
     */
    abstract static class TestResultAction implements ResultAction<BuildResult> {
        // empty
    }
}

