# Service Implementation Fixes Needed

After fixing repositories to match actual entity fields, service implementations need updates.

## Summary: 28 Compilation Errors in 6 Service Files

### 1. HeaderTemplateServiceImpl (4 errors)
- Line 50: `findByIsDefaultTrue()` → Does not exist (entity has no `isDefault` field)
- Line 58: `findByIsActiveTrue()` → Changed to `findByActiveTrue()`
- Line 67: `findByNameContainingIgnoreCaseOrCompanyNameContainingIgnoreCase()` → Removed (no such fields)
- Line 188: `findByIsDefaultTrue()` → Does not exist

**Available methods:**
- `findByActiveTrue()`
- `findByActiveFalse()`
- `searchTemplates(String search)` - searches all line1-8 fields
- `countActiveTemplates()`

### 2. VehicleImageServiceImpl (2 errors)
- Line 75: `findByVehicleTypeIdAndIsPrimaryTrue()` → Does not exist (no `isPrimary` field)
- Line 247: `findByVehicleTypeIdAndIsPrimaryTrue()` → Does not exist

**Available methods:**
- `findByVehicleType(VehicleType)`
- `findByVehicleTypeId(Long)`
- `findByVehicleTypeIdOrderByDisplayOrderAsc(Long)` - Use first image as primary
- `findByCaptionContainingIgnoreCase(String)`
- `countByVehicleType(VehicleType)`
- `countByVehicleTypeId(Long)`
- `existsByVehicleTypeIdAndDisplayOrder(Long, Integer)`
- `deleteByVehicleTypeId(Long)`

### 3. ClientServiceImpl (1 error)
- Line 168: `existsByClientName()` → Changed to `existsByName()`

**Available methods:**
- `findByClientId(String)`
- `existsByClientId(String)`
- `findByNameContainingIgnoreCase(String)` - Changed from `findByClientNameContainingIgnoreCase`
- `findByCity(String)`
- `findByActiveTrue()`
- `findByTaggedTrue()`
- `searchClients(String)` - searches name, clientId, city, phone
- `searchClients(String, Pageable)`
- `countActiveClients()`
- `findAllExceptMisc()`
- `searchClientsByName(String)`
- `existsByName(String)` - Changed from `existsByClientName`
- `existsByPhone(String)`

### 4. VehicleTypeServiceImpl (6 errors)
- Line 64: `findByTypeNameContainingIgnoreCase()` → Changed to `findByTypeDescContainingIgnoreCase()`
- Line 73: `findBySeatingCapacity()` → Does not exist (no such field)
- Line 82: `findByHasAc()` → Does not exist (no such field)
- Line 94: `existsByTypeName()` → Changed to `existsByTypeDesc()`
- Line 114: `existsByTypeNameAndIdNot()` → Changed to `existsByTypeDescAndIdNot()`
- Line 144: `existsByTypeName()` → Changed to `existsByTypeDesc()`
- Line 149: `existsByTypeNameAndIdNot()` → Changed to `existsByTypeDescAndIdNot()`

**Available methods:**
- `findByTypeId(String)`
- `existsByTypeId(String)`
- `findByTypeDescContainingIgnoreCase(String)` - Changed from `findByTypeNameContainingIgnoreCase`
- `findByTaggedTrue()`
- `searchVehicleTypes(String, Pageable)` - searches typeId and typeDesc
- `countActiveVehicleTypes()`
- `existsByTypeDesc(String)` - Changed from `existsByTypeName`
- `existsByTypeDescAndIdNot(String, Long)` - Changed from `existsByTypeNameAndIdNot`

### 5. AccountServiceImpl (10 errors)
All methods using non-existent fields removed. Account entity has: `num`, `date`, `desc`, `recd`, `bill`, `tagged`, `deleted`

- Line 51: `findByCode()` → Does not exist
- Line 65: `findByAccountType()` → Does not exist
- Line 74: `findByIsActiveTrue()` → Does not exist
- Line 83: `findByParentAccountIsNull()` → Does not exist
- Line 98: `findByParentAccountId()` → Does not exist
- Line 107: `findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase()` → Does not exist
- Line 118: `existsByCode()` → Does not exist
- Line 161: `existsByCode()` → Does not exist
- Line 259: `findByParentAccountId()` → Does not exist
- Line 270: `existsByCode()` → Does not exist

**Available methods:**
- `findByNum(Integer)`
- `existsByNum(Integer)`
- `findByDate(LocalDate)`
- `findByDateBetween(LocalDate, LocalDate)`
- `findByDescContainingIgnoreCase(String)`
- `findByTaggedTrue()`
- `searchAccounts(String, Pageable)` - searches desc only
- `getTotalReceivedForPeriod(LocalDate, LocalDate)`
- `getTotalBilledForPeriod(LocalDate, LocalDate)`

### 6. AddressServiceImpl (3 errors)
- Line 58: `findByCategory()` → Does not exist (no `category` field)
- Line 67: `findByIsActiveTrue()` → Does not exist (no `isActive` field)
- Line 76: `findByNameContainingIgnoreCaseOrCompanyNameContainingIgnoreCase...()` → Does not exist

**Available methods:**
- `findByClientId(String)`
- `findByClientId(String, Pageable)`
- `findByDeptContainingIgnoreCase(String)` - dept is the name/contact field
- `findByDept(String)`
- `findByCity(String)`
- `findByCityIgnoreCase(String)`
- `findByPinCode(String)` - String type, not Integer
- `findByTaggedTrue()`
- `findByClientIdAndDept(String, String)`
- `searchAddresses(String, Pageable)` - searches clientId, dept, desc, city, phone
- `searchByClient(String, String)`
- `countByClientId(String)`
- `countByCity(String)`
- `findAllCities()`
- `findAllDepartments()`

### 7. BillingServiceImpl (1 error)
- Line 143: `findByTripSheet()` → Does not exist (no relationship field)

**Available methods:**
- `existsByBillNum(Integer)`
- `findByBillDate(LocalDate)`
- `findByClientId(String)`
- `findByTrpNum(Integer)` - Use this to find billing by trip sheet number
- `findByTaggedTrue()`
- `findByDateRange(LocalDate, LocalDate)`
- `findByClientAndDateRange(String, LocalDate, LocalDate)`
- `searchBillings(String, Pageable)`
- `countByClientId(String)`
- `getTotalAmountForPeriod(LocalDate, LocalDate)`
- `getTotalAmountForClient(String)`
- `findMaxBillNumber()`
- `findByBillNum(Integer)`
- `findByBillDateBetween(LocalDate, LocalDate)`

## Action Plan

1. Fix HeaderTemplateServiceImpl - remove default template logic, use active templates
2. Fix VehicleImageServiceImpl - use first image by displayOrder as primary
3. Fix ClientServiceImpl - change existsByClientName to existsByName
4. Fix VehicleTypeServiceImpl - change all typeName to typeDesc, remove seatingCapacity and hasAc
5. Fix AccountServiceImpl - major refactor needed, remove all code/parentAccount logic
6. Fix AddressServiceImpl - remove category and isActive, use dept for name searches
7. Fix BillingServiceImpl - use findByTrpNum instead of findByTripSheet