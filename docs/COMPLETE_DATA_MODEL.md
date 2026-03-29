# Complete Data Model Reference

## Entity Field Definitions

### 1. Account Entity
**Table:** `accounts`
**Fields:**
- `id` (Long) - Primary key
- `num` (Integer) - Account number
- `date` (LocalDate) - Transaction date
- `desc` (String, 50) - Description
- `recd` (BigDecimal) - Amount received
- `bill` (BigDecimal) - Bill amount
- `tagged` (Boolean) - Tagged flag
- `deleted` (Boolean) - Soft delete flag

**Repository Methods:**
- `findByNum(Integer num)` → Optional<Account>
- `existsByNum(Integer num)` → boolean
- `findByDateBetween(LocalDate start, LocalDate end)` → List<Account>
- `findByTaggedTrue()` → List<Account>

---

### 2. Address Entity
**Table:** `addresses`
**Fields:**
- `id` (Long) - Primary key
- `dept` (String, 35) - Department/Name
- `desc` (String, 35) - Description/Address line
- `city` (String, 25) - City
- `pinCode` (String, 10) - PIN code
- `phone` (String, 15) - Phone number
- `clientId` (String, 10) - Foreign key to Client
- `client` (Client) - ManyToOne relationship
- `tagged` (Boolean) - Tagged flag
- `deleted` (Boolean) - Soft delete flag

**Repository Methods:**
- `findByClientId(String clientId)` → List<Address>
- `findByCity(String city)` → List<Address>
- `findByDeptContainingIgnoreCase(String dept)` → List<Address>

---

### 3. Billing Entity
**Table:** `billings`
**Fields:**
- `id` (Long) - Primary key
- `billNum` (Integer) - Bill number (unique)
- `billDate` (LocalDate) - Bill date
- `clientId` (String, 10) - Foreign key to Client
- `client` (Client) - ManyToOne relationship
- `trpNum` (Integer) - Trip number
- `billAmt` (BigDecimal) - Bill amount
- `tagged` (Boolean) - Tagged flag
- `deleted` (Boolean) - Soft delete flag

**Repository Methods:**
- `findByBillNum(Integer billNum)` → Optional<Billing>
- `existsByBillNum(Integer billNum)` → boolean
- `findByClientId(String clientId)` → List<Billing>
- `findByBillDateBetween(LocalDate start, LocalDate end)` → List<Billing>
- `findByTrpNum(Integer trpNum)` → List<Billing>

---

### 4. Booking Entity
**Table:** `bookings`
**Fields:**
- `id` (Long) - Primary key
- `bookDate` (LocalDate) - Booking/execution date
- `todayDate` (LocalDate) - Creation date
- `ref` (String, 15) - Reference number
- `time` (String, 5) - Time in HH:MM format
- `typeId` (String, 5) - Foreign key to VehicleType
- `vehicleType` (VehicleType) - ManyToOne relationship
- `clientId` (String, 10) - Foreign key to Client
- `client` (Client) - ManyToOne relationship
- `info1` (String, 50) - Info line 1
- `info2` (String, 50) - Info line 2
- `info3` (String, 50) - Info line 3
- `info4` (String, 50) - Info line 4
- `tagged` (Boolean) - Tagged flag
- `deleted` (Boolean) - Soft delete flag

**Repository Methods:**
- `findByBookDate(LocalDate bookDate)` → List<Booking>
- `findByClientId(String clientId)` → List<Booking>
- `findByTypeId(String typeId)` → List<Booking>
- `findByBookDateAndClientId(LocalDate bookDate, String clientId)` → Optional<Booking>
- `findByBookDateBetween(LocalDate start, LocalDate end)` → List<Booking>

---

### 5. Client Entity
**Table:** `clients`
**Fields:**
- `id` (Long) - Primary key
- `clientId` (String, 10) - Client ID (unique)
- `name` (String, 35) - Client name
- `address` (String, 35) - Address
- `city` (String, 25) - City
- `pinCode` (String, 10) - PIN code
- `phone` (String, 15) - Phone number
- `active` (Boolean) - Active status
- `tagged` (Boolean) - Tagged flag
- `deleted` (Boolean) - Soft delete flag

**Repository Methods:**
- `findByClientId(String clientId)` → Optional<Client>
- `existsByClientId(String clientId)` → boolean
- `findByNameContainingIgnoreCase(String name)` → List<Client>
- `findByCity(String city)` → List<Client>
- `findByActiveTrue()` → List<Client>

---

### 6. HeaderTemplate Entity
**Table:** `header_templates`
**Fields:**
- `id` (Long) - Primary key
- `line1` (String, 80) - Header line 1
- `line2` (String, 80) - Header line 2
- `line3` (String, 80) - Header line 3
- `line4` (String, 80) - Header line 4
- `line5` (String, 80) - Header line 5
- `line6` (String, 80) - Header line 6
- `line7` (String, 80) - Header line 7
- `line8` (String, 80) - Header line 8
- `active` (Boolean) - Active template flag

**Repository Methods:**
- `findByActiveTrue()` → Optional<HeaderTemplate>

---

### 7. Role Entity
**Table:** `roles`
**Fields:**
- `id` (Long) - Primary key
- `name` (String, 50) - Role name (unique)
- `description` (String, 255) - Role description

**Repository Methods:**
- `findByName(String name)` → Optional<Role>
- `existsByName(String name)` → boolean

---

### 8. StandardFare Entity
**Table:** `standard_fares`
**Fields:**
- `id` (Long) - Primary key
- `vehicleCode` (String, 5) - Vehicle code
- `fareType` (String, 1) - Fare type (F/S/O)
- `hours` (Integer) - Hours
- `rate` (BigDecimal) - Rate per hour/km
- `freeKm` (Integer) - Free kilometers
- `tagged` (Boolean) - Tagged flag
- `deleted` (Boolean) - Soft delete flag

**Repository Methods:**
- `findByVehicleCode(String vehicleCode)` → List<StandardFare>
- `findByFareType(String fareType)` → List<StandardFare>
- `findByVehicleCodeAndFareType(String vehicleCode, String fareType)` → List<StandardFare>

---

### 9. TripSheet Entity
**Table:** `trip_sheets`
**Fields:**
- `id` (Long) - Primary key
- `trpNum` (Integer) - Trip number (unique)
- `clientName` (String, 35) - Client name (denormalized)
- `trpDate` (LocalDate) - Trip date
- `regNum` (String, 14) - Vehicle registration number
- `startKm` (Integer) - Starting kilometer
- `endKm` (Integer) - Ending kilometer
- `typeId` (String, 5) - Foreign key to VehicleType
- `vehicleType` (VehicleType) - ManyToOne relationship
- `startDt` (LocalDate) - Start date
- `endDt` (LocalDate) - End date
- `startTm` (String, 5) - Start time HH:MM
- `endTm` (String, 5) - End time HH:MM
- `driver` (String, 25) - Driver name
- `clientId` (String, 10) - Foreign key to Client
- `client` (Client) - ManyToOne relationship
- `isBilled` (Boolean) - Is billed flag
- `billNum` (Integer) - Bill number
- `billDate` (LocalDate) - Bill date
- `status` (String, 1) - Status (F/S/O)
- `hiring` (BigDecimal) - Hiring charges
- `extra` (BigDecimal) - Extra charges
- `halt` (BigDecimal) - Halt charges
- `minimum` (BigDecimal) - Minimum charges
- `time` (Integer) - Total hours
- `days` (Integer) - Total days
- `permit` (BigDecimal) - Permit charges
- `misc` (BigDecimal) - Miscellaneous charges
- `tagged` (Boolean) - Tagged flag
- `deleted` (Boolean) - Soft delete flag

**Repository Methods:**
- `findByTrpNum(Integer trpNum)` → Optional<TripSheet>
- `existsByTrpNum(Integer trpNum)` → boolean
- `findByTrpDate(LocalDate trpDate)` → List<TripSheet>
- `findByClientId(String clientId)` → List<TripSheet>
- `findByRegNum(String regNum)` → List<TripSheet>
- `findByDriver(String driver)` → List<TripSheet>
- `findByTypeId(String typeId)` → List<TripSheet>
- `findByIsBilledTrue()` → List<TripSheet>
- `findByIsBilledFalse()` → List<TripSheet>
- `findByBillNum(Integer billNum)` → List<TripSheet>
- `findByStatus(String status)` → List<TripSheet>
- `findByStartDtBetween(LocalDate start, LocalDate end)` → List<TripSheet>

---

### 10. User Entity
**Table:** `users`
**Fields:**
- `id` (Long) - Primary key
- `username` (String, 50) - Username (unique)
- `password` (String, 255) - Encrypted password
- `email` (String, 100) - Email (unique)
- `firstName` (String, 50) - First name
- `lastName` (String, 50) - Last name
- `enabled` (Boolean) - Account enabled flag
- `roles` (Set<Role>) - ManyToMany relationship

**Repository Methods:**
- `findByUsername(String username)` → Optional<User>
- `findByEmail(String email)` → Optional<User>
- `existsByUsername(String username)` → boolean
- `existsByEmail(String email)` → boolean

---

### 11. VehicleImage Entity
**Table:** `vehicle_images`
**Fields:**
- `id` (Long) - Primary key
- `vehicleType` (VehicleType) - ManyToOne relationship
- `imageUrl` (String, 255) - Image URL
- `caption` (String, 100) - Image caption
- `displayOrder` (Integer) - Display order

**Repository Methods:**
- `findByVehicleType(VehicleType vehicleType)` → List<VehicleImage>
- `findByVehicleTypeOrderByDisplayOrder(VehicleType vehicleType)` → List<VehicleImage>

---

### 12. VehicleType Entity
**Table:** `vehicle_types`
**Fields:**
- `id` (Long) - Primary key
- `typeId` (String, 5) - Type ID (unique)
- `typeDesc` (String, 35) - Type description
- `images` (List<VehicleImage>) - OneToMany relationship
- `tagged` (Boolean) - Tagged flag
- `deleted` (Boolean) - Soft delete flag

**Repository Methods:**
- `findByTypeId(String typeId)` → Optional<VehicleType>
- `existsByTypeId(String typeId)` → boolean
- `findByTypeDescContainingIgnoreCase(String typeDesc)` → List<VehicleType>
- `findByTaggedTrue()` → List<VehicleType>
- `findByActiveTrue()` → List<VehicleType> (deleted = false)

---

## Common Patterns

### Field Naming Convention
- Legacy DBF fields use abbreviated names (e.g., `desc`, `num`, `trpNum`)
- All entity fields use camelCase
- Repository method names MUST match exact field names

### Repository Method Naming
- `findBy{FieldName}` - Find by exact match
- `findBy{FieldName}Containing` - Find by partial match (String fields)
- `findBy{FieldName}IgnoreCase` - Case-insensitive search
- `findBy{FieldName}Between` - Range query (dates, numbers)
- `existsBy{FieldName}` - Check existence
- `countBy{FieldName}` - Count records

### Relationships
- **ManyToOne**: Client, VehicleType (use `{field}Id` String for FK)
- **OneToMany**: VehicleType → VehicleImages
- **ManyToMany**: User ↔ Role

### Common Fields
All entities have:
- `id` (Long) - Primary key
- `tagged` (Boolean) - For filtering/reporting
- `deleted` (Boolean) - Soft delete flag

---

## Critical Rules

1. **NEVER** create repository methods for fields that don't exist in the entity
2. **ALWAYS** use exact field names from entities in repository methods
3. **NEVER** use `@Transient` fields in repository query methods
4. **ALWAYS** check FIELD_MAPPING.md before creating new repository methods
5. For relationships, use the String FK field (e.g., `clientId`) not the entity object in queries

---

Generated: 2026-03-27