param(
    [Parameter(Mandatory=$true)]
    [string]$RepoUrl,
    
    [Parameter(Mandatory=$false)]
    [string]$BranchName = "main"
)

Write-Host "=== GitHub Repository Setup ===" -ForegroundColor Green
Write-Host ""

# Check if Git is installed
try {
    $gitVersion = git --version
    Write-Host "Git found: $gitVersion" -ForegroundColor Green
} catch {
    Write-Host "Git not installed. Please install Git first." -ForegroundColor Red
    Write-Host "Download: https://git-scm.com/download/win"
    exit 1
}

# Check if already a Git repository
if (Test-Path ".git") {
    Write-Host "Directory is already a Git repository" -ForegroundColor Yellow
    $continue = Read-Host "Continue with remote setup? (y/N)"
    if ($continue -ne "y" -and $continue -ne "Y") {
        exit 0
    }
} else {
    Write-Host "Initializing Git repository..." -ForegroundColor Cyan
    git init
    Write-Host "Git repository initialized" -ForegroundColor Green
}

# Add all files
Write-Host "Adding project files..." -ForegroundColor Cyan
git add .

# Check if files were added
$status = git status --porcelain
if ([string]::IsNullOrEmpty($status)) {
    Write-Host "No files to commit" -ForegroundColor Yellow
} else {
    Write-Host "Added $(($status -split "`n").Count) files" -ForegroundColor Green
}

# Create initial commit
Write-Host "Creating initial commit..." -ForegroundColor Cyan
try {
    git commit -m "Initial commit: Exercise Tracker Android App

Features:
- Calendar interface with navigation
- Exercise recording (20/30/40 minute options)
- Data persistence storage
- Daily 11:30 exercise reminders
- Record immutability guarantee

Testing:
- Property-Based Testing
- Unit tests
- Integration tests

CI/CD:
- GitHub Actions auto-build
- Automatic APK generation
- Auto-release workflow"

    Write-Host "Initial commit created" -ForegroundColor Green
} catch {
    Write-Host "Commit creation failed - may be no new changes" -ForegroundColor Yellow
}

# Set remote repository
Write-Host "Setting remote repository..." -ForegroundColor Cyan
try {
    git remote add origin $RepoUrl
    Write-Host "Remote repository set: $RepoUrl" -ForegroundColor Green
} catch {
    Write-Host "Remote may already exist, trying to update..." -ForegroundColor Yellow
    git remote set-url origin $RepoUrl
    Write-Host "Remote repository URL updated" -ForegroundColor Green
}

# Set main branch
Write-Host "Setting main branch to: $BranchName" -ForegroundColor Cyan
git branch -M $BranchName

# Push to remote repository
Write-Host "Pushing to remote repository..." -ForegroundColor Cyan
try {
    git push -u origin $BranchName
    Write-Host "Code pushed successfully!" -ForegroundColor Green
} catch {
    Write-Host "Push failed. Please check:" -ForegroundColor Red
    Write-Host "  1. Repository URL is correct" -ForegroundColor Yellow
    Write-Host "  2. You have push permissions" -ForegroundColor Yellow
    Write-Host "  3. Network connection is working" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "GitHub repository setup complete!" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Visit GitHub repository: $RepoUrl" -ForegroundColor White
Write-Host "2. Go to 'Actions' tab to see auto-build status" -ForegroundColor White
Write-Host "3. Wait for build completion and download APK" -ForegroundColor White
Write-Host ""
Write-Host "Auto-build features:" -ForegroundColor Cyan
Write-Host "- Every code push triggers APK build" -ForegroundColor White
Write-Host "- Download APK from Actions page" -ForegroundColor White
Write-Host "- Create tag (v1.0.0) for automatic Release" -ForegroundColor White
Write-Host ""
Write-Host "Create Release example:" -ForegroundColor Cyan
Write-Host "git tag v1.0.0" -ForegroundColor Gray
Write-Host "git push origin v1.0.0" -ForegroundColor Gray