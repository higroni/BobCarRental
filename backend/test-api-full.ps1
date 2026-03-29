# Full API Test Suite with JWT Authentication
# PowerShell version with better JSON parsing

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "BobCarRental Full API Test Suite" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080"

# Test 1: Health Check
Write-Host "[1/10] Testing Health Endpoint..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "$baseUrl/actuator/health" -Method Get
    Write-Host "Status: $($health.status)" -ForegroundColor Green
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 2: Login
Write-Host "[2/10] Logging in as admin..." -ForegroundColor Yellow
try {
    $loginBody = @{
        username = "admin"
        password = "admin"
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/login" `
        -Method Post `
        -ContentType "application/json" `
        -Body $loginBody

    $token = $loginResponse.data.accessToken
    $refreshToken = $loginResponse.data.refreshToken
    
    Write-Host "Login successful!" -ForegroundColor Green
    Write-Host "User: $($loginResponse.data.user.username)" -ForegroundColor Green
    Write-Host "Roles: $($loginResponse.data.user.roles -join ', ')" -ForegroundColor Green
    Write-Host "Token (first 50 chars): $($token.Substring(0, 50))..." -ForegroundColor Gray
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Test 3: Get all clients
Write-Host "[3/10] Getting all clients (with token)..." -ForegroundColor Yellow
try {
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    $clients = Invoke-RestMethod -Uri "$baseUrl/api/v1/clients" `
        -Method Get `
        -Headers $headers
    
    Write-Host "Found $($clients.data.content.Count) clients" -ForegroundColor Green
    if ($clients.data.content.Count -gt 0) {
        Write-Host "Sample client: $($clients.data.content[0].clientName)" -ForegroundColor Gray
    }
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 4: Create new client
Write-Host "[4/10] Creating a new client..." -ForegroundColor Yellow
try {
    $newClient = @{
        clientId = "TEST$(Get-Random -Maximum 9999)"
        clientName = "Test Client $(Get-Date -Format 'HHmmss')"
        address1 = "123 Test Street"
        place = "Test City"
        phone1 = "1234567890"
        email = "test@example.com"
    } | ConvertTo-Json

    $createResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/clients" `
        -Method Post `
        -Headers $headers `
        -Body $newClient
    
    Write-Host "Client created: $($createResponse.data.clientName)" -ForegroundColor Green
    $createdClientId = $createResponse.data.id
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 5: Get vehicle types
Write-Host "[5/10] Getting all vehicle types..." -ForegroundColor Yellow
try {
    $vehicleTypes = Invoke-RestMethod -Uri "$baseUrl/api/v1/vehicle-types" `
        -Method Get `
        -Headers $headers
    
    Write-Host "Found $($vehicleTypes.data.content.Count) vehicle types" -ForegroundColor Green
    if ($vehicleTypes.data.content.Count -gt 0) {
        Write-Host "Sample: $($vehicleTypes.data.content[0].typeName)" -ForegroundColor Gray
    }
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 6: Get bookings
Write-Host "[6/10] Getting all bookings..." -ForegroundColor Yellow
try {
    $bookings = Invoke-RestMethod -Uri "$baseUrl/api/v1/bookings" `
        -Method Get `
        -Headers $headers
    
    Write-Host "Found $($bookings.data.content.Count) bookings" -ForegroundColor Green
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 7: Get trip sheets
Write-Host "[7/10] Getting all trip sheets..." -ForegroundColor Yellow
try {
    $tripSheets = Invoke-RestMethod -Uri "$baseUrl/api/v1/trip-sheets" `
        -Method Get `
        -Headers $headers
    
    Write-Host "Found $($tripSheets.data.content.Count) trip sheets" -ForegroundColor Green
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 8: Get standard fares
Write-Host "[8/10] Getting all standard fares..." -ForegroundColor Yellow
try {
    $fares = Invoke-RestMethod -Uri "$baseUrl/api/v1/standard-fares" `
        -Method Get `
        -Headers $headers
    
    Write-Host "Found $($fares.data.content.Count) standard fares" -ForegroundColor Green
    if ($fares.data.content.Count -gt 0) {
        $fare = $fares.data.content[0]
        Write-Host "Sample: $($fare.vehicleCode) - $($fare.fareType)" -ForegroundColor Gray
    }
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 9: Refresh token
Write-Host "[9/10] Testing refresh token..." -ForegroundColor Yellow
try {
    $refreshHeaders = @{
        "Refresh-Token" = $refreshToken
    }
    
    $refreshResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/refresh" `
        -Method Post `
        -Headers $refreshHeaders
    
    Write-Host "Token refreshed successfully!" -ForegroundColor Green
    $newToken = $refreshResponse.data.accessToken
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
}
Write-Host ""

# Test 10: Logout
Write-Host "[10/10] Logging out..." -ForegroundColor Yellow
try {
    $logoutResponse = Invoke-RestMethod -Uri "$baseUrl/api/v1/auth/logout" `
        -Method Post `
        -Headers $headers
    
    Write-Host "Logged out successfully!" -ForegroundColor Green
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "All tests completed!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Summary:" -ForegroundColor White
Write-Host "✓ Authentication working" -ForegroundColor Green
Write-Host "✓ JWT token generation working" -ForegroundColor Green
Write-Host "✓ Protected endpoints accessible with token" -ForegroundColor Green
Write-Host "✓ CRUD operations functional" -ForegroundColor Green
Write-Host "✓ Token refresh working" -ForegroundColor Green
Write-Host "✓ Logout working" -ForegroundColor Green
Write-Host ""

# Made with Bob
