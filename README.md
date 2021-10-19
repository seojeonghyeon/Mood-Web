# Mood-Web
  Copyright 2021. 서정현, 김일준 및 Mood 기획, 개발진 Mood All Rights Reserved.
  
  
  데이팅 앱(Mood)의 서버(Back-end)관련 개발 진행 중인 소스입니다. 본 코드는 수익과 관련이 있을 수 있으므로 무단 유출과 배포, 사용을 금지합니다.(2021.10.7)
  
  개인적인 소스 코드에 대한 Notion 확인이 가능합니다.
  ```
  Notion : https://seojeonghyeon0630.notion.site/35a8b455c30d4255a5410934f3b3beba?v=e0f72e4a793f40c985120312b8bf854d
  ```

* [Introduction](*introduction)
* [DOCUMENT](#document)
* [Point](#point)
* [Server](#server)
* [Docker](#docker)
* [Mood-WEB](#mood-web)
* [CONFIG-SERVER](#config-server)
* [APIGATEWAY-SERVER](#apigateway-server)
* [USER-SERVICE](#user-service)
* [MATCHING-SERVICE](#matching-service)
* [LOCK-SERVICE](#lock-service)
* [POST-SERVICE](#post-service)
* [SQL](#sql)


## Introduction
- Server(Back-end) 1차 개발 진행상황
![그림4](https://user-images.githubusercontent.com/24422677/137906684-0757e676-0cd6-4df2-9228-a4c790feb125.png)



- Android Application
<p float="center">
  <img width="33%" alt="그림1" src="https://user-images.githubusercontent.com/24422677/137891508-f32fb44b-7e63-4ecf-af3f-f5e93aef550f.png">
  <img width="33%" alt="그림2" src="https://user-images.githubusercontent.com/24422677/137893181-67ff4b16-65ba-4739-a749-d93a6b59e85f.png">
  <img width="33%" alt="그림3" src="https://user-images.githubusercontent.com/24422677/137891977-eaa69668-09e2-4fb9-b416-b2475fd6a9b6.png">
</p>

짧은 개발기간과 인수인계에 대한 부담에 Kafka Server, 관리자서버(Vue.js or Nuxt.js)에 대한 구현은 2차 개발로 미루게 되었다.

## Document
  안드로이드와 서버 개발 간 사용한 통신 프로토콜이다.
  ```
  GitHub(Here) : https://github.com/seojeonghyeon/Mood-Web/tree/main/Communication%20Protocol
  ```

## Point

사용자가 매칭 서비스를 이용하게 되면, 사용자가 설정한 정보에 따라 알맞는 대상을 검색하게 된다. 이때, 최대한 두 사람의 Mood Distance가 유사하게 나와야 두 사람의 성향이 비슷하다는 것을 알 수 있기 때문에 같은 그룹으로 묶게 된다.

1. Mood Distance 탐색(Algorithm to find the area of a polygon활용)
<img width="100%" alt="스크린샷 2021-10-19 18 28 59" src="https://user-images.githubusercontent.com/24422677/137910827-362d4cf5-fbb1-48d6-a02c-bcf5f7c870d7.png">
사람과 사람을 매칭할 때, 가입할 때 조사한 5가지 항목을 토대로 오각형을 그리게 된다. 위 사진에서 서로의 오각형의 모양이 비슷한 정도. 즉, 서로의 성향이 비슷한 정도가 Mood Distance이다.
<img width="100%" alt="스크린샷 2021-10-19 18 29 17" src="https://user-images.githubusercontent.com/24422677/137911188-a3b3f7fb-9388-433f-931a-20ba8a4bea20.png">
Mood Distance는 위 그림과 같이 교집합/합집합으로 계산한다. 계산된 값은 퍼센테이지로 표시된다. 그렇다면 해당 교집합 부분과 합집합 부분의 넓이를 어떻게 하면 구할 수 있을까?
넓이를 구할 수 있는 방법에는 무수히 많지만 그 중에서 'Algorithm to find the area of a polygon'을 사용하기로 하였다. 해당 알고리즘은 각 점들의 좌표만 알게 된다면 해당 도형의 넓이값을 알 수 있다는 장점을 가지고 있다.

<img width="100%" alt="스크린샷 2021-10-19 18 29 33" src="https://user-images.githubusercontent.com/24422677/137911859-fb8c2112-5d81-4e0c-8731-da454f3fcc46.png">
그렇다면 'Algorithm to find the area of a polygon'이 무엇일까? 위 사진과 같이 점들이 주어진다고 하면 (x1,y1)부터 (x4,y4)까지의 비선형 함수와 x축까지의 넓이를 구한다고 한다면 아마 적분을 할 것이다. 위 알고리즘이 바로 그것이다. 각 점들의 위를 이용해서 직선을 만들고 각 직선을 적분하여 해당 도형의 넓이를 구하는 것이다. 이렇게 되면, 어떤 도형이든 각 점들의 좌표만 알면 도형의 넓이를 구할 수 있다.
위 내용은 사이트(https://www.mathopenref.com/coordpolygonarea2.html)를 참고하였다.

이제 점들의 좌표를 구해보자.
먼저 오각형의 영역을 나누어주었다. 오각형은 각 점들이 일정한 각도를 이루고 있기 때문에 각도를 이용하여 선형함수를 구해 해당 선형함수 위에 값을 표시하면 되는것이다.
<img width="100%" alt="스크린샷 2021-10-19 18 29 51" src="https://user-images.githubusercontent.com/24422677/137913367-6703602f-11e6-46da-8381-681531436616.png">
위와 같이 영역을 나누어주게되면 해당영역에서 좌표를 찍을 경우 일어나는 현상은 크게 세가지이다.
<p float="center">
  <img width="33%" alt="스크린샷 2021-10-19 18 30 01" src="https://user-images.githubusercontent.com/24422677/137913605-96bdf87e-b330-4a0b-8965-e1981eac1fa6.png">
  <img width="33%" alt="스크린샷 2021-10-19 18 30 41" src="https://user-images.githubusercontent.com/24422677/137913616-b6d96700-122a-49f8-a668-732fe2b7e82f.png">
  <img width="33%" alt="스크린샷 2021-10-19 18 30 50" src="https://user-images.githubusercontent.com/24422677/137913632-33aca8cd-cb70-4ea4-9c41-6559eeafea18.png">
</p>

위 그림과 같이 (1)평행하거나 (2)겹치거나 (3)교점이 생긴다. 이를 토대로 처리를 해주면 충분히 정상적인 값이 나온다. 이를 각 각도에 맞춰 5개의 영역에 대해 실시하고 추출된 교점에 대해 Mood Distance를 구하면된다.


2. 사용자 그룹 분류(Classification : Decision Tree)
![그림6](https://user-images.githubusercontent.com/24422677/137914391-168b9fba-ee78-4a40-a80e-34fc4e0352f2.png)
사용자의 그룹을 분류하는 큰 목적은 유사한 성향의 사용자들을 그룹지어 만족도를 높이는 것에 있다. 다양한 사용자가 존재하고 그에 따라 오각형의 모양도 저마다 다를 것이다.
이때, 특이한 모양에 대해 따로 그룹을 묶어서 그룹에 묶이지 않은 사용자들에 대해서는 가우시안 분포에 이르도록 하는 것이다. 특이한 모양에 대해 정의를 하자면 (1)1가지 특성만 지나치게 높다. (2)2가지 특성만 지나치게 높다. (3) 1가지 특성만 지나치게 낮다. (4)2가지 특성만 지나치게 낮다. (5)전체적으로 Mood Distance(이때, Mood Distance는 (사용자의 오각형의 넓이/전체넓이)이다.)의 크기가 크다. (6)전체적으로 Mood Distance의 크기가 작다. 등이 된다. 특정 사용자에 대해 그룹화를 하여 매칭의 효율을 높여주는 역할을 해준다. 이후, 위 사진과 같이 가운데 분포한 사용자의 경우나 각 그룹에 사용자가 많아진다면 추가적인 영역을 나눠 피라미드 구조가 되도록 하여야 한다. 그래야 VIP가 이익을 보기 때문이다. 
 위 Classification을 사용하기 위해서는 다음과 같은 제약사항이 존재한다. (1)사용자의 수가 충분히 많아야 한다. (2)그룹이 너무 많다면 서비스가 제대로 될 수 없다. 이를 보안하기 위해서 주기적으로 사용자 수를 확인한다. 그리고 사용자 수에 대해 그룹의 수를 달리하여 매칭의 효율을 높인다.




## Server

1. 서버에 접속 방법
   - Mac OS(Terminal)

      ```
      sudo ssh -i ServerKey.pem ubuntu@ServerIP
      ```
      ```
      ex. <code>sudo ssh -i /Users/zayden/Documents/Work/mood/KeyPair.pem ubuntu@15.0.0.0</code>
      ```
   - Windows
      Putty 사용(Googling)


2. 서버로 파일 전송
   - Mac OS(Terminal)
     ```
     scp -i "ServerKey.pem" (-f : 폴더 전체 복사) 파일위치 및 파일 명 ubuntu@ServerIP:복사하고자하는위치
     ```
     ```
     ex)  Folder :  scp -i "moodServerKey.pem" -f /Document/ ubuntu@ec2~~~~~~.apnortheast2.compute.amazonaws.com:/root/test/
          File   :  scp -i "moodServerKey.pem" /Document/abc.txt ubuntu@ec2~~~~~~.apnortheast2.compute.amazonaws.com:/root/test/
     ```
    
   - Windows
      FileZilla 사용(Googling)


## Docker
  Docker에 대한 정의는 Googling하면 자세한 설명들이 있다. 짧게 이야기하면, 가상머신과 같이 OS위에 컨테이너를 생성하여 해당 컨테이너 공간에 이미지를 생성해주는 역할을 한다. 
  + Build는 DockerFile을 토대로 원하는 대상을 이미지로 만드는 작업이다.
  + Push는 Build된 이미지를 Docker서버로 이미지를 올리는 작업이다.   
  + Pull은 Docker서버로부터 이미지를 받는 작업이다.
  + Run은 이미지를 컨테이너 내에 생성하는 작업이다.
  
  
  1. Service File 생성

      (1) DockerFile생성
    
    
      (2) 배포를 위한 파일(Jar)생성
    
    
      (3) 개발도구에서 maven package단계까지 실행 또는 명령어를 통한 maven 실행
    
        ex. target Folder가 보이는 Folder에서, (-DskipTests=true는 Test 단계를 통과해야하는 경우에 사용) 
    
        ```
        mvn clean compile package (-DskipTests=true)   
        ```


  2. Service File Build

     DockerFile을 토대로 대상을 이미지화 한다. 
     (DockerFile이 위치한 곳에서) (-t는 태그이름)
     ```
     docker build --tag(or -t) (Docker계정)/(Service명):(Version) .(위치)
     ```
     ```
     ex) docker build --tag seojeonghyeon0630/user-service:0.0.1 .
     ```
     
  3. Service File Push
     ```
     docker push seojeonghyeon0630/mood-web:0.0.1
     ```


  4. Service File Pull


     ```
     docker pull seojeonghyeon0630/mood-web:0.0.1
     ```

  5. Service File Run

     Docker Bridge Network 생성(172.18.0.1에서부터 subnet mask를 16으로)
     ```
     docker network create --gateway 172.18.0.1 --subnet 172.18.0.0/16 mood-network
     ```
     
     
     생성된 Docker Network를 확인(확인하게 되면 기본적으로 제공하는 Network 3개와 추가로 설정한 1개의 Bridge Network를 확인 가능)
     ```
     Docker network ls
     ```
     
     생성한 Network에 Config서버 이미지를 Run(-d : 백그라운드 실행, -p : 포트명(in:out), -e : 환경변수 설정)
     ```
     docker run -d -p 8888:8888 --network mood-network -e "spring.profiles.active=prod” --name config-service seojeonghyeon0630/config-service:0.0.1
     ```
     Seojeonghyeon의 GitHub(Private) : Mood-Config를 확인하면 3가지(default, dev, prod)로 업로드 하였다.
     
     알맞는 설정에 맞춰 선택하여 해당 환경변수를 유동적으로 변경해주면 된다.
     
     순차적으로 나머지 서버들도 컨테이너를 생성해주자.
     
     
     
     Discovery서버의 이미지를 컨테이너에 생성
     ```
     docker run -d -p 8761:8761 --network mood-network \
     -e "spring.cloud.config.uri=http://config-service:8888" \
     --name mood-web seojeonghyeon0630/mood-web:0.0.1
     ```
     
     ApiGateway서버의 이미지를 컨테이너에 생성
     ```
     docker run -d -p 8000:8000 --network mood-network \
     -e "spring.cloud.config.uri=http://config-service:8888" \
     -e "eureka.client.serviceUrl.defaultZone=http://mood-web:8761/eureka/" \
     --name apigateway-service seojeonghyeon0630/apigateway-service:0.0.1
     ```
     
     기존 Local PC에서 사용하던 MariaDB를 이미지화 하여 사용하던 데이터들도 함께 서버로 보내주었다.(물론, MariaDB 이미지를 Docker서버로부터 가져와서 Run해도 된다. Table을 생성해주는 것이 번거롭지만..)
     
     
     mysql database 정보를 담고 있는 폴더를 다른 폴더에 담아두고 DockerFile로 생성하여 Build해주었다. 
     
     
     DockerFile 내용을 보면 mysql_data라는 폴더를 만들었고 데이터베이스 내용을 담고 있는 mysql폴더를 복사해왔다. 컨테이너가 생성되면 /var/lib/mysql에 복사되어 들어가게 된다.
     ```
     FROM mariadb
     ENV MYSQL_ROOT_PASSWORD 비밀번호
     ENV MYSQL_DATABASE mooddb
     COPY ./mysql_data/mysql /var/lib/mysql
     EXPOSE 3306
     ENTRYPOINT ["mysqld", "--user=root"]
     ```
     DockerFile을 토대로 빌드해서 이미지를 생성하고 Push해서 Docker서버로 올렸다가 Pull로 원하는 서버로 내려받자.
     MariaDB도 Bridge Network에 포함시켜서 컨테이너를 생성시켜주자.
     ```
     docker run -d -p 3306:3306 --network mood-network --name mariadb seojeonghyeon0630/my_mariadb:0.0.1
     ```
     
     컨테이너 생성이 완료되었다면 컨테이너 외부에서 접근 가능하게, 그리고 데이터들이 온전하게 잘 들어갔는지 확인하기 위해 컨테이너 내부로 접근하자. 
     ```
     docker exec -it mariadb /bin/bash
     ```
     
     MariaDB 로그인(127.0.0.1은 현재 접근할 수 없다. 외부 접근 설정을 해줘야만 사용 가능하다.)
     ```
     mysql -hlocalhost -uroot -p
     ```
     
     Database 중에 mysql, 사용하려는 Database로 접근해서 권한을 부여하자. 부여가 완료되었다면, 데이터베이스 내용들이 잘 들어가 있는지 검토해주자.
     ```
     grant all privileges on *.* to 'root'@'%' identified by '비밀번호';
     ```
     
     User 서버도 유사하게 Build해서 컨테이너를 생성해주자. 
     ```
     docker run -d --network mood-network \
     --name user-service \
     -e "spring.cloud.config.uri=http://config-service:8888" \
     -e "eureka.client.serviceUrl.defaultZone=http://mood-web:8761/eureka/" \
     -e “spring.datasource.url=jdbc:mariadb://mariadb:3306/mooddb” \
     -e "logging.file=/api-logs/users-ws.log" \
     seojeonghyeon0630/user-service:0.0.1
     ```


  6. Container와 Log를 통한 Service 작동상태 확인
     ```
     Docker 이미지 확인 : docker images -a (-a는 전체)
     Docker 컨테이너 확인 : Docker container ls -a
     Docker 컨테이너 내부로 접속 : docker exec -it 컨테이너이름 /bin/bash
     Docker 컨테이너를 멈추고 삭제 : docker stop fa7 && docker rm fa7 (다 입력할 필요 없다. 똑똑해)
     Docker 내 안쓰고 있는 데이터들 정리 : docker system prune
     Docker Log 확인 : docker logs fa7
     Docker Log 지속 확인 : docker -f logs fa7
     ```
    



## Mood-Web


## Config-server


## Apigateway-server


## user-service


## matching-service


## lock-service


## post-service


## SQL


<img width="489" alt="스크린샷 2021-10-19 18 28 59" src="https://user-images.githubusercontent.com/24422677/137883285-c6dfe5ab-05ca-4208-9400-41abaa0f1d9f.png">
