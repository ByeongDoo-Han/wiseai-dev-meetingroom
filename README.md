# 회의실 예약 시스템 API 서버

## 1. 프로젝트 개요

사내 회의실 예약을 위한 RESTful API 서버입니다.
사용자는 회의실을 조회하고, 원하는 시간을 예약 및 결제하며, 예약을 취소할 수 있습니다. 다양한 결제 수단을 지원하기 위해 결제 시스템을 추상화하여 설계했으며, 동시성 문제를 해결하여 데이터 정합성을 보장합니다.

## 2. 핵심 기능
### 2.2 필수 API 엔드포인트
-   **회의실 목록 조회**:  `GET /meeting-rooms`
-   **예약 생성/조회/취소 등 CRUD**
- **결제 처리**: `POST /reservations/{id}/payment`
- **결제 상태 조회**: `GET /payments/{paymentId}/status`
- **결제사별 웹훅 수신**: `POST /webhooks/payments/{provider}`
- **모든 API는 Swagger UI로 테스트 가능해야 함**
### 2.3 필요 API 엔드포인트

| 구분  | 내용        | Method   | API                            | Authorization |
|:----|:----------|:---------|:-------------------------------|:-----------|
 | 회의실 | 회의실 모두 조회 | `GET`    | `/meeting-rooms`               |            |
|     | 회의실 수정    | `PUT`    | `/meeting-rooms/{id}`          | admin      |
|     | 회의실 생성    | `POST`   | `/meeting-rooms`               | admin      |
|     | 회의실 삭제    | `DELETE` | `/meeting-rooms/{id}`          | admin      |
| 예약  | 예약 생성     | `POST`   | `/reservation`                 | user       |
|     | 예약 수정     | `PUT`    | `/reservation/{id}`            | user       |
|     | 예약 모두 조회  | `GET`    | `/reservation`                 |            |
|     | 예약 취소     | `DELETE` | `/reservation/{id}`            | user       |
|     | 결제 처리     | `POST`   | `/reservation/{id}/payment`    | user       |
| 웹   | 결제사 별 웹훅  | `POST`   | `/webhooks/payments/{provider}` |           |




## 3. 기술 스택

| 구분            | 기술                             | 버전    |
| :-------------- |:-------------------------------|:------|
| **언어**        | Java                           | 17    |
| **프레임워크**  | Spring Boot                    | 3.4.8 |
| **데이터베이스**| PostgreSQL                     | 8.0+  |
| **ORM**         | Spring Data JPA                | -     |
| **API 문서화**  | Springdoc OpenAPI (Swagger UI) | 2.5.0 |
| **컨테이너**    | Docker, Docker Compose         | -     |
| **테스트**      | JUnit 5, Mockito               | -     |
| **빌드 도구**   | Gradle                         | -     |
 | **배포** | AWS| -     |
## 4. 실행 방법

### 4.1. 사전 요구사항

-   Docker 및 Docker Compose가 설치되어 있어야 합니다.
- 설치되지 않았다면 4.1.1 을 참고해 Docker를 설치할 수 있습니다.
### 4.1.1 Docker 설치하기
💻 Windows / macOS / Linux 사용자 공통

1. Docker Desktop 설치
    1.	https://www.docker.com/products/docker-desktop/ 접속
    2.	OS에 맞는 Docker Desktop 설치
    3.	설치 후 실행 (최초 설치 시 로그인 필요 – 무료 계정 생성 가능)

윈도우 사용자는 반드시 WSL2가 설치되어 있어야 합니다.

	•   설치 가이드: https://learn.microsoft.com/ko-kr/windows/wsl/install

### 4.1.2
macOS 터미널에서 설치하기 
```bash
brew install docker
brew install docker-compose
```

### 4.1.3 설치 확인
터미널 (cmd, powershell, 터미널 등)에서 아래 명령 실행
```
docker version
docker compose version
```
출력 예시
```
Docker version 28.0.4, build cb74dfc
Docker Compose version v2.34.0
```
### 4.2. 애플리케이션 실행
프로젝트 클론
```
git clone https://github.com/ByeongDoo-Han/wiseai-dev-meetingroom
```

디렉토리 이동 && 실행 권한 부여 && 프로젝트 빌드
```
cd wiseai-dev-meetingroom
chmod +x ./gradlew
./gradlew clean build
```

프로젝트 루트 디렉토리에서 아래 명령어를 실행하여 애플리케이션 서버와 데이터베이스를 한 번에 실행합니다.

```bash
docker-compose up --build
```

-   `--build` 옵션은 최초 실행 시 또는 코드 변경 사항이 있을 때 이미지를 새로 빌드하기 위해 사용합니다.

### 4.3. API 문서 확인

애플리케이션이 정상적으로 실행된 후, 웹 브라우저에서 아래 주소로 접속하여 Swagger UI를 통해 API 문서를 확인하고 직접 테스트할 수 있습니다.

-   **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/docs)

## 5. 테스트 실행 방법

프로젝트의 단위 테스트 및 통합 테스트를 실행하려면 아래 명령어를 사용합니다.

```bash
./gradlew test
```

## 6. 아키텍처 및 설계

### 6.1. 데이터베이스 모델 (ERD)

![ERD](src/main/resources/erd_meeting_room.png)

-   **MeetingRoom**: 회의실 정보 (id, name, capacity, price_per_hour)
-   **User**: 사용자 정보 (id, username, password)
-   **Reservation**: 예약 정보 (id, start_time, end_time, payment_status, total_amount, user_id, meeting_room_id)
-   **Payment**: 결제 정보 (id, provider_type, amount, status, external_payment_id, reservation_id)
-   **PaymentProvider**: 결제사 정보 (id, name, api_endpoint, auth_info)

## 7. 주요 설계 결정사항

### 7.1.  결제 시스템 추상화 

이 프로젝트의 결제 시스템은 다양한 결제 수단(신용카드, 간편결제, 가상계좌)을 유연하게
통합하고 관리하기 위해 추상화 계층을 도입했습니다.


* `PaymentGateway` 인터페이스:
    * 모든 결제 게이트웨이가 구현해야 하는 표준 계약을 정의합니다. supports() 메소드를
      통해 특정 결제 수단(PaymentProviderType)을 지원하는지 확인하고, pay() 메소드를 통해
      실제 결제 로직을 수행합니다.
    * 이를 통해 새로운 결제 수단이 추가되거나 기존 결제 수단의 구현이 변경되어도
      PaymentsService와 같은 상위 계층의 코드를 최소한으로 변경할 수 있습니다.
* 구현체 분리:
    * CardPaymentGateway, SimplePaymentGateway, VirtualAccountPaymentGateway와 같이 각
      결제 수단별로 PaymentGateway 인터페이스를 구현하는 클래스를 분리했습니다.
    * 각 구현체는 해당 결제 수단에 특화된 API 호출 로직(예: JSON/XML 요청, 특정 헤더, 인증
      방식)을 캡슐화합니다.
* `PaymentsService`의 역할:
    * PaymentsService는 클라이언트로부터 받은 PaymentRequest를 기반으로 적절한
      PaymentGateway 구현체를 찾아 결제를 위임합니다.
    * 이 서비스는 결제 게이트웨이의 세부 구현에 의존하지 않고, PaymentGateway 인터페이스를
      통해 추상화된 방식으로 결제를 처리합니다.
    * 결제 요청 금액과 예약 금액의 일치 여부, 예약 소유권 확인 등 결제 전 공통 비즈니스
      로직을 담당합니다.
* DTO를 통한 데이터 표준화:
    * PaymentRequest, PaymentResult와 같은 공통 DTO를 사용하여 다양한 결제 수단으로부터의
      요청 및 응답 데이터를 표준화합니다.
    * 결제 수단별 상세 정보는 PaymentRequest 내의 details 필드를 통해 유연하게 전달됩니다.


### 7.2 동시성 제어
예약 시스템에서 동일한 회의실의 특정 시간대에 대한 동시 예약 시도를 방지하고 데이터
일관성을 유지하기 위해 분산 락(Distributed Lock)을 활용한 동시성 제어 메커니즘을
구현했습니다.


* `@DistributedLock` 어노테이션:
    * ReservationService의 createReservation 메소드와 같이 동시성 제어가 필요한 비즈니스
      로직에 적용됩니다.
    * 락의 키(key), 대기 시간(waitTime), 임대 시간(leaseTime) 등을 설정할 수 있습니다.
* Redisson 기반 분산 락:
    * Redis를 기반으로 하는 Redisson 라이브러리를 사용하여 분산 환경에서도 동작하는 락을
      구현했습니다. 이는 여러 애플리케이션 인스턴스 간에도 락을 공유하고 관리할 수 있게
      합니다.
* AOP (`DistributedLockAop`):
    * @DistributedLock 어노테이션이 붙은 메소드가 호출될 때 AOP(Aspect-Oriented
      Programming)를 통해 락 획득 및 해제 로직이 자동으로 실행됩니다.
    * 락 획득에 성공하면 실제 비즈니스 로직(aopForTransaction.proceed(joinPoint))을
      실행하고, 실패하면 예외를 발생시키거나 특정 값을 반환합니다.
* 트랜잭션 동기화:
    * 가장 중요한 설계 결정 중 하나로, 분산 락이 데이터베이스 트랜잭션 커밋 이후에
      해제되도록 TransactionSynchronizationManager를 사용했습니다.
    * 이는 락이 해제되기 전에 데이터베이스 변경 사항이 완전히 영구화되어, 다음 락을 획득한
      스레드가 최신 데이터를 볼 수 있도록 보장하여 "조회 후 삽입(select-then-insert)"과
      같은 경합 조건을 효과적으로 방지합니다.
* 비관적 락 (`@Lock(LockModeType.PESSIMISTIC_WRITE)`):
    * ReservationRepository의 existDuplicatedReservation 쿼리에 비관적 락을 적용하여, 중복
      예약 여부를 조회하는 동안 해당 데이터에 대한 다른 트랜잭션의 접근을 막습니다.
    * 이는 분산 락과 함께 작동하여, 락을 획득한 스레드가 중복 검사를 수행하는 동안
      데이터베이스 수준에서 추가적인 보호를 제공합니다.


### 7.3 Mock 결제 서버
모의 결제 서버 (Mock Payment Servers)


이 프로젝트는 실제 결제 게이트웨이(Payment Gateway, PG)와의 연동 없이 결제 시스템의 개발
및 테스트를 용이하게 하기 위해 세 가지 모의(Mock) 결제 서버를 사용합니다. 이 모의
서버들은 WireMock (http://wiremock.org/)을 기반으로 구현되었으며, Docker 컨테이너로
격리되어 실행됩니다.


목적:
* 개발 및 테스트 용이성: 실제 PG 연동에 필요한 복잡한 인증 절차나 네트워크 지연 없이 결제
  로직을 빠르게 개발하고 테스트할 수 있습니다.
* 독립적인 개발: 백엔드 개발팀이 실제 PG의 API 개발 일정에 구애받지 않고 독립적으로 결제
  기능을 개발할 수 있습니다.
* 다양한 시나리오 테스트: 성공, 실패, 취소 등 다양한 결제 시나리오를 쉽게 시뮬레이션하여
  애플리케이션의 견고성을 테스트할 수 있습니다.

구성:
docker-compose.yml 파일에 정의되어 있으며, 각각 독립적인 Dockerfile과 WireMock 매핑 파일을
가집니다.


1. `mock-pg-a-card` (신용카드 결제 모의 서버)
    * 역할: 신용카드 결제를 모의합니다.
    * 엔드포인트: POST /v1/payments/card
    * 요청: JSON 형식의 카드 정보(카드 번호, 금액 등)를 받습니다. X-API-KEY 헤더를
      요구합니다.
    * 응답: 결제 성공(00) 또는 실패 코드를 포함하는 JSON 응답을 반환합니다.
    * 포트: 호스트의 8089번 포트에 매핑됩니다.


2. `mock-pg-b-simple` (간편결제 모의 서버)
    * 역할: 간편결제를 모의합니다.
    * 엔드포인트: POST /v1/simplepay
    * 요청: application/x-www-form-urlencoded 형식의 폼 데이터(사용자 ID, 금액 등)를
      받습니다. Authorization: Basic 헤더를 요구합니다.
    * 응답: SUCCESS|트랜잭션ID 또는 FAILURE|오류메시지와 같은 텍스트 응답을 반환합니다.
    * 포트: 호스트의 8090번 포트에 매핑됩니다.


3. `mock-pg-c-virtual` (가상계좌 결제 모의 서버)
    * 역할: 가상계좌 발급을 모의합니다.
    * 엔드포인트: POST /v1/virtualaccount
    * 요청: XML 형식의 결제 정보(상품명, 금액 등)를 받습니다. X-CLIENT-ID, X-CLIENT-SECRET
      헤더를 요구합니다.
    * 응답: 가상계좌 발급 상태(WAITING), 계좌 번호, 은행명 등을 포함하는 XML 응답을
      반환합니다.
    * 포트: 호스트의 8091번 포트에 매핑됩니다.


작동 방식:
각 모의 서버는 WireMock의 매핑 파일(mappings/*.json)에 정의된 규칙에 따라 특정 요청을
수신하면 미리 정의된 응답을 반환합니다. 이를 통해 백엔드 애플리케이션은 실제 PG와
통신하는 것처럼 결제 로직을 테스트할 수 있습니다.
## 8. 아키텍처 
![ARCHITECTURE](src/main/resources/architecture_meeting_room.png)
