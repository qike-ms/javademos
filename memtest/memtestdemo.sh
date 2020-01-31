# JAVA memory demo

###### Preparations
az account set -s "AKS Test (qike)"
az acr login -n qkeastus
az configure --defaults location=eastus
az configure --defaults group=qike_eastus
az configure --defaults acr=qkeastus.azurecr.io
az configure --defaults spring-cloud=scdemo

git clone https://github.com/qike-ms/memtest.git
cd memtest
mvn clean package -D skipTests
# Local test
java -Xmx400m -XX:+PrintGC -DlogLevel=FINE -jar target/mem-test.jar
# Docker build and test with different max heap sizes
docker build -f Dockerfile --build-arg heap_size="2g" -t qkeastus.azurecr.io/demo/mem-test:latest .
docker container run --rm qkeastus.azurecr.io/demo/mem-test:latest env 
docker run --rm -e JAVA_OPTS='-Xmx1.5g -XX:+PrintGC' qkeastus.azurecr.io/demo/mem-test:latest
docker run --rm -e JAVA_OPTS='-Xmx200m -XX:+PrintGC' qkeastus.azurecr.io/demo/mem-test:latest



###### Live demo
#### Cluster Overload issue on AKS:
### Case 0:  Show availability zone support

### Case 1:  Resource limit too high
k apply -f mem-test-deployment.yaml
"-Xmx2g" and not set resources #  "2Gi"
# evicted and all nodes got tainted due to memory pressure
k describe po -lapp=mem-test
k describe no

### Case 2:  Resource limit too low, lower than heapsize
        resources:
          requests:
            memory: "250Mi"
            cpu: "250m"
          limits:
            memory: "500Mi"
            cpu: "500m"
k get po -lapp=mem-test
k describe  po -lapp=mem-test  # <-- See CrashLoop Reason, OOMKilled
#k exec -it memtest-gk7rv -- /bin/bash
#k logs -lapp=mem-test --tail=-1


### Case 3: AZ
k get no --show-labels
k scale deployment.v1.apps/memtest --replicas=3
k get po -o wide  # <-- distributed on 3 nodes

# Container logs from LA
ContainerLog 
| sort by TimeOfCommand desc
| take 50
| project TimeOfCommand, LogEntry 


####  Use Spring Cloud to run the memtest app
az spring-cloud app create --name memtest2 --cpu 1 --memory 1 --instance-count 1
az spring-cloud app deploy -n memtest2 --jar-path ./target/mem-test.jar --env JAVA_OPTS='-Xmx500m' --env logLevel="FINE"

# Check logs for gateway app
az spring-cloud app log tail --name memtest2

# memtest2 logs
AppPlatformLogsforSpring
| project TimeGenerated, ServiceName, AppName , InstanceName , Log
# | summarize count() by AppName
| where AppName == "memtest2"
| sort by TimeGenerated desc 








