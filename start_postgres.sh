#!/bin/bash

CONTAINER_NAME="videos"
POSTGRES_PASSWORD="postgres"
DB_NAME="videos"

# Перевірити, чи існує контейнер
if [ ! "$(docker ps -q -f name=$CONTAINER_NAME)" ]; then
    if [ "$(docker ps -aq -f status=exited -f name=$CONTAINER_NAME)" ]; then
        # Видалити існуючий, але зупинений контейнер
        docker rm $CONTAINER_NAME
    fi
    # Запустити новий контейнер
    docker run -d \
        --name $CONTAINER_NAME \
        -e POSTGRES_PASSWORD=$POSTGRES_PASSWORD \
        -p 5432:5432 \
        postgres
fi

# Дочекатися запуску PostgreSQL
echo "Waiting for PostgreSQL to start..."
sleep 5

# Створити базу даних, якщо вона ще не створена
docker exec -i $CONTAINER_NAME psql -U postgres -c "SELECT 1 FROM pg_database WHERE datname = '$DB_NAME';" | grep -q 1 || \
docker exec -i $CONTAINER_NAME psql -U postgres -c "CREATE DATABASE $DB_NAME;"

echo "Container and database are ready."
