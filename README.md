# Garosugil Server

Spring Boot 프로젝트 with PostgreSQL (PostGIS), Redis

## 기술 스택

- **Java 17**
- **Spring Boot 3.2.1**
- **PostgreSQL 15 with PostGIS**
- **Redis 7**
- **Hibernate Spatial**
- **Docker & Docker Compose**

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/garosugil/
│   │   ├── GarosuggilServerApplication.java
│   │   ├── domain/
│   │   │   └── location/
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── repository/
│   │   │       ├── entity/
│   │   │       └── dto/
│   │   └── global/
│   │       └── config/
│   └── resources/
│       └── application.yml
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

## API 엔드포인트

### Location API

- `POST /api/locations` - 새 위치 생성
- `GET /api/locations` - 모든 위치 조회
- `GET /api/locations/{id}` - 특정 위치 조회
- `GET /api/locations/nearby?longitude={lng}&latitude={lat}&radius={m}` - 반경 내 위치 검색
- `GET /api/locations/search?name={name}` - 이름으로 위치 검색
- `DELETE /api/locations/{id}` - 위치 삭제

### 예제 요청

```bash
# 위치 생성
curl -X POST http://localhost:8080/api/locations \
  -H "Content-Type: application/json" \
  -d '{
    "name": "가로수길 카페",
    "description": "멋진 카페",
    "longitude": 127.0223,
    "latitude": 37.5186,
    "address": "서울특별시 강남구 신사동"
  }'

# 반경 1km 내 위치 검색
curl "http://localhost:8080/api/locations/nearby?longitude=127.0223&latitude=37.5186&radius=1000"
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

## 환경 변수

`docker-compose.yml`에서 다음 환경 변수를 수정할 수 있습니다:

- `POSTGRES_DB`: 데이터베이스 이름
- `POSTGRES_USER`: 데이터베이스 사용자
- `POSTGRES_PASSWORD`: 데이터베이스 비밀번호
- `SPRING_DATA_REDIS_HOST`: Redis 호스트
- `SPRING_DATA_REDIS_PORT`: Redis 포트

## 메모리 최적화

1GB EC2 인스턴스를 위한 JVM 옵션이 Dockerfile에 설정되어 있습니다:

- Xmx384m: 최대 힙 메모리 384MB
- Xms256m: 초기 힙 메모리 256MB
- MaxMetaspaceSize=128m: 최대 메타스페이스 128MB

## 라이센스

MIT
