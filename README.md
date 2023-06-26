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
  > src.main.java.com.capston.bowler
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
          | HankerJobA.java // Quartz 스케줄러를 사용하여 특정 작업을 수행하는 클래스, 여기서는 특정 시간이 되면 해당 작업을 일시중지 한다
          | TestBatch.java // 배치 테스트를 위한 Dto라고 보면 된다
        > repository
          | TestBatchDao.java // 배치의 사용을 테스트 및 실제 구현 Dao 클래스
        | JobConfiguration.java // Spring Batch를 사용해 정의된 여러 작업 단계(Step)를 이용해 배치 작업(Job)을 수행하는 설정(Configuration)을 정의
        | JobScheduler.java // Spring Batch 작업을 스케줄링하는 JobScheduler 클래스다. 주석 처리된 runJob 메소드를 사용하여 특정 시간에 배치 작업을 실행하도록 스케줄링하는 기능을 포함하고 있다
        | JobSetting.java // Quartz 스케줄러를 사용하여 HankerJobA 클래스의 작업을 20초 간격으로 실행하도록 스케줄링하는 설정(JobSetting)을 정의
      > domain
        > game
          > dao
            | GameRoomDao.java // 게임 방과 관련된 데이터베이스 작업(매치 룸 인덱스 조회, 매치 룸 상태 업데이트, 히스토리 인덱스 및 닉네임 조회, 룸 상태 조회, 팀 인덱스 조회, 히스토리 업데이트, 볼링 점수 업데이트)을 수행하는 DAO 클래스
          > dto
            | AdminSendScoreDTO.java // 볼링장에서 중앙서버로 점수를 보내주기 위한 Request를 정의한 클래스
            | GameEndReq.java // 게임이 종료된 후 클라이언트 -> 서버로의 점수전송을 위한 Request 형식을 정의한 클래스
            | GameEndRes.java // 게임 결과가 잘 저장되었는지 Response 해주는 클래스
            | HistoryInfo.java //  게임 방에 있는 유저의 히스토리 인덱스와 닉네임 정보를 표현하는 데이터 전송 객체 클래스
            | PostCheckSocketActiveReq.java // 사용자가 게임 방의 상태를 확인하려고 요청할 때 사용되는 요청 본문을 정의하는 데이터 전송 객체 클래스
            | PostCheckSocketActiveRes.java // 게임 방의 상태와 해당 게임 방에 있는 사용자들의 히스토리 정보를 응답으로 반환하는 데이터 전송 객체 클래스
            | PostMatchCodeReq.java // 게임 방의 인덱스를 얻어오는 요청을 위한 데이터 전송 객체 클래스
            | PostMatchCodeRes.java // 게임 방의 인덱스와 해당 게임 방에 있는 사용자들의 히스토리 정보를 응답으로 반환하는 데이터 전송 객체 클래스
            | ScoreSendMessageDTO.java // 클라이언트가 게임에 참여하거나 메시지를 전송할 때 사용하는 데이터 전송 객체로, 메시지가 어느 매치에 속하며 누가 보냈는지, 그리고 어떤 메시지를 보냈는지를 나타냄
          > service
            | GameRoomService.java // 게임 방과 관련된 비즈니스 로직을 처리하는 클래스
          > websock
            | ChatPreHandler.java // ChannelInterceptorAdapter를 상속받아 웹소켓 메시지가 전송되기 전에 해당 메시지를 처리한다. 클라이언트에서 받은 메시지를 StompHeaderAccessor를 이용해 STOMP 형태의 메시지로 가공하며, StompCommand에 따라 다양한 작업을 수행한다. 예를 들어 클라이언트가 접속하거나 연결을 끊었을 때, 또는 구독을 시작하거나 취소했을 때를 구분해 각각의 처리를 하는 클래스
            | GameRoomController.java // 게임 방에 관련된 HTTP 요청을 처리하는 클래스
            | StompGameController.java // STOMP 메시지를 처리하는 컨트롤러로, 클라이언트가 게임에 참여, 메시지 전송, 게임 시작 등의 액션을 요청하면 이에 따른 메시지를 구독 중인 클라이언트들에게 전달함
            | StompWebSocketConfig.java // 웹소켓의 설정을 담당한다. STOMP 프로토콜을 활용한 웹소켓을 설정하며, 클라이언트가 메시지를 보낼 수 있는 경로, 서버가 클라이언트에게 메시지를 보낼 수 있는 경로를 설정한다. 또한 ChatPreHandler를 인터셉터로 등록함으로써, 클라이언트에서 오는 메시지를 ChatPreHandler에서 먼저 처리하도록 하는 클래스
        > history
          > dao
            | HistoryDao.java // 히스토리 및 플레이어 정보와 관련된 데이터베이스 작업을 수행하는 클래스
          > dto
            | NewHistoryPlayerRes.java // 게임 히스토리에 참여하는 플레이어의 정보를 포함하는 데이터 전송 객체 클래스
        > match
          > dao
            | MatchDao.java // 데이터베이스에서 경기에 관련된 데이터를 조회하거나 업데이트하는 데 사용되는 여러 메서드들로 구성되어 있다. 이 메서드들은 경기 방의 생성, 유저의 참가 히스토리, 경기 계획, 참가자 정보 등을 처리하는 클래스
          > dto
            | ByNetworkRes.java // '경기 날짜', '점수대', '경기 장소', '인원 수', '매칭방Idx' 등의 정보를 포함하는 데이터 전송 객체 클래스로, 온라인 매칭방 리스트를 제공하는 데 사용되는 클래스
            | GetMatchPlanDetailRes.java //  '팀 인덱스', '유저 인덱스', '닉네임', '프로필 이미지 URL', '최고 점수', '평균 점수', '게임 횟수', '이긴 횟수', '진 횟수', '홈/어웨이' 등의 정보를 포함하는 데이터 전송 객체 클래스
            | GetMatchPlanDetailResList.java // '매치 인덱스', '게임 시간', '매치 코드'와 같은 정보와 함께 GetMatchPlanDetailRes 객체의 목록을 포함하는 데이터 전송 객체 클래스
            | GetMatchPlanRes.java // '게임 시간', '네트워크 유형', '닉네임', '프로필 이미지 URL', '카운트', '히스토리 인덱스', '사용자 인덱스', '매치 인덱스', '팀 인덱스', '홈 또는 어웨이', '장소' 등의 정보를 포함하는 데이터 전송 객체 클래스
            | GetMatchPlanResList.java // '매치 인덱스', '게임 시간', '네트워크 유형' 정보와 'GetMatchPlanRes' 객체 리스트를 포함하는 데이터 전송 객체 클래스
            | HAmatchRecordsRes.java //  '매치 인덱스'와 'MatchRecordsRes' 객체 리스트를 포함하는 데이터 전송 객체 클래스다. 이 객체는 홈팀과 어웨이팀의 매치 기록을 나타냄
            | MatchCandidate.java // 매치 참가자의 정보를 담고 있는 데이터 전송 객체 클래스. 참가자의 '유저 인덱스', '팀 인덱스', '닉네임', '프로필 이미지 URL'을 포함
            | MatchRecordsRes.java // 매치 기록에 대한 정보를 담고 있는 데이터 전송 객체 클래스. 경기 날짜, 닉네임, 온라인 또는 오프라인 매칭 유형, 게임 인원 수, 플레이어 인덱스, 매칭 방 인덱스, 팀 번호, 홈 또는 어웨이 식별, 승패 여부 (settle_type), 최종 점수를 포함
            | MatchRoomDetailRes.java // 매치 방 세부 정보를 담고 있는 데이터 전송 객체 클래스. 경기 날짜, 게시자 닉네임, 제목, 내용, 인원 수, 목표 점수, 비용, 위치, 장소, 매치 방 소유자 사용자 인덱스를 포함
            | PossibleMatchesRes.java // 참여 가능한 매치의 총 개수를 담고 있는 데이터 전송 객체 클래스. count 필드를 통해 매치의 총 개수를 나타냄
            | PostCreateMatchRoomReq.java // 매치방 생성 요청에 필요한 정보를 담고 있는 데이터 전송 객체 클래스
            | PostCreateMatchRoomRes.java // 매치방 생성 결과를 담고 있는 응답 데이터 전송 객체 클래스
            | UserHistoryInfo.java // 사용자의 매치 기록 정보를 담고 있는 데이터 전송 객체 클래스
          > service
            | MatchService.java // 매치를 생성하고 관리하는 서비스 레이어의 로직을 담고 있으며, 매치 생성, 매치 히스토리 조회, 매치 카운트, 매치 방 정보, 참가자 정보 등을 처리하는 클래스
          | MatchController.java // 새로운 매치방 생성, 매치방 상세보기, 참가 가능한 매치 수 조회, 지역별 매치 조회, 특정 네트워크(온/오프라인)에 따른 매치방 조회, 예정 매치 조회 등의 기능을 제공하는 클래스
        > push
          > dao
            | PushDao.java // 푸시 관련 데이터 액세스 기능을 제공하는 클래스
          > dto
            | JoinAcceptOrNotReq.java // 매칭방 참가 신청에 대한 수락 또는 거절을 요청하는 데이터 전송 객체 클래스
            | JoinAcceptOrNotRes.java // 매칭방 참가 신청에 대한 수락 또는 거절 결과를 응답하는 데이터 전송 객체 클래스
            | MatchCancelReq.java // 매칭 취소 요청을 전달하는 데이터 전송 객체 클래스, 매칭을 취소하는 유저들의 정보를 담은 리스트를 포함
            | MatchCancelUser.java // 매칭 취소하는 유저의 정보를 담은 데이터 전송 객체 클래스
            | MatchJoinPushReq.java // 매칭 참가 신청 요청의 정보를 담은 데이터 전송 객체 클래스
            | MatchJoinPushRes.java // 매칭 참가 신청 요청의 결과를 담은 데이터 전송 객체 클래스
          > service
            | PushService.java // 푸시 관련 기능을 처리하는 서비스이다. joinPush() 메서드는 매칭방 참가 신청을 처리하고, ownerAccepted() 메서드는 매칭방 참가 신청 수락을 처리한다. matchCancel() 메서드는 매칭방 참가 신청 거절을 처리하며, sendFcmPush() 메서드는 푸시 알림을 전송
          | PushController.java // 푸시 관련 API 엔드포인트를 처리하는 컨트롤러이다. MatchJoinPushReq를 받아 매칭방 참여 신청을 처리하고, JoinAcceptOrNotReq를 받아 매칭방 참여 신청 수락을 처리하며, MatchCancelReq를 받아 매칭방 참여 신청 거절을 처리하는 클래스
        > user
          > dao
            | UserDao.java // 사용자 관련 데이터베이스 처리를 담당하는 DAO 클래스. 회원가입, 로그인, 중복 체크, 사용자 정보 조회 등의 기능을 제공
          > dto
            | GetPushListRes.java // 클래스는 푸시 알림 기록을 검색할 때 응답 데이터를 표현하는 데이터 전송 객체 클래스
            | GetPushListResByDateArr.java // 날짜별 푸시 알림 목록을 검색할 때 날짜와 해당 날짜의 푸시 알림 세부 정보를 표현하는 데이터 전송 객체 클래스
            | GetUserProfileImgRes.java // 사용자의 프로필 이미지 정보를 나타내는 데이터 전송 객체 클래스
            | PostCheckDuplicateReq.java // 중복 아이디 체크 요청을 나타내는 데이터 전송 객체 클래스
            | PostCheckDuplicateRes.java // 중복 아이디 체크 응답을 나타내는 데이터 전송 객체 클래스
            | PostLoginReq.java // 로그인 요청을 나타내는 데이터 전송 객체 클래스
            | PostLoginRes.java // 로그인 응답을 나타내는 데이터 전송 객체 클래스
            | PostSignUpReq.java // 회원가입 요청을 나타내는 데이터 전송 객체 클래스
            | PostSignUpRes.java // 회원가입 응답을 나타내는 데이터 전송 객체 클래스
            | User.java // 사용자 정보를 나타내는 데이터 전송 객체 클래스
            | UserNameNickName.java // 사용자의 이름과 닉네임 정보를 나타내는 데이터 전송 객체 클래스
            | UserProfileInfo.java // 사용자의 프로필 정보를 나타내는 데이터 전송 객체 클래스
            | UserSimpleInfo.java // 사용자의 간단한 정보를 나타내는 데이터 전송 객체 클래스
          > service
            | UserService.java // 사용자와 관련된 비즈니스 로직을 처리하는 서비스 클래스이다. 회원가입, 로그인, 중복 체크, 사용자 정보 조회 등의 기능을 제공
          | UserController.java // UserService와 MatchService를 사용하여 비즈니스 로직을 처리한다. 또한 JwtService를 사용하여 인증된 사용자를 확인하고, 사용자와 관련된 API 엔드포인트를 제공하는 컨트롤러이다. 회원가입, 로그인, 중복 체크, 사용자 정보 조회 등의 기능을 제공
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

### src - main - resources
log는 통신 시에 발생하는 오류들을 기록하는 곳이다. 실제 메인 코드는 src에 담겨있다. src > main > resources를 먼저 살펴보자.

`application.yml`

에서 **포트 번호를 정의**하고 **DataBase 연동**을 위한 값을 설정한다. 현재는 gitignore의 처리로 외부에서는 보기 힘든 상태로 해놨다.

`logback-spring.xml`

logs 폴더에 로그 기록을 어떤 형식으로 남길 것인지 설정한다.

### src - main - java

`com.capston.bowler` 패키지에는 크게 `config` 폴더, `src` 폴더와 이 프로젝트의 시작점인 `DemoApplication.java`가 있다.

`DemoApplication.java` 은 스프링 부트 프로젝트의 시작을 알리는 `@SpringBootApplication` 어노테이션을 사용하고 있다.

`src`폴더에는 실제 **API가 동작하는 프로세스**를 담았고 `config` 폴더에는 `src`에서 필요한 Secret key, Base 클래스를, `util` 폴더에는 JWT, 암호화, 정규표현식 등의 클래스를 모아놨다.

`src`는 각 **도메인**별로 패키지를 구분해 놓는다. **도메인**이란 게시글, 댓글, 회원, 정산, 결제 등 소프트웨어에 대한 요구사항 혹은 문제 영역이라고 생각하면 된다.

이 도메인들은 API 통신에서 어떤 프로세스로 처리되는가? API 통신의 기본은 Request → Response이다. 스프링 부트에서 **어떻게 Request를 받아서, 어떻게 처리하고, 어떻게 Response 하는지**를 중점적으로 살펴보면, 전반적인 API 통신 프로세스는 다음과 같다.

> **Request** → `XXXController.java`(=Router+Controller) → `Service` (CRUD) (=Business Logic) → `Dao` (DB) → **Response**

#### 1. Controller / `UserController.java`  / @RestController

> 1) API 통신의 **Routing** 처리
> 2) Request를 다른 계층에 넘기고 처리된 결과 값을 Response 해주는 로직

**1) `@Autowired`**

UserController의 생성자에 `@Autowired` 어노테이션이 붙어있다. 이는 **의존성 주입**을 위한 것으로, `UserController`  뿐만 아니라 다음에 살펴볼 `UserService`의 생성자에도 각각 붙어 있는 것을 확인할 수 있다. 간단히 요약하면 객체 생성을 자동으로 해주는 역할이다.

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
