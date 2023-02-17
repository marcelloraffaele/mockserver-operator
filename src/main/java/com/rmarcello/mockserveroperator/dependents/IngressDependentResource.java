package com.rmarcello.mockserveroperator.dependents;

import com.rmarcello.mockserveroperator.Mockserver;
import com.rmarcello.mockserveroperator.MockserverReconciler;

import static com.rmarcello.mockserveroperator.MockserverReconciler.LABELS_CONTEXT_KEY;
import java.util.Map;

import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition;

@SuppressWarnings("unchecked")
public class IngressDependentResource extends CRUDKubernetesDependentResource<Ingress, Mockserver> implements Condition<Ingress, Mockserver> {

  //private static final Logger LOG = LoggerFactory.getLogger(IngressDependentResource.class);

  public IngressDependentResource() {
    super(Ingress.class);
  }

  @Override
  protected Ingress desired(Mockserver mockserver, Context<Mockserver> context) {
    final var labels = (Map<String, String>) context.managedDependentResourceContext().getMandatory(LABELS_CONTEXT_KEY,
        Map.class);
    final var metadata = mockserver.getMetadata();
    final var spec = mockserver.getSpec();
    

    return new IngressBuilder()
                .withMetadata(MockserverReconciler.createMetadata(mockserver, labels))
                .withNewSpec()
                .addNewRule()
                  .withHost( spec.getIngressHost() )
                  .withNewHttp()
                    .addNewPath()
                      .withPath("/")
                      .withPathType("ImplementationSpecific")
                      .withNewBackend()
                      .withNewService()
                      .withName(metadata.getName())
                      .withNewPort().withNumber(8080).endPort()
                      .endService()
                      .endBackend()
                    .endPath()
                  .endHttp()
                .endRule()
                .endSpec()
                .build();
  }
 

  @Override
  public boolean isMet(Mockserver primary, Ingress secondary, Context<Mockserver> context) {
    return context.getSecondaryResource(Ingress.class).map(in -> {
      final var status = in.getStatus();
      if (status != null) {
          final var ingresses = status.getLoadBalancer().getIngress();
          // only set the status if the ingress is ready to provide the info we need
          return ingresses != null && !ingresses.isEmpty();
      }
      return false;
      }).orElse(false);
  }
  

}
