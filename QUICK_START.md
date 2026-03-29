# Bob Car Rental - Quick Start Guide

## 🚀 One-Click Startup

### Backend
```bash
cd bobcarrental/backend
run-full-test.bat
```
This will:
- Compile the backend
- Start Spring Boot application
- Run API tests
- Keep server running

### Frontend
```bash
cd bobcarrental/frontend
fix-and-run.bat
```
This will:
- Install dependencies
- Build the project
- Run linter
- Start dev server with proxy

## 🌐 Access Points

- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console

## 🔑 Default Login

- **Username**: `admin`
- **Password**: `admin123`

## 📝 Current Issues & Fixes

### Issue 1: Client Model Mismatch ✅ FIXED
**Problem**: Frontend ClientResponse model doesn't match backend
**Solution**: Updated model to match backend fields

### Issue 2: Empty String Handling ✅ FIXED
**Problem**: Backend throws NumberFormatException on empty strings
**Solution**: Added MapStruct converter to handle empty strings as null

### Issue 3: Search Not Working ⚠️ IN PROGRESS
**Problem**: Table filter doesn't work
**Solution**: Custom filterPredicate being added

## 🔧 Manual Fixes Needed

If you encounter TypeScript errors in the frontend, run:

```bash
cd bobcarrental/frontend
npm run lint --fix
```

## 📊 Testing Workflow

1. Start backend: `bobcarrental/backend/run-full-test.bat`
2. Wait for "Started BobCarRentalApplication"
3. Start frontend: `bobcarrental/frontend/fix-and-run.bat`
4. Open browser: http://localhost:4200
5. Login with admin/admin123
6. Test CRUD operations on Clients module

## 🐛 Debugging

### Backend Logs
Check console output from `run-full-test.bat`

### Frontend Errors
- Open browser DevTools (F12)
- Check Console tab for errors
- Check Network tab for API calls

### Common Issues

**401 Unauthorized**
- Token expired, login again
- Check if backend is running

**Proxy Error**
- Ensure backend is running on port 8080
- Check `proxy.conf.json` configuration

**Blank Screen**
- Check browser console for errors
- Verify Zone.js is installed
- Check routing configuration

## 📁 Project Structure

```
bobcarrental/
├── backend/          # Spring Boot API
│   ├── src/
│   ├── pom.xml
│   └── run-full-test.bat
├── frontend/         # Angular 21 UI
│   ├── src/
│   ├── angular.json
│   ├── proxy.conf.json
│   └── fix-and-run.bat
├── migration/        # DBF to H2 migration
└── docs/            # Documentation
```

## 🎯 Next Steps

1. ✅ Fix remaining TypeScript errors
2. ✅ Test full CRUD cycle
3. ⏳ Implement remaining 9 modules
4. ⏳ Add proper error handling
5. ⏳ Create shared components
6. ⏳ E2E testing with Playwright

## 💡 Tips

- Always start backend before frontend
- Use Chrome DevTools for debugging
- Check both console outputs for errors
- Backend auto-reloads on code changes
- Frontend auto-reloads on file saves

---
Made with ❤️ by Bob