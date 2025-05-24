FROM mysql:8.0

# Copy SQL initialization files only (no shell scripts)
COPY 01-init-schema.sql /docker-entrypoint-initdb.d/01-init-schema.sql
COPY 02-professors.sql /docker-entrypoint-initdb.d/02-professors.sql
COPY 03-lectures.sql /docker-entrypoint-initdb.d/03-lectures.sql

EXPOSE 3306