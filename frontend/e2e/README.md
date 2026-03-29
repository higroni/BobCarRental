# E2E Testing

## Test Files

- `address-crud-puppeteer.spec.js` - Puppeteer-based E2E tests for Address module
- `address-crud.spec.ts` - Playwright tests (requires installation)

## Running Puppeteer Tests

Puppeteer is already installed. To run tests manually:

```bash
cd bobcarrental/frontend
node e2e/address-crud-puppeteer.spec.js
```

Note: The test file uses Jest syntax but can be adapted to run standalone with Puppeteer.

## Running Playwright Tests (Optional)

Install Playwright:
```bash
npm install -D @playwright/test
npx playwright install
```

Run tests:
```bash
npx playwright test e2e/address-crud.spec.ts
```

## Test Coverage

Each test suite covers:
- Navigation to module pages
- Create new records
- View record details
- Edit existing records
- Delete records
- Search functionality
- Form validation
- Pagination and sorting

## Prerequisites

- Backend running on http://localhost:8080
- Frontend running on http://localhost:4200
- Test credentials: admin/admin

## Made with Bob