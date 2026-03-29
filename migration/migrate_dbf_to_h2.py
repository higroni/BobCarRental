#!/usr/bin/env python3
"""
DBF to H2 Database Migration Tool
Migrates data from legacy Clipper DBF files to modern H2 database via REST API
"""

import os
import sys
import json
import requests
from datetime import datetime
from pathlib import Path

# Add dbfread library check
try:
    from dbfread import DBF
except ImportError:
    print("ERROR: dbfread library not installed!")
    print("Install it with: pip install dbfread")
    sys.exit(1)

# Configuration
API_BASE_URL = "http://localhost:8080/api/v1"
ADMIN_USERNAME = "admin"
ADMIN_PASSWORD = "admin"
DBF_FILES_PATH = Path(__file__).parent.parent.parent  # Root directory with DBF files

# DBF file mappings
DBF_MAPPINGS = {
    'VEHTYPE.TRP': {
        'endpoint': '/vehicle-types',
        'entity': 'VehicleType',
        'field_mapping': {
            'TYPEID': 'typeId',
            'TYPEDESC': 'typeName',
            'TAGGED': 'tagged'
        }
    },
    'CLIENT.TRP': {
        'endpoint': '/clients',
        'entity': 'Client',
        'field_mapping': {
            'CLIENTID': 'clientId',
            'CLIENTNAME': 'clientName',
            'ADDRESS1': 'address1',
            'ADDRESS2': 'address2',
            'ADDRESS3': 'address3',
            'PLACE': 'place',
            'PINCODE': 'pin',
            'PHONE': 'phone1',
            'FAX': 'fax',
            'TAGGED': 'tagged'
        }
    },
    'ADDRESS.TRP': {
        'endpoint': '/addresses',
        'entity': 'Address',
        'field_mapping': {
            'CLIENTID': 'clientId',
            'NAME': 'name',
            'ADDRESS1': 'address1',
            'ADDRESS2': 'address2',
            'ADDRESS3': 'address3',
            'PLACE': 'place',
            'PHONE': 'phone',
            'TAGGED': 'tagged'
        }
    },
    'BOOKING.TRP': {
        'endpoint': '/bookings',
        'entity': 'Booking',
        'field_mapping': {
            'CLIENTID': 'clientId',
            'TYPEID': 'typeId',
            'BOOKDATE': 'bookDate',
            'TIME': 'time',
            'INFO1': 'info1',
            'INFO2': 'info2',
            'INFO3': 'info3',
            'INFO4': 'info4',
            'REF': 'ref',
            'TODAYDATE': 'todayDate',
            'TAGGED': 'tagged'
        }
    },
    'TRPSHEET.TRP': {
        'endpoint': '/trip-sheets',
        'entity': 'TripSheet',
        'field_mapping': {
            'TRPNUM': 'trpNum',
            'TRPDATE': 'trpDate',
            'CLIENTID': 'clientId',
            'CLIENTNAME': 'clientName',
            'TYPEID': 'typeId',
            'REGNUM': 'regNum',
            'DRIVER': 'driver',
            'STARTDT': 'startDate',
            'STARTTM': 'startTime',
            'STARTKM': 'startKm',
            'ENDDT': 'endDate',
            'ENDTM': 'endTime',
            'ENDKM': 'endKm',
            'DAYS': 'days',
            'TIME': 'time',
            'HIRING': 'hiring',
            'EXTRA': 'extra',
            'HALT': 'halt',
            'PERMIT': 'permit',
            'MISC': 'misc',
            'MINIMUM': 'minimum',
            'STATUS': 'status',
            'ISBILLED': 'isBilled',
            'BILLNUM': 'billNumber',
            'BILLDATE': 'billDate',
            'TAGGED': 'tagged'
        }
    },
    'BILLING.TRP': {
        'endpoint': '/billings',
        'entity': 'Billing',
        'field_mapping': {
            'BILLNUM': 'billNum',
            'BILLDATE': 'billDate',
            'CLIENTID': 'clientId',
            'BILLAMT': 'billAmount',
            'TAGGED': 'tagged'
        }
    }
}

class DBFMigrator:
    def __init__(self):
        self.token = None
        self.stats = {
            'total_files': 0,
            'total_records': 0,
            'successful': 0,
            'failed': 0,
            'skipped': 0
        }
        
    def login(self):
        """Authenticate and get JWT token"""
        print(f"\n[AUTH] Logging in as {ADMIN_USERNAME}...")
        try:
            response = requests.post(
                f"{API_BASE_URL}/auth/login",
                json={
                    "username": ADMIN_USERNAME,
                    "password": ADMIN_PASSWORD
                }
            )
            response.raise_for_status()
            data = response.json()
            self.token = data['data']['accessToken']
            print(f"[OK] Login successful! Token: {self.token[:50]}...")
            return True
        except Exception as e:
            print(f"[ERROR] Login failed: {e}")
            return False
    
    def get_headers(self):
        """Get HTTP headers with JWT token"""
        return {
            'Authorization': f'Bearer {self.token}',
            'Content-Type': 'application/json'
        }
    
    def convert_value(self, value, field_name):
        """Convert DBF value to appropriate JSON type"""
        if value is None:
            return None
        
        # Handle dates
        if isinstance(value, datetime):
            return value.strftime('%Y-%m-%d')
        
        # Handle booleans (Clipper uses 'T'/'F' or True/False)
        if isinstance(value, bool):
            return value
        if isinstance(value, str) and value.upper() in ['T', 'F', 'Y', 'N']:
            return value.upper() in ['T', 'Y']
        
        # Handle strings - trim whitespace
        if isinstance(value, str):
            return value.strip()
        
        # Handle numbers
        if isinstance(value, (int, float)):
            return value
        
        return str(value)
    
    def map_record(self, record, field_mapping):
        """Map DBF record to API request format"""
        mapped = {}
        for dbf_field, api_field in field_mapping.items():
            if dbf_field in record:
                value = self.convert_value(record[dbf_field], dbf_field)
                mapped[api_field] = value
        return mapped
    
    def migrate_file(self, dbf_filename, config):
        """Migrate single DBF file"""
        dbf_path = DBF_FILES_PATH / dbf_filename
        
        if not dbf_path.exists():
            print(f"[WARN] File not found: {dbf_path}")
            return

        print(f"\n[FOLDER] Migrating {dbf_filename} ({config['entity']})...")
        print(f"   Path: {dbf_path}")
        
        try:
            table = DBF(str(dbf_path), encoding='cp852')  # DOS Cyrillic encoding
            records = list(table)
            print(f"   Found {len(records)} records")
            
            self.stats['total_files'] += 1
            self.stats['total_records'] += len(records)
            
            for idx, record in enumerate(records, 1):
                try:
                    # Map DBF record to API format
                    data = self.map_record(record, config['field_mapping'])
                    
                    # Skip empty records
                    if not any(data.values()):
                        self.stats['skipped'] += 1
                        continue
                    
                    # Debug: Print first record
                    if idx == 1:
                        print(f"   DEBUG: First record data: {json.dumps(data, indent=2)}")
                    
                    # Send to API
                    response = requests.post(
                        f"{API_BASE_URL}{config['endpoint']}",
                        headers=self.get_headers(),
                        json=data
                    )
                    
                    if response.status_code in [200, 201]:
                        self.stats['successful'] += 1
                        if idx % 10 == 0:
                            print(f"   [OK] Migrated {idx}/{len(records)} records...")
                    else:
                        self.stats['failed'] += 1
                        try:
                            error_json = response.json()
                            print(f"   [ERROR] Failed record {idx}: {response.status_code}")
                            print(f"      Error: {json.dumps(error_json, indent=2)}")
                        except:
                            print(f"   [ERROR] Failed record {idx}: {response.status_code} - {response.text[:200]}")
                        
                except Exception as e:
                    self.stats['failed'] += 1
                    print(f"   [ERROR] Error processing record {idx}: {e}")

            print(f"   [OK] Completed {dbf_filename}")

        except Exception as e:
            print(f"   [ERROR] Error reading {dbf_filename}: {e}")
    
    def migrate_all(self):
        """Migrate all DBF files"""
        print("\n" + "="*60)
        print("[>>] DBF to H2 Migration Tool")
        print("="*60)
        
        # Login first
        if not self.login():
            return False
        
        # Migrate in order (dependencies first)
        migration_order = [
            'VEHTYPE.TRP',   # No dependencies
            'CLIENT.TRP',    # No dependencies
            'ADDRESS.TRP',   # Depends on CLIENT
            'BOOKING.TRP',   # Depends on CLIENT, VEHTYPE
            'TRPSHEET.TRP',  # Depends on CLIENT, VEHTYPE
            'BILLING.TRP'    # Depends on CLIENT
        ]
        
        for dbf_file in migration_order:
            if dbf_file in DBF_MAPPINGS:
                self.migrate_file(dbf_file, DBF_MAPPINGS[dbf_file])
        
        # Print summary
        print("\n" + "="*60)
        print("[STATS] Migration Summary")
        print("="*60)
        print(f"Files processed:    {self.stats['total_files']}")
        print(f"Total records:      {self.stats['total_records']}")
        print(f"[OK] Successful:    {self.stats['successful']}")
        print(f"[ERROR] Failed:     {self.stats['failed']}")
        print(f"[SKIP] Skipped:     {self.stats['skipped']}")
        print("="*60)
        
        return True

def main():
    """Main entry point"""
    migrator = DBFMigrator()
    success = migrator.migrate_all()
    sys.exit(0 if success else 1)

if __name__ == '__main__':
    main()

# Made with Bob
