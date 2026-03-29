# Repository Audit Report

## Issues Found - Methods Using Non-Existent Fields

### 1. AccountRepository ❌
**Issues:**
- `findByClientId(String clientId)` - Account entity does NOT have `clientId` field
- `findByCode(String code)` - Account entity does NOT have `code` field
- `findByAccountType(String accountType)` - Account entity does NOT have `accountType` field
- `findByIsActiveTrue()` - Account entity does NOT have `isActive` field
- `findByParentAccountIsNull()` - Account entity does NOT have `parentAccount` field
- `findByParentAccountId(Long parentId)` - Account entity does NOT have `parentAccount` field
- `existsByCode(String code)` - Account entity does NOT have `code` field
- `findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase()` - Account entity does NOT have `code` or `name` fields
- `countByClientId(String clientId)` - Account entity does NOT have `clientId` field

**Valid Fields in Account Entity:**
- `num`, `date`, `desc`, `recd`, `bill`, `tagged`, `deleted`

---

### 2. AddressRepository ❌
**Issues:**
- `findByNameContainingIgnoreCase(String name)` - Address entity does NOT have `name` field (has `dept`)
- `findByPinCode(Integer pinCode)` - Address entity has `pinCode` as String, not Integer
- `findByCategory(String category)` - Address entity does NOT have `category` field
- `findByIsActiveTrue()` - Address entity does NOT have `isActive` field
- `findByNameContainingIgnoreCaseOrCompanyNameContainingIgnoreCaseOrCityContainingIgnoreCase()` - Address entity does NOT have `name` or `companyName` fields

**Valid Fields in Address Entity:**
- `dept`, `desc`, `city`, `pinCode` (String), `phone`, `clientId`, `tagged`, `deleted`

---

### 3. BillingRepository ❌
**Issues:**
- `findByPrintedTrue()` - Billing entity does NOT have `printed` field
- `findByPrintedFalse()` - Billing entity does NOT have `printed` field
- `findByCancelledTrue()` - Billing entity does NOT have `cancelled` field
- `findByCancelledFalse()` - Billing entity does NOT have `cancelled` field
- `findByTripSheet(TripSheet tripSheet)` - Billing entity does NOT have `tripSheet` relationship (only `trpNum`)

**Valid Fields in Billing Entity:**
- `billNum`, `billDate`, `clientId`, `trpNum`, `billAmt`, `tagged`, `deleted`

---

### 4. ClientRepository ❌
**Issues:**
- `findByClientNameContainingIgnoreCase(String name)` - Client entity has `name` field, not `clientName`
- `findByIsSplitTrue()` - Client entity does NOT have `isSplit` field
- `existsByClientName(String name)` - Client entity has `name` field, not `clientName`
- `existsByPhone(String phone)` - Valid, Client has `phone` field ✓

**Valid Fields in Client Entity:**
- `clientId`, `name`, `address`, `city`, `pinCode`, `phone`, `active`, `tagged`, `deleted`

---

### 5. HeaderTemplateRepository ❌
**Issues:**
- `findByTemplateName(String templateName)` - HeaderTemplate entity does NOT have `templateName` field
- `existsByTemplateName(String templateName)` - HeaderTemplate entity does NOT have `templateName` field
- `findByTemplateType(TemplateType templateType)` - HeaderTemplate entity does NOT have `templateType` field
- `findByIsActiveTrue()` - HeaderTemplate entity has `active` field, not `isActive`
- `findByIsActiveFalse()` - HeaderTemplate entity has `active` field, not `isActive`
- `findByTemplateTypeAndIsActiveTrue()` - Both fields don't exist
- `countByTemplateType()` - HeaderTemplate entity does NOT have `templateType` field
- `findByLineCountRange()` - HeaderTemplate entity does NOT have `lineCount` field
- `findByIsDefaultTrue()` - HeaderTemplate entity does NOT have `isDefault` field
- `findByNameContainingIgnoreCaseOrCompanyNameContainingIgnoreCase()` - HeaderTemplate entity does NOT have `name` or `companyName` fields

**Valid Fields in HeaderTemplate Entity:**
- `line1`, `line2`, `line3`, `line4`, `line5`, `line6`, `line7`, `line8`, `active`

---

### 6. VehicleImageRepository ❌
**Issues:**
- `findByImageName(String imageName)` - VehicleImage entity does NOT have `imageName` field
- `findByContentType(String contentType)` - VehicleImage entity does NOT have `contentType` field
- `findByVehicleTypeIdAndIsPrimaryTrue()` - VehicleImage entity does NOT have `isPrimary` field

**Valid Fields in VehicleImage Entity:**
- `vehicleType`, `imageUrl`, `caption`, `displayOrder`

---

## Repositories That Are Correct ✓

### 1. BookingRepository ✓
All methods use valid fields from Booking entity.

### 2. RoleRepository ✓
All methods use valid fields from Role entity.

### 3. StandardFareRepository ✓
All methods use valid fields from StandardFare entity.

### 4. TripSheetRepository ✓
All methods use valid fields from TripSheet entity.

### 5. UserRepository ✓
All methods use valid fields from User entity.

### 6. VehicleTypeRepository ✓
All methods use valid fields from VehicleType entity.

---

## Summary

**Total Repositories:** 12
**Correct Repositories:** 6
**Repositories with Issues:** 6

**Total Invalid Methods Found:** ~50+

## Action Required

All invalid repository methods must be either:
1. **Removed** - if the field doesn't exist and isn't needed
2. **Fixed** - if the field name is wrong (e.g., `clientName` → `name`)
3. **Entity Updated** - if the field should exist but is missing (rare case)

---

Generated: 2026-03-27