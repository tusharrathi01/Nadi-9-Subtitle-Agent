# Windows Quick Run

## 1. Install
- JDK 17+
- Maven 3.9+

## 2. Open terminal
```powershell
cd nadi9-subtitle-agent
```

## 3. Run tests
```powershell
mvn clean test
```

## 4. Start
```powershell
mvn spring-boot:run
```

## 5. Test API
Open another terminal:

```powershell
curl http://localhost:8080/api/v1/health
```

Then:

```powershell
curl -X POST http://localhost:8080/api/v1/process `
  -H "Content-Type: application/json" `
  --data-binary "@sample_run/request.json"
```

If `curl` quoting is inconvenient on Windows, import `sample_run/request.json` into Postman and send it to:

`POST http://localhost:8080/api/v1/process`

Default mode is deterministic mock mode, so no API key is needed.
