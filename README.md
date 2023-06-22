# Capstone Project Server

### Folder Structure
- `src`: 메인 로직
  `src`에는 도메인 별로 패키지를 구성하도록 했다. **도메인**이란 회원(User), 게시글(Post), 댓글(Comment), 주문(Order) 등 소프트웨어에 대한 요구사항 혹은 문제 영역이라고 생각하면 된다. 설계할 APP을 분석하고 필요한 도메인을 도출하여 `src` 폴더를 구성하자.
- `config` 및 `util` 폴더: 메인 로직은 아니지만 `src` 에서 필요한 부차적인 파일들을 모아놓은 폴더
- 도메인 폴더 구조
> Route - Controller - Service - DAO
> Springboot에서는 Route기능을 Controller단에서 해준다.

- Route: Request에서 보낸 라우팅 처리
- Controller: Request를 처리하고 Response 해주는 곳. (Service에 넘겨주고 다시 받아온 결과값을 형식화)
- Service: 비즈니스 로직 처리, 의미적 Validation 처리, 형식적 Validation 처리
- DAO: Data Access Object의 줄임말. Query가 작성되어 있는 곳.


Controller -> Service -> DAO -> DB

> DB -> DAO(Repository) -> Service -> Controller -> Route -> `Response`

#### Springboot java (패키지매니저 = Maven (= Spring 선호), Gradle (Springboot 선호))
> Request(시작) / Response(끝) ⇄ Controller(= Router + Controller) ⇄ Service (CUD) / Provider (R) ⇄ DAO (DB)

### Validation
서버 API 구성의 기본은 Validation을 잘 처리하는 것이다. 외부에서 어떤 값을 날리든 Validation을 잘 처리하여 서버가 터지는 일이 없도록 유의하자.
값, 형식, 길이 등의 형식적 Validation과 DB에서 검증해야 하는 의미적 Validation은 Service에서 처리하게 한다.

## ✨Structure
앞에 (*)이 붙어있는 파일(or 폴더)은 추가적인 과정 이후에 생성된다.
```text
api-server-spring-boot
  > * build
  > gradle
  > * logs
    | app.log // warn, error 레벨에 해당하는 로그가 작성 되는 파일
    | app-%d{yyyy-MM-dd}.%i.gz
    | error.log // error 레벨에 해당하는 로그가 작성 되는 파일
    | error-%d{yyyy-MM-dd}.%i.gz
  > src.main.java.com.example.demo
    > config
      > secret
        | Secret.java // git에 추적되지 않아야 할 시크릿 키 값들이 작성되어야 하는 곳
      | BaseException.java // Controller, Service 에서 Response 용으로 공통적으로 사용 될 Exception 클래스
      | BaseResponse.java // Controller 에서 Response 용으로 공통적으로 사용되는 구조를 위한 모델 클래스
      | BaseResponseStatus.java // Controller, Service 에서 사용 할 Response Status 관리 클래스 
    > src
      > batch
        > domain
          | BooleanToYNConverter.java //  Boolean 값을 "Y" 또는 "N" 문자열로, 그리고 "Y" 또는 "N" 문자열을 Boolean 값으로 변환하는 AttributeConverter를 구현
          | HankerJobA.java // Quartz 스케줄러를 사용하여 특정 작업을 수행하는 클래스, 여기서는 특정 시간이 되면 해당 작업을 일시중지 한다.
          | TestBatch.java // 배치 테스트를 위한 Dto라고 보면 된다.
        > repository
          | TestBatchDao.java // 배치의 사용을 테스트 및 실제 구현 Dao 클래스
        | JobConfiguration.java // Spring Batch를 사용해 정의된 여러 작업 단계(Step)를 이용해 배치 작업(Job)을 수행하는 설정(Configuration)을 정의
        | JobScheduler.java // Spring Batch 작업을 스케줄링하는 JobScheduler 클래스다. 주석 처리된 runJob 메소드를 사용하여 특정 시간에 배치 작업을 실행하도록 스케줄링하는 기능을 포함하고 있다.
        | JobSetting.java // Quartz 스케줄러를 사용하여 HankerJobA 클래스의 작업을 20초 간격으로 실행하도록 스케줄링하는 설정(JobSetting)을 정의.
      > domain
        > game
          > dao
            | GameRoomDao.java // 게임 방과 관련된 데이터베이스 작업(매치 룸 인덱스 조회, 매치 룸 상태 업데이트, 히스토리 인덱스 및 닉네임 조회, 룸 상태 조회, 팀 인덱스 조회, 히스토리 업데이트, 볼링 점수 업데이트)을 수행하는 DAO 클래스
          > dto
            | AdminSendScoreDTO.java // 볼링장에서 중앙서버로 점수를 보내주기 위한 Request를 정의한 클래스
            | ChatMessageDTO.java
            | GameEndReq.java
            | GameEndRes.java
            | HistoryInfo.java
            | NewMatchOpenRes.java
            | PostCheckSocketActiveReq.java
            | PostCheckSocketActiveRes.java
            | PostMatchCodeReq.java
            | PostMatchCodeRes.java
          > service
            | GameRoomService.java
          > websock
            | ChatPreHandler.java
            | GameRoomController.java
            | StompGameController.java
            | StompWebSocketConfig.java
        > history
          > dao
            | HistoryDao.java
          > dto
            | NewHistoryPlayerRes.java
        > match
          > dao
            | MatchDao.java
          > dto
            | ByLocationRes.java
            | ByNetworkRes.java
            | GetMatchPlanDetailRes.java
            | GetMatchPlanDetailResList.java
            | GetMatchPlanRes.java
            | GetMatchPlanResList.java
            | HAmatchRecordsRes.java
            | MatchCandidate.java
            | MatchRecordsRes.java
            | MatchRoomDetailRes.java
            | PossibleMatchesRes.java
            | PostCreateMatchRoomReq.java
            | PostCreateMatchRoomRes.java
            | UserHistoryInfo.java
          > service
            | MatchService.java
          | MatchController.java
        > push
          > dao
            | PushDao.java
          > dto
            | JoinAcceptOrNotReq.java
            | JoinAcceptOrNotRes.java
            | MatchCancelReq.java
            | MatchCancelUser.java
            | MatchJoinPushReq.java
            | MatchJoinPushRes.java
          > service
            | PushService.java
          | PushController.java
        > user
          > dao
            | UserDao.java
          > dto
            | GetPushListRes.java
            | GetPushListResByDateArr.java
            | GetUserProfileImgRes.java
            | PostCheckDuplicateReq.java
            | PostCheckDuplicateRes.java
            | PostLoginReq.java
            | PostLoginRes.java
            | User.java
            | UserNameNickName.java
            | UserProfileInfo.java
            | UserSimpleInfo.java
          > service
            | UserService.java
          | UserController.java
      > test
        | TestController.java // logger를 어떻게 써야하는지 보여주는 테스트 클래스
      | WebSecurityConfig.java // spring-boot-starter-security, jwt 를 사용하기 위한 클래스 
    > utils
      | AES128.java // 암호화 관련 클래스
      | JwtService.java // jwt 관련 클래스
      | ValidateRegex.java // 정규표현식 관련 클래스
    | DemoApplication // SpringBootApplication 서버 시작 지점
  > resources
    | application.yml // Database 연동을 위한 설정 값 세팅 및 Port 정의 파일
    | logback-spring.xml // logger 사용시 console, file 설정 값 정의 파일
build.gradle // gradle 빌드시에 필요한 dependency 설정하는 곳
.gitignore // git 에 포함되지 않아야 하는 폴더, 파일들을 작성 해놓는 곳

```
## ✨Description

### Annotation
스프링 부트는 `어노테이션`을 다양하게 아는 것이 중요하다. SpringBoot의 시작점을 알리는 `@SpringBootApplication` 어노테이션 뿐만 아니라 `스프링 부트 어노테이션` 등의 키워드로 구글링 해서 **스프링 부트에서 자주 사용되는 다양한 어노테이션을 이해하고 외워두자.**

### Lombok
Java 라이브러리로 반복되는 getter, setter, toString 등의 메서드 작성 코드를 줄여주는 라이브러리이다. 기본적으로 각 도메인의 model 폴더 내에 생성하는 클래스에 lombok을 사용하여 코드를 효율적으로 짤 수 있도록 구성했다. 자세한 내용은 구글링과 model > PostUser, User를 통해 이해하자.


### src - main - resources
log는 통신 시에 발생하는 오류들을 기록하는 곳이다. 실제 메인 코드는 src에 담겨있다. src > main > resources를 먼저 살펴보자.

`application.yml`

에서 **포트 번호를 정의**하고 **DataBase 연동**을 위한 값을 설정한다. 현재는 gitignore의 처리로 외부에서는 보기 힘든 상태로 해놨다.

`logback-spring.xml`

logs 폴더에 로그 기록을 어떤 형식으로 남길 것인지 설정한다. logs 폴더에 어떻게 기록이 남겨져 있는지 확인해보자.

### src - main - java

`com.example.demo` 패키지에는 크게 `config` 폴더, `src` 폴더와 이 프로젝트의 시작점인 `DemoApplication.java`가 있다.

`DemoApplication.java` 은 스프링 부트 프로젝트의 시작을 알리는 `@SpringBootApplication` 어노테이션을 사용하고 있다. (구글링 통해 `@SpringBootApplication`의 다른 기능도 살펴보자.)

`src`폴더에는 실제 **API가 동작하는 프로세스**를 담았고 `config` 폴더에는 `src`에서 필요한 Secret key, Base 클래스를, `util` 폴더에는 JWT, 암호화, 정규표현식 등의 클래스를 모아놨다.

`src`를 자세하게 살펴보자. `src`는 각 **도메인**별로 패키지를 구분해 놓는다. **도메인**이란 게시글, 댓글, 회원, 정산, 결제 등 소프트웨어에 대한 요구사항 혹은 문제 영역이라고 생각하면 된다.

이 도메인들은 API 통신에서 어떤 프로세스로 처리되는가? API 통신의 기본은 Request → Response이다. 스프링 부트에서 **어떻게 Request를 받아서, 어떻게 처리하고, 어떻게 Response 하는지**를 중점적으로 살펴보자. 전반적인 API 통신 프로세스는 다음과 같다.

> **Request** → `XXXController.java`(=Router+Controller) → `Service` (CRUD) (=Business Logic) → `Dao` (DB) → **Response**

#### 1. Controller / `UserController.java`  / @RestController

> 1) API 통신의 **Routing** 처리
> 2) Request를 다른 계층에 넘기고 처리된 결과 값을 Response 해주는 로직

**1) `@Autowired`**

UserController의 생성자에 `@Autowired` 어노테이션이 붙어있다. 이는 **의존성 주입**을 위한 것으로, `UserController`  뿐만 아니라 다음에 살펴볼 `UserService`의 생성자에도 각각 붙어 있는 것을 확인할 수 있다. 간단히 요약하면 객체 생성을 자동으로 해주는 역할이다. 자세한 프로세스는 구글링을 통해 살펴보자.

나머지 어노테이션들 역시 구글링을 통해 이해하자.

**2) `BaseResponse`**

Response할 때, 공통 부분은 묶고 다른 부분은 제네릭을 통해 구현함으로써 반복되는 코드를 줄여준다. (`BaseResponse.java` 코드 살펴 볼 것. 여기에 쓰이는`BaseResponseStatus` 는 `enum`을 통해 Status 값을 관리하고 있다.)

**3) 메소드 네이밍룰**

메소드 명명 규칙은 다음과 같다.

> HTTP Method + 핵심 URI

- **GET** `/users` 를 처리하는 메소드명 → getUsers
- **PATCH** `/users` 를 처리하는 메소드명 →patchUsers

항상 이 규칙을 따라야 하는 것은 아니지만, 네이밍은 통일성 있게 해주었으면 한다.

**4) Res, Req 네이밍룰**

각 메소드에서 사용되는 Res, Req 모델의 명명 규칙도 메소드 명과 비슷하다.

> HTTP Method + 핵심 URI +**Res/Req**

**Patch** `/users/:userId` → PatchUserRes / PatchUserReq

이 Res, Req 모델은 `(도메인명) / models` 폴더에 만들면 된다.

#### 2. Service / `UserService.java` / @Service

> 1) **비즈니스 로직**을 다루는 곳 (DB 접근[CRUD], DB에서 받아온 것 형식화)
>  + Request의 **의미적** **Validation** 처리 (DB를 거쳐야 검사할 수 있는)
>  + Request의 **형식적** **Validation** 처리

`Service`와 `Provider`는 비즈니스 로직을 다루는 곳이다. **CRUD** 중 **R(Read)** 에 해당하는 코드가 긴 경우가 많기 때문에 **R(Read)** 만 따로 분리해 `Service`는 **CUD(Create, Update, Delete)** 를, `Provider`는 **R(Read)** 를 다루도록 했다. 유지 보수가 용이해진다.

`Provider`
> **R(Read)** 와 관련된 곳이다. DB에서 select 해서 얻어온 값을 가공해서 뱉어준다.

`Service`
> **CRUD(Create, Read, Update, Delete)** 와 관련된 곳이다.

**1) 메소드명**

메소드의 prefix로 다음 규칙을 따랐으면 한다.

C → createXXX `createInfo`

R → retrieveXXX `retrieveInfoList`

U → updateXXX `updateInfo`

D → deleteXXX `deleteInfo`

**2) BaseException**

`BaseException`을 통해 `Service`에서 `Controller`에 Exception을 던진다. 마찬가지로 Status 값은 `BaseResponseStatus` 의 `enum`을 통해 관리한다.

#### 3. DAO / `UserDao.java`
JdbcTemplate을 사용하여 구성되어 있다. 자세한 내용은 이곳 [공식 문서](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html) 을 참고하자.

## ✨Usage
로컬에서 DemoApplication을 실행시킨다. (로컬 서버 구동 시)

[DB 연결 없이 TEST]
1. src > test > TestController.java에 구성되어 있는 API를 테스트해보자.
2. 포스트맨을 통해 GET localhost:9000/test/log로 테스트가 잘 되는지 확인한다.

[DB 연결 이후 TEST]
1. resources > application.yml에서 본인의 DB 정보를 입력한다.
2. DB에 TEST를 위한 간단한 테이블을 하나 만든다.
3. UserController.java, UserService.java, UserDao.java를 구성하여 해당 테이블의 값들을 불러오는 로직을 만든다.
4. 포스트맨을 통해 본인이 만든 API 테스트가 잘 되는지 확인한다.

### nohup
무중단 서비스를 위해 nohup을 사용한다. nohup과 &의 사용으로 백그라운드에서의 서버 실행이 가능하다.

### Error
서버 Error를 마주했다면, 원인을 파악할 수 있는 다양한 방법들을 통해 문제 원인을 찾자.
- 컴파일 에러 확인
- log 폴더 확인
- 그 외 방법들
