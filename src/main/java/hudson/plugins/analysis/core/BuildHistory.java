package hudson.plugins.analysis.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.NoSuchElementException;

import hudson.model.Result;
import hudson.model.Run;

/**
 * History of build results of a specific plug-in. The plug-in is identified by
 * the corresponding {@link ResultAction} type.
 *
 * @author Ulli Hafner
 */
public class BuildHistory implements HistoryProvider {
    /** The build to start the history from. */
    private final Run<?, ?> baseline;
    /** Type of the action that contains the build results. */
    private final Class<? extends ResultAction> type;

    /**
     * Creates a new instance of {@link BuildHistory}.
     *
     * @param baseline
     *            the build to start the historyz from
     * @param type
     *            type of the action that contains the build results
     */
    public BuildHistory(final Run<?, ?> baseline, final Class<? extends ResultAction> type) {
        this.baseline = baseline;
        this.type = type;
    }

    /**
     * Returns the time of the baseline build.
     *
     * @return the time
     */
    public Calendar getTimestamp() {
        return baseline.getTimestamp();
    }

    private ResultAction<? extends BuildResult> getAction(final boolean isStatusRelevant) {
        return getAction(isStatusRelevant, false);
    }

    protected ResultAction<? extends BuildResult> getAction(final boolean isStatusRelevant, final boolean mustBeStable) {
        for (Run<?, ?> build = baseline.getPreviousBuild(); build != null; build = build.getPreviousBuild()) {
            ResultAction<? extends BuildResult> action = getResultAction(build);
            if (hasValidResult(build, mustBeStable, action) && isSuccessfulAction(action, isStatusRelevant)) {
                return action;
            }
        }
        return null;
    }

    private boolean isSuccessfulAction(final ResultAction<? extends BuildResult> action, final boolean isStatusRelevant) {
        return action != null && (action.isSuccessful() || !isStatusRelevant);
    }

    /**
     * Returns the result action of the specified build that should be used to
     * compute the history.
     *
     * @param build
     *            the build
     * @return the result action
     */
    @CheckForNull
    public ResultAction<? extends BuildResult> getResultAction(@Nonnull final Run<?, ?> build) {
        return build.getAction(type);
    }

    /**
     * Returns the action of the previous build.
     *
     * @return the action of the previous build, or <code>null</code> if no
     *         such build exists
     */
    @CheckForNull
    protected ResultAction<? extends BuildResult> getPreviousAction(final boolean mustBeStable) {
        return getAction(mustBeStable);
    }

    /**
     * Returns the action of the previous build.
     *
     *
     * @return the action of the previous build, or <code>null</code> if no
     *         such build exists
     */
    @CheckForNull
    protected ResultAction<? extends BuildResult> getPreviousAction() {
        return getAction(false);
    }

    protected boolean hasValidResult(final Run<?, ?> build) {
        return hasValidResult(build, false, null);
    }

    protected boolean hasValidResult(final Run<?, ?> build, final boolean mustBeStable, @CheckForNull final ResultAction<? extends BuildResult> action) {
        Result result = build.getResult();

        if (result == null) {
            return false;
        }
        if (mustBeStable) {
            return result == Result.SUCCESS;
        }
        return result.isBetterThan(Result.FAILURE) || isPluginCauseForFailure(action);
    }

    private boolean isPluginCauseForFailure(@CheckForNull final ResultAction<? extends BuildResult> action) {
        if (action == null) {
            return false;
        }
        else {
            return action.getResult().getPluginResult().isWorseOrEqualTo(Result.FAILURE);
        }
    }

    @Override
    public boolean hasPreviousResult() {
        return getPreviousAction() != null;
    }

    @Override
    public boolean isEmpty() {
        return !hasPreviousResult();
    }

    @Override
    public ResultAction<? extends BuildResult> getBaseline() {
        return getResultAction(baseline);
    }

    @Override
    public BuildResult getPreviousResult() {
        ResultAction<? extends BuildResult> action = getPreviousAction();
        if (action != null) {
            return action.getResult();
        }
        throw new NoSuchElementException("No previous result available");
    }

    /**
     * Returns the health descriptor used for the builds.
     *
     * @return the health descriptor
     */
    public AbstractHealthDescriptor getHealthDescriptor() {
        return getBaseline().getHealthDescriptor();
    }
}

