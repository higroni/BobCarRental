# Test API Script for BobCarRental
# This script tests the authentication and basic CRUD operations

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "BobCarRental API Test Script" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080/api/v1"

# Test 1: Login
Write-Host "Test 1: Login with admin/admin" -ForegroundColor Yellow
try {
    $loginBody = @{
        username = "admin"
        password = "admin"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$baseUrl/auth/login" `
        -Method POST `
        -Headers @{"Content-Type"="application/json"} `
        -Body $loginBody `
        -ErrorAction Stop

    $token = $response.data.accessToken
    Write-Host "✓ Login successful!" -ForegroundColor Green
    Write-Host "  Token: $($token.Substring(0, 20))..." -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "  Response: $($_.ErrorDetails.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please restart the application after enabling Liquibase:" -ForegroundColor Yellow
    Write-Host "  1. Stop the current application (Ctrl+C in the terminal)" -ForegroundColor Yellow
    Write-Host "  2. Run: cd bobcarrental/backend; mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

# Test 2: Get current user info
Write-Host "Test 2: Get current user info" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/me" `
        -Method GET `
        -Headers @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        } `
        -ErrorAction Stop

    Write-Host "✓ User info retrieved!" -ForegroundColor Green
    Write-Host "  Username: $($response.data.username)" -ForegroundColor Gray
    Write-Host "  Email: $($response.data.email)" -ForegroundColor Gray
    Write-Host "  Roles: $($response.data.roles -join ', ')" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Failed to get user info: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
}

# Test 3: Get all clients (paginated)
Write-Host "Test 3: Get all clients (first page)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/clients?page=0&size=10" `
        -Method GET `
        -Headers @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        } `
        -ErrorAction Stop

    Write-Host "✓ Clients retrieved!" -ForegroundColor Green
    Write-Host "  Total elements: $($response.data.totalElements)" -ForegroundColor Gray
    Write-Host "  Total pages: $($response.data.totalPages)" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Failed to get clients: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
}

# Test 4: Get all vehicle types
Write-Host "Test 4: Get all vehicle types" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/vehicle-types?page=0&size=10" `
        -Method GET `
        -Headers @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        } `
        -ErrorAction Stop

    Write-Host "✓ Vehicle types retrieved!" -ForegroundColor Green
    Write-Host "  Total elements: $($response.data.totalElements)" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Failed to get vehicle types: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
}

# Test 5: Health check
Write-Host "Test 5: Health check" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" `
        -Method GET `
        -ErrorAction Stop

    Write-Host "✓ Health check passed!" -ForegroundColor Green
    Write-Host "  Status: $($response.status)" -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Health check failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
}

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "API Testing Complete!" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan

# Made with Bob
