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
    var metadata = primary.getMetadata();
    var ns = metadata.getNamespace();

    LOG.info("Rolling restart deployment because the config map has changed in {}", ns);

    //rolling restart for the deployment in order to load the new volume
    getKubernetesClient().apps().deployments()
      .inNamespace( ns )
      .withName( metadata.getName() ).rolling().restart();

    return res;
  }

}
