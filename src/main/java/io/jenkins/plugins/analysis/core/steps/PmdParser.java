package io.jenkins.plugins.analysis.core.steps;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.plugins.analysis.util.model.FileAnnotation;

/**
 * FIXME: write comment.
 *
 * @author Ullrich Hafner
 */
public class PmdParser extends IssueParser {
    private boolean bool;

    @DataBoundConstructor
    public PmdParser(final boolean bool) {
        super("pmd");

        this.bool = bool;
    }

    @Override
    public Collection<FileAnnotation> parse(final File file, final String moduleName) throws InvocationTargetException {
        return null;
    }

    @Extension
    public static final IssueParserDescriptor D = new IssueParserDescriptor(PmdParser.class);
}
