# Startup Warnings - Fixed

## Overview
This document describes the warnings that appeared during application startup and how they were resolved.

## Warnings Fixed

### 1. iText7 Artifact Relocation Warning

**Warning:**
```
[WARNING] The artifact com.itextpdf:itext7-core:pom:8.0.3 has been relocated to com.itextpdf:itext-core:pom:8.0.3
```

**Root Cause:**
- iText library changed their artifact ID from `itext7-core` to `itext-core` in version 8.x
- Our pom.xml was using the old artifact ID

**Fix Applied:**
Changed in `pom.xml` line 134:
```xml
<!-- Before -->
<artifactId>itext7-core</artifactId>

<!-- After -->
<artifactId>itext-core</artifactId>
```

**Status:** ✅ Fixed

---

### 2. Deprecated JWT API Warning

**Warning:**
```
[INFO] JwtTokenProvider.java uses or overrides a deprecated API.
[INFO] Recompile with -Xlint:deprecation for details.
```

**Root Cause:**
- JJWT library (version 0.12.5) deprecated old builder methods
- `SignatureAlgorithm.HS512` enum is deprecated
- Old setter methods (`setSubject`, `setIssuedAt`, `setExpiration`) are deprecated

**Fix Applied:**
Updated `JwtTokenProvider.java`:

**Lines 45-51 (generateToken method):**
```java
// Before (deprecated)
return Jwts.builder()
        .setSubject(username)
        .claim("roles", roles)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();

// After (modern API)
return Jwts.builder()
        .subject(username)
        .claim("roles", roles)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(getSigningKey())
        .compact();
```

**Lines 62-67 (generateRefreshToken method):**
```java
// Before (deprecated)
return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();

// After (modern API)
return Jwts.builder()
        .subject(username)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(getSigningKey())
        .compact();
```

**Changes Made:**
1. Removed `SignatureAlgorithm.HS512` parameter (algorithm is auto-detected from key)
2. Changed `setSubject()` → `subject()`
3. Changed `setIssuedAt()` → `issuedAt()`
4. Changed `setExpiration()` → `expiration()`

**Status:** ✅ Fixed

---

## Verification

After these fixes, the application should start without any warnings related to:
- Deprecated APIs
- Relocated artifacts

### Expected Clean Startup Log:
```
[INFO] Compiling 110 source files with javac [debug parameters release 21] to target\classes
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

No warnings should appear during compilation or startup.

---

## Additional Notes

### JJWT 0.12.x Migration
The JJWT library underwent significant API changes in version 0.12.x:
- Builder methods now use property-style naming (no `set` prefix)
- `SignatureAlgorithm` enum is deprecated in favor of auto-detection
- `signWith(Key)` automatically selects the appropriate algorithm based on key type

### iText 8.x Migration
iText 7 was rebranded to iText 8 with new artifact IDs:
- Old: `com.itextpdf:itext7-core`
- New: `com.itextpdf:itext-core`

Both changes improve code clarity and follow modern Java conventions.

---

**Date Fixed:** 2026-03-28  
**Fixed By:** Bob  
**Status:** All startup warnings resolved ✅