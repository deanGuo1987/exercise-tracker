#!/usr/bin/env pwsh

Write-Host "🚀 推送 lasspath 错误修复" -ForegroundColor Green
Write-Host "========================" -ForegroundColor Green
Write-Host ""

Write-Host "📋 修复内容:" -ForegroundColor Cyan
Write-Host "✅ 创建直接 Gradle 构建工作流 (build-direct.yml)"
Write-Host "✅ 完全绕过 gradle wrapper"
Write-Host "✅ 使用官方 gradle-build-action"
Write-Host "✅ 添加详细调试信息"
Write-Host "✅ 更新 gradle wrapper 配置"
Write-Host ""

Write-Host "🔧 推送命令:" -ForegroundColor Yellow
Write-Host "git add ."
Write-Host "git commit -m 'Add direct Gradle build workflow to bypass wrapper issues'"
Write-Host "git push origin main"
Write-Host ""

try {
    Write-Host "📤 正在推送..." -ForegroundColor Magenta
    
    git add .
    if ($LASTEXITCODE -ne 0) {
        throw "Git add failed"
    }
    
    git commit -m "Add direct Gradle build workflow to bypass wrapper issues

- Create build-direct.yml that uses gradle directly instead of wrapper
- This completely avoids the 'lasspath' error by not using gradlew
- Uses gradle/gradle-build-action for reliable gradle setup
- Includes detailed debugging output
- Updated gradle-wrapper.properties with network timeout
- Added comprehensive solution documentation"
    
    if ($LASTEXITCODE -ne 0) {
        throw "Git commit failed"
    }
    
    git push origin main
    if ($LASTEXITCODE -ne 0) {
        throw "Git push failed"
    }
    
    Write-Host ""
    Write-Host "✅ 推送成功！" -ForegroundColor Green
    Write-Host ""
    Write-Host "🔍 下一步:" -ForegroundColor Yellow
    Write-Host "1. 访问: https://github.com/deanGuo1987/exercise-tracker/actions"
    Write-Host "2. 查看 'Direct Gradle Build (No Wrapper)' 工作流"
    Write-Host "3. 这个工作流应该能成功构建 APK"
    Write-Host ""
    
    # 打开 GitHub Actions 页面
    Start-Process "https://github.com/deanGuo1987/exercise-tracker/actions"
    
} catch {
    Write-Host ""
    Write-Host "❌ 推送失败: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "🔄 手动推送步骤:" -ForegroundColor Yellow
    Write-Host "1. 检查网络连接"
    Write-Host "2. 运行: git push origin main"
    Write-Host "3. 如果仍然失败，稍后重试"
}