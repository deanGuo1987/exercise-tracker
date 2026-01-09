# PowerShell script to download gradle wrapper jar
$wrapperUrl = "https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar"
$wrapperDir = "gradle/wrapper"
$wrapperJar = "$wrapperDir/gradle-wrapper.jar"

Write-Host "Downloading gradle wrapper jar..."

# Create wrapper directory if it doesn't exist
if (!(Test-Path $wrapperDir)) {
    New-Item -ItemType Directory -Path $wrapperDir -Force
}

try {
    # Download the gradle wrapper jar
    Invoke-WebRequest -Uri $wrapperUrl -OutFile $wrapperJar
    Write-Host "✓ Gradle wrapper jar downloaded successfully"
    
    # Verify the file was downloaded
    if (Test-Path $wrapperJar) {
        $fileSize = (Get-Item $wrapperJar).Length
        Write-Host "✓ File size: $fileSize bytes"
    } else {
        Write-Host "✗ Failed to download gradle wrapper jar"
        exit 1
    }
} catch {
    Write-Host "✗ Error downloading gradle wrapper: $($_.Exception.Message)"
    exit 1
}

Write-Host "Gradle wrapper setup complete!"