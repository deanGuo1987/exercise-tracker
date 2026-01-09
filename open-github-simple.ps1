Write-Host "=== GitHub Pages Opener ===" -ForegroundColor Green
Write-Host ""

$repoUrl = "https://github.com/deanGuo1987/exercise-tracker"

Write-Host "Opening GitHub pages for your Exercise Tracker project..." -ForegroundColor Cyan
Write-Host ""

# Open Actions page (most important for checking build status)
Write-Host "1. Opening Actions page to check build status..." -ForegroundColor Green
Start-Process "$repoUrl/actions"

Start-Sleep -Seconds 2

# Open main repository page
Write-Host "2. Opening repository main page..." -ForegroundColor Green
Start-Process $repoUrl

Write-Host ""
Write-Host "Pages opened in your browser!" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Check the Actions page for build status" -ForegroundColor White
Write-Host "2. Look for green checkmark (success) or red X (failed)" -ForegroundColor White
Write-Host "3. If successful, click on the build to see details" -ForegroundColor White
Write-Host "4. Scroll to bottom and find 'Artifacts' section" -ForegroundColor White
Write-Host "5. Download 'exercise-tracker-debug-apk'" -ForegroundColor White
Write-Host "6. Extract the zip file to get the APK" -ForegroundColor White
Write-Host ""
Write-Host "Repository URL: $repoUrl" -ForegroundColor Gray
Write-Host "Actions URL: $repoUrl/actions" -ForegroundColor Gray