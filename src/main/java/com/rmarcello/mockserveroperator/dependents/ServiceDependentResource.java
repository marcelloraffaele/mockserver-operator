package com.rmarcello.mockserveroperator.dependents;

import java.util.Map;

import com.rmarcello.mockserveroperator.Mockserver;
import static com.rmarcello.mockserveroperator.MockserverReconciler.LABELS_CONTEXT_KEY;
import static com.rmarcello.mockserveroperator.MockserverReconciler.createMetadata;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;

public class ServiceDependentResource extends CRUDKubernetesDependentResource<Service, Mockserver> {

    public ServiceDependentResource() {
        super(Service.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Service desired(Mockserver exposedApp, Context context) {
        final var labels = (Map<String, String>) context.managedDependentResourceContext()
                .getMandatory(LABELS_CONTEXT_KEY, Map.class);

        return new ServiceBuilder()
                .withMetadata(createMetadata(exposedApp, labels))
                .withNewSpec()
                .addNewPort()
                    .withName("http")
                    .withPort(8080)
                    .withNewTargetPort().withValue(1080).endTargetPort()
                .endPort()
                .withSelector(labels)
                .withType("ClusterIP")
                .endSpec()
                .build();
    }
}
