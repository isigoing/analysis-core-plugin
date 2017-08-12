package hudson.plugins.analysis.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import hudson.model.Run;

/**
 * FIXME: write comment.
 *
 * @author Ullrich Hafner
 */
public class DefaultResultSelector implements ResultSelector {
    private Class<? extends ResultAction> type;

    public DefaultResultSelector(final Class<? extends ResultAction> type) {
        this.type = type;
    }

    @Override @CheckForNull
    public ResultAction<? extends BuildResult> get(@Nonnull final Run<?, ?> build) {
        return build.getAction(type);
    }
}
