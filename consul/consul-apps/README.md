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
