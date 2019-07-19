Demo for testcontainers

Mongo with replica set

```java
@ClassRule
public static GenericContainer mongo = new GenericContainer<>("mongo:4.0.10")
    .withExposedPorts(27017).withCommand("--replSet tx-replica-set");
```

Initialize and create a collection

```java
@BeforeClass
public static void setup() throws IOException, InterruptedException {
    mongo.execInContainer("/bin/bash", "-c", "mongo test --eval 'rs.initiate()'");
    mongo.execInContainer("/bin/bash", "-c", "until mongo --eval \"printjson(rs.isMaster())\" | grep ismaster | grep true > /dev/null 2>&1;do sleep 1;done");
    mongo.execInContainer("/bin/bash", "-c", "mongo test --eval 'db.createCollection(\"customers\")'");
}
```

Watch the newly created docker containers:

```bash
$ watch docker ps -a


CONTAINER ID   IMAGE                              COMMAND               CREATED         STATUS          PORTS                     NAMES
3637f9912a45   mongo:4.0.10                       "docker-entrypoint"   4 seconds ago   Up 2 seconds    0.0.0.0:32831->27017/tcp  hardcore_bassi
de46ce1c9bb8   quay.io/testcontainers/ryuk:0.2.3  "/app"                5 seconds ago   Up 3 seconds    0.0.0.0:32830->8080/tcp   testcontainers-ryuk-d8455e78-814d-4569-9c76-1482be65bf16
...

```


