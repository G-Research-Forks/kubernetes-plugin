package org.csanchez.jenkins.plugins.kubernetes;

import java.util.Locale;

public class MetricNames {
    private static final String PREFIX = "kubernetes.cloud";

    public static final String CREATION_FAILED = PREFIX + ".pods.creation.failed";
    public static final String PODS_CREATED = PREFIX + ".pods.created";
    public static final String LAUNCH_FAILED = PREFIX + ".pods.launch.failed";
    public static final String PODS_TERMINATED = PREFIX + ".pods.terminated";
    public static final String REACHED_POD_CAP = PREFIX + ".provision.reached.pod.cap";
    public static final String REACHED_GLOBAL_CAP = PREFIX + ".provision.reached.global.cap";
    public static final String FAILED_TIMEOUT = PREFIX + ".pods.launch.failed.timeout";
    public static final String PROVISION_NODES = PREFIX + ".provision.nodes";
    public static final String PROVISION_FAILED = PREFIX + ".provision.failed";
    public static final String PODS_LAUNCHED = PREFIX + ".pods.launched";

    /** Duration of the whole garbage collection tick, including annotation and deletion */
    public static final String GC_SWEEP_DURATION = PREFIX + ".gc.sweep.duration.seconds";

    /** Duration histogram of the TTL refresh phase alone, across all live agents. */
    public static final String GC_ANNOTATE_DURATION = PREFIX + ".gc.annotate.duration.seconds";
    /** Number of live agents whose TTL annotation was considered for annotation. */
    public static final String GC_AGENTS_CONSIDERED = PREFIX + ".gc.agents.considered";
    /** Number of TTL refresh PATCHes that failed, potentially resulting in pod GC or slow sweeps */
    public static final String GC_ANNOTATE_PATCH_FAILED = PREFIX + ".gc.annotate.patch.failed";

    /** Per-agent duration of the Kubernetes PATCH that refreshes the TTL annotation. */
    public static final String GC_ANNOTATE_PATCH_DURATION = PREFIX + ".gc.annotate.patch.duration.seconds";

    /** Cumulative time spent in the TTL refresh phase, in microseconds. Histograms don't carry totals */
    public static final String GC_ANNOTATE_MICROS = PREFIX + ".gc.annotate.total_duration.micros";

    /** Duration of the orphan detection and deletion phase alone. */
    public static final String GC_COLLECT_DURATION = PREFIX + ".gc.collect.duration.seconds";
    /** Number of pods deleted for carrying a TTL annotation older than the configured timeout. */
    public static final String GC_PODS_DELETED = PREFIX + ".gc.pods.deleted";

    public static String metricNameForPodStatus(String status) {
        String formattedStatus = status == null ? "null" : status.toLowerCase(Locale.getDefault());
        return PREFIX + ".pods.launch.status." + formattedStatus;
    }
}
