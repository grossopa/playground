# Development
Install and run Consul agent in dev mode

`consul agent -dev`

## Java Reference

https://cloud.spring.io/spring-cloud-consul/reference/html/

## Consul + K8s

https://learn.hashicorp.com/tutorials/consul/kubernetes-kind

`choco install kind`

`helm repo add hashicorp https://helm.releases.hashicorp.com`

`helm repo update`

`kind create cluster --name consul`

`kubectl cluster-info --context kind-consul`

```
cat > config.yaml <<EOF
global:
name: consul
datacenter: dc1
server:
replicas: 1
ui:
enabled: true
service:
type: 'NodePort'
connectInject:
enabled: true
controller:
enabled: true
EOF
```


`helm install -f config.yaml consul hashicorp/consul --version "0.31.1"`

`kubectl get pods`

`kubectl port-forward consul-server-0 8500:8500`

`kubectl apply -f scripts/consul/consul-apps-user-video.yaml`

## Hashicorp Projects

```
kubectl apply -f scripts/demo/counting.yaml
kubectl apply -f scripts/demo/dashboard.yaml
```

## Projects
```
mvn clean install
docker build -t consul-apps/user-video .\consul-apps-user-video\
minikube image load consul-apps/user-video:latest

kubectl delete -f scripts/consul/consul-apps-user-video.yaml
consul services deregister -id=consul-apps-user-video
kubectl apply -f scripts/consul/consul-apps-user-video.yaml

docker build -t consul-apps/user-friends .\consul-apps-user-friends\
minikube image load consul-apps/user-friends:latest
kubectl delete -f scripts/consul/consul-apps-user-friends.yaml
consul services deregister -id=consul-apps-user-friends
kubectl apply -f scripts/consul/consul-apps-user-friends.yaml

docker build -t consul-apps/user .\consul-apps-user\
minikube image load consul-apps/user:latest
kubectl delete -f scripts/consul/consul-apps-user.yaml
consul services deregister -id=consul-apps-user
kubectl apply -f scripts/consul/consul-apps-user.yaml

kubectl port-forward consul-apps-user-* 8080:8080
```
