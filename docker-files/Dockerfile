FROM mariadb
ENV MYSQL_ROOT_PASSWORD 00000000
ENV MYSQL_DATABASE mooddb
COPY ./mysql_data/mysql /var/lib/mysql
EXPOSE 3306
ENTRYPOINT ["mysqld", "--user=root"]