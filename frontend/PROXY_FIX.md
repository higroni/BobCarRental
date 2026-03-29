# Angular Proxy Configuration Fix

## Problem
Frontend je pokušavao da direktno pristupi `http://localhost:8080/api/v1/clients`, ali Angular dev server je vraćao HTML umesto da prosledi zahtev backendu.

## Rešenje

### 1. Kreiran `proxy.conf.json`
```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "logLevel": "debug",
    "changeOrigin": true
  }
}
```

### 2. Ažuriran `angular.json`
Dodato `proxyConfig` u serve opcije:
```json
"serve": {
  "builder": "@angular/build:dev-server",
  "options": {
    "proxyConfig": "proxy.conf.json"
  },
  ...
}
```

### 3. Ažuriran `environment.ts`
Promenjen API URL sa apsolutnog na relativni:
```typescript
export const environment = {
  production: false,
  apiUrl: '/api/v1'  // Bilo: 'http://localhost:8080/api/v1'
};
```

## Kako radi

1. Frontend šalje zahtev na `/api/v1/clients`
2. Angular dev server presreće zahtev
3. Proxy prosleđuje zahtev na `http://localhost:8080/api/v1/clients`
4. Backend vraća JSON odgovor
5. Proxy prosleđuje odgovor frontendu

## Pokretanje

### Način 1: Batch fajl
```bash
cd bobcarrental/frontend
start.bat
```

### Način 2: NPM komanda
```bash
cd bobcarrental/frontend
npm start
```

### Način 3: Angular CLI
```bash
cd bobcarrental/frontend
ng serve
```

Svi načini automatski koriste proxy konfiguraciju.

## Testiranje

1. Pokreni backend: `cd bobcarrental/backend && mvnw spring-boot:run`
2. Pokreni frontend: `cd bobcarrental/frontend && npm start`
3. Otvori browser: `http://localhost:4200`
4. Login: `admin` / `admin123`
5. Idi na Clients stranicu
6. Proveri Network tab - zahtevi ka `/api/v1/clients` bi trebalo da vraćaju JSON

## Napomena

- Backend mora biti pokrenut na `http://localhost:8080`
- Frontend radi na `http://localhost:4200`
- Proxy automatski prosleđuje sve `/api/*` zahteve na backend
- CORS je konfigurisan na backendu da dozvoljava `http://localhost:4200`

## Made with Bob