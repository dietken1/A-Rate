#!/bin/bash

# .env 파일 로드
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
else
    cp .env.example .env
    echo -e "\e[31m.env파일이 설정되지 않아 기본 설정 파일을 토대로 새 .env 파일을 생성하였습니다. .env 파일 수정을 권장합니다\e[0m"
    export $(grep -v '^#' .env | xargs)
fi

# SSL 디렉토리 정리
cd ssl
find . -not -name 'init-letsencrypt.sh' -not -name 'setup_ssl.sh' -not -name 'cloudflare.ini' -not -name '.' -not -name '..' -exec rm -rf {} +

cd ..

echo "SERVER_NAME: $SERVER_NAME"

# Docker 컨테이너 중지 및 볼륨 삭제
docker compose down -v

# SSL 인증서 초기화
cd ssl
./init-letsencrypt.sh -y
cd ..

# 권한 설정
chmod -R 755 ssl

# Docker 컨테이너 시작
docker compose up -d --build