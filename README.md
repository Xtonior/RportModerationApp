# Требования
- python3
- python3-redis
- docker

# Запуск

## Redis для Service-2
```
cd redis
docker compose up -d
```

## Генерация данных
```
cd data-generator
python3 gen_data.py
```
Дополнительно скопируйте data.csv в папку jmeter/ 

## Redis для Service-1 (идемпотентность)
```
cd mod-redis-local
docker compose up -d
```

## Kafka
```
cd kafka
docker compose up -d
```

## Сервисы
```
cd redisapp
./mvnw spring-boot:run
```

```
cd moderationapp
./mvnw spring-boot:run
```

## Профиль Jmeter для проверки нагрузки
```
jmeter/Test Plan.jmx
```

в Jmeter указать путь до jmeter/data.csv

![arch.png](https://github.com/Xtonior/RportModerationApp/blob/main/img/arch.png?raw=true)