# kubernetes 배포 과정의 command
- 최초작성 : 2022-05-18

---
## minkube Cluster 시작
```
minikube start
```

## minikube Dashboard 시작
```
minikube dashboard
```

## Deploy application
```
kubectl create deployment {application_name} --image={image_name}
```

## Delete Deployment
```
kubectl delete -n default deployment {deployment_name}
```

## Delete Service
```
kubectl delete -n default service {service_name}
```

## Delete Pod
```
kubectl delete -n default pod {pod_name}
```

## Delete ReplicaSet
```
kubectl delete -n default replicaset {replica_name}
```
