# 快速打开GitHub相关页面的脚本

param(
    [Parameter(Mandatory=$false)]
    [string]$RepoUrl = "https://github.com/deanGuo1987/exercise-tracker"
)

Write-Host "=== GitHub Pages Opener ===" -ForegroundColor Green
Write-Host ""

# 定义要打开的页面
$pages = @{
    "1" = @{
        "name" = "仓库主页"
        "url" = $RepoUrl
        "description" = "查看项目概览和README"
    }
    "2" = @{
        "name" = "Actions页面"
        "url" = "$RepoUrl/actions"
        "description" = "查看构建状态和下载APK"
    }
    "3" = @{
        "name" = "Releases页面"
        "url" = "$RepoUrl/releases"
        "description" = "查看正式发布版本"
    }
    "4" = @{
        "name" = "最新构建"
        "url" = "$RepoUrl/actions/workflows/build-apk.yml"
        "description" = "直接查看APK构建工作流"
    }
}

Write-Host "选择要打开的页面:" -ForegroundColor Cyan
foreach ($key in $pages.Keys | Sort-Object) {
    $page = $pages[$key]
    Write-Host "$key. $($page.name) - $($page.description)" -ForegroundColor White
}
Write-Host "5. 全部打开" -ForegroundColor White
Write-Host "0. 退出" -ForegroundColor Gray
Write-Host ""

$choice = Read-Host "请输入选择 (1-5)"

switch ($choice) {
    "1" { 
        Write-Host "打开仓库主页..." -ForegroundColor Green
        Start-Process $pages["1"].url
    }
    "2" { 
        Write-Host "打开Actions页面..." -ForegroundColor Green
        Start-Process $pages["2"].url
    }
    "3" { 
        Write-Host "打开Releases页面..." -ForegroundColor Green
        Start-Process $pages["3"].url
    }
    "4" { 
        Write-Host "打开最新构建..." -ForegroundColor Green
        Start-Process $pages["4"].url
    }
    "5" { 
        Write-Host "打开所有页面..." -ForegroundColor Green
        foreach ($key in $pages.Keys) {
            Start-Process $pages[$key].url
            Start-Sleep -Milliseconds 500  # 避免同时打开太多页面
        }
    }
    "0" { 
        Write-Host "退出" -ForegroundColor Gray
        exit 0
    }
    default { 
        Write-Host "无效选择，打开Actions页面..." -ForegroundColor Yellow
        Start-Process $pages["2"].url
    }
}

Write-Host ""
Write-Host "页面已在浏览器中打开!" -ForegroundColor Green
Write-Host ""
Write-Host "📋 下载APK步骤提醒:" -ForegroundColor Cyan
Write-Host "1. 在Actions页面找到绿色✅的构建" -ForegroundColor White
Write-Host "2. 点击构建任务进入详情" -ForegroundColor White
Write-Host "3. 滚动到底部找到'Artifacts'" -ForegroundColor White
Write-Host "4. 下载'exercise-tracker-debug-apk'" -ForegroundColor White
Write-Host "5. 解压zip文件获得APK" -ForegroundColor White