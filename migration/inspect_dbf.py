#!/usr/bin/env python3
"""
DBF File Inspector
Shows the structure and sample data from DBF files
"""

import sys
from pathlib import Path

try:
    from dbfread import DBF
except ImportError:
    print("ERROR: dbfread library not installed!")
    print("Install it with: pip install dbfread")
    sys.exit(1)

def inspect_dbf(filepath):
    """Inspect DBF file structure and show sample data"""
    print(f"\n{'='*60}")
    print(f"File: {filepath.name}")
    print(f"{'='*60}")
    
    try:
        table = DBF(str(filepath), encoding='cp852')
        
        # Show field names
        print(f"\nFields ({len(table.fields)}):")
        for field in table.fields:
            print(f"  - {field.name:20s} ({field.type}, {field.length})")
        
        # Show first 3 records
        records = list(table)
        print(f"\nTotal Records: {len(records)}")
        
        if records:
            print(f"\nFirst {min(3, len(records))} record(s):")
            for idx, record in enumerate(records[:3], 1):
                print(f"\n  Record {idx}:")
                for field_name, value in record.items():
                    if value:  # Only show non-empty values
                        print(f"    {field_name:20s} = {value}")
        
    except Exception as e:
        print(f"ERROR: {e}")

def main():
    """Main entry point"""
    root_path = Path(__file__).parent.parent.parent
    
    dbf_files = [
        'VEHTYPE.TRP',
        'CLIENT.TRP',
        'ADDRESS.TRP',
        'BOOKING.TRP',
        'TRPSHEET.TRP',
        'BILLING.TRP'
    ]
    
    for dbf_file in dbf_files:
        filepath = root_path / dbf_file
        if filepath.exists():
            inspect_dbf(filepath)
        else:
            print(f"\n⚠️  File not found: {filepath}")

if __name__ == '__main__':
    main()

# Made with Bob
