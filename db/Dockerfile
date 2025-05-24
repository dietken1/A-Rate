FROM mysql:8.0

# 초기화 스크립트 복사 및 권한 설정
COPY init-mysql.sh /docker-entrypoint-initdb.d/
RUN chmod +x /docker-entrypoint-initdb.d/init-mysql.sh

# MySQL 설정
ENV MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
ENV MYSQL_DATABASE=${MYSQL_DATABASE}

EXPOSE 3306