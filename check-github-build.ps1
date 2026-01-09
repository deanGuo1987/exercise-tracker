#!/usr/bin/env pwsh

Write-Host "=== GitHub Actions Build Status Check ===" -ForegroundColor Green
Write-Host ""

$repoUrl = "https://github.com/deanGuo1987/exercise-tracker"
$actionsUrl = "$repoUrl/actions"

Write-Host "Repository: $repoUrl" -ForegroundColor Cyan
Write-Host "Actions URL: $actionsUrl" -ForegroundColor Cyan
Write-Host ""

Write-Host "Latest commits:" -ForegroundColor Yellow
git log --oneline -3

Write-Host ""
Write-Host "To check build status:" -ForegroundColor Yellow
Write-Host "1. Open: $actionsUrl"
Write-Host "2. Look for the latest 'Build Android APK' workflow"
Write-Host "3. Check if it shows:"
Write-Host "   ✅ Green checkmark = Build successful"
Write-Host "   ❌ Red X = Build failed"
Write-Host "   🟡 Yellow circle = Build in progress"
Write-Host ""

Write-Host "If build is successful:" -ForegroundColor Green
Write-Host "1. Click on the successful build"
Write-Host "2. Scroll down to 'Artifacts' section"
Write-Host "3. Download 'exercise-tracker-debug-apk'"
Write-Host "4. Extract the zip file to get the APK"
Write-Host ""

Write-Host "Opening GitHub Actions page..." -ForegroundColor Cyan
Start-Process $actionsUrl

Write-Host ""
Write-Host "Build should complete in 3-5 minutes." -ForegroundColor Green
Write-Host "The simplified workflow should fix the 'lasspath' error." -ForegroundColor Green