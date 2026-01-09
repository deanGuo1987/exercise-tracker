# GitHub仓库设置脚本
param(
    [Parameter(Mandatory=$true)]
    [string]$RepoUrl,
    
    [Parameter(Mandatory=$false)]
    [string]$BranchName = "main"
)

Write-Host "=== GitHub仓库设置脚本 ===" -ForegroundColor Green
Write-Host ""

# 检查Git是否安装
try {
    $gitVersion = git --version
    Write-Host "✓ Git已安装: $gitVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Git未安装，请先安装Git" -ForegroundColor Red
    Write-Host "下载地址: https://git-scm.com/download/win"
    exit 1
}

# 检查是否已经是Git仓库
if (Test-Path ".git") {
    Write-Host "⚠ 当前目录已经是Git仓库" -ForegroundColor Yellow
    $continue = Read-Host "是否继续设置远程仓库? (y/N)"
    if ($continue -ne "y" -and $continue -ne "Y") {
        exit 0
    }
} else {
    Write-Host "初始化Git仓库..." -ForegroundColor Cyan
    git init
    Write-Host "✓ Git仓库初始化完成" -ForegroundColor Green
}

# 添加所有文件
Write-Host "添加项目文件..." -ForegroundColor Cyan
git add .

# 检查是否有文件被添加
$status = git status --porcelain
if ([string]::IsNullOrEmpty($status)) {
    Write-Host "⚠ 没有文件需要提交" -ForegroundColor Yellow
} else {
    Write-Host "✓ 已添加 $(($status -split "`n").Count) 个文件" -ForegroundColor Green
}

# 创建初始提交
Write-Host "创建初始提交..." -ForegroundColor Cyan
try {
    git commit -m "Initial commit: 运动记录应用完整项目

✨ 功能特性:
- 📅 日历界面显示和导航
- 🏃‍♂️ 运动记录创建（20/30/40分钟选项）
- 💾 数据持久化存储
- 🔔 每日11:30运动提醒
- 🔒 记录不可变性保证

🧪 测试覆盖:
- 属性测试 (Property-Based Testing)
- 单元测试
- 集成测试

🚀 CI/CD:
- GitHub Actions自动构建
- 自动APK生成
- 自动发布流程"

    Write-Host "✓ 初始提交创建完成" -ForegroundColor Green
} catch {
    Write-Host "⚠ 提交创建失败，可能没有新的更改" -ForegroundColor Yellow
}

# 设置远程仓库
Write-Host "设置远程仓库..." -ForegroundColor Cyan
try {
    git remote add origin $RepoUrl
    Write-Host "✓ 远程仓库设置完成: $RepoUrl" -ForegroundColor Green
} catch {
    Write-Host "⚠ 远程仓库可能已存在，尝试更新..." -ForegroundColor Yellow
    git remote set-url origin $RepoUrl
    Write-Host "✓ 远程仓库URL已更新" -ForegroundColor Green
}

# 设置主分支
Write-Host "设置主分支为: $BranchName" -ForegroundColor Cyan
git branch -M $BranchName

# 推送到远程仓库
Write-Host "推送到远程仓库..." -ForegroundColor Cyan
try {
    git push -u origin $BranchName
    Write-Host "✓ 代码推送成功!" -ForegroundColor Green
} catch {
    Write-Host "✗ 推送失败，请检查:" -ForegroundColor Red
    Write-Host "  1. 仓库URL是否正确" -ForegroundColor Yellow
    Write-Host "  2. 是否有推送权限" -ForegroundColor Yellow
    Write-Host "  3. 网络连接是否正常" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "🎉 GitHub仓库设置完成!" -ForegroundColor Green
Write-Host ""
Write-Host "📋 下一步操作:" -ForegroundColor Cyan
Write-Host "1. 访问GitHub仓库页面: $RepoUrl" -ForegroundColor White
Write-Host "2. 进入 'Actions' 标签页查看自动构建状态" -ForegroundColor White
Write-Host "3. 等待构建完成后下载APK文件" -ForegroundColor White
Write-Host ""
Write-Host "🚀 自动构建功能:" -ForegroundColor Cyan
Write-Host "- ✅ 每次推送代码都会自动构建APK" -ForegroundColor White
Write-Host "- ✅ 可以在Actions页面下载构建的APK" -ForegroundColor White
Write-Host "- ✅ 创建tag (如 v1.0.0) 会自动创建Release" -ForegroundColor White
Write-Host ""
Write-Host "📱 创建Release示例:" -ForegroundColor Cyan
Write-Host "git tag v1.0.0" -ForegroundColor Gray
Write-Host "git push origin v1.0.0" -ForegroundColor Gray