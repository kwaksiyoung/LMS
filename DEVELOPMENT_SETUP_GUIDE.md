# 🛠 LMS 개발 환경 설정 가이드

**프로젝트**: Learning Management System (eGovFrame 4.2.0)  
**마지막 업데이트**: 2026년 3월 17일

---

## 📋 목차

1. [필수 시스템 요구사항](#필수-시스템-요구사항)
2. [개발 환경 설정](#개발-환경-설정)
3. [데이터베이스 설정](#데이터베이스-설정)
4. [Redis 설정](#redis-설정)
5. [프로젝트 빌드 및 실행](#프로젝트-빌드-및-실행)
6. [IDE 설정 (Eclipse/IntelliJ)](#ide-설정)
7. [테스트 및 검증](#테스트-및-검증)
8. [문제 해결](#문제-해결)

---

## 필수 시스템 요구사항

### 운영 체제
- Windows 10/11 이상
- macOS 10.15 이상
- Linux (Ubuntu 20.04 LTS 이상 권장)

### 필수 소프트웨어

| 소프트웨어 | 버전 | 용도 |
|-----------|------|------|
| Java JDK | 1.8 (8) | 컴파일 및 실행 |
| Maven | 3.6.0 이상 | 빌드 도구 |
| Tomcat | 8.5 이상 | 웹 서버 (또는 STS/IDE 내장) |
| MariaDB | 10.4 이상 | 데이터베이스 |
| Redis | 6.0 이상 | 세션 클러스터링 |
| Git | 최신 | 버전 관리 |

---

## 개발 환경 설정

### 1. Java JDK 설치

#### Windows
```bash
# JDK 다운로드 (https://www.oracle.com/java/technologies/downloads/)
# 또는 OpenJDK 사용
choco install openjdk8

# 설치 확인
java -version
javac -version
```

#### macOS
```bash
# Homebrew를 이용한 설치
brew install openjdk@8

# 심링크 설정
sudo ln -sfn /usr/local/opt/openjdk@8/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-8.jdk

# 설치 확인
java -version
```

#### Linux
```bash
# Ubuntu
sudo apt-get install openjdk-8-jdk

# CentOS
sudo yum install java-1.8.0-openjdk-devel

# 설치 확인
java -version
```

### 2. Maven 설치

#### Windows
```bash
# Maven 다운로드: https://maven.apache.org/download.cgi
# ZIP 파일 추출 후 환경 변수 설정
# M2_HOME = C:\apache-maven-3.9.0
# PATH에 %M2_HOME%\bin 추가

# 설치 확인
mvn -version
```

#### macOS
```bash
brew install maven
mvn -version
```

#### Linux
```bash
sudo apt-get install maven
mvn -version
```

### 3. 환경 변수 설정

```bash
# Windows (시스템 변수)
JAVA_HOME=C:\Program Files\Java\jdk1.8.0_xxx
M2_HOME=C:\apache-maven-3.9.0
PATH=%JAVA_HOME%\bin;%M2_HOME%\bin

# Linux/macOS (.bashrc 또는 .zshrc)
export JAVA_HOME=/usr/libexec/java_home -v 1.8
export M2_HOME=/usr/local/opt/maven
export PATH=$JAVA_HOME/bin:$M2_HOME/bin:$PATH
```

---

## 데이터베이스 설정

### 1. MariaDB 설치

#### Windows (Chocolatey)
```bash
choco install mariadb
```

#### macOS (Homebrew)
```bash
brew install mariadb
brew services start mariadb
```

#### Linux (Ubuntu)
```bash
sudo apt-get install mariadb-server
sudo mysql_secure_installation
sudo systemctl start mariadb
```

### 2. 데이터베이스 및 사용자 생성

```bash
# MariaDB 접속
mysql -u root -p

# 데이터베이스 생성
CREATE DATABASE lms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 사용자 생성 및 권한 부여
CREATE USER 'lms_user'@'localhost' IDENTIFIED BY 'lms_password';
GRANT ALL PRIVILEGES ON lms.* TO 'lms_user'@'localhost';
FLUSH PRIVILEGES;

# 확인
SHOW GRANTS FOR 'lms_user'@'localhost';
```

### 3. 데이터베이스 스키마 및 초기 데이터 로드

```bash
# 프로젝트 디렉토리로 이동
cd /path/to/LMS

# 스키마 생성
mysql -u lms_user -p lms < src/main/resources/egovframework/sqlmap/authority-schema.sql

# 초기 데이터 로드
mysql -u lms_user -p lms < src/main/resources/egovframework/sqlmap/authority-initial-data.sql

# 데이터 확인
mysql -u lms_user -p lms
> SHOW TABLES;
> SELECT COUNT(*) FROM tb_user;
```

### 4. 데이터베이스 연결 설정

`src/main/resources/application.properties` 확인:

```properties
db.driver=org.mariadb.jdbc.Driver
db.url=jdbc:mariadb://localhost:3306/lms?characterEncoding=UTF-8&serverTimezone=Asia/Seoul
db.username=lms_user
db.password=lms_password
```

---

## Redis 설정

### 1. Redis 설치

#### Windows (WSL2 또는 Docker 권장)
```bash
# WSL2 Ubuntu에서
wsl --install
wsl
sudo apt-get install redis-server

# Docker 사용 (권장)
docker run -d -p 6379:6379 redis:latest
```

#### macOS
```bash
brew install redis
brew services start redis
```

#### Linux
```bash
sudo apt-get install redis-server
sudo systemctl start redis-server
sudo systemctl enable redis-server
```

### 2. Redis 연결 확인

```bash
# Redis CLI 접속
redis-cli

# 연결 테스트
PING  # 응답: PONG

# 테스트 데이터 저장
SET testkey "Hello Redis"
GET testkey

# 종료
EXIT
```

### 3. Redis 설정 파일 확인

```bash
# Redis 설정 위치
# Linux: /etc/redis/redis.conf
# macOS: /usr/local/etc/redis.conf

# 중요 설정항목
maxmemory 256mb           # 메모리 제한
maxmemory-policy allkeys-lru  # 메모리 정책
timeout 0                 # 클라이언트 타임아웃 비활성화
```

### 4. 애플리케이션 Redis 설정

`src/main/resources/application.properties`:

```properties
redis.host=localhost
redis.port=6379
redis.password=          # 비밀번호 없으면 비워둠
redis.database=0
```

---

## 프로젝트 빌드 및 실행

### 1. 소스 코드 다운로드

```bash
# Git 클론
git clone <repository-url>
cd LMS

# 또는 다운로드된 프로젝트로 이동
cd /path/to/LMS
```

### 2. Maven 빌드

```bash
# 의존성 다운로드 및 빌드
mvn clean install

# 또는 테스트 스킵
mvn clean install -DskipTests

# 빌드 결과 확인
ls -la target/LMS-1.0.0.war
```

### 3. Tomcat에서 실행

#### IDE 내장 Tomcat (권장)

**Eclipse/STS:**
```
1. Run → Run Configurations
2. Apache Tomcat v8.5 선택
3. 프로젝트 추가
4. Run 클릭
```

**IntelliJ IDEA:**
```
1. Run → Edit Configurations
2. Tomcat Server → Local 추가
3. Deployment에 프로젝트 WAR 추가
4. Run 클릭
```

#### 독립 Tomcat 서버

```bash
# Tomcat 디렉토리로 이동
cd $CATALINA_HOME

# WAR 파일 배포
cp /path/to/LMS-1.0.0.war webapps/LMS.war

# Tomcat 시작
bin/catalina.sh run  # Linux/macOS
bin/catalina.bat run # Windows

# 또는 서비스로 시작
bin/startup.sh   # Linux/macOS
bin/startup.bat  # Windows
```

### 4. 애플리케이션 접속

```
주소: http://localhost:8080/LMS
테스트 로그인:
  - ID: admin
  - PW: password123
```

---

## IDE 설정

### Eclipse/Spring Tool Suite (STS) 설정

#### 1. 프로젝트 Import

```
1. File → Import → Maven → Existing Maven Projects
2. Root Directory: /path/to/LMS
3. Finish
```

#### 2. JDK 설정

```
1. Project → Properties → Java Build Path
2. Libraries 탭 → JRE System Library 편집
3. Java 1.8 선택
```

#### 3. Tomcat 서버 설정

```
1. Preferences → Server → Runtime Environments
2. Add → Apache Tomcat v8.5
3. Tomcat 설치 경로 지정
4. Apply
```

#### 4. 프로젝트 Facets 설정

```
1. Project → Properties → Project Facets
2. Convert to faceted form
3. Runtimes 탭 → Apache Tomcat v8.5 선택
4. Apply
```

### IntelliJ IDEA 설정

#### 1. 프로젝트 열기

```
1. Open → /path/to/LMS 선택
2. Trust Project (Maven 프로젝트로 자동 인식)
```

#### 2. JDK 설정

```
1. File → Project Structure → Project
2. SDK: Java 1.8 선택
3. Language level: 8 선택
```

#### 3. Tomcat 설정

```
1. Run → Edit Configurations
2. "+" → Tomcat Server → Local
3. Configure: Tomcat 홈 디렉토리 지정
4. Deployment: "+" → Artifact → LMS:war
```

---

## 테스트 및 검증

### 1. 헬스 체크

```bash
# API 헬스 체크
curl http://localhost:8080/LMS/api/v1/health

# 응답 예시:
# {
#   "success": true,
#   "data": {
#     "status": "UP",
#     "timestamp": 1234567890,
#     "application": "LMS (eGovFrame 4.2.0)",
#     "version": "1.0.0"
#   }
# }
```

### 2. 데이터베이스 연결 확인

```bash
# 로그 확인
tail -f /path/to/LMS/target/logs/lms.log

# "Database connection successful" 메시지 확인
```

### 3. Redis 연결 확인

```bash
# 애플리케이션 로그에서
# "[Redis] ✓ Redis 연결 성공" 메시지 확인

# 또는 Redis CLI에서
redis-cli
KEYS lms:session:*  # 세션 키 확인
```

### 4. 로그인 테스트

```bash
# Postman 또는 curl 사용
curl -X POST http://localhost:8080/LMS/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "admin",
    "password": "password123"
  }'
```

---

## 문제 해결

### 포트 충돌

```bash
# 이미 사용 중인 포트 확인
lsof -i :8080    # Linux/macOS
netstat -ano | findstr :8080  # Windows

# Tomcat 포트 변경
# conf/server.xml에서 <Connector port="8080" 변경
```

### MariaDB 연결 오류

```bash
# MariaDB 서버 실행 확인
sudo systemctl status mariadb

# 사용자 및 권한 확인
mysql -u root -p
> SHOW DATABASES;
> SELECT user, host FROM mysql.user;
```

### Redis 연결 오류

```bash
# Redis 서버 실행 확인
redis-cli PING

# 포트 확인
netstat -tlnp | grep 6379  # Linux

# 방화벽 확인 (필요시)
sudo ufw allow 6379  # Linux
```

### Maven 빌드 실패

```bash
# Maven 캐시 삭제
mvn clean

# 의존성 재다운로드
mvn dependency:resolve-plugins
mvn dependency:tree

# 로컬 저장소 정리
rm -rf ~/.m2/repository
mvn clean install
```

### Tomcat 시작 오류

```bash
# Tomcat 로그 확인
tail -f $CATALINA_HOME/logs/catalina.out
tail -f $CATALINA_HOME/logs/localhost.log

# Tomcat 재시작
./bin/shutdown.sh
sleep 2
./bin/startup.sh
```

---

## 🚀 개발 시작 체크리스트

- [ ] Java JDK 8 설치 및 확인
- [ ] Maven 설치 및 확인
- [ ] MariaDB 설치 및 `lms` 데이터베이스 생성
- [ ] Redis 설치 및 실행
- [ ] 프로젝트 폴더 다운로드/클론
- [ ] `mvn clean install` 실행
- [ ] IDE 설정 완료
- [ ] Tomcat 설정 완료
- [ ] 애플리케이션 시작
- [ ] `http://localhost:8080/LMS` 접속 확인
- [ ] API 헬스 체크 성공 (`/api/v1/health`)
- [ ] 데이터베이스 연결 확인 (로그)
- [ ] Redis 연결 확인 (로그)
- [ ] 테스트 로그인 (admin / password123)

---

## 💡 팁

### Hot Reload (개발 중 편의 기능)

**Spring Boot DevTools 사용 (권장):**
```xml
<!-- pom.xml에 추가 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

**IDE에서 자동 빌드 활성화:**
- Eclipse: Project → Build Automatically 체크
- IntelliJ: Build → Compile 또는 Ctrl+Shift+F9

### 로그 레벨 조정

개발 중에 DEBUG 레벨로 상세 로그 확인:
```properties
# application.properties
logging.level.kr.co.lms=DEBUG
logging.level.org.springframework.security=DEBUG
```

### 데이터베이스 마이그레이션

스키마 변경 시:
```bash
# 기존 데이터 백업
mysqldump -u lms_user -p lms > lms_backup.sql

# 스키마 재생성
mysql -u lms_user -p lms < authority-schema.sql
mysql -u lms_user -p lms < authority-initial-data.sql
```

---

**문제 발생 시**: 프로젝트 README.md 또는 개발팀에 문의하세요.
