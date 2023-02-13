package com.rmarcello.mockserveroperator.dependents;

import java.util.Map;

import com.rmarcello.mockserveroperator.Mockserver;
import static com.rmarcello.mockserveroperator.MockserverReconciler.LABELS_CONTEXT_KEY;
import static com.rmarcello.mockserveroperator.MockserverReconciler.createMetadata;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.Matcher;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;

public class DeploymentDependentResource
    extends CRUDKubernetesDependentResource<Deployment, Mockserver>
    implements Matcher<Deployment, Mockserver> {

  public DeploymentDependentResource() {
    super(Deployment.class);
  }

  @SuppressWarnings("unchecked")
  public Deployment desired(Mockserver smocker, Context context) {
    final var labels = (Map<String, String>) context.managedDependentResourceContext().getMandatory(LABELS_CONTEXT_KEY,
        Map.class);
    final var name = smocker.getMetadata().getName();
    final var spec = smocker.getSpec();
    final var image = spec.getImage();
    final var replica = spec.getReplica();
    // final var config = spec.getConfig();

    var containerBuilder = new DeploymentBuilder()
        .withMetadata(createMetadata(smocker, labels))
        .withNewSpec()
        .withNewSelector().withMatchLabels(labels).endSelector()
        .withReplicas(replica)
        .withNewTemplate()
        .withNewMetadata().withLabels(labels).endMetadata()
        .withNewSpec()
        .addNewContainer()
        .withName(name).withImage(image)
        .addNewPort()
        .withName("http-test").withProtocol("TCP").withContainerPort(8080)
        .endPort()
        .addNewPort()
        .withName("http-management").withProtocol("TCP").withContainerPort(8081)
        .endPort()
        .endContainer()
        .endSpec()
        .endTemplate()
        .endSpec();
    return containerBuilder.build();
  }

  @Override
  public Result<Deployment> match(Deployment actual, Mockserver primary, Context<Mockserver> context) {
    final var desiredSpec = primary.getSpec();
    final var container = actual.getSpec().getTemplate().getSpec().getContainers()
        .stream()
        .findFirst();
    boolean match = container.isPresent() && container.get().getImage().equals(desiredSpec.getImage())
        && desiredSpec.getReplica().equals(actual.getSpec().getReplicas());
    return Result.nonComputed(match);
  }

}