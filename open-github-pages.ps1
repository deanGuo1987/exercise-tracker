# 快速打开GitHub相关页面的脚本

param(
    [Parameter(Mandatory=$false)]
    [string]$RepoUrl = "https://github.com/deanGuo1987/exercise-tracker"
)

Write-Host "=== GitHub Pages Opener ===" -ForegroundColor Green
Write-Host ""

# 提取仓库信息
if ($RepoUrl -match "github\.com/([^/]+)/([^/]+)") {
    $owner = $matches[1]
    $repo = $matches[2].TrimEnd('.git')
    
    Write-Host "Repository: $owner/$repo" -ForegroundColor Cyan
    Write-Host ""
    
    # 定义页面URLs
    $pages = @{
        "1" = @{
            "name" = "Repository Home"
            "url" = "$RepoUrl"
            "description" = "Main repository page"
        }
        "2" = @{
            "name" = "Actions (Build Status)"
            "url" = "$RepoUrl/actions"
            "description" = "Check build status and download APK"
        }
        "3" = @{
            "name" = "Latest Build"
            "url" = "$RepoUrl/actions/workflows/build-apk.yml"
            "description" = "Latest APK build workflow"
        }
        "4" = @{
            "name" = "Releases"
            "url" = "$RepoUrl/releases"
            "description" = "Published releases and APK downloads"
        }
        "5" = @{
            "name" = "Issues"
            "url" = "$RepoUrl/issues"
            "description" = "Report problems or request features"
        }
    }
    
    # 显示选项
    Write-Host "Available pages:" -ForegroundColor Yellow
    foreach ($key in $pages.Keys | Sort-Object) {
        $page = $pages[$key]
        Write-Host "$key. $($page.name)" -ForegroundColor White
        Write-Host "   $($page.description)" -ForegroundColor Gray
        Write-Host ""
    }
    
    # 获取用户选择
    $choice = Read-Host "Enter page number to open (1-5), 'a' for all, or Enter to open Actions page"
    
    if ([string]::IsNullOrEmpty($choice) -or $choice -eq "2") {
        # 默认打开Actions页面
        Write-Host "Opening Actions page..." -ForegroundColor Green
        Start-Process $pages["2"].url
    }
    elseif ($choice -eq "a" -or $choice -eq "A") {
        # 打开所有页面
        Write-Host "Opening all pages..." -ForegroundColor Green
        foreach ($key in $pages.Keys | Sort-Object) {
            Start-Process $pages[$key].url
            Start-Sleep -Milliseconds 500  # 避免同时打开太多页面
        }
    }
    elseif ($pages.ContainsKey($choice)) {
        # 打开选择的页面
        $selectedPage = $pages[$choice]
        Write-Host "Opening $($selectedPage.name)..." -ForegroundColor Green
        Start-Process $selectedPage.url
    }
    else {
        Write-Host "Invalid choice. Opening Actions page by default..." -ForegroundColor Yellow
        Start-Process $pages["2"].url
    }
    
} else {
    Write-Host "Invalid repository URL format" -ForegroundColor Red
    Write-Host "Expected format: https://github.com/username/repository" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Tip: Bookmark the Actions page to quickly check build status!" -ForegroundColor Cyan