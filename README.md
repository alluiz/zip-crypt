# zip-crypt

Spring Boot API that receives plain text, creates a file at runtime, creates an encrypted ZIP, and returns the ZIP binary so clients can download it directly.

## Security and compatibility approach

- Uses **zip4j** for encrypted ZIP generation.
- ZIP archive is **always encrypted**.
- Password is generated with `SecureRandom`.
- Password is **never returned** in API responses.
- Password is sent only to `NotificationService` abstraction.
- Includes a test-friendly `ConsoleNotificationService` implementation that prints the password in console only.
- Supports compatibility profile selection by `targetOs` and `targetTool`:
    - `NATIVE`: uses ZipCrypto (`ZIP_STANDARD`) for broad native unzip compatibility.
    - `SEVEN_ZIP`, `WINZIP`, `GENERIC`: uses AES-256.

## API

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

## Run

```bash
mvn spring-boot:run
```

## Test

```bash
mvn test
```
