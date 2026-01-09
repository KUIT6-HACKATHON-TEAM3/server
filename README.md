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
