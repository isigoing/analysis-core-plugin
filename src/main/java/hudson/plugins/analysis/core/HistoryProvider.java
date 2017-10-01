package hudson.plugins.analysis.core;

import java.util.NoSuchElementException;

/**
 * FIXME: write comment.
 *
 * @author Ullrich Hafner
 */
public interface HistoryProvider extends Iterable<BuildResult> {
    /**
     * Returns whether a previous build result exists.
     *
     * @return <code>true</code> if a previous build result exists.
     * @see #isEmpty()
     */
    boolean hasPreviousResult();

    /**
     * Returns whether there is no history available, i.e. the current build is
     * the first valid one.
     *
     * @return <code>true</code> if there is no previous build available
     * @see #hasPreviousResult()
     */
    boolean isEmpty();

    /**
     * Returns the baseline action.
     *
     * @return the baseline action
     * @see #hasPreviousResult()
     * @throws NoSuchElementException
     *             if there is no previous result
     */
    ResultAction<? extends BuildResult> getBaseline();

    /**
     * Returns the previous build result.
     *
     * @return the previous build result
     * @see #hasPreviousResult()
     * @throws NoSuchElementException
     *             if there is no previous result
     */
    BuildResult getPreviousResult();
}
