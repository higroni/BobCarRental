# Database Migration Tools

This folder contains tools for migrating legacy data to the modern H2 database:

1. **DBF Migration Tool** - Migrates data from Clipper/dBase DBF files
2. **Fares & Headers Parser** - Parses FARES.TXT and HEADER.TXT configuration files

## Tools Overview

### 1. DBF Migration (`migrate_dbf_to_h2.py`)

Migrates data from legacy Clipper/dBase DBF files to the modern H2 database via REST API.

## Prerequisites

1. **Python 3.7+** installed
2. **Spring Boot application running** on `http://localhost:8080`
3. **Admin credentials** (default: admin/admin123)

## Installation

Install required Python packages:

```bash
pip install -r requirements.txt
```

Or install manually:

```bash
pip install dbfread requests
```

## DBF Files

The tool expects the following DBF files in the project root directory:

- `VEHTYPE.TRP` - Vehicle types
- `CLIENT.TRP` - Clients/customers
- `ADDRESS.TRP` - Client addresses
- `BOOKING.TRP` - Bookings
- `TRPSHEET.TRP` - Trip sheets
- `BILLING.TRP` - Billing records

## Usage

### 1. Start the Spring Boot Application

```bash
cd ../backend
mvn spring-boot:run
```

Wait until you see "Started BobCarRentalApplication" in the logs.

### 2. Run the Migration

```bash
python migrate_dbf_to_h2.py
```

## Migration Process

The tool will:

1. **Authenticate** with the API using admin credentials
2. **Read DBF files** using cp852 encoding (DOS Cyrillic)
3. **Map fields** from DBF format to API format
4. **POST records** to appropriate REST endpoints
5. **Report progress** and statistics

### Migration Order

Files are migrated in dependency order:

1. Vehicle Types (no dependencies)
2. Clients (no dependencies)
3. Addresses (depends on Clients)
4. Bookings (depends on Clients, Vehicle Types)
5. Trip Sheets (depends on Clients, Vehicle Types)
6. Billing (depends on Clients)

## Field Mappings

### Vehicle Types (VEHTYPE.TRP → /api/v1/vehicle-types)

| DBF Field   | API Field  | Type    |
|-------------|------------|---------|
| TYPE_ID     | typeId     | String  |
| TYPE_DESC   | typeName   | String  |
| TAGGED      | tagged     | Boolean |

### Clients (CLIENT.TRP → /api/v1/clients)

| DBF Field   | API Field   | Type    |
|-------------|-------------|---------|
| CLIENT_ID   | clientId    | String  |
| NAME        | clientName  | String  |
| ADDRESS1    | address1    | String  |
| ADDRESS2    | address2    | String  |
| ADDRESS3    | address3    | String  |
| PLACE       | place       | String  |
| PIN         | pin         | String  |
| PHONE1      | phone1      | String  |
| PHONE2      | phone2      | String  |
| FAX         | fax         | String  |
| EMAIL       | email       | String  |
| CONTACT     | contact     | String  |
| TAGGED      | tagged      | Boolean |

### Addresses (ADDRESS.TRP → /api/v1/addresses)

| DBF Field   | API Field  | Type    |
|-------------|------------|---------|
| CLIENT_ID   | clientId   | String  |
| ADDRESS1    | address1   | String  |
| ADDRESS2    | address2   | String  |
| ADDRESS3    | address3   | String  |
| PLACE       | place      | String  |
| PIN         | pin        | String  |
| PHONE       | phone      | String  |
| CONTACT     | contact    | String  |
| TAGGED      | tagged     | Boolean |

### Bookings (BOOKING.TRP → /api/v1/bookings)

| DBF Field   | API Field  | Type    |
|-------------|------------|---------|
| CLIENT_ID   | clientId   | String  |
| TYPE_ID     | typeId     | String  |
| BOOK_DATE   | bookDate   | Date    |
| TIME        | time       | String  |
| INFO1       | info1      | String  |
| INFO2       | info2      | String  |
| INFO3       | info3      | String  |
| INFO4       | info4      | String  |
| REF         | ref        | String  |
| TODAY_DATE  | todayDate  | Date    |
| TAGGED      | tagged     | Boolean |

### Trip Sheets (TRPSHEET.TRP → /api/v1/trip-sheets)

| DBF Field    | API Field   | Type    |
|--------------|-------------|---------|
| TRP_NUM      | trpNum      | String  |
| TRP_DATE     | trpDate     | Date    |
| CLIENT_ID    | clientId    | String  |
| CLIENT_NAME  | clientName  | String  |
| TYPE_ID      | typeId      | String  |
| REG_NUM      | regNum      | String  |
| DRIVER       | driver      | String  |
| START_DT     | startDt     | Date    |
| START_TM     | startTm     | String  |
| START_KM     | startKm     | Integer |
| END_DT       | endDt       | Date    |
| END_TM       | endTm       | String  |
| END_KM       | endKm       | Integer |
| DAYS         | days        | Integer |
| TIME         | time        | String  |
| HIRING       | hiring      | Decimal |
| EXTRA        | extra       | Decimal |
| HALT         | halt        | Decimal |
| PERMIT       | permit      | Decimal |
| MISC         | misc        | Decimal |
| MINIMUM      | minimum     | Decimal |
| STATUS       | status      | String  |
| IS_BILLED    | isBilled    | Boolean |
| BILL_NUM     | billNum     | String  |
| BILL_DATE    | billDate    | Date    |
| TAGGED       | tagged      | Boolean |

### Billing (BILLING.TRP → /api/v1/billings)

| DBF Field    | API Field   | Type    |
|--------------|-------------|---------|
| BILL_NUM     | billNum     | String  |
| BILL_DATE    | billDate    | Date    |
| CLIENT_ID    | clientId    | String  |
| CLIENT_NAME  | clientName  | String  |
| AMOUNT       | amount      | Decimal |
| TAGGED       | tagged      | Boolean |

## Output

The tool provides real-time progress updates:

```
============================================================
🚀 DBF to H2 Migration Tool
============================================================

🔐 Logging in as admin...
✅ Login successful! Token: eyJhbGciOiJIUzUxMiJ9...

📂 Migrating VEHTYPE.TRP (VehicleType)...
   Path: d:/POSAO/alankar/VEHTYPE.TRP
   Found 3 records
   ✅ Migrated 3/3 records...
   ✅ Completed VEHTYPE.TRP

📂 Migrating CLIENT.TRP (Client)...
   Path: d:/POSAO/alankar/CLIENT.TRP
   Found 2 records
   ✅ Migrated 2/2 records...
   ✅ Completed CLIENT.TRP

============================================================
📊 Migration Summary
============================================================
Files processed:    6
Total records:      150
✅ Successful:      145
❌ Failed:          3
⏭️  Skipped:         2
============================================================
```

## Troubleshooting

### Error: "dbfread library not installed"

Install the library:
```bash
pip install dbfread
```

### Error: "Login failed"

1. Check if Spring Boot application is running
2. Verify admin credentials in the script
3. Check API URL (default: http://localhost:8080)

### Error: "File not found"

Ensure DBF files are in the project root directory (same level as bobcarrental folder).

### Error: "Failed record: 400 Bad Request"

Check the API logs for validation errors. Common issues:
- Missing required fields
- Invalid data format
- Foreign key constraints

### Encoding Issues

The tool uses `cp852` encoding (DOS Cyrillic). If you have different encoding:
1. Open `migrate_dbf_to_h2.py`
2. Find line: `table = DBF(str(dbf_path), encoding='cp852')`
3. Change encoding (e.g., `cp437` for US, `cp850` for Western Europe)

## Configuration

Edit `migrate_dbf_to_h2.py` to customize:

```python
# API Configuration
API_BASE_URL = "http://localhost:8080/api/v1"
ADMIN_USERNAME = "admin"
ADMIN_PASSWORD = "admin123"

# DBF Files Path
DBF_FILES_PATH = Path(__file__).parent.parent.parent
```

## Notes

- The tool uses the REST API, so all business logic and validation is applied
- Existing records with same IDs will cause conflicts (API returns 409)
- Empty records are automatically skipped
- Progress is shown every 10 records
- All dates are converted to ISO format (YYYY-MM-DD)
- Boolean fields accept: T/F, Y/N, True/False

## Next Steps

After successful migration:

1. Verify data in H2 console: http://localhost:8080/h2-console
2. Run API tests: `cd ../backend && .\run-full-test.bat`
3. Check data integrity and relationships
4. Parse FARES.TXT and HEADER.TXT files
5. Begin frontend development

### 2. Fares & Headers Parser (`parse_fares_and_headers.py`)

Parses legacy FARES.TXT and HEADER.TXT configuration files and creates StandardFare and HeaderTemplate records.

#### FARES.TXT Format

The file contains fare configurations in sections:

**[LOCAL]** - Local hire fares
```
Format: CARCODE-HRS-RATE-FREEKMS-SPLITFARE-SPLITFUELKMSRATE
Example: AMB-1-160.00-25-110.00-50
```

**[EXTRA]** - Extra hour fares
```
Format: CARCODE-HRS-RATE-FREEKM-HIRERATE-HIREKM
Example: AMB-1-30.00-0-30.00-0
```

**[GENERAL]** - General rates
```
Format: CARECODE-FUELRATE-EXCESSRATE
Example: AMB-2.75-4.00
```

**[OUTSTATION]** - Outstation fares
```
Format: CARECODE-RATEPERKM-MINKMPERDAY-NIGHTHALT
Example: AMB-4.00-225-50.00
```

#### HEADER.TXT Format

Plain text file with invoice header lines (company name, address, etc.)

#### Usage

```bash
cd bobcarrental/migration
python parse_fares_and_headers.py
```

Or use the batch file:
```bash
parse_fares.bat
```

---
