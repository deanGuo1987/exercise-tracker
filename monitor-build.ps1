#!/usr/bin/env pwsh

Write-Host "🚀 GitHub Actions 构建监控" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""

$repoUrl = "https://github.com/deanGuo1987/exercise-tracker"
$actionsUrl = "$repoUrl/actions"

Write-Host "📋 构建信息:" -ForegroundColor Cyan
Write-Host "仓库: $repoUrl"
Write-Host "Actions: $actionsUrl"
Write-Host ""

Write-Host "📝 最新提交:" -ForegroundColor Yellow
git log --oneline -3
Write-Host ""

Write-Host "🔍 构建状态检查步骤:" -ForegroundColor Yellow
Write-Host "1. 访问 GitHub Actions 页面"
Write-Host "2. 查找最新的 'Build Android APK' 工作流"
Write-Host "3. 检查状态图标:"
Write-Host "   ✅ 绿色对勾 = 构建成功"
Write-Host "   ❌ 红色叉号 = 构建失败"  
Write-Host "   🟡 黄色圆圈 = 构建进行中"
Write-Host ""

Write-Host "📱 APK下载步骤 (构建成功后):" -ForegroundColor Green
Write-Host "1. 点击成功的构建记录"
Write-Host "2. 滚动到页面底部的 'Artifacts' 部分"
Write-Host "3. 下载 'exercise-tracker-debug-apk'"
Write-Host "4. 解压zip文件获取APK"
Write-Host "5. 安装到Android设备"
Write-Host ""

Write-Host "⏱️ 预计构建时间: 3-5分钟" -ForegroundColor Magenta
Write-Host "🔧 修复内容: 简化工作流，修复lasspath错误" -ForegroundColor Magenta
Write-Host ""

Write-Host "正在打开GitHub Actions页面..." -ForegroundColor Cyan
Start-Process $actionsUrl

Write-Host ""
Write-Host "✨ 构建已触发！请在浏览器中查看进度。" -ForegroundColor Green