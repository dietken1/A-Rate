FROM nginx:alpine
# 기존 default.conf 삭제
RUN rm /etc/nginx/conf.d/default.conf
# 사용자 정의 nginx.conf 복사
COPY nginx.conf /etc/nginx/conf.d/default.conf
# 80, 443 포트 개방
EXPOSE 80
EXPOSE 443