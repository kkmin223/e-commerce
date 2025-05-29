## Kafka 개념 및 Spring Boot 연동 실습 보고서

---

### 1. Kafka 주요 개념 정리

#### 1.1 Producer, Consumer, Broker
- **Producer**: Kafka로 메시지를 발행(전송)하는 주체.
- **Consumer**: Kafka에서 메시지를 소비(수신)하는 주체.
- **Broker**: Kafka 서버 인스턴스. 여러 Broker가 모여 클러스터를 구성하며, 메시지를 저장·전달한다.

#### 1.2 Topic, Partition, Consumer Group
- **Topic**: 메시지의 논리적 분류 단위. Producer는 Topic에 메시지를 발행하고, Consumer는 Topic에서 메시지를 구독한다.
- **Partition**: Topic을 물리적으로 분할한 단위. 각 Partition은 메시지의 순서를 보장하며, 병렬 처리를 가능하게 한다.
- **Consumer Group**: 여러 Consumer가 하나의 그룹을 이루어 Topic의 Partition을 분산 처리한다. 같은 그룹 내에서는 Partition이 중복 소비되지 않는다.

#### 1.3 Offset, Message, Replication, Cluster
- **Offset**: Partition 내 메시지의 고유한 위치(번호). Consumer는 Offset을 기준으로 메시지를 읽는다.
- **Message**: Kafka에서 주고받는 데이터 단위. Key-Value 형태로 저장된다.
- **Replication**: Partition의 복제본을 여러 Broker에 저장하여 장애 시 데이터 유실을 방지한다.
- **Cluster**: 여러 Broker가 모여 이중화와 확장성을 제공하는 구조.

#### 1.4 Key, Partitioning, 순서 보장
- **Key**: 메시지의 파티션 배정을 결정하는 값. 동일 Key의 메시지는 항상 같은 Partition에 저장되어 순서가 보장된다.
- **Partitioning**: Key의 해시값을 이용해 파티션을 지정. Key가 없으면 라운드로빈 등으로 분배된다.

#### 1.5 Rebalancing
- **Rebalancing**: Consumer Group 내 Consumer 수 변화 등으로 Partition 할당이 변경될 때 발생. 일시적으로 메시지 처리가 중단될 수 있다.

---

### 2. Kafka의 장단점

#### 2.1 장점
- **높은 처리량과 확장성**: 분산 구조와 파티션 덕분에 대용량 실시간 데이터 처리에 적합.
- **내결함성**: Replication, 장애 시 자동 복구.
- **유연한 데이터 보관**: 메시지 보존 기간 설정 가능.
- **다양한 데이터 소비**: 여러 Consumer Group이 동일 Topic 데이터를 독립적으로 소비 가능.

#### 2.2 단점
- **운영 복잡성**: 클러스터/파티션/복제본 관리 필요.
- **학습 곡선**: 다양한 개념과 설정 이해 필요.
- **리소스 소모**: 대용량 처리 시 디스크, 네트워크, CPU 사용량 증가.

---

### 3. Kafka 메시지 중복/유실 방지 방법

- **중복 방지**: Idempotent Producer, Exactly-Once Semantics, Consumer의 Offset 관리.
- **유실 방지**: acks=all, min.insync.replicas 설정, Producer/Consumer 재시도 정책, 장애 시 복제본 활용.

---

### 4. Spring Boot에서 Kafka 연동 및 이벤트 발행

#### 4.1 의존성 추가
```gradle
implementation 'org.springframework.kafka:spring-kafka'
```

#### 4.2 프로듀서 설정 (application.yml)
```yaml
spring:
  kafka:
    # 카프카 클러스터 초기 연결 주소 (Docker의 EXTERNAL 리스너 포트 9094 사용)
    bootstrap-servers: localhost:9094

    # 공통 프로퍼티 (Producer + Consumer)
    properties:
      # 브로커 요청 타임아웃 (20초)
      request.timeout.ms: 20000
      # 재시도 간 대기 시간 (0.5초)
      retry.backoff.ms: 500

      # 토픽 자동 생성 비활성화
      auto.create.topics.enable: false
      # 스키마 자동 등록 비활성화
      auto.register.schemas: false
      # 스키마 최신 버전 사용 
      use.latest.version: true
      # 인증 정보 소스
      basic.auth.credentials.source: USER_INFO

    # 프로듀서 설정
    producer:
      # 키 직렬화 방식 (String)
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      # 값 직렬화 방식 (JSON)
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      # 메시지 전송 실패 시 재시도 횟수
      retries: 5

    # 컨슈머 설정
    consumer:
      # 키 역직렬화 방식 (String)
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      # 값 역직렬화 방식 (ByteArray)
      value-deserializer: org.apache.kafka.common.serialization.ByteArrayDeserializer
      properties:
        # 오프셋 자동 커밋 비활성화 (수동 커밋 사용)
        enable-auto-commit: false
        # 오프셋 없을 때 시작 지점 (latest: 가장 최신 메시지)
        auto.offset.reset: latest 

    # 리스너 설정
    listener:
      # 수동 오프셋 커밋 모드
      ack-mode: manual
```

#### 4.3 Key의 역할
- 메시지의 파티션 배정 및 순서 보장에 사용.
- 예시:
  ```java
  kafkaTemplate.send("ORDER", "DATA_PLATFORM", orderEvent);
  ```
  여기서 `"DATA_PLATFORM"`은 메시지 Key로, 해당 Key의 메시지는 항상 같은 파티션에 저장됨.

---

### 5. Kafka 운영 및 테스트 환경

#### 5.1 Docker로 Kafka 실행
- Bitnami Kafka 이미지를 활용해 Docker Compose로 손쉽게 브로커를 띄울 수 있음.
- 주요 환경 변수: `KAFKA_CFG_LISTENERS`, `KAFKA_CFG_ADVERTISED_LISTENERS` 등.

#### 5.2 터미널에서 데이터 확인
- 토픽의 파티션/메시지 조회:
  ```bash
  kafka-topics.sh --bootstrap-server localhost:9094 --topic <토픽명> --describe
  kafka-console-consumer.sh --bootstrap-server localhost:9094 --topic <토픽명> --partition <번호> --offset <시작오프셋> --max-messages <개수>
  ```

#### 5.3 Testcontainers로 Kafka 테스트 환경 구축
- Java 코드에서 간단히 Kafka 컨테이너를 띄워 통합 테스트 가능.
```java

@Configuration
class TestcontainersConfiguration {
    
    public static final KafkaContainer KAFKA_CONTAINER;
    
    static {
        KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));
        KAFKA_CONTAINER.start();
    }

    @PreDestroy
    public void preDestroy() {
        
        if (KAFKA_CONTAINER.isRunning()) {
            KAFKA_CONTAINER.stop();
        }
    }

```
---

### 6. 파티션 수, 컨슈머 수, 복제본 설정

- **파티션 수**
    - 파티션은 Kafka의 병렬성과 처리량을 결정하는 핵심 요소입니다.
    - 파티션 수가 많을수록 더 많은 컨슈머가 병렬로 데이터를 처리할 수 있지만, 파티션이 지나치게 많으면 브로커의 관리 오버헤드가 커지고 리밸런싱 시간도 길어집니다.
    - 적정 파티션 수는 예상 최대 처리량(TPS), 컨슈머 수, 하드웨어 스펙 등을 고려해 산정합니다.
    - 일반적으로 컨슈머 수 ≤ 파티션 수가 되도록 설계하고, 파티션 수는 향후 확장 가능성을 염두에 두고 결정합니다.

- **컨슈머 수**
    - 컨슈머 그룹 내 컨슈머 수가 파티션 수보다 많으면, 초과된 컨슈머는 할당받는 파티션이 없어 유휴 상태가 됩니다.
    - 컨슈머 수가 파티션 수보다 적으면, 일부 컨슈머가 여러 파티션을 담당하게 되어 개별 컨슈머의 부하가 커질 수 있습니다.
    - 컨슈머 스케일 아웃/인 시 리밸런싱이 발생하므로, 컨슈머 수 변동이 잦은 서비스는 리밸런싱 전략(예: Cooperative Sticky 등)도 고려해야 합니다.

- **복제본(Replication) 설정**
    - 복제본(replication factor)은 데이터 내구성과 장애 허용성을 결정합니다.
    - 최소 2~3개로 설정하는 것이 일반적이며, 복제본이 많을수록 장애 발생 시 데이터 유실 가능성이 줄어듭니다.
    - 복제본 수가 브로커 수를 초과할 수 없으므로, 브로커 수와 복제본 수를 함께 고려해야 합니다.

---

### 7. 장애 메시지 처리 전략 요약

Kafka 메시지 소비(리스닝) 과정에서 장애(예외)가 발생할 경우, 데이터 유실 방지와 시스템 안정성을 위해 다양한 장애 메시지 처리 전략을 함께 적용할 수 있습니다.

#### 1. Dead Letter Queue(DLQ)
- **설명**: 지정 횟수만큼 재시도 후에도 처리에 실패한 메시지를 별도의 토픽(DLQ)에 저장.
- **장점**: 장애 메시지 유실 방지, 원인 분석 및 재처리 가능.

#### 2. 재시도(Retry)
- **설명**: 메시지 처리 실패 시 즉시 또는 일정 시간 지연 후 재시도.
- **활용**: 일시적 네트워크 오류, 외부 시스템 장애 등 복구 가능성이 있는 경우에 효과적.
- **구현**: Spring Kafka의 ErrorHandler, RetryTemplate, 또는 별도 재시도 토픽 활용.

#### 3. 포이즌 필(Poison Pill) 건너뛰기
- **설명**: 역직렬화 불가, 스키마 불일치 등으로 반복 실패하는 메시지는 건너뛰거나 오프셋을 수동 조정하여 시스템 정지 방지.
- **주의**: 데이터 유실 가능성이 있으므로, 로그 기록 및 알림 필수.

#### 4. 커스텀 에러 핸들러
- **설명**: 예외 유형별로 재시도, DLQ 전송, 무시 등 다양한 분기 처리.
- **활용**: 비즈니스 요구에 맞는 세밀한 장애 대응 정책 구현 가능.

#### 5. 트랜잭션/Exactly-Once
- **설명**: 메시지 처리와 결과 저장을 하나의 트랜잭션으로 묶어, 장애 시 데이터 정합성 보장.


