#!/usr/bin/env python3
"""
FARES.TXT and HEADER.TXT Parser
Parses legacy fare configuration and header templates and migrates to H2 database
"""

import os
import sys
import json
import requests
import re
from pathlib import Path
from typing import Dict, List, Tuple

# Configuration
API_BASE_URL = "http://localhost:8080/api/v1"
ADMIN_USERNAME = "admin"
ADMIN_PASSWORD = "admin"
ROOT_PATH = Path(__file__).parent.parent.parent

class FaresHeaderParser:
    def __init__(self):
        self.token = None
        self.stats = {
            'fares_parsed': 0,
            'fares_created': 0,
            'headers_parsed': 0,
            'headers_created': 0,
            'errors': 0
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
            print(f"[OK] Login successful!")
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
    
    def parse_fares_txt(self, filepath: Path) -> List[Dict]:
        """Parse FARES.TXT file"""
        print(f"\n[FOLDER] Parsing {filepath.name}...")
        
        if not filepath.exists():
            print(f"[WARN]  File not found: {filepath}")
            return []
        
        fares = []
        current_section = None
        
        with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
            for line_num, line in enumerate(f, 1):
                line = line.strip()
                
                # Skip empty lines and comments
                if not line or line.startswith('{'):
                    continue
                
                # Detect section headers
                if line.startswith('[') and line.endswith(']'):
                    current_section = line[1:-1].upper()
                    print(f"   Section: {current_section}")
                    continue
                
                # Parse fare data based on section
                try:
                    if current_section == 'LOCAL':
                        fare = self.parse_local_fare(line, line_num)
                    elif current_section == 'EXTRA':
                        fare = self.parse_extra_fare(line, line_num)
                    elif current_section == 'GENERAL':
                        fare = self.parse_general_fare(line, line_num)
                    elif current_section == 'OUTSTATION':
                        fare = self.parse_outstation_fare(line, line_num)
                    else:
                        continue
                    
                    if fare:
                        fares.append(fare)
                        self.stats['fares_parsed'] += 1
                        
                except Exception as e:
                    print(f"   [WARN]  Error parsing line {line_num}: {line} - {e}")
                    self.stats['errors'] += 1
        
        print(f"   [OK] Parsed {len(fares)} fare records")
        return fares
    
    def parse_local_fare(self, line: str, line_num: int) -> Dict:
        """Parse LOCAL section: CARCODE-HRS-RATE-FREEKMS-SPLITFARE-SPLITFUELKMSRATE"""
        parts = line.split('-')
        if len(parts) != 6:
            return None
        
        return {
            'vehicleCode': parts[0].strip(),
            'fareType': 'LOCAL',
            'hours': int(parts[1].strip()),
            'rate': float(parts[2].strip()),
            'freeKm': int(parts[3].strip()),
            'splitFare': float(parts[4].strip()),
            'splitFuelKm': int(parts[5].strip()),
            'description': f'Local fare for {parts[0]} - {parts[1]} hours',
            'isActive': True
        }
    
    def parse_extra_fare(self, line: str, line_num: int) -> Dict:
        """Parse EXTRA section: CARCODE-HRS-RATE-FREEKM-HIRERATE-HIREKM"""
        parts = line.split('-')
        if len(parts) != 6:
            return None
        
        return {
            'vehicleCode': parts[0].strip(),
            'fareType': 'EXTRA',
            'hours': int(parts[1].strip()),
            'rate': float(parts[2].strip()),
            'freeKm': int(parts[3].strip()),
            'hireRate': float(parts[4].strip()),
            'hireKm': int(parts[5].strip()),
            'description': f'Extra fare for {parts[0]} - {parts[1]} hours',
            'isActive': True
        }
    
    def parse_general_fare(self, line: str, line_num: int) -> Dict:
        """Parse GENERAL section: CARECODE-FUELRATE-EXCESSRATE"""
        parts = line.split('-')
        if len(parts) != 3:
            return None
        
        return {
            'vehicleCode': parts[0].strip(),
            'fareType': 'GENERAL',
            'fuelRatePerKm': float(parts[1].strip()),
            'ratePerExcessKm': float(parts[2].strip()),
            'description': f'General fare for {parts[0]}',
            'isActive': True
        }
    
    def parse_outstation_fare(self, line: str, line_num: int) -> Dict:
        """Parse OUTSTATION section: CARECODE-RATEPERKM-MINKMPERDAY-NIGHTHALT"""
        parts = line.split('-')
        if len(parts) != 4:
            return None
        
        return {
            'vehicleCode': parts[0].strip(),
            'fareType': 'OUTSTATION',
            'ratePerKm': float(parts[1].strip()),
            'minKmPerDay': int(parts[2].strip()),
            'nightHalt': float(parts[3].strip()),
            'description': f'Outstation fare for {parts[0]}',
            'isActive': True
        }
    
    def parse_header_txt(self, filepath: Path) -> List[Dict]:
        """Parse HEADER.TXT file"""
        print(f"\n[FOLDER] Parsing {filepath.name}...")
        
        if not filepath.exists():
            print(f"[WARN]  File not found: {filepath}")
            return []
        
        headers = []
        
        with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
            content = f.read()
        
        # Clean up special characters
        content = content.replace('�', '').strip()
        lines = [line.strip() for line in content.split('\n') if line.strip()]
        
        # Create header template from content
        if lines:
            # Combine all lines into a single template content
            template_content = '\n'.join(lines)
            
            header = {
                'templateName': 'Default Invoice Header',
                'templateContent': template_content,
                'description': 'Legacy header template from HEADER.TXT',
                'active': True,
                'isDefault': True,
                'templateType': 'INVOICE'
            }
            headers.append(header)
            self.stats['headers_parsed'] += 1
            print(f"   [OK] Parsed header template")
        
        return headers
    
    def create_fare(self, fare: Dict) -> bool:
        """Create fare via API"""
        try:
            response = requests.post(
                f"{API_BASE_URL}/standard-fares",
                headers=self.get_headers(),
                json=fare
            )
            
            if response.status_code in [200, 201]:
                self.stats['fares_created'] += 1
                return True
            else:
                print(f"   [ERROR] Failed to create fare: {response.status_code} - {response.text[:100]}")
                self.stats['errors'] += 1
                return False
                
        except Exception as e:
            print(f"   [ERROR] Error creating fare: {e}")
            self.stats['errors'] += 1
            return False
    
    def create_header(self, header: Dict) -> bool:
        """Create header template via API"""
        try:
            response = requests.post(
                f"{API_BASE_URL}/headertemplates",
                headers=self.get_headers(),
                json=header
            )
            
            if response.status_code in [200, 201]:
                self.stats['headers_created'] += 1
                return True
            else:
                print(f"   [ERROR] Failed to create header: {response.status_code} - {response.text[:100]}")
                self.stats['errors'] += 1
                return False
                
        except Exception as e:
            print(f"   [ERROR] Error creating header: {e}")
            self.stats['errors'] += 1
            return False
    
    def process_all(self):
        """Process both FARES.TXT and HEADER.TXT"""
        print("\n" + "="*60)
        print("[>>] FARES.TXT and HEADER.TXT Parser")
        print("="*60)
        
        # Login first
        if not self.login():
            return False
        
        # Parse and migrate FARES.TXT
        fares_path = ROOT_PATH / 'FARES.TXT'
        fares = self.parse_fares_txt(fares_path)
        
        if fares:
            print(f"\n[UPLOAD] Creating {len(fares)} fare records...")
            for idx, fare in enumerate(fares, 1):
                if self.create_fare(fare):
                    if idx % 5 == 0:
                        print(f"   [OK] Created {idx}/{len(fares)} fares...")
        
        # Parse and migrate HEADER.TXT
        header_path = ROOT_PATH / 'HEADER.TXT'
        headers = self.parse_header_txt(header_path)
        
        if headers:
            print(f"\n[UPLOAD] Creating {len(headers)} header template(s)...")
            for header in headers:
                self.create_header(header)
        
        # Print summary
        print("\n" + "="*60)
        print("[STATS] Processing Summary")
        print("="*60)
        print(f"Fares parsed:       {self.stats['fares_parsed']}")
        print(f"[OK] Fares created:   {self.stats['fares_created']}")
        print(f"Headers parsed:     {self.stats['headers_parsed']}")
        print(f"[OK] Headers created: {self.stats['headers_created']}")
        print(f"[ERROR] Errors:          {self.stats['errors']}")
        print("="*60)
        
        return True

def main():
    """Main entry point"""
    parser = FaresHeaderParser()
    success = parser.process_all()
    sys.exit(0 if success else 1)

if __name__ == '__main__':
    main()

# Made with Bob
