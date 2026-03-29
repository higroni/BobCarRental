# Field Mapping Reference

This document contains the authoritative field names for all entities in the BobCarRental system.

## Account Entity
| DB Column | Java Field | Type | Notes |
|-----------|------------|------|-------|
| id | id | Long | Primary Key |
| desc | desc | String | Description (15 chars) |
| num | num | Long | Document number |
| date | date | LocalDate | Entry date |
| client_id | clientId | String | Foreign Key (10 chars) |
| recd | recd | BigDecimal | Amount received |
| bill | bill | BigDecimal | Amount billed |

## Address Entity
| DB Column | Java Field | Type | Notes |
|-----------|------------|------|-------|
| id | id | Long | Primary Key |
| client_id | clientId | String | Foreign Key (10 chars) |
| dept | dept | String | Department (15 chars) |
| desc | desc | String | Description (10 chars) |
| name | name | String | Contact name (40 chars) |
| address1 | address1 | String | Address line 1 (35 chars) |
| address2 | address2 | String | Address line 2 (30 chars) |
| address3 | address3 | String | Address line 3 (25 chars) |
| place | place | String | Place (20 chars) |
| city | city | String | City (15 chars) |
| pin_code | pinCode | Integer | PIN code |
| phone | phone | String | Phone (25 chars) |
| fax | fax | String | Fax (25 chars) |
| tagged | tagged | Boolean | Tagged flag |
| deleted | deleted | Boolean | Soft delete flag |
| category | category | String | Category (20 chars) |
| company_name | companyName | String | Company name (100 chars) |
| is_active | isActive | Boolean | Active status |

## Billing Entity
| DB Column | Java Field | Type | Notes |
|-----------|------------|------|-------|
| id | id | Long | Primary Key |
| bill_num | billNum | Integer | Bill number (unique) |
| bill_date | billDate | LocalDate | Bill date |
| client_id | clientId | String | Foreign Key (10 chars) |
| bill_img | billImg | String | Bill image/content (TEXT) |
| trp_num | trpNum | Integer | Trip sheet number FK |
| printed | printed | Boolean | Printed flag |
| cancelled | cancelled | Boolean | Cancelled flag |
| bill_amt | billAmt | BigDecimal | Bill amount |
| bill_no | billNo | String | Bill number string (20 chars) |
| total_amount | totalAmount | BigDecimal | Total amount |
| cgst | cgst | BigDecimal | CGST amount |
| sgst | sgst | BigDecimal | SGST amount |
| igst | igst | BigDecimal | IGST amount |
| paid | paid | BigDecimal | Paid amount |
| tagged | tagged | Boolean | Tagged flag |

## TripSheet Entity
| DB Column | Java Field | Type | Notes |
|-----------|------------|------|-------|
| id | id | Long | Primary Key |
| trp_num | trpNum | Integer | Trip number (unique) |
| client_name | clientName | String | Client name (35 chars) |
| trp_date | trpDate | LocalDate | Trip date |
| reg_num | regNum | String | Registration number (14 chars) |
| start_km | startKm | Integer | Start kilometer |
| end_km | endKm | Integer | End kilometer |
| type_id | typeId | String | Vehicle type ID (5 chars) |
| start_dt | startDt | LocalDate | Start date |
| end_dt | endDt | LocalDate | End date |
| start_tm | startTm | String | Start time (5 chars HH:MM) |
| end_tm | endTm | String | End time (5 chars HH:MM) |
| driver | driver | String | Driver name (25 chars) |
| client_id | clientId | String | Foreign Key (10 chars) |
| is_billed | isBilled | Boolean | Billed flag |
| bill_num | billNum | Integer | Bill number FK |
| bill_date | billDate | LocalDate | Bill date |
| status | status | String | Trip status (1 char: F/S/O) |
| hiring | hiring | BigDecimal | Hiring charges |
| extra | extra | BigDecimal | Extra charges |
| halt | halt | BigDecimal | Halt charges |
| minimum | minimum | BigDecimal | Minimum charges |
| time | time | Integer | Total hours |
| permit | permit | BigDecimal | Permit charges |
| misc | misc | BigDecimal | Miscellaneous charges |

## StandardFare Entity
| DB Column | Java Field | Type | Notes |
|-----------|------------|------|-------|
| id | id | Long | Primary Key |
| vehicle_code | vehicleCode | String | Vehicle code (5 chars) |
| fare_type | fareType | FareType | Enum: LOCAL/EXTRA/GENERAL/OUTSTATION |
| hours | hours | Integer | Hours (nullable) |
| rate | rate | BigDecimal | Rate/Basic charge |
| free_km | freeKm | Integer | Free kilometers |
| split_fare | splitFare | BigDecimal | Split fare/Hire rate |
| split_fuel_km | splitFuelKm | Integer | Split fuel KM rate |
| hire_rate | hireRate | BigDecimal | Hire rate (EXTRA type) |
| extra_km_rate | extraKmRate | BigDecimal | Extra KM rate |
| extra_hr_rate | extraHrRate | BigDecimal | Extra hour rate |
| permit | permit | BigDecimal | Permit charges |
| night_halt | nightHalt | BigDecimal | Night halt charges |
| driver_allowance | driverAllowance | BigDecimal | Driver allowance |

## Client Entity
| DB Column | Java Field | Type | Notes |
|-----------|------------|------|-------|
| id | id | Long | Primary Key |
| client_id | clientId | String | Client ID (10 chars, unique) |
| name | name | String | Client name (35 chars) |
| address1 | address1 | String | Address line 1 (35 chars) |
| address2 | address2 | String | Address line 2 (30 chars) |
| address3 | address3 | String | Address line 3 (25 chars) |
| city | city | String | City (15 chars) |
| pin_code | pinCode | Integer | PIN code |
| phone | phone | String | Phone (25 chars) |
| fax | fax | String | Fax (25 chars) |
| is_active | isActive | Boolean | Active status |

## VehicleType Entity
| DB Column | Java Field | Type | Notes |
|-----------|------------|------|-------|
| id | id | Long | Primary Key |
| type_id | typeId | String | Type ID (5 chars, unique) |
| type_desc | typeDesc | String | Type description (50 chars) |
| tagged | tagged | Boolean | Tagged flag |
| deleted | deleted | Boolean | Soft delete flag |

## Booking Entity
| DB Column | Java Field | Type | Notes |
|-----------|------------|------|-------|
| id | id | Long | Primary Key |
| book_date | bookDate | LocalDate | Booking/reporting date (PRIMARY KEY in legacy) |
| today_date | todayDate | LocalDate | Order/creation date |
| ref | ref | String | Reference number (15 chars) |
| time | time | String | Reporting time (5 chars HH:MM) |
| type_id | typeId | String | Vehicle type FK (5 chars) |
| client_id | clientId | String | Foreign Key (10 chars) |
| info1 | info1 | String | Info line 1 (50 chars) |
| info2 | info2 | String | Info line 2 (50 chars) |
| info3 | info3 | String | Info line 3 (50 chars) |
| info4 | info4 | String | Info line 4 (50 chars) |
| from_place | fromPlace | String | From location (30 chars) |
| to_place | toPlace | String | To location (30 chars) |
| tagged | tagged | Boolean | Tagged flag |
| deleted | deleted | Boolean | Soft delete flag |

## User Entity
| DB Column | Java Field | Type | Notes |
|-----------|------------|------|-------|
| id | id | Long | Primary Key |
| username | username | String | Username (50 chars, unique) |
| password | password | String | Encrypted password |
| email | email | String | Email (100 chars, unique) |
| full_name | fullName | String | Full name (100 chars) |
| is_active | isActive | Boolean | Active status |
| role_id | roleId | Long | Role FK |

## Role Entity
| DB Column | Java Field | Type | Notes |
|-----------|------------|------|-------|
| id | id | Long | Primary Key |
| name | name | String | Role name (50 chars, unique) |
| description | description | String | Description (200 chars) |

---

**IMPORTANT RULES:**
1. Always use the exact Java field names from this table in repositories, services, and controllers
2. Never use alternative names (e.g., use `trpNum` not `tripNumber`, `billNum` not `billNumber`)
3. When writing JPQL queries, use the Java field names (e.g., `t.trpNum`, `b.billAmt`)
4. For HQL string conversion, use `str()` function, not `CAST(... AS string)`
5. All field names are case-sensitive

**Last Updated:** 2026-03-27