Write-Host "=== Project Status Check ===" -ForegroundColor Green
Write-Host ""

# Check Git status
Write-Host "Git Repository Status:" -ForegroundColor Cyan
try {
    $branch = git branch --show-current
    Write-Host "Current branch: $branch" -ForegroundColor White
    
    $remote = git remote get-url origin 2>$null
    if ($remote) {
        Write-Host "Remote URL: $remote" -ForegroundColor White
    } else {
        Write-Host "No remote configured" -ForegroundColor Yellow
    }
    
    $status = git status --porcelain
    if ([string]::IsNullOrEmpty($status)) {
        Write-Host "Working directory: Clean" -ForegroundColor Green
    } else {
        Write-Host "Working directory: Has changes" -ForegroundColor Yellow
    }
    
    $commits = git rev-list --count HEAD 2>$null
    if ($commits) {
        Write-Host "Total commits: $commits" -ForegroundColor White
    }
} catch {
    Write-Host "Not a Git repository" -ForegroundColor Red
}

Write-Host ""

# Check project files
Write-Host "Project Files Status:" -ForegroundColor Cyan

$requiredFiles = @(
    "app/build.gradle",
    "app/src/main/java/com/exercisetracker/MainActivity.kt",
    "app/src/main/AndroidManifest.xml",
    ".github/workflows/build-apk.yml",
    "README.md"
)

foreach ($file in $requiredFiles) {
    if (Test-Path $file) {
        Write-Host "✓ $file" -ForegroundColor Green
    } else {
        Write-Host "✗ $file" -ForegroundColor Red
    }
}

Write-Host ""

# Check GitHub Actions files
Write-Host "GitHub Actions Configuration:" -ForegroundColor Cyan
$workflowFiles = Get-ChildItem ".github/workflows/*.yml" -ErrorAction SilentlyContinue
if ($workflowFiles) {
    foreach ($workflow in $workflowFiles) {
        Write-Host "✓ $($workflow.Name)" -ForegroundColor Green
    }
} else {
    Write-Host "✗ No workflow files found" -ForegroundColor Red
}

Write-Host ""

# Next steps
Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "1. Create GitHub repository at: https://github.com/new" -ForegroundColor White
Write-Host "   - Repository name: exercise-tracker" -ForegroundColor Gray
Write-Host "   - Make it Public for free Actions" -ForegroundColor Gray
Write-Host "   - Don't add README, .gitignore, or license" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Push code to GitHub:" -ForegroundColor White
Write-Host "   git push -u origin main" -ForegroundColor Gray
Write-Host ""
Write-Host "3. Check Actions tab for auto-build" -ForegroundColor White
Write-Host "4. Download APK from Artifacts" -ForegroundColor White

Write-Host ""
Write-Host "Project is ready for GitHub deployment!" -ForegroundColor Green