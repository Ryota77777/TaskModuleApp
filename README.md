# TaskModuleApp

**TaskModuleApp** — это веб-приложение на базе Spring Boot для управления задачами сотрудников внутри организации. Система поддерживает регистрацию пользователей, авторизацию, создание и отслеживание задач, а также административное управление.

---

## 📦 Стек технологий

- **Java 17+**
- **Spring Boot**
- Spring Security
- Spring MVC
- Spring Data JPA (Hibernate)
- Thymeleaf
- PostgreSQL (или H2)
- HTML/CSS
- Bootstrap (опционально)
- Docker (опционально)

---

## 🚀 Основной функционал

### 👤 Аутентификация и авторизация
- Регистрация и вход пользователя
- Авторизация с помощью Spring Security
- Роли: обычный пользователь, администратор
- Автоматический логин после регистрации
- Безопасный logout

### 🧑‍💼 Личный кабинет
- Просмотр и редактирование профиля
- Загрузка аватарки
- Контактные данные, должность, отдел

### ✅ Управление задачами
- Создание задач (титул, описание, срок, приоритет, тип, файл)
- Просмотр своих задач
- Поиск задач по названию и статусу
- Обновление задач
- Завершение задач
- Удаление задач
- Загрузка файлов к задачам

### 🛠 Админ-панель
- Доступна администраторам
- Просмотр всех пользователей
- Управление задачами сотрудников

---

## Скриншоты

### Главная страница
![Скриншот главной страницы](https://github.com/Ryota77777/TaskModuleApp/blob/main/uploads/main5.jpg?raw=true)

### Профиль
![Скриншот профиля](https://github.com/Ryota77777/TaskModuleApp/blob/main/uploads/profile5.jpg?raw=true)

### Заявки
![Скриншот заявок](https://github.com/Ryota77777/TaskModuleApp/blob/main/uploads/myrequests5.jpg?raw=true)

### Управление
![Скриншот управления заявками](https://github.com/Ryota77777/TaskModuleApp/blob/main/uploads/manage5.jpg?raw=true)

### Аналитика
![Скриншот аналитики](https://github.com/Ryota77777/TaskModuleApp/blob/main/uploads/analytics5.jpg?raw=true)

### Главная страница админа
![Скриншот главной страницы админа](https://github.com/Ryota77777/TaskModuleApp/blob/main/uploads/admin5.jpg?raw=true)

### Заявки админа
![Скриншот заявок админа](https://github.com/Ryota77777/TaskModuleApp/blob/main/uploads/requests.jpg?raw=true)


## ⚙️ Установка и запуск

### 1. Клонировать репозиторий

```bash
git clone https://github.com/Ryota77777/TaskModuleApp.git
cd TaskModuleApp
```

### 2. Настроить базу данных

По умолчанию используется PostgreSQL. Убедитесь, что в `application.properties` указаны корректные настройки:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmodule
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
app.upload.dir=uploads
```

### 3. Собрать и запустить

```bash
./mvnw spring-boot:run
```

или

```bash
mvn clean install
java -jar target/TaskModuleApp-0.0.1-SNAPSHOT.jar
```

---

## 🖥 Примеры страниц

- `/register` — форма регистрации
- `/login` — вход в систему
- `/home` — домашняя страница после логина
- `/profile` — профиль пользователя
- `/requests` — список задач пользователя
- `/manage` — управление задачами (для создателя)
- `/admin` — панель администратора

---

## 🔐 Безопасность

- Хранение паролей с использованием BCrypt
- Контроль доступа по ролям
- Защита пользовательских данных

---

## 📎 Загрузка файлов

Файлы, прикрепленные к задачам или профилю, сохраняются в директорию `uploads/` и доступны по ссылкам внутри приложения.

---

## 📌 Возможные улучшения

- Email-уведомления
- REST API для мобильного приложения
- Асинхронная обработка задач
- Контроль статусов (в процессе, завершено, отменено)
- Логирование действий пользователей
- CI/CD-пайплайны

---


## 📄 Лицензия

MIT License. Используйте свободно для личных или коммерческих целей.
