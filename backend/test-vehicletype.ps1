# Test VehicleType Creation
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Testing VehicleType API" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080/api/v1"

# 1. Login
Write-Host "1. Logging in as admin..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "admin"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.data.accessToken
    Write-Host "✓ Login successful!" -ForegroundColor Green
    Write-Host "Token: $($token.Substring(0, 50))..." -ForegroundColor Gray
    Write-Host ""
} catch {
    Write-Host "✗ Login failed: $_" -ForegroundColor Red
    exit 1
}

# 2. Test VehicleType Creation
Write-Host "2. Creating new VehicleType (TEST)..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$vehicleTypeBody = @{
    typeId = "TEST"
    typeName = "TEST VEHICLE"
    tagged = $true
}

$vehicleTypeJson = $vehicleTypeBody | ConvertTo-Json

Write-Host "Request body:" -ForegroundColor Gray
Write-Host $vehicleTypeJson -ForegroundColor Gray
Write-Host ""

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/vehicle-types" -Method Post -Headers $headers -Body $vehicleTypeJson
    Write-Host "✓ VehicleType created successfully!" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Gray
    Write-Host ($response | ConvertTo-Json -Depth 10) -ForegroundColor Gray
} catch {
    Write-Host "✗ VehicleType creation failed!" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
    
    # Try to get detailed error message
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $reader.DiscardBufferedData()
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body:" -ForegroundColor Red
        Write-Host $responseBody -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Test completed" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan

# Made with Bob
