FROM mysql:8.0

COPY init-mysql.sh /docker-entrypoint-initdb.d/01-init-mysql.sh
COPY professors.sql /docker-entrypoint-initdb.d/02-professors.sql
COPY lectures.sql /docker-entrypoint-initdb.d/03-lectures.sql

RUN chmod +x /docker-entrypoint-initdb.d/01-init-mysql.sh

EXPOSE 3306