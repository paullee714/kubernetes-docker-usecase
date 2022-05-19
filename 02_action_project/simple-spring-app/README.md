# Project with Kubernetes

## Build Simple Spring Project
- 정말 간단한 api 서비스를 생성하자
- `/api` 와 `/api/info` 만 생성하려고한다
### Dependency
- `spring-boot-starter-web` 만 있으면 된다
```
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```
### Controller
- 컨트롤러를 사용해서 API Endpoint를 생성 해 준다
    ```java
    package com.example.demo.controller;

    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/api")
    public class testController {

        @GetMapping
        public String homeApi() {
            return "home API";
        }

        @GetMapping("/info")
        public String infoApi() {
            return "information API";
        }

    }
    ```

### JAR Build
- 만든 프로젝트의 소스코드를 너무 쉽게 보여주고싶다! 면 build, run 모두 Dockerfile에 넣어도 되겠지만 이번 정리에서는 Jar파일을 빌드하고 배포하는 과정을 정리하려고 한다
- Project의 Root 폴더에서 실행
    ```
    ./gradlew clean build
    ```
- 최근에는 `./gradle build bootBuildImage` 를 사용하기도 한다. 이부분은 추후 다시 정리하려고 한다

### Docker Build
- 위에서 생성한 JAR파일을 가지고 Docker Image를 만들어 주자
- Dockerfile에 JAR파일을 실행하는 명령어를 사용한다
    ```Dockerfile
    FROM openjdk:11-jre-slim
    VOLUME /tmp
    ARG JAR_FILE=build/libs/*.jar
    COPY ${JAR_FILE} app.jar
    ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
    ```

- public DockerHub에 파일을 올릴 수 있도록 Dockerfile을 빌드 해 준다
    ```
    docker build -t paullee714/spring-boot-sample .
    ```
- build 된 도커파일 태그이름으로 docker hub repo에 올린다
    ```
    docker push paullee714/springboot-sample
    ```
    - docker push를 사용 할 경우 Repo 아이디로 로그인이 필요
        ```
        docker login
        ```

## Kubernetes
- 쿠버네티스 환경을 준비하고, 세팅하는 과정을 적었다
- 쿠버네티스에서 스프링부트 이미지를 가져오고, 배포하고, 접근 할 수 있도록 포트포워딩을 해 주는 작업을 정리한다

### Minikube Start & Dashboard
- 쿠베 환경 실행과 편한 사용을 위해 UI 대시보드를 띄우려고한다
- minikube 설치
    ```
    brew install kubectl
    brew install minikube
    ```
- minikube 실행
    ```
    minikube start
    ```
- minikube 실행 후 대시보드 접속
    ```
    minikube dashboard
    ```

### 배포를 위한 deployment.yaml 생성
```
kubectl create deployment spring-sample --image=paullee714/springboot-sample --dry-run=client -o yaml > devployment.yaml
```


### service.yaml 생성
```
kubectl create service clusterip spring-sample --tcp=8080:8080 --dry-run=client -o yaml > service.yaml
```

### development.yaml, service.yaml 배포
```
kubectl apply -f devployment.yaml
kubectl apply -f service.yaml
```

### 확인하기
- Dashboard의 Pod 에서 확인 할 수 있다
- 명령어를 실행해서 확인
    ```
    kubectl get all | grep spring-sample
    ```

### 포트포워딩
- 쿠버네티스 내부의 포트 8080으로 통신하고있지만, 외부로 나가는 포트를 열어주지 않았다
```
kubectl port-foward svc/spring-sample 8080:8080 #1
kubectl port-foward deployment/spring-sample 8080:8080 #2
```
- 1,2 모두 같은 뜻이다
- 잘 실행이 된다면 아래와같이 터미널에 출력이 될 것이다
    ```
    kubectl port-forward deployment/spring-sample 8080:8080
    Forwarding from 127.0.0.1:8080 -> 8080
    Forwarding from [::1]:8080 -> 8080
    ```
- 위의 명령어는 커맨드라인 종료가 되지 않으므로 계속 켜져있어야 한다. 계속 켜놓은 상태를 유지하고싶으면 nohup을 사용하면 된다


## 실제로 실무에서는
- 특정 서비스라면 docker public repo 보다 private을 사용
- eks위에서 ecr을 사용한 레지스트리를 사용
- 그라파나, 프로메테우스가 연동되어 모니터링 툴로 사용
