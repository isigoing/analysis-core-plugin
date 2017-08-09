package hudson.plugins.analysis.core;

import org.apache.commons.lang.StringUtils;
import org.jvnet.localizer.Localizable;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import static hudson.plugins.analysis.util.ThresholdValidator.*;

import hudson.plugins.analysis.util.model.AnnotationProvider;
import hudson.plugins.analysis.util.model.Priority;

/**
 * A base class for serializable health descriptors.
 *
 * @author Ulli Hafner
 */
@ExportedBean
public abstract class AbstractHealthDescriptor implements HealthDescriptor {
    private static final long serialVersionUID = -3709673381162699834L;

    /** The minimum priority to consider during health and stability calculation. */
    private final Priority minimumPriority;
    /** Report health as 100% when the number of warnings is less than this value. */
    private final String healthy;
    /** Report health as 0% when the number of warnings is greater than this value. */
    private final String unHealthy;

    /**
     * Creates a new instance of {@link AbstractHealthDescriptor} based on the
     * values of the specified descriptor.
     *
     * @param healthDescriptor the descriptor to copy the values from
     */
    public AbstractHealthDescriptor(final HealthDescriptor healthDescriptor) {
        this(healthDescriptor.getHealthy(), healthDescriptor.getUnHealthy(), healthDescriptor.getMinimumPriority());
    }

    /**
     * Creates a new instance of {@link AbstractHealthDescriptor} based on the specified values.
     *
     * @param healthy         the healthy threshold, i.e. the number of issues when health is reported as 100%
     * @param unHealthy       the unhealthy threshold, i.e. the number of issues when health is reported as 0%
     * @param minimumPriority the minimum priority to consider when computing the health report. Issues with a priority
     *                        less than this value will be ignored.
     */
    public AbstractHealthDescriptor(final String healthy, final String unHealthy, final Priority minimumPriority) {
        this.minimumPriority = minimumPriority;
        this.healthy = healthy;
        this.unHealthy = unHealthy;
    }

    /**
     * Creates a new instance of {@link AbstractHealthDescriptor}.
     */
    public AbstractHealthDescriptor() {
        healthy = StringUtils.EMPTY;
        unHealthy = StringUtils.EMPTY;
        minimumPriority = Priority.LOW;
    }

    @Override @Exported
    public Priority getMinimumPriority() {
        return minimumPriority;
    }

    @Override @Exported
    public String getHealthy() {
        return healthy;
    }

    @Override @Exported
    public String getUnHealthy() {
        return unHealthy;
    }

    /**
     * Returns whether health reporting is enabled, i.e. at least one of
     * the health properties differs from the default values.
     *
     * @return <code>true</code> if health thresholds are provided,
     *         <code>false</code> otherwise
     */
    public boolean isEnabled() {
        return isHealthyReportEnabled();
    }

    /**
     * Returns a localized description of the build health.
     *
     * @param result
     *            the result of the build
     * @return a localized description of the build health
     */
    protected abstract Localizable createDescription(final AnnotationProvider result);

    /**
     * Determines whether a health report should be created.
     *
     * @return <code>true</code> if a health report should be created
     */
    public boolean isHealthyReportEnabled() {
        if (isValid(healthy) && isValid(unHealthy)) {
            int healthyNumber = convert(healthy);
            int unHealthyNumber = convert(unHealthy);

            return unHealthyNumber > healthyNumber;
        }
        return false;
    }

    /**
     * Returns the healthy threshold for annotations, i.e. when health is reported as 100%.
     *
     * @return the 100% healthiness
     * @throws IllegalArgumentException if the healthy values are not valid
     * @see #isHealthyReportEnabled()
     */
    public int getHealthyAnnotations() {
        if (isHealthyReportEnabled()) {
            return convert(healthy);
        }
        throw createException();
    }

    /**
     * Creates a new {@link IllegalArgumentException}.
     *
     * @return a new {@link IllegalArgumentException}
     */
    private IllegalArgumentException createException() {
        return new IllegalArgumentException("Healthy values are not valid: " + healthy + ", " + unHealthy);
    }

    /**
     * Returns the unhealthy threshold of annotations, i.e. when health is reported as 0%.
     *
     * @return the 0% unhealthiness
     * @throws IllegalArgumentException if the healthy values are not valid
     * @see #isHealthyReportEnabled()
     */
    public int getUnHealthyAnnotations() {
        if (isHealthyReportEnabled()) {
            return convert(unHealthy);
        }
        throw createException();
    }

    /**
     * Initializes new fields that are not serialized yet.
     *
     * @return the object
     */
    @SuppressWarnings("deprecation")
    protected Object readResolve() {
        thresholds = null;
        return this;
    }

    /** Backward compatibility. @deprecated */
    @Deprecated
    private transient Thresholds thresholds;
    /** Backward compatibility. @deprecated */
    @Deprecated
    private transient String threshold;
    /** Backward compatibility. @deprecated */
    @Deprecated
    private transient String newThreshold;
    /** Backward compatibility. @deprecated */
    @Deprecated
    private transient String failureThreshold;
    /** Backward compatibility. @deprecated */
    @Deprecated
    private transient String newFailureThreshold;
    /** Backward compatibility. @deprecated */
    @SuppressWarnings("unused")
    @Deprecated
    private transient boolean isFailureThresholdEnabled;
    /** Backward compatibility. @deprecated */
    @SuppressWarnings("unused")
    @Deprecated
    private transient int minimumAnnotations;
    /** Backward compatibility. @deprecated */
    @SuppressWarnings("unused")
    @Deprecated
    private transient int healthyAnnotations;
    /** Backward compatibility. @deprecated */
    @SuppressWarnings("unused")
    @Deprecated
    private transient int unHealthyAnnotations;
    /** Backward compatibility. @deprecated */
    @SuppressWarnings("all")
    @Deprecated
    private transient boolean isHealthyReportEnabled;
}

