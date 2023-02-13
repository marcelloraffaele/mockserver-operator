package com.rmarcello.mockserveroperator.dependents;

import com.rmarcello.mockserveroperator.Mockserver;
import static com.rmarcello.mockserveroperator.MockserverReconciler.LABELS_CONTEXT_KEY;
import static com.rmarcello.mockserveroperator.MockserverReconciler.createMetadata;

import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;

public class ConfigmapDependentResource extends CRUDKubernetesDependentResource<ConfigMap, Mockserver> {

    public ConfigmapDependentResource() {
        super(ConfigMap.class);
    }
    

    @Override
  protected ConfigMap desired(Mockserver Mockserver, Context<Mockserver> context) {
    final var labels = (Map<String, String>) context.managedDependentResourceContext().getMandatory(LABELS_CONTEXT_KEY, Map.class);
    final var spec = Mockserver.getSpec();
    final var config = spec.getConfig();
    Map<String, String> data = new HashMap<>();
    data.put("config.yaml", config);

    return new ConfigMapBuilder()
        .withMetadata( createMetadata(Mockserver, labels) )
        .withData(data)
        .build();
  }

  //@Override
  //public ConfigMap update(ConfigMap actual, ConfigMap target, WebPage primary,
  //    Context<WebPage> context) {
  //  var res = super.update(actual, target, primary, context);
  //  var ns = actual.getMetadata().getNamespace();
  //  log.info("Restarting pods because HTML has changed in {}",
  //      ns);
  //  // not that this is not necessary, eventually mounted config map would be updated, just this way
  //  // is much faster; what is handy for demo purposes.
  //  // https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#mounted-configmaps-are-updated-automatically
  //  getKubernetesClient()
  //      .pods()
  //      .inNamespace(ns)
  //      .withLabel("app", deploymentName(primary))
  //      .delete();
  //  return res;
  //}

}
