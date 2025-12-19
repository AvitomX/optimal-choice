# Быстрый старт развертывания

## Минимальные шаги для развертывания на VPS

### 1. Подготовка сервера
```bash
# Установка Docker и Docker Compose
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 2. Клонирование и настройка
```bash
git clone <ваш-репозиторий> /opt/optimal-choice
cd /opt/optimal-choice

# Создание .env файла
cp .env.production.example .env
nano .env  # Заполните пароли и настройки
```

### 3. Запуск
```bash
docker-compose -f docker-compose.prod.yml build
docker-compose -f docker-compose.prod.yml up -d
```

### 4. Проверка
```bash
# Проверка статуса
docker-compose -f docker-compose.prod.yml ps

# Просмотр логов
docker-compose -f docker-compose.prod.yml logs -f
```

### 5. Настройка домена (опционально)
```bash
# Установка Nginx и Certbot
sudo apt install nginx certbot python3-certbot-nginx -y

# Копирование конфигурации
sudo cp nginx/optimal-choice.conf /etc/nginx/sites-available/
sudo ln -s /etc/nginx/sites-available/optimal-choice.conf /etc/nginx/sites-enabled/

# Редактирование домена в конфиге
sudo nano /etc/nginx/sites-available/optimal-choice.conf

# Получение SSL
sudo certbot --nginx -d yourdomain.com
```

## Важные переменные в .env

Обязательно измените:
- `POSTGRES_PASSWORD` - пароль для PostgreSQL
- `DB_APP_PASSWORD` - пароль для пользователя приложения
- `VITE_API_URL` - URL API (если используете домен)

## Доступ к приложению

- Frontend: http://your-server-ip или https://yourdomain.com
- Backend API: http://your-server-ip:8090 или https://yourdomain.com/api
- Health check: http://your-server-ip:8090/actuator/health

## Подробная документация

См. [DEPLOYMENT.md](./DEPLOYMENT.md) для полной документации.
