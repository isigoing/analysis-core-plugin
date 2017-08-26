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
public class CheckStyleParser extends IssueParser {
    private int integer;

    @DataBoundConstructor
    public CheckStyleParser(final int integer) {
        super("checkstyle");

        this.integer = integer;
    }

    @Override
    public Collection<FileAnnotation> parse(final File file, final String moduleName) throws InvocationTargetException {
        return null;
    }

    @Extension
    public static final IssueParserDescriptor D = new IssueParserDescriptor(CheckStyleParser.class);
}
