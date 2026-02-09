# 📍 Bitgram Backend

위치 기반 SNS **Bitgram**의 백엔드 리포지토리입니다.

## 🛠 Tech Stack
- Java 21 / Spring Boot 3.x
- PostgreSQL (+PostGIS) / Redis
- Spring Security + JWT
- QueryDSL

## 🚀 Getting Started (실행 방법)

### 1. DB 실행 (Docker 필수)
프로젝트 루트에서 아래 명령어를 입력해 DB를 띄워주세요.
$ docker-compose up -d

### 2. 환경 변수 설정
`src/main/resources/application-local.yml`을 확인하고 로컬 환경에 맞게 수정하세요.

### 3. 애플리케이션 실행
BitgramBackendApplication.java 파일을 실행하세요.

## 🤝 Git Convention
- feat: 새로운 기능 추가
- fix: 버그 수정
- docs: 문서 수정
- refactor: 코드 리팩토링