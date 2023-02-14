package com.rmarcello.mockserveroperator.dependents;

import com.rmarcello.mockserveroperator.Mockserver;
import static com.rmarcello.mockserveroperator.MockserverReconciler.LABELS_CONTEXT_KEY;
import static com.rmarcello.mockserveroperator.MockserverReconciler.createMetadata;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;

public class ConfigmapDependentResource extends CRUDKubernetesDependentResource<ConfigMap, Mockserver> {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigmapDependentResource.class);

  public ConfigmapDependentResource() {
    super(ConfigMap.class);
  }

  @Override
  protected ConfigMap desired(Mockserver mockserver, Context<Mockserver> context) {
    final var labels = (Map<String, String>) context.managedDependentResourceContext().getMandatory(LABELS_CONTEXT_KEY,
        Map.class);
    final var spec = mockserver.getSpec();
    final var config = spec.getConfig();
    Map<String, String> data = new HashMap<>();
    data.put("initializerJson.json", config);

    return new ConfigMapBuilder()
        .withMetadata(createMetadata(mockserver, labels))
        .withData(data)
        .build();
  }

  @Override
  public ConfigMap update(ConfigMap actual, ConfigMap target, Mockserver primary, Context<Mockserver> context) {
    var res = super.update(actual, target, primary, context);
    var ns = actual.getMetadata().getNamespace();
    LOG.info("Restarting pods because the config map has changed in {}", ns);

    final var labels = (Map<String, String>) context.managedDependentResourceContext().getMandatory(LABELS_CONTEXT_KEY,
        Map.class);
    // not that this is not necessary, eventually mounted config map would be
    // updated, just this way
    // is much faster; what is handy for demo purposes.
    // https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#mounted-configmaps-are-updated-automatically
    getKubernetesClient()
        .pods()
        .inNamespace(ns)
        .withLabels(labels)
        .delete();
    return res;
  }

}
