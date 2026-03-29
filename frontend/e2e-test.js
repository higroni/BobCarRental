/**
 * E2E Test for Client Edit functionality
 * Run with: node e2e-test.js
 */

const puppeteer = require('puppeteer');
const fs = require('fs');
const path = require('path');

async function testClientEdit() {
  console.log('🚀 Starting E2E test...\n');
  
  // Create screenshots directory if it doesn't exist
  const screenshotsDir = path.join(__dirname, 'test-screenshots');
  if (!fs.existsSync(screenshotsDir)) {
    fs.mkdirSync(screenshotsDir, { recursive: true });
  }
  
  const browser = await puppeteer.launch({
    headless: false, // Set to true for CI/CD
    slowMo: 100 // Slow down for visibility
  });
  
  const page = await browser.newPage();
  
  // Capture console logs
  page.on('console', msg => {
    const type = msg.type();
    const text = msg.text();
    if (type === 'error') {
      console.log('❌ Browser Error:', text);
    } else if (type === 'warning') {
      console.log('⚠️  Browser Warning:', text);
    }
  });
  
  // Capture network errors
  page.on('requestfailed', request => {
    console.log('❌ Network Error:', request.url(), request.failure().errorText);
  });
  
  try {
    // Step 1: Navigate to login
    console.log('📍 Step 1: Navigating to login page...');
    await page.goto('http://localhost:4200/login', { waitUntil: 'networkidle2' });
    await page.screenshot({ path: 'test-screenshots/01-login.png' });
    
    // Step 2: Login
    console.log('📍 Step 2: Logging in...');
    await page.type('input[formcontrolname="username"]', 'admin');
    await page.type('input[formcontrolname="password"]', 'admin');
    await page.click('button[type="submit"]');
    await page.waitForNavigation({ waitUntil: 'networkidle2' });
    await page.screenshot({ path: 'test-screenshots/02-dashboard.png' });
    console.log('✅ Login successful\n');
    
    // Step 3: Navigate to clients
    console.log('📍 Step 3: Navigating to clients list...');
    await page.goto('http://localhost:4200/clients', { waitUntil: 'networkidle2' });
    await page.screenshot({ path: 'test-screenshots/03-clients-list.png' });
    console.log('✅ Clients list loaded\n');
    
    // Step 4: Click Edit button
    console.log('📍 Step 4: Clicking Edit button...');
    const editButton = await page.$('button[mattooltip="Edit"]');
    if (!editButton) {
      throw new Error('Edit button not found!');
    }
    await editButton.click();
    
    // Wait for navigation or form to appear
    await new Promise(resolve => setTimeout(resolve, 2000));
    await page.screenshot({ path: 'test-screenshots/04-after-edit-click.png' });
    
    // Check current URL
    const currentUrl = page.url();
    console.log('📍 Current URL:', currentUrl);
    
    // Check if form is visible
    const formVisible = await page.$('form[formGroup="clientForm"]') !== null;
    console.log('📍 Form visible:', formVisible);
    
    if (currentUrl.includes('/clients/edit/')) {
      console.log('✅ Navigation to edit page successful\n');
      
      // Check for form fields
      const fields = await page.$$eval('mat-form-field', fields => fields.length);
      console.log('📍 Form fields found:', fields);
      
      if (fields > 0) {
        console.log('✅ Edit form loaded successfully!\n');
      } else {
        console.log('❌ Edit form not loaded - no form fields found\n');
      }
    } else {
      console.log('❌ Navigation failed - still on:', currentUrl, '\n');
    }
    
    // Get all console errors
    const errors = await page.evaluate(() => {
      return window.console.errors || [];
    });
    
    if (errors.length > 0) {
      console.log('❌ Console Errors:', errors);
    }
    
    console.log('\n✅ Test completed!');
    
  } catch (error) {
    console.error('❌ Test failed:', error.message);
    await page.screenshot({ path: 'test-screenshots/error.png' });
  } finally {
    await browser.close();
  }
}

// Run test
testClientEdit().catch(console.error);

// Made with Bob
