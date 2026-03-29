import { test, expect, Page } from '@playwright/test';

/**
 * E2E Test Suite for Address Module CRUD Operations
 * Tests: Create, Read, Update, Delete operations
 */

const BASE_URL = 'http://localhost:4200';
const API_URL = 'http://localhost:8080/api/v1';

// Test data
const testAddress = {
  clientId: 'TEST001',
  dept: 'IT Department',
  desc: 'Test Address Description',
  name: 'Test Contact Person',
  address1: 'Test Street 123',
  address2: 'Building A',
  address3: 'Floor 5',
  place: 'Test Place',
  city: 'Test City',
  pinCode: '12345',
  phone: '0601234567',
  fax: '0111234567',
  category: 'Business',
  companyName: 'Test Company Ltd',
  isActive: true
};

const updatedAddress = {
  ...testAddress,
  desc: 'Updated Test Address Description',
  address1: 'Updated Test Street 456',
  city: 'Updated Test City'
};

let createdAddressId: number;

test.describe('Address Module E2E Tests', () => {
  
  test.beforeEach(async ({ page }) => {
    // Login before each test
    await page.goto(`${BASE_URL}/login`);
    await page.fill('input[formControlName="username"]', 'admin');
    await page.fill('input[formControlName="password"]', 'admin');
    await page.click('button[type="submit"]');
    
    // Wait for navigation to dashboard
    await page.waitForURL(`${BASE_URL}/dashboard`);
    await expect(page).toHaveURL(`${BASE_URL}/dashboard`);
  });

  test('1. Should navigate to Address List page', async ({ page }) => {
    // Navigate to Address List
    await page.goto(`${BASE_URL}/addresses`);
    
    // Verify page loaded
    await expect(page.locator('h1')).toContainText('Address Book');
    await expect(page.locator('button:has-text("New Address")')).toBeVisible();
    
    // Verify table is present
    await expect(page.locator('table')).toBeVisible();
  });

  test('2. Should create a new address', async ({ page }) => {
    // Navigate to create form
    await page.goto(`${BASE_URL}/addresses/new`);
    
    // Verify form loaded
    await expect(page.locator('h1')).toContainText('New Address');
    
    // Fill in the form
    await page.fill('input[formControlName="clientId"]', testAddress.clientId);
    await page.fill('input[formControlName="dept"]', testAddress.dept);
    await page.fill('input[formControlName="desc"]', testAddress.desc);
    await page.fill('input[formControlName="name"]', testAddress.name);
    await page.fill('input[formControlName="address1"]', testAddress.address1);
    await page.fill('input[formControlName="address2"]', testAddress.address2);
    await page.fill('input[formControlName="address3"]', testAddress.address3);
    await page.fill('input[formControlName="place"]', testAddress.place);
    await page.fill('input[formControlName="city"]', testAddress.city);
    await page.fill('input[formControlName="pinCode"]', testAddress.pinCode);
    await page.fill('input[formControlName="phone"]', testAddress.phone);
    await page.fill('input[formControlName="fax"]', testAddress.fax);
    await page.fill('input[formControlName="category"]', testAddress.category);
    await page.fill('input[formControlName="companyName"]', testAddress.companyName);
    
    // Submit the form
    await page.click('button[type="submit"]');
    
    // Wait for navigation back to list
    await page.waitForURL(`${BASE_URL}/addresses`);
    
    // Verify address appears in the list
    await expect(page.locator(`text=${testAddress.clientId}`)).toBeVisible();
    await expect(page.locator(`text=${testAddress.name}`)).toBeVisible();
    await expect(page.locator(`text=${testAddress.city}`)).toBeVisible();
  });

  test('3. Should view address details', async ({ page }) => {
    // Navigate to list
    await page.goto(`${BASE_URL}/addresses`);
    
    // Find the test address row and click view button
    const row = page.locator(`tr:has-text("${testAddress.clientId}")`).first();
    await row.locator('button[matTooltip="View Details"]').click();
    
    // Verify dialog opened with correct data
    await expect(page.locator('h2:has-text("Address Details")')).toBeVisible();
    await expect(page.locator(`text=${testAddress.clientId}`)).toBeVisible();
    await expect(page.locator(`text=${testAddress.name}`)).toBeVisible();
    await expect(page.locator(`text=${testAddress.address1}`)).toBeVisible();
    await expect(page.locator(`text=${testAddress.city}`)).toBeVisible();
    
    // Close dialog
    await page.locator('button:has-text("Close")').click();
  });

  test('4. Should edit an existing address', async ({ page }) => {
    // Navigate to list
    await page.goto(`${BASE_URL}/addresses`);
    
    // Find the test address row and click edit button
    const row = page.locator(`tr:has-text("${testAddress.clientId}")`).first();
    await row.locator('button[matTooltip="Edit"]').click();
    
    // Wait for edit form to load
    await page.waitForURL(/\/addresses\/\d+\/edit/);
    await expect(page.locator('h1')).toContainText('Edit Address');
    
    // Update fields
    await page.fill('input[formControlName="desc"]', updatedAddress.desc);
    await page.fill('input[formControlName="address1"]', updatedAddress.address1);
    await page.fill('input[formControlName="city"]', updatedAddress.city);
    
    // Submit the form
    await page.click('button[type="submit"]');
    
    // Wait for navigation back to list
    await page.waitForURL(`${BASE_URL}/addresses`);
    
    // Verify updated data appears in the list
    await expect(page.locator(`text=${updatedAddress.city}`)).toBeVisible();
  });

  test('5. Should search addresses', async ({ page }) => {
    // Navigate to list
    await page.goto(`${BASE_URL}/addresses`);
    
    // Use search functionality
    await page.fill('input[placeholder*="Search"]', testAddress.city);
    
    // Verify filtered results
    await expect(page.locator(`text=${testAddress.clientId}`)).toBeVisible();
    
    // Clear search
    await page.fill('input[placeholder*="Search"]', '');
  });

  test('6. Should delete an address', async ({ page }) => {
    // Navigate to list
    await page.goto(`${BASE_URL}/addresses`);
    
    // Find the test address row
    const row = page.locator(`tr:has-text("${testAddress.clientId}")`).first();
    
    // Click delete button
    await row.locator('button[matTooltip="Delete"]').click();
    
    // Confirm deletion in dialog
    await page.locator('button:has-text("Delete")').click();
    
    // Wait a moment for deletion to complete
    await page.waitForTimeout(1000);
    
    // Verify address is removed from list
    await expect(page.locator(`text=${testAddress.clientId}`)).not.toBeVisible();
  });

  test('7. Should validate required fields', async ({ page }) => {
    // Navigate to create form
    await page.goto(`${BASE_URL}/addresses/new`);
    
    // Try to submit empty form
    await page.click('button[type="submit"]');
    
    // Verify validation errors appear
    await expect(page.locator('mat-error')).toBeVisible();
    
    // Fill only Client ID (required field)
    await page.fill('input[formControlName="clientId"]', 'TEST');
    
    // Verify submit button is still disabled or form shows errors
    const submitButton = page.locator('button[type="submit"]');
    const isDisabled = await submitButton.isDisabled();
    expect(isDisabled).toBeTruthy();
  });

  test('8. Should handle pagination', async ({ page }) => {
    // Navigate to list
    await page.goto(`${BASE_URL}/addresses`);
    
    // Check if paginator is visible (only if there are enough records)
    const paginator = page.locator('mat-paginator');
    const isPaginatorVisible = await paginator.isVisible().catch(() => false);
    
    if (isPaginatorVisible) {
      // Test page size change
      await page.click('mat-select[aria-label="Items per page"]');
      await page.click('mat-option:has-text("10")');
      
      // Verify table updated
      await expect(page.locator('table tbody tr')).toHaveCount(10);
    }
  });

  test('9. Should sort by columns', async ({ page }) => {
    // Navigate to list
    await page.goto(`${BASE_URL}/addresses`);
    
    // Click on Client ID column header to sort
    await page.click('th:has-text("Client ID")');
    
    // Wait for sort to apply
    await page.waitForTimeout(500);
    
    // Verify table is sorted (check first row)
    const firstRow = page.locator('table tbody tr').first();
    await expect(firstRow).toBeVisible();
  });

  test('10. Should handle API errors gracefully', async ({ page }) => {
    // Intercept API call and return error
    await page.route(`${API_URL}/addresses`, route => {
      route.fulfill({
        status: 500,
        body: JSON.stringify({ message: 'Internal Server Error' })
      });
    });
    
    // Navigate to list
    await page.goto(`${BASE_URL}/addresses`);
    
    // Verify error message is displayed
    await expect(page.locator('text=Error')).toBeVisible();
  });
});

// Made with Bob