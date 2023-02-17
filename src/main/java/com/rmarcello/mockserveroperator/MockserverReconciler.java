package com.rmarcello.mockserveroperator;

import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rmarcello.mockserveroperator.dependents.ConfigmapDependentResource;
import com.rmarcello.mockserveroperator.dependents.DeploymentDependentResource;
import com.rmarcello.mockserveroperator.dependents.IngressDependentResource;
import com.rmarcello.mockserveroperator.dependents.ServiceDependentResource;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Cleaner;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ContextInitializer;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.DeleteControl;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;

@ControllerConfiguration(
    dependents = {
        @Dependent(type = ConfigmapDependentResource.class),
        @Dependent(type = DeploymentDependentResource.class),
        @Dependent(type = ServiceDependentResource.class),
        @Dependent(type = IngressDependentResource.class)
    }
)
public class MockserverReconciler implements Reconciler<Mockserver>, Cleaner<Mockserver>, ContextInitializer<Mockserver> {

    private static final Logger LOG = LoggerFactory.getLogger(MockserverReconciler.class);
    public static final String APP_LABEL = "app.kubernetes.io/name";
    public static final String LABELS_CONTEXT_KEY = "labels";

    @Override
    public void initContext(Mockserver smocker, Context context) {
        final var labels = Map.of(APP_LABEL, smocker.getMetadata().getName());
        context.managedDependentResourceContext().put(LABELS_CONTEXT_KEY, labels);
    }

    public UpdateControl<Mockserver> reconcile(Mockserver smocker, Context<Mockserver> context) throws Exception {

        String ns = smocker.getMetadata().getNamespace();

        LOG.info("update, ns: {}, name: {}", ns, smocker.getMetadata().getName() );
        
        final var name = smocker.getMetadata().getName();
        // retrieve the workflow reconciliation result and re-schedule if we have dependents that are not yet ready
        return context.managedDependentResourceContext().getWorkflowReconcileResult()
            .map( wrs -> {
                if (wrs.allDependentResourcesReady()) {
                    MockserverStatus status = createSuccessfullStatus(smocker);
                    smocker.setStatus(status);
                    return UpdateControl.updateStatus(smocker);
                } else {
                    final Duration duration = Duration.ofSeconds(5);
                    LOG.info("App {} is not ready yet, rescheduling reconciliation after {}s", name, duration.toSeconds());
                    return UpdateControl.<Mockserver> noUpdate().rescheduleAfter(duration);
                }
            } ).orElseThrow();
        
    }

    

    @Override
    public DeleteControl cleanup(Mockserver smocker, Context<Mockserver> context) {
        LOG.info("cleanup: {}", smocker.getMetadata().getName() );
        return DeleteControl.defaultDelete();
    }

    public static ObjectMeta createMetadata(Mockserver resource, Map<String, String> labels) {
        final var metadata = resource.getMetadata();
        return new ObjectMetaBuilder()
                .withName(metadata.getName())
                .withNamespace(metadata.getNamespace())
                .withLabels(labels)
                .build();
    }

    private MockserverStatus createSuccessfullStatus(Mockserver smocker) {
        final var metadata = smocker.getMetadata();
        final var spec = smocker.getSpec();
        final MockserverStatus status = new MockserverStatus();
        status.setAreWeGood(true);
        status.setInternalUrl(String.format("http://%s.%s.svc.cluster.local:8080", metadata.getName(), metadata.getNamespace()));
        status.setExternalUrl(String.format("http://%s", spec.getIngressHost()));
        return status;
    }

}
