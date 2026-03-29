@echo off
echo Rebuilding MapStruct Mappers...
echo.

echo Step 1: Cleaning target directory...
call mvn clean

echo.
echo Step 2: Compiling and generating mappers...
call mvn compile

echo.
echo Step 3: Checking generated mapper implementation...
if exist "target\generated-sources\annotations\com\bobcarrental\mapper\TripSheetMapperImpl.java" (
    echo ✓ TripSheetMapperImpl.java generated successfully
    echo.
    echo Showing first 50 lines of generated mapper:
    type "target\generated-sources\annotations\com\bobcarrental\mapper\TripSheetMapperImpl.java" | more
) else (
    echo ✗ ERROR: TripSheetMapperImpl.java NOT generated!
    echo.
    echo Please check for compilation errors above.
)

echo.
pause

@REM Made with Bob
