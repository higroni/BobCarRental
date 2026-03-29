# API Testing Guide

## Quick Test Scripts

### test-auth.bat
Basic authentication test - tests login, wrong password, and unauthorized access.

**Usage:**
```bash
cd bobcarrental/backend
.\test-auth.bat
```

**What it tests:**
1. Health check endpoint (`/actuator/health`)
2. Successful login with admin/admin
3. Failed login with wrong password
4. Protected endpoint without authentication (should fail)

### test-api-full.bat (curl version)
Comprehensive API test using curl - tests all major endpoints with JWT authentication.

**Usage:**
```bash
cd bobcarrental/backend
.\test-api-full.bat
```

**What it tests:**
1. Health check
2. Login and token extraction
3. Get all clients (with token)
4. Create new client
5. Get vehicle types
6. Get bookings
7. Get trip sheets
8. Get standard fares
9. Refresh token
10. Logout

### run-full-test.bat (PowerShell version)
Advanced API test using PowerShell with better JSON parsing and colored output.

**Usage:**
```bash
cd bobcarrental/backend
.\run-full-test.bat
```

**Features:**
- Automatic token extraction and reuse
- Colored console output
- Better error handling
- JSON response parsing
- Tests same endpoints as curl version

### Expected Results

#### 1. Health Check
```json
{"status":"UP"}
```
HTTP Status: 200

#### 2. Successful Login
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "tokenType": "Bearer",
    "expiresIn": 86400000
  }
}
```
HTTP Status: 200

#### 3. Failed Login
```json
{
  "success": false,
  "message": "Authentication failed",
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "Bad credentials"
  }
}
```
HTTP Status: 401

#### 4. Protected Endpoint Without Token
```json
{
  "success": false,
  "message": "Authentication required",
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Full authentication is required to access this resource"
  }
}
```
HTTP Status: 401

## Manual Testing with curl

### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"admin\"}"
```

### Access Protected Endpoint
```bash
# First, get the token from login response
TOKEN="your_access_token_here"

# Then use it to access protected endpoints
curl -X GET http://localhost:8080/api/v1/clients \
  -H "Authorization: Bearer $TOKEN"
```

### Refresh Token
```bash
REFRESH_TOKEN="your_refresh_token_here"

curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Refresh-Token: $REFRESH_TOKEN"
```

### Logout
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer $TOKEN"
```

## Testing with Postman

1. Import the collection from `postman/BobCarRental.postman_collection.json` (if available)
2. Set environment variable `baseUrl` to `http://localhost:8080`
3. Run the "Login" request to get tokens
4. Tokens will be automatically saved to environment variables
5. Other requests will use the saved token automatically

## Default Test Users

| Username | Password | Role  |
|----------|----------|-------|
| admin    | admin    | ADMIN |
| user     | user     | USER  |

## Troubleshooting

### "curl is not installed"
- Install Git for Windows (includes curl)
- Or download curl from https://curl.se/windows/

### "Connection refused"
- Make sure the application is running: `mvn spring-boot:run`
- Check if port 8080 is available

### "401 Unauthorized" on login
- Check if the password hash in database matches
- Verify seed data was loaded correctly
- Check application logs for authentication errors

### "Database locked" error
- Stop all running instances of the application
- Delete `data/` folder and restart

## API Documentation

Full API documentation is available at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Next Steps

After successful authentication testing:
1. Test CRUD operations for each entity
2. Test fare calculation endpoints
3. Test report generation
4. Test file upload (vehicle images)
5. Run integration tests: `mvn test`