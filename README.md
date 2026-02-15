# zip-crypt

Spring Boot API that receives plain text, creates a file at runtime, creates an encrypted ZIP, and returns the ZIP binary so clients can download it directly.

## Security and compatibility approach

- Uses **zip4j** for encrypted ZIP generation.
- ZIP archive is **always encrypted**.
- Password is generated with `SecureRandom`.
- Password is sent to a `NotificationService` abstraction.
- Password is also cached in-memory for up to **1 day** keyed by generated zip filename.
- Supports compatibility profile selection by `targetOs` and `targetTool`:
  - `NATIVE`: uses ZipCrypto (`ZIP_STANDARD`) for broad native unzip compatibility.
  - `SEVEN_ZIP`, `WINZIP`, `GENERIC`: uses AES-256.

## API

### Create encrypted archive

`POST /api/v1/archives`

Request body:

```json
{
  "plainText": "my secret text",
  "sourceFileName": "note.txt",
  "targetOs": "WINDOWS",
  "targetTool": "NATIVE"
}
```

Response:
- `201 Created`
- `Content-Type: application/zip`
- `Content-Disposition: attachment; filename="<archive-id>.zip"`
- `X-Archive-Id: <archive-id>`
- Body: ZIP binary content

### Retrieve cached password (admin only)

`GET /api/v1/archives/{fileName}/password`

- Requires HTTP Basic credentials with `ADMIN` role.
- Returns `404` if password is missing/expired (TTL 1 day).

Example response:

```json
{
  "fileName": "<archive-id>.zip",
  "password": "generated-password"
}
```

## Admin credentials configuration

Configured through `application.yml`:

```yaml
zip:
  security:
    admin-username: admin
    admin-password: admin123
```

## Run

```bash
mvn spring-boot:run
```

## Test

```bash
mvn test
```
