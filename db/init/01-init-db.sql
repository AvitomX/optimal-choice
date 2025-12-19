-- Создание схемы optch в базе данных postgres
-- Выполняется от имени суперпользователя postgres
-- База данных postgres создается автоматически при инициализации PostgreSQL

-- Создание схемы
CREATE SCHEMA IF NOT EXISTS optch;

-- Выдача прав на схему суперпользователю
GRANT ALL PRIVILEGES ON SCHEMA optch TO postgres;

-- Выдача прав на все таблицы в схеме (для будущих таблиц)
ALTER DEFAULT PRIVILEGES IN SCHEMA optch GRANT ALL ON TABLES TO postgres;
ALTER DEFAULT PRIVILEGES IN SCHEMA optch GRANT ALL ON SEQUENCES TO postgres;

