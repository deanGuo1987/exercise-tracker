Write-Host "=== Opening GitHub Actions Page ===" -ForegroundColor Green
Write-Host ""

$repoUrl = "https://github.com/deanGuo1987/exercise-tracker"
$actionsUrl = "$repoUrl/actions"

Write-Host "Repository: $repoUrl" -ForegroundColor Cyan
Write-Host "Actions URL: $actionsUrl" -ForegroundColor Cyan
Write-Host ""

Write-Host "Opening GitHub Actions page to check build status..." -ForegroundColor Yellow
Start-Process $actionsUrl

Write-Host ""
Write-Host "What to look for:" -ForegroundColor Green
Write-Host "1. Green checkmark = Build successful" -ForegroundColor White
Write-Host "2. Red X = Build failed" -ForegroundColor White  
Write-Host "3. Yellow circle = Build in progress" -ForegroundColor White
Write-Host ""
Write-Host "To download APK:" -ForegroundColor Green
Write-Host "1. Click on a successful build" -ForegroundColor White
Write-Host "2. Scroll down to 'Artifacts' section" -ForegroundColor White
Write-Host "3. Download 'exercise-tracker-debug-apk'" -ForegroundColor White
Write-Host "4. Extract the zip file to get the APK" -ForegroundColor White