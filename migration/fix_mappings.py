#!/usr/bin/env python3
"""
Auto-generate correct field mappings based on actual DBF structure
"""

# Correct mappings based on inspect_dbf.py output
CORRECT_MAPPINGS = {
    'VEHTYPE.TRP': {
        'TYPEID': 'typeId',
        'TYPEDESC': 'typeName',
        'TAGGED': 'tagged'
    },
    'CLIENT.TRP': {
        'CLIENTID': 'clientId',
        'CLIENTNAME': 'clientName',
        'ADDRESS1': 'address1',
        'ADDRESS2': 'address2',
        'ADDRESS3': 'address3',
        'PLACE': 'place',
        'CITY': 'city',  # Note: DBF has CITY but API might expect place
        'PINCODE': 'pin',
        'PHONE': 'phone1',
        'FAX': 'fax',
        'TAGGED': 'tagged'
        # Note: FARE and ISSPLIT fields exist in DBF but not in API
    },
    'ADDRESS.TRP': {
        'CLIENTID': 'clientId',
        'NAME': 'contact',  # NAME in DBF → contact in API
        'ADDRESS1': 'address1',
        'ADDRESS2': 'address2',
        'ADDRESS3': 'address3',
        'PLACE': 'place',
        'PHONE': 'phone',
        'TAGGED': 'tagged'
    },
    'BOOKING.TRP': {
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
    },
    'TRPSHEET.TRP': {
        'TRPNUM': 'trpNum',
        'TRPDATE': 'trpDate',
        'CLIENTID': 'clientId',
        'CLIENTNAME': 'clientName',
        'TYPEID': 'typeId',
        'REGNUM': 'regNum',
        'DRIVER': 'driver',
        'STARTDT': 'startDt',
        'STARTTM': 'startTm',
        'STARTKM': 'startKm',
        'ENDDT': 'endDt',
        'ENDTM': 'endTm',
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
        'BILLNUM': 'billNum',
        'BILLDATE': 'billDate',
        'TAGGED': 'tagged'
    },
    'BILLING.TRP': {
        'BILLNUM': 'billNum',
        'BILLDATE': 'billDate',
        'CLIENTID': 'clientId',
        'BILLAMT': 'amount',  # BILLAMT in DBF → amount in API
        'TAGGED': 'tagged'
        # Note: BILLIMG, TRPNUM, PRINTED, CANCELLED exist in DBF but not in current API
    }
}

# Print Python dict format for easy copy-paste
print("# Correct DBF_MAPPINGS for migrate_dbf_to_h2.py")
print("DBF_MAPPINGS = {")
for dbf_file, mappings in CORRECT_MAPPINGS.items():
    endpoint = dbf_file.replace('.TRP', '').lower()
    if endpoint == 'vehtype':
        endpoint = 'vehicle-types'
    elif endpoint == 'trpsheet':
        endpoint = 'trip-sheets'
    elif endpoint == 'billing':
        endpoint = 'billings'
    else:
        endpoint = endpoint + 's'
    
    entity = dbf_file.replace('.TRP', '').title().replace('Vehtype', 'VehicleType').replace('Trpsheet', 'TripSheet')
    
    print(f"    '{dbf_file}': {{")
    print(f"        'endpoint': '/api/v1/{endpoint}',")
    print(f"        'entity': '{entity}',")
    print(f"        'field_mapping': {{")
    for dbf_field, api_field in mappings.items():
        print(f"            '{dbf_field}': '{api_field}',")
    print(f"        }}")
    print(f"    }},")
print("}")

# Made with Bob
