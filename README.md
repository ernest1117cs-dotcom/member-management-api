# Member Management API

以 Spring Boot 與 JDBC 開發的會員管理 RESTful API 專案。

本專案實作會員資料的新增、查詢、修改、刪除、搜尋、分頁與狀態管理，
並加入資料驗證、全域例外處理、Swagger API 文件及 Docker 容器化。

## 技術棧

- Java
- Spring Boot
- Spring JDBC
- MySQL
- Maven
- Docker / Docker Compose
- Swagger / OpenAPI
- Jakarta Validation
- JUnit

## 系統架構

專案採用分層架構：

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL
```

- Controller：處理 HTTP Request / Response
- Service：處理商業邏輯
- Repository：使用 JDBC 操作資料庫
- DTO：接收與驗證 API 請求資料
- GlobalExceptionHandler：統一處理 API 錯誤

## API 功能

| Method | API | 功能 |
|---|---|---|
| GET | `/api/members` | 取得所有會員 |
| GET | `/api/members/{id}` | 取得單一會員 |
| POST | `/api/members` | 新增會員 |
| PUT | `/api/members/{id}` | 更新會員 |
| DELETE | `/api/members/{id}` | 刪除單一會員 |
| DELETE | `/api/members` | 刪除所有會員 |
| GET | `/api/members/search` | 依姓名搜尋會員 |
| GET | `/api/members/page` | 分頁查詢會員 |
| PATCH | `/api/members/{id}/status` | 修改會員狀態 |

## 會員狀態

會員具有以下兩種狀態：

- `ACTIVE`
- `INACTIVE`

建立會員時預設為 `ACTIVE`。

## 資料驗證與錯誤處理

使用 Jakarta Validation 驗證 API 輸入資料，例如：

- 姓名不可為空
- Email 格式驗證
- 年齡範圍驗證
- 分頁參數驗證

並透過 `GlobalExceptionHandler` 統一處理錯誤回應。

常見 HTTP Status：

- `200 OK`：請求成功
- `201 Created`：會員建立成功
- `204 No Content`：刪除成功
- `400 Bad Request`：輸入資料驗證失敗
- `404 Not Found`：會員不存在

## Swagger API 文件

啟動專案後，可透過 Swagger UI 查看及測試 API：

```text
http://localhost:8080/swagger-ui/index.html
```

## Docker 啟動方式

使用 Docker Compose 建立應用程式與 MySQL 環境：

```bash
docker compose up --build
```

停止服務：

```bash
docker compose down
```

## Database

主要會員資料包含：

| 欄位 | 說明 |
|---|---|
| id | 會員 ID |
| name | 姓名 |
| email | Email |
| age | 年齡 |
| status | ACTIVE / INACTIVE |
| created_at | 建立時間 |

## 專案特色

- RESTful API 設計
- Controller / Service / Repository 分層架構
- Spring JDBC 資料庫操作
- DTO 與資料驗證
- Global Exception Handling
- 搜尋與分頁功能
- 會員啟用 / 停用狀態管理
- Swagger / OpenAPI 文件
- Docker Compose 容器化