#!/usr/bin/env pwsh

Write-Host "🔧 GitHub Actions 构建修复监控" -ForegroundColor Green
Write-Host "====================================" -ForegroundColor Green
Write-Host ""

Write-Host "📋 修复内容:" -ForegroundColor Cyan
Write-Host "✅ 更新 Gradle Wrapper 到 8.5 版本"
Write-Host "✅ 优化 gradle.properties 配置"
Write-Host "✅ 增强 build-apk.yml 调试功能"
Write-Host "✅ 添加简化版 build-simple.yml 工作流"
Write-Host "✅ 禁用 gradle daemon 和并行构建"
Write-Host "✅ 添加 gradle wrapper 验证步骤"
Write-Host ""

Write-Host "📝 最新提交:" -ForegroundColor Yellow
git log --oneline -3
Write-Host ""

Write-Host "🚀 现在有两个构建工作流:" -ForegroundColor Magenta
Write-Host "1. Build Android APK (增强版，包含详细调试)"
Write-Host "2. Simple Android Build (简化版，作为备用)"
Write-Host ""

$repoUrl = "https://github.com/deanGuo1987/exercise-tracker"
$actionsUrl = "$repoUrl/actions"

Write-Host "🔍 监控步骤:" -ForegroundColor Yellow
Write-Host "1. 访问: $actionsUrl"
Write-Host "2. 查看两个工作流的运行状态"
Write-Host "3. 如果主工作流仍然失败，简化版应该能成功"
Write-Host ""

Write-Host "📱 成功后下载APK:" -ForegroundColor Green
Write-Host "1. 点击成功的构建"
Write-Host "2. 下载 Artifacts 中的APK文件"
Write-Host ""

Write-Host "正在打开 GitHub Actions 页面..." -ForegroundColor Cyan
Start-Process $actionsUrl

Write-Host ""
Write-Host "✨ 修复已推送！应该能解决 'lasspath' 错误。" -ForegroundColor Green