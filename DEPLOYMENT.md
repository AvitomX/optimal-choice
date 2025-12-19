# Руководство по развертыванию на хостинге

Это руководство описывает процесс развертывания приложения Optimal Choice на хостинге.

## Архитектура приложения

Приложение состоит из трех основных компонентов:
- **Backend**: Spring Boot приложение (Java 17) на порту 8090
- **Frontend**: Vue.js приложение (статический сайт)
- **База данных**: PostgreSQL 15

## Варианты развертывания

### Вариант 1: Развертывание на VPS с Docker (Рекомендуется)

Этот вариант подходит для VPS серверов (DigitalOcean, Hetzner, AWS EC2, и т.д.)

#### Требования
- VPS с Ubuntu 20.04+ или аналогичным Linux дистрибутивом
- Минимум 2GB RAM, 2 CPU cores
- Docker и Docker Compose установлены
- Доменное имя (опционально, для SSL)

#### Шаги развертывания

1. **Подготовка сервера**

```bash
# Обновление системы
sudo apt update && sudo apt upgrade -y

# Установка Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Установка Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Добавление пользователя в группу docker
sudo usermod -aG docker $USER
```

2. **Клонирование репозитория на сервер**

```bash
git clone <ваш-репозиторий> /opt/optimal-choice
cd /opt/optimal-choice
```

3. **Настройка переменных окружения**

Создайте файл `.env` на основе `.env.example`:

```bash
cp .env.example .env
nano .env
```

Настройте следующие переменные:
```env
# PostgreSQL
POSTGRES_USER=postgres
POSTGRES_PASSWORD=<надежный-пароль>
POSTGRES_DB=optimal_choice
POSTGRES_PORT=5432

# Приложение
DB_HOST=postgres
DB_PORT=5432
DB_NAME=optimal_choice
DB_APP_USER=optimal_choice_user
DB_APP_PASSWORD=<надежный-пароль>

# Spring Boot
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8090

# Frontend
VITE_API_URL=https://yourdomain.com/api
```

4. **Сборка и запуск**

```bash
# Сборка образов
docker-compose -f docker-compose.prod.yml build

# Запуск сервисов
docker-compose -f docker-compose.prod.yml up -d

# Проверка статуса
docker-compose -f docker-compose.prod.yml ps
docker-compose -f docker-compose.prod.yml logs -f
```

5. **Настройка Nginx (для SSL и reverse proxy)**

```bash
# Установка Nginx
sudo apt install nginx certbot python3-certbot-nginx -y

# Копирование конфигурации
sudo cp nginx/optimal-choice.conf /etc/nginx/sites-available/
sudo ln -s /etc/nginx/sites-available/optimal-choice.conf /etc/nginx/sites-enabled/

# Настройка домена в конфиге
sudo nano /etc/nginx/sites-available/optimal-choice.conf
# Замените yourdomain.com на ваш домен

# Проверка конфигурации
sudo nginx -t

# Перезагрузка Nginx
sudo systemctl reload nginx

# Получение SSL сертификата
sudo certbot --nginx -d yourdomain.com
```

6. **Настройка автозапуска**

```bash
# Создание systemd сервиса
sudo nano /etc/systemd/system/optimal-choice.service
```

Содержимое файла:
```ini
[Unit]
Description=Optimal Choice Application
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/opt/optimal-choice
ExecStart=/usr/local/bin/docker-compose -f docker-compose.prod.yml up -d
ExecStop=/usr/local/bin/docker-compose -f docker-compose.prod.yml down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
```

Активация:
```bash
sudo systemctl daemon-reload
sudo systemctl enable optimal-choice.service
```

### Вариант 2: Развертывание на облачных платформах

#### Heroku

1. Установите Heroku CLI
2. Создайте приложение:
```bash
heroku create optimal-choice-app
heroku addons:create heroku-postgresql:hobby-dev
```

3. Настройте переменные окружения:
```bash
heroku config:set DB_HOST=<host>
heroku config:set DB_PORT=5432
heroku config:set DB_NAME=<name>
heroku config:set DB_APP_USER=<user>
heroku config:set DB_APP_PASSWORD=<password>
```

4. Деплой:
```bash
git push heroku main
```

#### AWS (Elastic Beanstalk)

1. Установите EB CLI
2. Инициализируйте приложение:
```bash
eb init -p "Java 17" optimal-choice
eb create optimal-choice-env
```

3. Настройте переменные окружения через консоль AWS

#### Google Cloud Platform (Cloud Run)

1. Соберите Docker образ:
```bash
docker build -t gcr.io/PROJECT_ID/optimal-choice:latest .
```

2. Загрузите в Container Registry:
```bash
gcloud builds submit --tag gcr.io/PROJECT_ID/optimal-choice:latest
```

3. Разверните на Cloud Run:
```bash
gcloud run deploy optimal-choice \
  --image gcr.io/PROJECT_ID/optimal-choice:latest \
  --platform managed \
  --region us-central1
```

### Вариант 3: Развертывание без Docker

#### Backend

1. **Установка Java 17**
```bash
sudo apt install openjdk-17-jdk -y
```

2. **Сборка JAR файла**
```bash
cd optimal-choice-app
mvn clean package -DskipTests
```

3. **Запуск приложения**
```bash
java -jar target/optimal-choice-app-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/optimal_choice \
  --spring.datasource.username=optimal_choice_user \
  --spring.datasource.password=<password>
```

4. **Настройка systemd сервиса**
```bash
sudo nano /etc/systemd/system/optimal-choice.service
```

```ini
[Unit]
Description=Optimal Choice Application
After=network.target postgresql.service

[Service]
Type=simple
User=www-data
WorkingDirectory=/opt/optimal-choice/optimal-choice-app
ExecStart=/usr/bin/java -jar /opt/optimal-choice/optimal-choice-app/target/optimal-choice-app-0.0.1-SNAPSHOT.jar
Restart=always
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DB_HOST=localhost"
Environment="DB_PORT=5432"
Environment="DB_NAME=optimal_choice"
Environment="DB_APP_USER=optimal_choice_user"
Environment="DB_APP_PASSWORD=<password>"

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable optimal-choice.service
sudo systemctl start optimal-choice.service
```

#### Frontend

1. **Установка Node.js**
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs
```

2. **Сборка приложения**
```bash
cd optimal-choice-ui
npm install
npm run build
```

3. **Настройка Nginx для статических файлов**

См. конфигурацию в `nginx/optimal-choice.conf`

## Настройка базы данных

### Создание базы данных вручную

```bash
# Подключение к PostgreSQL
sudo -u postgres psql

# Создание базы данных и пользователя
CREATE DATABASE optimal_choice;
CREATE USER optimal_choice_user WITH PASSWORD 'your_secure_password';
CREATE SCHEMA optch;
GRANT ALL PRIVILEGES ON DATABASE optimal_choice TO optimal_choice_user;
GRANT ALL PRIVILEGES ON SCHEMA optch TO optimal_choice_user;
ALTER USER optimal_choice_user SET search_path TO optch;
\q
```

### Применение миграций

Миграции Flyway применяются автоматически при запуске Spring Boot приложения.

## Безопасность

### Рекомендации для production

1. **Измените все пароли по умолчанию**
   - Используйте надежные пароли (минимум 16 символов)
   - Используйте разные пароли для разных компонентов

2. **Настройте файрвол**
```bash
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

3. **Включите SSL для PostgreSQL**
   - Настройте SSL в `postgresql.conf`
   - Обновите connection string в `application.yaml`:
   ```yaml
   url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}?ssl=true&sslmode=require
   ```

4. **Используйте переменные окружения**
   - Не храните пароли в коде
   - Используйте секреты Docker или переменные окружения сервера

5. **Настройте CORS правильно**
   - Ограничьте разрешенные домены
   - Не используйте `*` в production

6. **Регулярные обновления**
   - Обновляйте систему и зависимости
   - Следите за уязвимостями

## Мониторинг и логирование

### Просмотр логов

```bash
# Docker Compose
docker-compose -f docker-compose.prod.yml logs -f

# Отдельные сервисы
docker-compose -f docker-compose.prod.yml logs -f backend
docker-compose -f docker-compose.prod.yml logs -f postgres

# Systemd
sudo journalctl -u optimal-choice.service -f
```

### Настройка ротации логов

Добавьте в `docker-compose.prod.yml`:
```yaml
logging:
  driver: "json-file"
  options:
    max-size: "10m"
    max-file: "3"
```

### Мониторинг ресурсов

```bash
# Использование ресурсов контейнерами
docker stats

# Использование дискового пространства
df -h
docker system df
```

## Резервное копирование

### Резервное копирование базы данных

```bash
# Создание бэкапа
docker exec optimal-choice-postgres pg_dump -U postgres optimal_choice > backup_$(date +%Y%m%d_%H%M%S).sql

# Автоматическое резервное копирование (cron)
# Добавьте в crontab:
0 2 * * * docker exec optimal-choice-postgres pg_dump -U postgres optimal_choice > /backups/optimal_choice_$(date +\%Y\%m\%d).sql
```

### Восстановление из бэкапа

```bash
cat backup.sql | docker exec -i optimal-choice-postgres psql -U postgres optimal_choice
```

## Обновление приложения

### Процесс обновления

1. **Остановка сервисов**
```bash
docker-compose -f docker-compose.prod.yml down
```

2. **Обновление кода**
```bash
git pull origin main
```

3. **Пересборка образов**
```bash
docker-compose -f docker-compose.prod.yml build --no-cache
```

4. **Запуск обновленных сервисов**
```bash
docker-compose -f docker-compose.prod.yml up -d
```

5. **Проверка**
```bash
docker-compose -f docker-compose.prod.yml ps
docker-compose -f docker-compose.prod.yml logs -f
```

## Troubleshooting

### Приложение не запускается

1. Проверьте логи:
```bash
docker-compose -f docker-compose.prod.yml logs backend
```

2. Проверьте переменные окружения:
```bash
docker-compose -f docker-compose.prod.yml config
```

3. Проверьте подключение к БД:
```bash
docker exec -it optimal-choice-postgres psql -U postgres -d optimal_choice
```

### Ошибки подключения к базе данных

1. Убедитесь, что PostgreSQL запущен:
```bash
docker-compose -f docker-compose.prod.yml ps postgres
```

2. Проверьте переменные окружения для подключения

3. Проверьте сеть Docker:
```bash
docker network ls
docker network inspect optimal-choice-network
```

### Проблемы с Nginx

1. Проверьте конфигурацию:
```bash
sudo nginx -t
```

2. Проверьте логи:
```bash
sudo tail -f /var/log/nginx/error.log
```

3. Проверьте, что порты открыты:
```bash
sudo netstat -tlnp | grep :80
sudo netstat -tlnp | grep :443
```

## Полезные команды

```bash
# Перезапуск всех сервисов
docker-compose -f docker-compose.prod.yml restart

# Перезапуск конкретного сервиса
docker-compose -f docker-compose.prod.yml restart backend

# Остановка всех сервисов
docker-compose -f docker-compose.prod.yml down

# Остановка с удалением volumes (ОСТОРОЖНО!)
docker-compose -f docker-compose.prod.yml down -v

# Просмотр использования ресурсов
docker stats

# Очистка неиспользуемых ресурсов
docker system prune -a
```

## Контакты и поддержка

При возникновении проблем проверьте:
1. Логи приложения
2. Логи базы данных
3. Логи Nginx
4. Статус контейнеров
5. Переменные окружения
