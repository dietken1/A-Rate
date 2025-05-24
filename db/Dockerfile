FROM mysql:latest

COPY init-mysql.sh /docker-entrypoint-initdb.d/
RUN chmod +x /docker-entrypoint-initdb.d/init-mysql.sh

EXPOSE 3306