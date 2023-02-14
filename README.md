# mockserver-operator

This project uses Quarkus, the Supersonic Subatomic Java Framework.


...


# HOW TO USE

## Locally
You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

```yaml Mockserver
apiVersion: "rmarcello.mockserveroperator/v1"
kind: Mockserver
metadata:
  name: mockserver-test
spec:
  replica: 1
  image: mockserver/mockserver:latest
  config: |
    [
    {
      "httpRequest": {
        "path": "/hello"
      },
      "httpResponse": {
        "statusCode": 200,
        "body": {
          "json": {
            "message": "Hello World"
          }
        }
      }
    }
    ]
```
or 
```
kubectl apply -f .\k8s\mockserver-test.yaml
```
### Verify
```
kubectl port-forward svc/mockserver-test 8080:8080
```
from your browser: http://localhost:8080/mockserver/dashboard

we can see the initialization data.
```
curl http://localhost:8080/hello
...
```
## Update

When we change the CRD changing the configuration, for exaple addiing new API mocks:
```
kubectl apply -f .\k8s\mockserver-test2.yaml
```

The operator will update the Mockserver and add the new API:


```


curl http://localhost:8080/products | jq
...
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/mockserver-operator-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

