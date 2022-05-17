# Kubernetes, Docker 설치 및 세팅

## Install Docker Desktop
- [공식 설치 링크](https://www.docker.com/products/docker-desktop/)
- Docker Destop의 설정에서 `Kubernetes` 를 사용하도록 설정 해준다 (enable Kubernetes)

## Install Kubernetes
- Kubernetes를 binary혹은 brew로 받아 설치한다.
    ```bash
    brew install kubectl
    brew install kubernetes-cli
    ```
- minikube를 사용해도 된다
    ```bash
    brew install minikube
    minikube start
    ```

## Kubernetes Dashboard 설치
- [공식문서](https://kubernetes.io/ko/docs/tasks/access-application-cluster/web-ui-dashboard/) 를 참고했다
- 대시보드는 웹 기반 쿠버네티스 유저 인터페이스이다. 대시보드를 통해 컨테이너화 된 애플리케이션을 쿠버네티스 클러스터에 배포할 수 있고, 컨테이너화 된 애플리케이션을 트러블슈팅할 수 있으며, 클러스터 리소스들을 관리할 수 있다

- 대시보드 UI 배포
    ```bash
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.4.0/aio/deploy/recommended.yaml
    ```
- 대시보드 활성화
    ```bash
    kubectl proxy
    ```

## Kubernetes Dashbard 로그인 작업 - token 방법
- Kubernetes에 접근하는 경우, 접근 권한을 설정해야 한다.
- NAME, TYPE, DATA, AGE 출력
    ```bash
    kubectl get secrets
    ```
- 위의 명령어에서 나온 `NAME` 을 가지고와서 `secret`을 `describe` 해준다
    ```bash
    NAME                  TYPE                                  DATA   AGE
    default-token-85fsn   kubernetes.io/service-account-token   3      15h
    ```
- 해당 시크릿 describe 명령어
    ```bash
    kubectl describe secret default-token-85fsn
    ```
- 위의 명령어에서 나온 token값을 가지고 로그인 시 사용
    ```bash
    kubectl get secret default-token-85fsn -o jsonpath='{.data.token}' | base64 --decode
    ```
- 혹은 minikube를 사용해도 된다
    ```bash
    minikube dashboard
    ```
    - 이 경우, minikube는 kubectl

## Kubernetes Dashbard 로그인 작업 - config yaml 작성방법
- 접근권한 관련 yaml 파일을 생성한다

### admin-user 계정 생성용 yaml생성
- 임의의 위치에서 `service-account.yml` 파일 생성 후 아래와 같이 편짐
    ```yaml
    apiVersion: v1
    kind: ServiceAccount
    metadata:
        name: admin-user
        namespace: default # kube-system
    ```
    - kind : ServiceAccount -> 쿠버네티스에서 관리하는 게정 생성을 의미
    - name : admin-user -> 생성할 계정 이름
    - namespace : default -> 계정을 생성할 네임스페이스

### admin-user 권한 부여용 yaml 생성
- 마찬가지로 임의의 위치에서 `role-binding.yaml` 파일을 작성하고 아래와 같이 편집
    ```yaml
    apiVersion: rbac.authorization.k8s.io/v1
    kind: ClusterRoleBinding
    metadata:
        name: admin-user
    roleRef:
        apiGroup: rbac.authorization.k8s.io
        kind: ClusterRole
        name: cluster-admin
    subjects:
        - kind: ServiceAccount
          name: admin-user
          namespace: default # kube-system
    ```

### 생성한 유저와 권한 적용하기
- yaml파일로 `service-account.yaml` 과 `role-binding.yaml`을 가지고 계정을 생성 및 권한을 부여한다
    ```bash
    kubectl create -f service-account.yaml
    kubectl create -f role-binding.yaml
    ```

### 계정의 토큰을 조회
```bash
kubectl -n kube-system describe secret $(kubectl -n kube-system get secret | grep admin-user | awk '{print $1}')
```



## Kubernetes Dashboard 실행
- Kubernetes Dashboard
    ```bash
    kubectl proxy
    ```
- 위의 명령어를 실행 한 후 아래의 주소에 접속한다
    - http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/

- Minikube를 사용 할 경우 -> 간단하지만 동시에 사용 x
    ```bash
    minikube dashboard
    ```
    - 위의 명령어로 실행 할 수 있지만, kubectl로 배포된 dashboard와 중복되지 못한다 -> 아래의 명령어로 dashboard를 지우고 다시 실행한다
        ```bash
        kubectl delete clusterrolebinding kubernetes-dashboard
        ```
