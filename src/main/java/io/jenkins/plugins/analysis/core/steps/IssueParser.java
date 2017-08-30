package io.jenkins.plugins.analysis.core.steps;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.kohsuke.stapler.DataBoundSetter;

import jenkins.model.Jenkins;

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
    private static final String ICONS_PREFIX = "/plugin/analysis-core/icons/";
    private static final String SMALL_ICON_URL = ICONS_PREFIX + "analysis-24x24.png";
    private static final String LARGE_ICON_URL = ICONS_PREFIX + "analysis-48x48.png";

    private String id;
    private String defaultPattern;
    private String defaultEncoding;

    @CheckForNull
    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    /**
     * Sets the default encoding used to read files (warnings, source code, etc.).
     *
     * @param defaultEncoding the encoding, e.g. "ISO-8859-1"
     */
    @DataBoundSetter
    public void setDefaultEncoding(final String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    public IssueParser(final String id) {
        this(id, "**/*");
    }

    public IssueParser(final String id, final String defaultPattern) {
        this.id = id;
        this.defaultPattern = defaultPattern;
    }

    public String getId() {
        return id;
    }

    public String getDefaultPattern() {
        return defaultPattern;
    }

    public String getName() {
        return "Static Analysis Issues";
    }

    public String getTrendName() {
        return "Static Analysis Issues Trend";
    }

    public String getSmallIconUrl() {
        return SMALL_ICON_URL;
    }

    public String getLargeIconUrl() {
        return LARGE_ICON_URL;
    }

    public String getResultUrl() {
        return getId() + "Result";
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

    public static Collection<? extends IssueParser> all() {
        return Jenkins.getInstance().getExtensionList(IssueParser.class);
    }

    public static IssueParser find(final String id) {
        for (IssueParser parser : all()) {
            if (parser.getId().equals(id)) {
                return parser;
            }
        }
        throw new NoSuchElementException("IssueParser not found: " + id);
    }


}
