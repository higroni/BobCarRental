/**
 * E2E Test Suite for Address Module CRUD Operations using Puppeteer
 * Tests: Create, Read, Update, Delete operations
 */

const puppeteer = require('puppeteer');

const BASE_URL = 'http://localhost:4200';
const TIMEOUT = 30000;

// Test data
const testAddress = {
  clientId: 'E2ETEST',
  dept: 'IT Department',
  desc: 'E2E Test Address',
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
  companyName: 'Test Company Ltd'
};

describe('Address Module E2E Tests', () => {
  let browser;
  let page;

  beforeAll(async () => {
    browser = await puppeteer.launch({
      headless: false, // Set to true for CI/CD
      slowMo: 50,
      args: ['--no-sandbox', '--disable-setuid-sandbox']
    });
  });

  afterAll(async () => {
    if (browser) {
      await browser.close();
    }
  });

  beforeEach(async () => {
    page = await browser.newPage();
    await page.setViewport({ width: 1920, height: 1080 });
    
    // Login
    await page.goto(`${BASE_URL}/login`, { waitUntil: 'networkidle2' });
    await page.type('input[formcontrolname="username"]', 'admin');
    await page.type('input[formcontrolname="password"]', 'admin');
    await page.click('button[type="submit"]');
    await page.waitForNavigation({ waitUntil: 'networkidle2' });
  });

  afterEach(async () => {
    if (page) {
      await page.close();
    }
  });

  test('1. Should navigate to Address List page', async () => {
    await page.goto(`${BASE_URL}/addresses`, { waitUntil: 'networkidle2' });
    
    const heading = await page.$eval('h1', el => el.textContent);
    expect(heading).toContain('Address');
    
    const newButton = await page.$('button:has-text("New Address")');
    expect(newButton).toBeTruthy();
  }, TIMEOUT);

  test('2. Should create a new address', async () => {
    await page.goto(`${BASE_URL}/addresses/new`, { waitUntil: 'networkidle2' });
    
    // Fill form
    await page.type('input[formcontrolname="clientId"]', testAddress.clientId);
    await page.type('input[formcontrolname="dept"]', testAddress.dept);
    await page.type('input[formcontrolname="desc"]', testAddress.desc);
    await page.type('input[formcontrolname="name"]', testAddress.name);
    await page.type('input[formcontrolname="address1"]', testAddress.address1);
    await page.type('input[formcontrolname="address2"]', testAddress.address2);
    await page.type('input[formcontrolname="address3"]', testAddress.address3);
    await page.type('input[formcontrolname="place"]', testAddress.place);
    await page.type('input[formcontrolname="city"]', testAddress.city);
    await page.type('input[formcontrolname="pinCode"]', testAddress.pinCode);
    await page.type('input[formcontrolname="phone"]', testAddress.phone);
    await page.type('input[formcontrolname="fax"]', testAddress.fax);
    await page.type('input[formcontrolname="category"]', testAddress.category);
    await page.type('input[formcontrolname="companyName"]', testAddress.companyName);
    
    // Submit
    await page.click('button[type="submit"]');
    await page.waitForNavigation({ waitUntil: 'networkidle2' });
    
    // Verify in list
    const content = await page.content();
    expect(content).toContain(testAddress.clientId);
  }, TIMEOUT);

  test('3. Should view address details', async () => {
    await page.goto(`${BASE_URL}/addresses`, { waitUntil: 'networkidle2' });
    
    // Find and click view button for test address
    const viewButtons = await page.$$('button[mattoolti p="View Details"]');
    if (viewButtons.length > 0) {
      await viewButtons[0].click();
      await page.waitForTimeout(1000);
      
      const dialogContent = await page.content();
      expect(dialogContent).toContain('Address Details');
    }
  }, TIMEOUT);

  test('4. Should edit an address', async () => {
    await page.goto(`${BASE_URL}/addresses`, { waitUntil: 'networkidle2' });
    
    // Find and click edit button
    const editButtons = await page.$$('button[mattooltip="Edit"]');
    if (editButtons.length > 0) {
      await editButtons[0].click();
      await page.waitForNavigation({ waitUntil: 'networkidle2' });
      
      // Update a field
      await page.evaluate(() => {
        const input = document.querySelector('input[formcontrolname="desc"]');
        if (input) input.value = '';
      });
      await page.type('input[formcontrolname="desc"]', 'Updated Description');
      
      // Submit
      await page.click('button[type="submit"]');
      await page.waitForNavigation({ waitUntil: 'networkidle2' });
      
      const content = await page.content();
      expect(content).toContain('Updated');
    }
  }, TIMEOUT);

  test('5. Should delete an address', async () => {
    await page.goto(`${BASE_URL}/addresses`, { waitUntil: 'networkidle2' });
    
    // Get initial row count
    const initialRows = await page.$$('table tbody tr');
    const initialCount = initialRows.length;
    
    // Find and click delete button for test address
    const deleteButtons = await page.$$('button[mattooltip="Delete"]');
    if (deleteButtons.length > 0) {
      await deleteButtons[0].click();
      await page.waitForTimeout(500);
      
      // Confirm deletion
      const confirmButton = await page.$('button:has-text("Delete")');
      if (confirmButton) {
        await confirmButton.click();
        await page.waitForTimeout(1000);
        
        // Verify row count decreased
        const finalRows = await page.$$('table tbody tr');
        expect(finalRows.length).toBeLessThan(initialCount);
      }
    }
  }, TIMEOUT);

  test('6. Should search addresses', async () => {
    await page.goto(`${BASE_URL}/addresses`, { waitUntil: 'networkidle2' });
    
    // Type in search box
    const searchInput = await page.$('input[placeholder*="Search"]');
    if (searchInput) {
      await searchInput.type('Test');
      await page.waitForTimeout(1000);
      
      // Verify filtered results
      const rows = await page.$$('table tbody tr');
      expect(rows.length).toBeGreaterThan(0);
    }
  }, TIMEOUT);
});

console.log('Address Module E2E Tests - Ready to run with: npm test');

// Made with Bob