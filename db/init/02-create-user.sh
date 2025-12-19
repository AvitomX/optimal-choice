#!/bin/bash
set -e

# Получение переменных из окружения или значений по умолчанию
DB_APP_USER="${DB_APP_USER}"
DB_APP_PASSWORD="${DB_APP_PASSWORD}"
DB_NAME="${POSTGRES_DB}"

# Проверка: если переменные не установлены, пропускаем создание пользователя
if [ -z "$DB_APP_USER" ] || [ -z "$DB_APP_PASSWORD" ]; then
    echo "DB_APP_USER and DB_APP_PASSWORD not set. Skipping application user creation."
    echo "To create application user, set these variables in .env and docker-compose.yml"
    exit 0
fi

echo "Creating application database user: $DB_APP_USER"

# Создание пользователя
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    DO \$\$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_user WHERE usename = '$DB_APP_USER') THEN
            CREATE USER $DB_APP_USER WITH PASSWORD '$DB_APP_PASSWORD';
        END IF;
    END
    \$\$;

    -- Выдача прав на базу данных
    GRANT CONNECT ON DATABASE $DB_NAME TO $DB_APP_USER;
EOSQL

# Подключение к базе данных и выдача прав на схему
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$DB_NAME" <<-EOSQL
    -- Выдача прав на схему
    GRANT USAGE ON SCHEMA optch TO $DB_APP_USER;
    GRANT CREATE ON SCHEMA optch TO $DB_APP_USER;
    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA optch TO $DB_APP_USER;
    GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA optch TO $DB_APP_USER;

    -- Выдача прав на будущие таблицы и последовательности
    ALTER DEFAULT PRIVILEGES IN SCHEMA optch GRANT ALL ON TABLES TO $DB_APP_USER;
    ALTER DEFAULT PRIVILEGES IN SCHEMA optch GRANT ALL ON SEQUENCES TO $DB_APP_USER;
EOSQL

echo "User $DB_APP_USER created successfully with access to schema optch"

