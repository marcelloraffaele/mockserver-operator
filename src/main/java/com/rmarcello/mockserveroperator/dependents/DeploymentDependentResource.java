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
    final var volumeName = name +"-vol";
    final var initializerFileName = "initializerJson.json";
    final var initializerFilePath = "/config/"+initializerFileName;
    final var ENV_MOCKSERVER_INITIALIZATION_JSON_PATH = "MOCKSERVER_INITIALIZATION_JSON_PATH";

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
            .withName("http")
            .withProtocol("TCP")
            .withContainerPort(1080)
          .endPort()

          .addNewEnv()
            .withName(ENV_MOCKSERVER_INITIALIZATION_JSON_PATH)
            .withValue(initializerFilePath)
          .endEnv()

          .addNewVolumeMount()
            .withName(volumeName)
            .withMountPath(initializerFilePath)
            .withSubPath(initializerFileName)
            .withReadOnly(true)
          .endVolumeMount()
            
        .endContainer()
          .addNewVolume()
              .withName(volumeName)
              .withNewConfigMap()
                .withName(name)
                .addNewItem()
                  .withKey(initializerFileName)
                  .withPath(initializerFileName)
                .endItem()
              .endConfigMap()
          .endVolume()
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