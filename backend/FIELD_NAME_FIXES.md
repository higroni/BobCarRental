# Field Name Fixes for @PageableDefault

## Problems Found

Controllers are using incorrect field names in `@PageableDefault(sort="...")` annotations.
These field names must match actual JPA entity field names.

## Required Fixes

### 1. ClientController
- **Current**: `sort = "name"`
- **Correct**: `sort = "clientName"`
- **Entity field**: `Client.clientName` (line 36)

### 2. TripSheetController  
- **Current**: `sort = "startDate"`
- **Correct**: `sort = "trpDate"`
- **Entity field**: `TripSheet.trpDate` (line 57)

### 3. StandardFareController
- **Current**: `sort = "vehicleType.name"`
- **Correct**: `sort = "vehicleType.typeDesc"`
- **Entity field**: `VehicleType.typeDesc` (line 39)
- **Note**: VehicleType has `getName()` helper method but it's not a JPA field

### 4. VehicleTypeController
- **Current**: `sort = "typeName"`
- **Correct**: `sort = "typeDesc"`
- **Entity field**: `VehicleType.typeDesc` (line 39)

### 5. HeaderTemplateController (2 places)
- **Current**: `sort = "name"`
- **Correct**: `sort = "templateName"`
- **Entity field**: `HeaderTemplate.templateName` (line 37)

### 6. AddressController (2 places)
- **Current**: `sort = "name"`
- **Correct**: Address entity doesn't have a `name` field!
- **Options**: 
  - Use `clientId` (line 38)
  - Use `dept` (line 49)
  - Use `id` (default)
- **Recommendation**: `sort = "clientId"` for consistency

### 7. VehicleImageController
- **Current**: `sort = "displayOrder"`
- **Need to verify**: Check if VehicleImage has this field
- **Entity field**: Need to check line 60+

## Fields That Are Correct
- ✅ BookingController: `sort = "bookDate"` - CORRECT
- ✅ BillingController: `sort = "billDate"` - CORRECT  
- ✅ AccountController: `sort = "code"` - CORRECT