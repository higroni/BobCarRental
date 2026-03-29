# GitHub Setup Instructions

## Koraci za postavljanje projekta na GitHub

### 1. Kreiraj novi repozitorijum na GitHub-u

1. Idi na https://github.com/new
2. Unesi ime repozitorijuma: `bobcarrental` (ili bilo koje drugo ime)
3. **NE** inicijalizuj repozitorijum sa README, .gitignore ili licencom (već imamo sve lokalno)
4. Klikni "Create repository"

### 2. Poveži lokalni repozitorijum sa GitHub-om

Nakon što kreiraš repozitorijum, GitHub će ti prikazati instrukcije. Koristi sledeće komande u PowerShell terminalu iz `bobcarrental` foldera:

```powershell
# Dodaj GitHub remote (zameni YOUR_USERNAME sa tvojim GitHub korisničkim imenom)
git remote add origin https://github.com/YOUR_USERNAME/bobcarrental.git

# Proveri da li je remote dodat
git remote -v

# Push-uj kod na GitHub
git push -u origin master
```

**NAPOMENA:** Zameni `YOUR_USERNAME` sa svojim GitHub korisničkim imenom!

### 3. Autentifikacija

Kada pokreneš `git push`, GitHub će tražiti autentifikaciju:

**Opcija 1: Personal Access Token (Preporučeno)**
1. Idi na GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Klikni "Generate new token (classic)"
3. Daj mu ime (npr. "Bob Car Rental")
4. Selektuj scope: `repo` (full control of private repositories)
5. Klikni "Generate token"
6. **KOPIRAJ TOKEN** (nećeš ga više videti!)
7. Kada git traži password, unesi ovaj token umesto passworda

**Opcija 2: GitHub CLI**
```powershell
# Instaliraj GitHub CLI
winget install --id GitHub.cli

# Autentifikuj se
gh auth login
```

### 4. Verifikuj upload

Nakon uspešnog push-a, proveri svoj GitHub repozitorijum u browseru:
```
https://github.com/YOUR_USERNAME/bobcarrental
```

## Trenutno stanje projekta

✅ **Lokalni git repozitorijum kreiran**
- Initial commit napravljen
- 393 fajla
- 61,906 linija koda
- Username: higroni
- Email: higroni@users.noreply.github.com

✅ **.gitignore konfigurisan**
- Isključeni: node_modules, target, database fajlovi, build artifakti

## Struktura projekta

```
bobcarrental/
├── backend/          # Spring Boot aplikacija
├── frontend/         # Angular aplikacija
├── migration/        # DBF to H2 migration scripts
├── docs/            # Dokumentacija
└── README.md        # Glavni README
```

## Sledeći koraci nakon GitHub upload-a

1. Dodaj README badge-ove (build status, license, itd.)
2. Konfiguriši GitHub Actions za CI/CD
3. Dodaj CONTRIBUTING.md
4. Konfiguriši GitHub Issues templates
5. Dodaj LICENSE fajl

## Pomoć

Ako imaš problema sa push-om:

```powershell
# Proveri status
git status

# Proveri remote
git remote -v

# Proveri log
git log --oneline

# Ako treba, force push (PAŽLJIVO!)
git push -f origin master
```

## Kontakt

Za pitanja i podršku, kontaktiraj: higroni