# PowerShell script to build APK manually
Write-Host "=== 手动构建Android APK ==="

# Check for Java
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "✓ Java found: $javaVersion"
} catch {
    Write-Host "✗ Java not found. Please install Java JDK."
    exit 1
}

# Create build directories
$buildDir = "build"
$classesDir = "$buildDir/classes"
$resDir = "$buildDir/res"
$apkDir = "$buildDir/apk"

Write-Host "Creating build directories..."
New-Item -ItemType Directory -Path $classesDir -Force | Out-Null
New-Item -ItemType Directory -Path $resDir -Force | Out-Null  
New-Item -ItemType Directory -Path $apkDir -Force | Out-Null

# Try to find Android SDK
$possibleSdkPaths = @(
    "$env:ANDROID_HOME",
    "$env:ANDROID_SDK_ROOT",
    "$env:LOCALAPPDATA\Android\Sdk",
    "C:\Users\$env:USERNAME\AppData\Local\Android\Sdk",
    "C:\Android\Sdk"
)

$androidSdk = $null
foreach ($path in $possibleSdkPaths) {
    if ($path -and (Test-Path "$path\platforms")) {
        $androidSdk = $path
        Write-Host "✓ Android SDK found at: $androidSdk"
        break
    }
}

if (-not $androidSdk) {
    Write-Host "✗ Android SDK not found. Please install Android Studio or Android SDK."
    Write-Host "Searched paths:"
    foreach ($path in $possibleSdkPaths) {
        if ($path) { Write-Host "  - $path" }
    }
    exit 1
}

# Find Android platform
$platformsDir = "$androidSdk\platforms"
$platforms = Get-ChildItem $platformsDir | Where-Object { $_.Name -match "android-\d+" } | Sort-Object Name -Descending

if ($platforms.Count -eq 0) {
    Write-Host "✗ No Android platforms found in $platformsDir"
    exit 1
}

$targetPlatform = $platforms[0].Name
$androidJar = "$platformsDir\$targetPlatform\android.jar"

Write-Host "✓ Using platform: $targetPlatform"
Write-Host "✓ Android JAR: $androidJar"

# Find build tools
$buildToolsDir = "$androidSdk\build-tools"
$buildTools = Get-ChildItem $buildToolsDir | Sort-Object Name -Descending

if ($buildTools.Count -eq 0) {
    Write-Host "✗ No build tools found in $buildToolsDir"
    exit 1
}

$buildToolsVersion = $buildTools[0].Name
$buildToolsPath = "$buildToolsDir\$buildToolsVersion"

Write-Host "✓ Using build tools: $buildToolsVersion"

# Check required tools
$aapt = "$buildToolsPath\aapt.exe"
$dx = "$buildToolsPath\dx.bat"
$zipalign = "$buildToolsPath\zipalign.exe"
$apksigner = "$buildToolsPath\apksigner.bat"

if (-not (Test-Path $aapt)) {
    Write-Host "✗ aapt not found at $aapt"
    exit 1
}

Write-Host "✓ Build tools ready"

# Generate R.java
Write-Host "Generating R.java..."
try {
    & $aapt package -f -m -J app/src/main/java -M app/src/main/AndroidManifest.xml -S app/src/main/res -I $androidJar
    Write-Host "✓ R.java generated"
} catch {
    Write-Host "✗ Failed to generate R.java: $($_.Exception.Message)"
    exit 1
}

Write-Host "✓ APK build preparation complete!"
Write-Host ""
Write-Host "注意：完整的APK构建需要："
Write-Host "1. 编译Kotlin源代码（需要kotlinc）"
Write-Host "2. 处理资源文件"
Write-Host "3. 生成DEX文件"
Write-Host "4. 打包和签名APK"
Write-Host ""
Write-Host "建议使用Android Studio打开项目并构建APK，或者安装完整的gradle环境。"