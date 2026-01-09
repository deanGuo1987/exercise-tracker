#!/usr/bin/env pwsh

Write-Host "🔧 重新生成 Gradle Wrapper" -ForegroundColor Green
Write-Host "=========================" -ForegroundColor Green
Write-Host ""

Write-Host "📋 问题分析:" -ForegroundColor Yellow
Write-Host "- 'lasspath' 错误表明 gradle wrapper 脚本可能损坏"
Write-Host "- 需要重新生成干净的 wrapper 文件"
Write-Host ""

Write-Host "🛠️ 解决方案:" -ForegroundColor Cyan
Write-Host "1. 删除现有的 gradle wrapper 文件"
Write-Host "2. 重新生成 wrapper"
Write-Host "3. 使用直接 gradle 构建作为备选"
Write-Host ""

# 备份现有文件
Write-Host "📦 备份现有 wrapper 文件..." -ForegroundColor Magenta
if (Test-Path "gradle/wrapper") {
    Copy-Item -Path "gradle/wrapper" -Destination "gradle/wrapper.backup" -Recurse -Force
    Write-Host "✅ 已备份到 gradle/wrapper.backup"
}

# 删除现有 wrapper 文件
Write-Host "🗑️ 删除现有 wrapper 文件..." -ForegroundColor Magenta
if (Test-Path "gradle/wrapper/gradle-wrapper.jar") {
    Remove-Item "gradle/wrapper/gradle-wrapper.jar" -Force
    Write-Host "✅ 删除 gradle-wrapper.jar"
}

if (Test-Path "gradlew") {
    Remove-Item "gradlew" -Force
    Write-Host "✅ 删除 gradlew"
}

if (Test-Path "gradlew.bat") {
    Remove-Item "gradlew.bat" -Force
    Write-Host "✅ 删除 gradlew.bat"
}

Write-Host ""
Write-Host "📝 创建新的 gradle wrapper 配置..." -ForegroundColor Cyan

# 创建新的 gradle-wrapper.properties
$wrapperProperties = @"
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
networkTimeout=10000
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
"@

Set-Content -Path "gradle/wrapper/gradle-wrapper.properties" -Value $wrapperProperties -Encoding UTF8
Write-Host "✅ 创建新的 gradle-wrapper.properties"

Write-Host ""
Write-Host "🚀 下一步:" -ForegroundColor Green
Write-Host "1. 推送更改到 GitHub"
Write-Host "2. 使用 'Direct Gradle Build' 工作流"
Write-Host "3. 这个工作流不依赖 wrapper，直接使用 gradle"
Write-Host ""

Write-Host "✨ 准备推送修复..." -ForegroundColor Green