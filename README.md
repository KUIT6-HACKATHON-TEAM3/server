# Garosugil Server

Spring Boot 기반 가로수길 산책 경로 추천 서버

## 기술 스택

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Security + JWT**
- **PostgreSQL 15**
- **Hibernate Spatial**
- **Docker & Docker Compose**
- **Swagger UI (OpenAPI 3.0)**

## 주요 기능

- 🔐 JWT 기반 인증/인가 (Access Token: HttpOnly Cookie, Refresh Token: Response Body)
- 🗺️ 가로수길 경로 추천 (최단 경로 / 에코 경로)
- 🎵 날씨/시간대별 음악 추천
- ⭐ 도로 좋아요 및 태그 시스템
- 📍 관심 장소 저장
- 📝 Swagger UI를 통한 API 문서화
- ✨ git actions을 이용한 CI/CD

## 프로젝트 소개

가로수길 서버는 서울시 가로수길 데이터를 활용하여 사용자에게 최적의 산책 경로를 추천하는 Spring Boot 기반 백엔드 서버입니다. 

**핵심 가치:**
- 🗺️ **공간 데이터 활용**: PostgreSQL의 PostGIS 확장을 활용하여 지리적 데이터를 효율적으로 저장하고 조회합니다.
- 🚶 **스마트 경로 추천**: 최단 경로와 여유 경로(가로수길 최대화) 두 가지 옵션을 제공하여 사용자의 목적에 맞는 경로를 제안합니다.
- 🎯 **사용자 경험 최적화**: 지도 영역(Bounding Box) 기반 조회로 불필요한 데이터 전송을 방지하고, 실시간 태깅 시스템으로 사용자 참여를 유도합니다.

**주요 기술 특징:**
- PostgreSQL 15 + PostGIS를 활용한 공간 데이터 처리
- JWT 기반의 안전한 인증/인가 시스템
- Time Bucket DP 알고리즘을 활용한 최적 경로 탐색
- Docker Compose를 통한 간편한 배포 및 개발 환경 구성

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/garosugil/
│   │   ├── GarosuggilServerApplication.java
│   │   ├── common/          # 공통 예외, 응답 처리
│   │   ├── config/          # 설정 (Security, OpenAPI, RestTemplate)
│   │   ├── controller/      # 컨트롤러
│   │   │   └── auth/
│   │   ├── domain/          # 엔티티
│   │   │   ├── user/
│   │   │   ├── favorite/
│   │   │   ├── music/
│   │   │   └── road/
│   │   ├── dto/             # DTO
│   │   ├── repository/      # JPA Repository
│   │   ├── route/           # 경로 알고리즘
│   │   ├── security/        # JWT, 인증 필터
│   │   ├── service/         # 비즈니스 로직
│   │   └── util/            # 유틸리티
│   └── resources/
│       ├── application.yml
│       └── all_roads_walking_paths.json
└── test/
```

## 실행 방법

### 1. Docker Compose로 실행 (권장)

```bash
# 모든 서비스 시작 (PostgreSQL, Redis, Spring Boot)
docker-compose up -d

# 로그 확인
docker-compose logs -f spring

# 서비스 중지
docker-compose down

# 볼륨까지 삭제
docker-compose down -v
```

### 2. 로컬 개발 환경

```bash
# PostgreSQL & Redis만 실행
docker-compose up -d postgres redis

# Spring Boot 애플리케이션 실행
./gradlew bootRun
```

## API 문서

애플리케이션 실행 후 Swagger UI에서 API 문서를 확인할 수 있습니다:

```
http://localhost:8080/swagger-ui/index.html
```

## 주요 API 엔드포인트

### 🔐 인증 API (`/api/auth`)

- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/login` - 로그인 (Access Token: Cookie, Refresh Token: Body)
- `POST /api/auth/reissue` - 토큰 재발급
- `GET /api/auth/my` - 내 정보 조회 (인증 필요)

### 🗺️ 경로 API (`/api/routes`)

- `POST /api/routes/search` - 경로 검색 (최단 경로 + 에코 경로)

### 🎵 음악 API (`/api/music`)

- `POST /api/music/recommend` - 음악 추천

### ⭐ 도로 API (`/api/roads`)

- `GET /api/roads/{segmentId}` - 도로 상세 조회
- `POST /api/roads/{segmentId}/like` - 도로 좋아요
- `DELETE /api/roads/{segmentId}/like` - 도로 좋아요 취소
- `POST /api/roads/{segmentId}/tags` - 도로 태그 추가
- `GET /api/roads/{segmentId}/tags/stats` - 도로 태그 통계

### 📍 관심 장소 API (`/api/favorites`)

- `POST /api/favorites` - 관심 장소 추가
- `GET /api/favorites` - 관심 장소 목록 조회
- `DELETE /api/favorites/{favoriteId}` - 관심 장소 삭제


## 서비스 핵심 기능별 코드 설명

### 1. 경로 검색 API (`POST /api/routes/search`)

**핵심 코드 위치:** RouteService.java, AvenueRouteService.java

**동작 원리:**
1. TMAP API를 호출하여 최단 경로를 먼저 조회합니다.
2. 사용자가 요청한 추가 시간(`addedTimeReq`)을 기반으로 여유 경로를 탐색합니다.
3. 여유 경로는 Time Bucket DP 알고리즘을 사용하여 제한 시간 내 가로수길 거리를 최대화합니다.

**주요 코드:**
// RouteService.java
RouteSearchResponse.RouteInfo fastestRouteInfo = searchFastestRoute(start, end);
RouteSearchResponse.RouteInfo avenueRouteInfo = searchEcoRoute(start, end, reqAddedTimeSec, fastestTimeSec);

### 2. 주변 가로수길 조회 API (GET /api/roads/nearby)
**핵심 코드 위치:** RoadController.java, RoadService.java, StreetSegmentRepository.java

**동작 원리:**
1. 클라이언트로부터 Bounding Box 좌표(minLat, minLng, maxLat, maxLng)를 받습니다.
2. PostGIS의 ST_Intersects와 ST_MakeEnvelope 함수를 사용하여 해당 영역 내의 가로수길만 조회합니다.
3. JTS LineString을 [{lat, lng}, ...] 형태의 좌표 배열로 변환하여 반환합니다.

주요 코드:
// StreetSegmentRepository.java@Query(value = "SELECT * FROM street_segments WHERE ST_Intersects(coordinates, ST_MakeEnvelope(:minLng, :minLat, :maxLng, :maxLat, 4326))", nativeQuery = true)List<StreetSegment> findSegmentsInBoundingBox(@Param("minLat") double minLat, ...);// RoadService.javaList<StreetSegment> segments = streetSegmentRepository.findSegmentsInBoundingBox(minLat, minLng, maxLat, maxLng);List<Location> polyline = lineStringToLocations(segment.getCoordinates());

### 3. 도로 상세 조회 API (GET /api/roads/{segmentId})

**핵심 코드 위치**: RoadService.java, RoadTagService.java

**동작 원리:**
1. 도로 기본 정보(이름, 좋아요 수 등)를 조회합니다.
2. RoadTagService를 통해 해당 도로의 태그 통계를 조회합니다.
3. 상위 3개의 인기 태그(이모지 + 라벨)를 추출하여 응답에 포함합니다.

**주요 코드:**

// RoadService.javaRoadTagStatsResponse tagStats = roadTagService.getStats(segmentId.longValue(), currentUserId);List<String> topTags = tagStats.getStats().stream()    .limit(3)    .map(item -> item.getEmoji() + " " + item.getLabel())    .collect(Collectors.toList());

### 4. 이모지 태깅 API (POST /api/emoji/{segment_id}/tags)

**핵심 코드 위치:** EmojiController.java, EmojiService.java

**동작 원리:**
1. 사용자 인증 정보를 확인합니다.
2. 같은 사용자가 오늘 이미 해당 도로에 태그를 남겼는지 확인합니다 (하루 1회 제한).
3. RoadTagLog 엔티티를 생성하여 데이터베이스에 저장합니다.
   
**주요 코드:**

// EmojiService.java
if (roadTagLogRepository.findBySegmentIdAndUserIdAndCreatedAtDate(segmentId, userId, today).isPresent()) {
    throw new IllegalStateException("오늘은 이미 태그를 남기셨어요.");
}
RoadTagLog log = RoadTagLog.builder()
    .segmentId(segmentId)
    .user(user)
    .tagCode(request.getTagCode())
    .build();
roadTagLogRepository.save(log);

### 예제 요청

#### 회원가입

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123!",
    "nickname": "산책러버"
  }'
```

#### 로그인

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123!"
  }' \
  -c cookies.txt
```

#### 내 정보 조회 (쿠키 사용)

```bash
curl -X GET http://localhost:8080/api/auth/my \
  -b cookies.txt
```

#### 경로 검색

```bash
curl -X POST http://localhost:8080/api/routes/search \
  -H "Content-Type: application/json" \
  -d '{
    "user_location": {
      "lat": 37.5665,
      "lng": 126.9780
    },
    "target_type": "ROAD_ENTRY",
    "road_info": {
      "start": {"lat": 37.5186, "lng": 127.0223},
      "end": {"lat": 37.5196, "lng": 127.0233}
    }
  }'
```

# 🌲 주요 코드 설명: 여유길 찾기 알고리즘
여유길 찾기 알고리즘은 사용자가 요청한 추가 시간(여유 시간) 내에서 가로수길(AVENUE)을 최대한 많이 지나는 경로를 탐색하는 최적화 알고리즘입니다.

이 알고리즘은 크게 1. 그래프 생성, 2. DP 탐색, 3. 경로 구성의 3단계로 이루어져 있습니다.

## 1단계: 그래프 생성 (AvenueRouteGraphBuilder)
목표: 출발지와 도착지를 연결하고, 주변의 가로수길 정보를 포함하는 그래프를 구성합니다.

### 1.1 후보 도로 필터링
전체 도로 데이터를 모두 탐색하는 것은 비효율적이므로, 출발지와 도착지를 포함하는 Bounding Box(약 3km 반경) 내의 가로수길만 후보로 선정하여 계산량을 줄입니다.

Java

// AvenueRouteGraphBuilder.java
List<RoadDataService.FullRoadInfo> candidates = loadCandidatesInExpandedBox(start, end, radiusKm);

### 1.2 노드 생성
그래프의 정점(Node)을 정의합니다. 동일한 좌표를 가진 노드는 재사용하여 그래프의 크기를 최소화합니다.

START / END 노드: 사용자의 출발 및 도착 좌표

도로 엔드포인트: 각 가로수길(Link)의 시작점과 끝점

Java

int startId = g.addNode(start, "START");
int endId = g.addNode(end, "END");

// 도로의 시작점과 끝점을 노드로 등록 (중복 제거)
int a = nodeMap.computeIfAbsent(key(road.getStart()), k -> g.addNode(...));
int b = nodeMap.computeIfAbsent(key(road.getEnd()), k -> g.addNode(...));

### 1.3 AVENUE 엣지 추가
가로수길 구간을 양방향 엣지(Edge)로 추가합니다.

비용(Cost): 도로 길이 / 보행 속도 (초당 1.4m)

보상(Reward): 가로수길 거리 (AVENUE 엣지는 도로 길이와 동일)

Java

// 양방향 연결
g.addEdge(new Edge(a, b, EdgeType.AVENUE, roadTime, roadDistance, roadDistance, ...));
g.addEdge(new Edge(b, a, EdgeType.AVENUE, roadTime, roadDistance, roadDistance, ...));

### 1.4 WALK 엣지 추가 (성능 최적화)
가로수길 사이를 연결하는 일반 보행로(WALK)를 추가합니다. 그래프 복잡도가 폭발적으로 증가하는 것을 막기 위해 제약을 둡니다.

거리 제한: 500m 이내의 노드만 연결

개수 제한: 각 노드당 가장 가까운 상위 15개 노드만 연결 (KNN)

Java

// AvenueRouteGraphBuilder.java
private static final double WALK_LINK_MAX_M = 500.0;
private static final int WALK_LINK_TOP_K = 15;

## 2단계: DP 탐색 (TimeBucketDpSolver)
목표: Time Bucket DP를 사용하여 제한 시간 내 가로수길 거리를 최대화하는 경로를 찾습니다.

### 2.1 DP 상태 정의
시간을 연속적인 값이 아닌 **버킷(Bucket) 단위(기본 10초)**로 이산화하여 관리합니다.

dpAvenue[b][v]: 버킷 b 시간에 노드 v에 도착했을 때의 최대 가로수길 거리

dpTime[b][v]: 실제 소요 시간

dpTotalDist[b][v]: 총 이동 거리

Java

// TimeBucketDpSolver.java
// B: 최대 시간 버킷 개수, V: 노드 개수
int B = (int) Math.ceil((double) maxTimeSec / bucketSec);

double[][] dpAvenue = new double[B + 1][V];
double[][] dpTime = new double[B + 1][V];
double[][] dpTotalDist = new double[B + 1][V];

### 2.2 DP 전이 (Transition)
각 버킷(시간)과 노드에 대해 연결된 모든 엣지를 탐색하며 상태를 업데이트합니다.

Java

for (int b = 0; b <= B; b++) {
    for (int v = 0; v < V; v++) {
        if (dpAvenue[b][v] <= NEG / 2) continue; // 도달 불가능 상태 제외
        
        for (Edge e : g.getAdj().get(v)) {
            double newTime = dpTime[b][v] + e.getCostSec();
            if (newTime > maxTimeSec) continue; // 시간 제한 초과 시 제외
            
            // 다음 상태의 버킷 인덱스 계산
            int nb = (int) Math.ceil(newTime / bucketSec);
            double newAvenue = dpAvenue[b][v] + e.getRewardAvenueM();
            
            // 더 나은 경로일 경우 상태 업데이트
            if (isBetter(newAvenue, newTime, newTotalDist, ...)) {
                dpAvenue[nb][e.getTo()] = newAvenue;
                dpTime[nb][e.getTo()] = newTime;
                // 경로 복원을 위한 역추적 정보 저장
                prevBucket[nb][e.getTo()] = b;
                prevNode[nb][e.getTo()] = v;
                prevEdge[nb][e.getTo()] = e;
            }
        }
    }
}

### 2.3 최적 경로 선택 기준 (isBetter)
동일한 버킷 내에서 더 나은 경로를 판단하는 우선순위는 다음과 같습니다.

가로수길 거리가 길수록 우선

총 소요 시간이 짧을수록 우선

총 이동 거리가 짧을수록 우선

Java

private boolean isBetter(double a1, double t1, double d1, double a2, double t2, double d2) {
    if (a1 > a2 + 1e-9) return true;  // 1순위: 가로수길 거리 최대화
    if (a1 < a2 - 1e-9) return false;
    
    if (t1 < t2 - 1e-9) return true;  // 2순위: 시간 최소화
    if (t1 > t2 + 1e-9) return false;
    
    return d1 < d2 - 1e-9;            // 3순위: 거리 최소화
}

### 2.4 경로 복원 (Backtracking)
탐색 종료 후, 도착지(END 노드)에 도달한 모든 버킷 상태 중 가장 isBetter한 상태를 선택합니다. 그 후 prevEdge 배열을 역추적하여 경로를 완성합니다.

Java

// 1. 도착지 도달 상태 중 최적값 선택
for (int b = 0; b <= B; b++) {
    if (dpAvenue[b][endId] <= NEG / 2) continue;
    if (isBetter(dpAvenue[b][endId], ..., bestA, ...)) {
        bestB = b;
        // ...
    }
}

// 2. 경로 역추적
List<Edge> edges = new ArrayList<>();
int curNode = endId;
int curB = bestB;

while (!(curB == 0 && curNode == startId)) {
    Edge pe = prevEdge[curB][curNode];
    edges.add(pe);
    curNode = prevNode[curB][curNode];
    curB = prevBucket[curB][curNode];
}
Collections.reverse(edges); // 역순 정렬하여 출발->도착 순서로 변환

## 3단계: 경로 구성
DP 결과로 얻은 List<Edge>를 클라이언트가 사용할 수 있는 좌표 배열(Polyline) 형태의 데이터로 변환하여 반환합니다.

### 💡 알고리즘의 장점

#### 정확성: 시간 제약을 엄격히 준수하면서 가로수길 비율이 가장 높은 경로를 보장합니다.

#### 성능 최적화:

#### 후보 필터링: 탐색 범위를 3km 반경으로 제한

#### WALK 엣지 제한: 노드 간 연결을 근접 상위 15개로 제한

#### Time Bucket: 시간을 이산화하여 DP 상태 공간을 효율적으로 관리

#### 확장성: 그래프 기반 구조이므로 추후 '경사도', '치안' 등 새로운 가중치(Cost)를 쉽게 추가할 수 있습니다.



## EC2 배포

### 1. Swap 메모리 설정 (1GB EC2 인스턴스용)

```bash
# 2GB swap 파일 생성
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# 영구 설정
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

### 2. Docker & Docker Compose 설치

```bash
# Docker 설치
sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 3. 프로젝트 배포

```bash
# 프로젝트 클론
git clone <repository-url>
cd Garosugil-Server

# Docker Compose로 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f
```

## 환경 변수 설정

`application.yml` 또는 환경 변수로 다음을 설정할 수 있습니다:

```yaml
jwt:
  secret: your-secret-key-here # JWT 시크릿 키 (최소 256비트)
  access-expiration: 3600000 # Access Token 만료 시간 (1시간)
  refresh-expiration: 604800000 # Refresh Token 만료 시간 (7일)

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/garosugil
    username: postgres
    password: postgres
```

## JWT 인증 흐름

1. **로그인**: `/api/auth/login`

   - Access Token → HttpOnly Cookie (1시간)
   - Refresh Token → Response Body (7일)

2. **API 요청**: 쿠키의 Access Token으로 자동 인증

   - `GET /api/auth/my`
   - 기타 인증이 필요한 API

3. **토큰 재발급**: `/api/auth/reissue`
   - Request: Refresh Token (Body)
   - Response: 새로운 Access Token (Cookie) + 새로운 Refresh Token (Body)

## Swagger UI 사용법

1. 애플리케이션 실행 후 `http://localhost:8080/swagger-ui/index.html` 접속
2. `/api/auth/login` API로 로그인
3. 브라우저가 자동으로 쿠키 저장
4. 이후 API 요청 시 쿠키가 자동으로 포함되어 인증됨

## 메모리 최적화

1GB EC2 인스턴스를 위한 JVM 옵션이 Dockerfile에 설정되어 있습니다:

- Xmx384m: 최대 힙 메모리 384MB
- Xms256m: 초기 힙 메모리 256MB
- MaxMetaspaceSize=128m: 최대 메타스페이스 128MB

## 라이센스

MIT
