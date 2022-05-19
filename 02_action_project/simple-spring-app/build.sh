#!/bin/bash
dir="${PWD}"
cd "${dir}/demo/"
./gradlew clean build
docker build -t paullee714/spring-boot-sample .
docker push paullee714/spring-boot-sample

kubectl create deployment spring-sample --image=paullee714/springboot-sample --dry-run=client -o yaml > devployment.yaml
kubectl create service clusterip spring-sample --tcp=8080:8080 --dry-run=client -o yaml > service.yaml
kubectl apply -f devployment.yaml
kubectl apply -f service.yaml

kubectl get all | grep spring-sample

kubectl port-forward deployment/spring-sample 8080:8080
