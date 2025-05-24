# .env 파일 로드
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
else
    cp .env.example .env
    echo -e "\e[31m.env파일이 설정되지 않아 기본 설정 파일을 토대로 새 .env 파일을 생성하였습니다. .env 파일 수정을 권장합니다\e[0m"
    export $(grep -v '^#' .env | xargs)
fi

cd ssl
find . -not -name 'init-letsencrypt.sh' -not -name 'setup_ssl.sh' -not -name 'cloudflare.ini' -not -name '.' -not -name '..' -exec sudo rm -rf {} +

cd ..

echo "SERVER_NAME: $SERVER_NAME"

docker-compose down -v

cd ssl
sudo ./init-letsencrypt.sh -y
cd ..

sudo chmod -R 755 ssl
docker-compose up -d --build