# Mood-Web
  Copyright 2021. 서정현, 김일준, Mood All Rights Reserved.
  
  
  데이팅 앱(Mood)의 서버(Back-end)관련 개발 진행 중인 소스입니다. 본 코드는 수익과 관련이 있을 수 있으므로 무단 유출과 배포, 사용을 금지합니다.(2021.10.7)
  
  개인적인 소스 코드에 대한 Notion 확인이 가능합니다.
  https://seojeonghyeon0630.notion.site/35a8b455c30d4255a5410934f3b3beba?v=e0f72e4a793f40c985120312b8bf854d

* [DOCUMENT](#document)
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


## Document
  This part is for communication protocol(Android and Back-end Server). Let's see the document
  ```
  GitHub(Here) : https://github.com/seojeonghyeon/Mood-Web/tree/main/Communication%20Protocol
  ```

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

