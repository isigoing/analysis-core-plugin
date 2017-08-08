package hudson.plugins.analysis.core;

import javax.annotation.CheckForNull;

import hudson.model.Run;
import hudson.plugins.analysis.util.model.AnnotationContainer;
import hudson.plugins.analysis.util.model.DefaultAnnotationContainer;

/**
 * FIXME: write comment.
 *
 * @since 2.0
 * @author Ullrich Hafner
 */
public abstract class ReferenceFinder extends BuildHistory implements ReferenceProvider {
    public enum ReferenceProviderAlgorithms {
        PREVIOUS_BUILD,


    }

    public static ReferenceProvider create(final Run<?, ?> run, final Class<? extends ResultAction> resultActionClass,
            final boolean usePreviousBuildAsReference, final boolean useStableBuildAsReference) {
        if (usePreviousBuildAsReference) {
            return new PreviousBuildReference(run, resultActionClass, useStableBuildAsReference);
        }
        else {
            return new StablePluginReference(run, resultActionClass, useStableBuildAsReference);
        }
    }
    /**
     * Creates a new instance of {@link BuildHistory}.
     *
     * @param baseline
     *            the build to start the history from
     * @param type
     *            type of the action that contains the build results
     */
    public ReferenceFinder(final Run<?, ?> baseline, final Class<? extends ResultAction> type) {
        super(baseline, type);
    }

    /**
     * Returns the action of the reference build.
     *
     * @return the action of the reference build, or {@code null} if no
     *         such build exists
     */
    protected abstract ResultAction<? extends BuildResult> getReferenceAction();

    /**
     * Returns whether a reference build result exists.
     *
     * @return <code>true</code> if a reference build result exists.
     */
    private boolean hasReferenceAction() {
        return getReferenceAction() != null;
    }

    @Override
    @CheckForNull
    public Run<?, ?> getReference() {
        ResultAction<? extends BuildResult> action = getReferenceAction();
        if (action != null) {
            Run<?, ?> build = action.getBuild();
            if (hasValidResult(build)) {
                return build;
            }
        }
        return null;
    }

    @Override
    public boolean hasReference() {
        return getReference() != null;
    }

    /**
     * Returns the annotations of the reference build.
     *
     * @return the annotations of the reference build
     */
    @Override
    public AnnotationContainer getIssues() {
        ResultAction<? extends BuildResult> action = getReferenceAction();
        if (action != null) {
            return action.getResult().getContainer();
        }
        return new DefaultAnnotationContainer();
    }
}
