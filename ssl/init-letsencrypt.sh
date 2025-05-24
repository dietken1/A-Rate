#!/bin/bash

# 필요한 패키지 설치 확인 및 설치
echo "### Checking and installing required packages..."
packages=(curl docker docker-compose openssl)

# 패키지 매니저 확인
if command -v apt-get &> /dev/null; then
    PKG_MANAGER="apt-get"
    PKG_UPDATE="apt-get update"
elif command -v yum &> /dev/null; then
    PKG_MANAGER="yum"
    PKG_UPDATE="yum update"
else
    echo "지원되지 않는 패키지 매니저입니다."
    exit 1
fi

# 패키지 매니저 업데이트
sudo $PKG_UPDATE

# 필요한 패키지 설치
for pkg in "${packages[@]}"; do
    if ! command -v $pkg &> /dev/null; then
        echo "$pkg 설치 중..."
        sudo $PKG_MANAGER install -y $pkg
    fi
done

# Docker 서비스 실행 확인 및 시작
if ! systemctl is-active --quiet docker; then
    echo "Docker 서비스 시작 중..."
    sudo systemctl start docker
fi

# .env 파일 로드
if [ -f ../.env ]; then
  export $(grep -v '^#' ../.env | xargs)
fi

if [ -z "$SERVER_NAME" ]; then
  echo "Error: SERVER_NAME environment variable is not set."
  exit 1
fi

if [ -z "$SSL_EMAIL" ]; then
  echo "Error: SSL_EMAIL environment variable is not set."
  exit 1
fi

if ! [ -x "$(command -v docker-compose)" ]; then
  echo 'Error: docker-compose is not installed.' >&2
  exit 1
fi

domains=("${SERVER_NAME}")
rsa_key_size=4096
data_path="./certbot"
email="${SSL_EMAIL}"
staging=0 # 실제 인증서 발급을 위해 0으로 변경

# -y 옵션 처리
auto_yes=false
while getopts "y" opt; do
  case $opt in
    y) auto_yes=true ;;
    *) ;;
  esac
done

if [ -d "$data_path" ]; then
  if [ "$auto_yes" = true ]; then
    decision="y"
  else
    read -p "Existing data found for $domains. Continue and replace existing certificate? (y/N) " decision
  fi
  if [ "$decision" != "Y" ] && [ "$decision" != "y" ]; then
    exit
  fi
fi

if [ ! -e "$data_path/conf/options-ssl-nginx.conf" ] || [ ! -e "$data_path/conf/ssl-dhparams.pem" ]; then
  echo "### Downloading recommended TLS parameters ..."
  mkdir -p "$data_path/conf"
  curl -s https://raw.githubusercontent.com/certbot/certbot/master/certbot-nginx/certbot_nginx/_internal/tls_configs/options-ssl-nginx.conf > "$data_path/conf/options-ssl-nginx.conf"
  curl -s https://raw.githubusercontent.com/certbot/certbot/master/certbot/certbot/ssl-dhparams.pem > "$data_path/conf/ssl-dhparams.pem"
  echo
fi

echo "### Creating dummy certificate for $domains ..."
path="/etc/letsencrypt/live/$domains"
mkdir -p "$data_path/conf/live/$domains"
docker-compose run --rm --entrypoint "\
  openssl req -x509 -nodes -newkey rsa:$rsa_key_size -days 1\
    -keyout '$path/privkey.pem' \
    -out '$path/fullchain.pem' \
    -subj '/CN=localhost'" certbot
echo

echo "### Starting nginx ..."
docker-compose up --force-recreate -d nginx
echo

echo "### Deleting dummy certificate for $domains ..."
docker-compose run --rm --entrypoint "\
  rm -Rf /etc/letsencrypt/live/$domains && \
  rm -Rf /etc/letsencrypt/archive/$domains && \
  rm -Rf /etc/letsencrypt/renewal/$domains.conf" certbot
echo

echo "### Requesting Let's Encrypt certificate for $domains ..."
#Join $domains to -d args
domain_args=""
for domain in "${domains[@]}"; do
  domain_args="$domain_args -d $domain"
done

# Select appropriate email arg
case "$email" in
  "") email_arg="--register-unsafely-without-email" ;;
  *) email_arg="--email $email" ;;
esac

# Enable staging mode if needed
if [ $staging != "0" ]; then staging_arg="--staging"; fi

docker-compose run --rm --entrypoint "\
  certbot certonly --webroot -w /var/www/certbot \
    $staging_arg \
    $email_arg \
    $domain_args \
    --rsa-key-size $rsa_key_size \
    --agree-tos \
    --force-renewal \
    --preferred-challenges http" certbot
echo

# 인증서 복사
echo "### Copying certificate to desired path ..."
sudo cp "/etc/letsencrypt/live/${domains}/fullchain.pem" "$data_path/conf/live/${domains}/fullchain.pem"
sudo cp "/etc/letsencrypt/live/${domains}/privkey.pem" "$data_path/conf/live/${domains}/privkey.pem"
echo

echo "### Reloading nginx ..."
docker-compose exec nginx nginx -s reload
