#!/usr/bin/env python3
"""
Test VehicleType API endpoint
"""
import requests
import json

BASE_URL = "http://localhost:8080/api/v1"

def test_vehicle_type():
    print("=" * 60)
    print("Testing VehicleType API")
    print("=" * 60)
    print()
    
    # 1. Login
    print("1. Logging in as admin...")
    login_data = {
        "username": "admin",
        "password": "admin"
    }
    
    try:
        response = requests.post(f"{BASE_URL}/auth/login", json=login_data)
        response.raise_for_status()
        token = response.json()["data"]["accessToken"]
        print("[OK] Login successful!")
        print(f"Token: {token[:50]}...")
        print()
    except Exception as e:
        print(f"[FAIL] Login failed: {e}")
        return
    
    # 2. Create VehicleType
    print("2. Creating new VehicleType (TEST)...")
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    vehicle_type_data = {
        "typeId": "TEST",
        "typeName": "TEST VEHICLE",
        "tagged": True
    }
    
    print("Request body:")
    print(json.dumps(vehicle_type_data, indent=2))
    print()
    
    try:
        response = requests.post(
            f"{BASE_URL}/vehicle-types",
            headers=headers,
            json=vehicle_type_data
        )
        
        print(f"Status Code: {response.status_code}")
        print()
        
        if response.status_code == 201:
            print("[OK] VehicleType created successfully!")
            print("Response:")
            print(json.dumps(response.json(), indent=2))
        else:
            print("[FAIL] VehicleType creation failed!")
            print("Response:")
            print(json.dumps(response.json(), indent=2))
            
    except Exception as e:
        print(f"[FAIL] Request failed: {e}")
        if hasattr(e, 'response') and e.response is not None:
            print("Response:")
            print(e.response.text)
    
    print()
    print("=" * 60)
    print("Test completed")
    print("=" * 60)

if __name__ == "__main__":
    test_vehicle_type()

# Made with Bob
