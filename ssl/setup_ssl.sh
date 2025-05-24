#!/bin/sh

# .env 파일 로드
if [ -f ../.env ]; then
    export $(grep -v '^#' ../.env | xargs)
else
    echo ".env 파일을 찾을 수 없습니다. .env.example을 사용하여 .env 파일을 생성하세요."
    exit 1
fi

# mkcert 설치 확인 및 설치
install_mkcert() {
  if ! command -v mkcert >/dev/null 2>&1; then
    echo "mkcert가 설치되어 있지 않습니다. 설치를 시작합니다."

    case "$(uname)" in
      Linux*)
        echo "리눅스 환경에서 mkcert 설치 중..."
        apt update
        apt install -y libnss3-tools
        curl -L https://dl.filippo.io/mkcert/latest?for=linux/amd64 -o mkcert
        chmod +x mkcert
        mv mkcert /usr/local/bin/
        ;;
      Darwin*)
        echo "맥OS 환경에서 mkcert 설치 중..."
        brew install mkcert
        brew install nss # Firefox를 사용하는 경우 필요
        ;;
      *)
        echo "지원하지 않는 운영 체제입니다."
        exit 1
        ;;
    esac
  else
    echo "mkcert가 이미 설치되어 있습니다."
  fi
}

# 로컬 CA 설치
install_local_ca() {
  echo "로컬 CA 설치 중..."
  mkcert -install
}

# SSL 인증서 발급 및 이동
generate_and_move_ssl_certificates() {
  # IS_LOCAL이 true이면 localhost, 아니면 SERVER_NAME 사용
  if [ "$SERVER_NAME" = "localhost" ]; then
    local domains="localhost"
  else
    local domains="$SERVER_NAME"
  fi

  echo "SSL 인증서 발급 중... 도메인: $domains"
  mkcert $domains

  # 정확한 인증서 파일 이름을 확인하고 수정
  local cert_file="${domains}.pem"
  local key_file="${domains}-key.pem"

  if [ -f "$cert_file" ] && [ -f "$key_file" ]; then
    mv "$cert_file" "fullchain.pem"
    mv "$key_file" "privkey.pem"
    echo "인증서 파일이 $(pwd)에 fullchain.pem 및 privkey.pem으로 저장되었습니다."
  else
    echo "인증서 파일 생성에 실패했습니다. 파일이 존재하지 않습니다: $cert_file 또는 $key_file"
  fi
}

# 스크립트 실행
install_mkcert
install_local_ca
generate_and_move_ssl_certificates

echo "SSL 인증서 발급 및 이동이 완료되었습니다."