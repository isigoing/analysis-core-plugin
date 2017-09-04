package hudson.plugins.analysis.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import hudson.model.Run;

/**
 * FIXME: write comment.
 *
 * @author Ullrich Hafner
 */
public interface ResultSelector {
    /**
     * Returns the result action of the specified build that should be used to compute the history.
     *
     * @param build the build
     * @return the result action
     */
    @CheckForNull
    ResultAction<? extends BuildResult> get(@Nonnull Run<?, ?> build);
}
