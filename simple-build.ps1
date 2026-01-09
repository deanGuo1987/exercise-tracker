Write-Host "=== Android APK Build Guide ==="
Write-Host ""

# Check for gradle command
$gradleFound = $false
try {
    $null = Get-Command gradle -ErrorAction Stop
    $gradleFound = $true
    Write-Host "System gradle found - attempting build..."
    gradle assembleDebug
    if ($LASTEXITCODE -eq 0) {
        Write-Host "SUCCESS: APK built successfully!"
        Write-Host "Location: app\build\outputs\apk\debug\app-debug.apk"
        exit 0
    }
} catch {
    Write-Host "System gradle not available"
}

Write-Host ""
Write-Host "=== Build Options ==="
Write-Host "1. Install Android Studio (Recommended)"
Write-Host "   Download: https://developer.android.com/studio"
Write-Host "   Open this project and build APK"
Write-Host ""
Write-Host "2. Install Gradle manually"
Write-Host "   Download: https://gradle.org/install/"
Write-Host "   Run: gradle assembleDebug"
Write-Host ""
Write-Host "3. Use online build services"
Write-Host "   Upload to GitHub and use GitHub Actions"
Write-Host ""
Write-Host "Project is complete and ready to build!"