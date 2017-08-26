package io.jenkins.plugins.analysis.core.steps;

import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.plugins.analysis.core.AnnotationParser;

/**
 * FIXME: write comment.
 *
 * @author Ullrich Hafner
 */
public abstract class IssueParser extends AbstractDescribableImpl<IssueParser> implements AnnotationParser, ExtensionPoint {
    private String id;

    public String getId() {
        return id;
    }

    public IssueParser(final String id) {
        this.id = id;
    }

    public static class IssueParserDescriptor extends Descriptor<IssueParser> {
        public IssueParserDescriptor(final Class<? extends IssueParser> clazz) {
            super(clazz);
        }

        @Override
        public String getDisplayName() {
            return clazz.getSimpleName();
        }
    }
}
