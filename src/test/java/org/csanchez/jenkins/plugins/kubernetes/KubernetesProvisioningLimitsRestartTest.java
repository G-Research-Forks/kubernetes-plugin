package org.csanchez.jenkins.plugins.kubernetes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import hudson.slaves.JNLPLauncher;
import hudson.slaves.RetentionStrategy;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsSessionRule;

public class KubernetesProvisioningLimitsRestartTest {

    @Rule
    public JenkinsSessionRule s = new JenkinsSessionRule();

    /**
     * Agents created from an ephemeral pod template (e.g. a {@code podTemplate} pipeline step) are counted at startup,
     * but their template no longer resolves after a restart. Deleting such an agent must still release its slot.
     */
    @Test
    public void slotReleasedForAgentWithUnresolvableTemplateAfterRestart() throws Throwable {
        AtomicReference<String> ephemeralTemplateId = new AtomicReference<>();
        s.then(r -> {
            KubernetesCloud cloud = new KubernetesCloud("kubernetes");
            cloud.setContainerCap(10);
            PodTemplate configured = new PodTemplate("configured");
            configured.setName("configured");
            cloud.addTemplate(configured);
            r.jenkins.clouds.add(cloud);

            // Not added to the cloud: only exists in memory, like a podTemplate step template
            PodTemplate ephemeral = new PodTemplate("ephemeral");
            ephemeral.setName("ephemeral");
            ephemeralTemplateId.set(ephemeral.getId());
            KubernetesSlave agent = KubernetesSlave.builder()
                    .name("ephemeral-agent")
                    .podTemplate(ephemeral)
                    .cloud(cloud)
                    .computerLauncher(new JNLPLauncher())
                    .retentionStrategy(RetentionStrategy.NOOP)
                    .build();
            r.jenkins.addNode(agent);
        });
        s.then(r -> {
            KubernetesCloud cloud = r.jenkins.clouds.get(KubernetesCloud.class);
            KubernetesSlave agent = (KubernetesSlave) r.jenkins.getNode("ephemeral-agent");
            assertNull("template should not resolve after restart", agent.getTemplateOrNull());

            KubernetesProvisioningLimits limits = KubernetesProvisioningLimits.get();
            // Triggers initialization, which counts the agent loaded from disk
            assertTrue(limits.register(cloud, cloud.getTemplateById("configured"), 1));
            assertEquals(2, limits.getGlobalCount("kubernetes"));
            assertEquals(1, limits.getPodTemplateCount(ephemeralTemplateId.get()));

            r.jenkins.removeNode(agent);

            assertEquals(1, limits.getGlobalCount("kubernetes"));
            assertEquals(0, limits.getPodTemplateCount(ephemeralTemplateId.get()));
        });
    }
}
