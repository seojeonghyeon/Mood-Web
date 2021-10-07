# Mood-Web
  Copyright 2021. 서정현, 김일준, Mood All Rights Reserved.
  
  
  데이팅 앱(Mood)의 서버(Back-end)관련 개발 소스입니다. 본 코드는 수익과 관련이 있을 수 있으므로 무단 유출과 배포, 사용을 금지합니다.(2021.10.7)

* Document(Communication Protocol)
* Server(AWS:EC2)
* Docker
* Mood-Web
* Config-server
* Apigateway-server
* user-service
* matching-service
* lock-service
* post-service
* SQL(MariaDB)


## Communication Protocol
  This part is for communication protocol(Android and Back-end Server). Let's see the document
  ```
  GitHub(Here) : https://github.com/seojeonghyeon/Mood-Web/tree/main/Communication%20Protocol
  ```

## Server(AWS:EC2)

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
     추가 적으로 주로 쓰는 명령어
     ```
     Docker 이미지 확인 : docker images -a (-a는 전체)
     Docker 컨테이너 확인 : Docker container ls -a
     Docker 컨테이너 내부로 접속 : docker exec -it 컨테이너이름 /bin/bash
     ```
     
  4. Service File Push
  5. Service File Pull
  6. Service File Run
  7. Container와 Log를 통한 Service 작동상태 확인





## Mood-Web


## Config-server


## Apigateway-server


## user-service


## matching-service


## lock-service


## post-service


## SQL(MariaDB)

