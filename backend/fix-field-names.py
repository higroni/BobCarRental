#!/usr/bin/env python3
"""
Script to fix incorrect field names in @PageableDefault annotations
"""

import re
import os

# Define all fixes as (file_path, old_pattern, new_value)
fixes = [
    # ClientController
    ('src/main/java/com/bobcarrental/controller/ClientController.java',
     r'sort = "name"',
     'sort = "clientName"'),
    
    # TripSheetController
    ('src/main/java/com/bobcarrental/controller/TripSheetController.java',
     r'sort = "startDate"',
     'sort = "trpDate"'),
    
    # StandardFareController
    ('src/main/java/com/bobcarrental/controller/StandardFareController.java',
     r'sort = "vehicleType\.name"',
     'sort = "vehicleType.typeDesc"'),
    
    # VehicleTypeController
    ('src/main/java/com/bobcarrental/controller/VehicleTypeController.java',
     r'sort = "typeName"',
     'sort = "typeDesc"'),
    
    # HeaderTemplateController (will fix both occurrences)
    ('src/main/java/com/bobcarrental/controller/HeaderTemplateController.java',
     r'sort = "name"',
     'sort = "templateName"'),
    
    # AddressController (will fix both occurrences)
    ('src/main/java/com/bobcarrental/controller/AddressController.java',
     r'sort = "name"',
     'sort = "clientId"'),
]

def fix_file(file_path, pattern, replacement):
    """Fix a single file"""
    if not os.path.exists(file_path):
        print(f"[X] File not found: {file_path}")
        return False
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Count matches
    matches = len(re.findall(pattern, content))
    if matches == 0:
        print(f"[!] No matches found in {file_path}")
        return False
    
    # Replace
    new_content = re.sub(pattern, replacement, content)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(new_content)
    
    print(f"[OK] Fixed {matches} occurrence(s) in {file_path}")
    return True

def main():
    print("=" * 70)
    print("Fixing @PageableDefault field names")
    print("=" * 70)
    print()
    
    fixed_count = 0
    for file_path, pattern, replacement in fixes:
        if fix_file(file_path, pattern, replacement):
            fixed_count += 1
        print()
    
    print("=" * 70)
    print(f"[OK] Fixed {fixed_count} out of {len(fixes)} files")
    print("=" * 70)

if __name__ == '__main__':
    main()

# Made with Bob
