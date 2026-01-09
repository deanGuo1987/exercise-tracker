# GitHub Actions 自动构建设置指南

## 🎯 概述

我已经为你的运动记录应用配置了完整的GitHub Actions CI/CD流水线，包括自动构建、测试、发布等功能。

## 📁 已创建的Workflow文件

### 1. `.github/workflows/build-apk.yml` - 主构建流水线
- **触发条件**: 推送到main/master/develop分支，PR，手动触发
- **功能**:
  - 运行单元测试和属性测试
  - 构建Debug和Release APK
  - 上传APK到Artifacts
  - 生成构建报告

### 2. `.github/workflows/release.yml` - 发布流水线
- **触发条件**: 推送tag（如v1.0.0），手动触发
- **功能**:
  - 构建正式版APK
  - 创建GitHub Release
  - 自动生成更新日志
  - 上传APK到Release页面

### 3. `.github/workflows/pr-check.yml` - PR质量检查
- **触发条件**: 创建或更新Pull Request
- **功能**:
  - 代码质量检查（Lint）
  - 运行测试套件
  - 安全扫描
  - 自动评论PR结果

## 🚀 使用步骤

### 第一步：上传项目到GitHub

1. **创建GitHub仓库**
   ```bash
   # 在GitHub网站创建新仓库，例如: exercise-tracker
   ```

2. **初始化本地Git仓库**
   ```bash
   git init
   git add .
   git commit -m "Initial commit: 运动记录应用完整项目"
   ```

3. **连接到GitHub仓库**
   ```bash
   git remote add origin https://github.com/你的用户名/exercise-tracker.git
   git branch -M main
   git push -u origin main
   ```

### 第二步：启用GitHub Actions

1. **自动启用**
   - 推送代码后，GitHub Actions会自动检测workflow文件
   - 在仓库的"Actions"标签页可以看到运行状态

2. **检查权限**
   - 确保仓库设置中启用了Actions
   - Settings → Actions → General → Allow all actions

### 第三步：获取构建的APK

#### 方法1：从Actions页面下载
1. 进入GitHub仓库的"Actions"页面
2. 点击最新的"Build Android APK"工作流
3. 在页面底部找到"Artifacts"部分
4. 下载对应的APK文件（zip格式）

#### 方法2：从Release页面下载（推荐）
1. 创建并推送一个版本标签：
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
2. 进入GitHub仓库的"Releases"页面
3. 下载自动创建的Release中的APK文件

## 🔧 高级配置

### 自定义构建

你可以通过以下方式自定义构建：

1. **手动触发构建**
   - 在Actions页面点击"Build Android APK"
   - 点击"Run workflow"
   - 选择构建类型（debug/release/both）

2. **修改构建配置**
   - 编辑`.github/workflows/build-apk.yml`
   - 可以修改触发分支、构建参数等

### 添加签名（可选）

如果需要发布到Google Play，可以添加APK签名：

1. **创建密钥库**
   ```bash
   keytool -genkey -v -keystore release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias release
   ```

2. **添加GitHub Secrets**
   - 在仓库Settings → Secrets and variables → Actions
   - 添加以下secrets：
     - `KEYSTORE_FILE`: base64编码的keystore文件
     - `KEYSTORE_PASSWORD`: keystore密码
     - `KEY_ALIAS`: 密钥别名
     - `KEY_PASSWORD`: 密钥密码

3. **修改workflow文件**
   - 在release构建步骤中添加签名配置

## 📊 构建状态监控

### 构建徽章

在README.md中添加构建状态徽章：

```markdown
![Build Status](https://github.com/你的用户名/exercise-tracker/workflows/Build%20Android%20APK/badge.svg)
```

### 通知设置

1. **邮件通知**
   - GitHub会自动发送构建失败通知到你的邮箱

2. **Slack/Discord通知**
   - 可以配置webhook发送通知到团队聊天工具

## 🐛 故障排除

### 常见问题

1. **构建失败：权限问题**
   ```
   解决方案：检查GITHUB_TOKEN权限，确保workflow有写入权限
   ```

2. **构建失败：依赖下载超时**
   ```
   解决方案：重新运行workflow，通常是网络临时问题
   ```

3. **APK上传失败**
   ```
   解决方案：检查文件路径是否正确，确保APK构建成功
   ```

### 调试技巧

1. **查看详细日志**
   - 在Actions页面点击失败的构建
   - 展开各个步骤查看详细输出

2. **本地测试**
   ```bash
   # 在本地运行相同的构建命令
   ./gradlew assembleDebug --stacktrace
   ```

## 📈 工作流程图

```
代码推送 → 触发Actions → 运行测试 → 构建APK → 上传Artifacts
    ↓
创建Tag → 触发Release → 构建正式版 → 创建Release → 发布APK
```

## 🎉 完成！

现在你的项目已经配置了完整的CI/CD流水线：

✅ **自动构建** - 每次代码更新都会自动构建APK  
✅ **自动测试** - 运行完整的测试套件确保质量  
✅ **自动发布** - 创建tag即可自动发布新版本  
✅ **质量检查** - PR会自动进行代码质量检查  

只需要将代码推送到GitHub，就可以获得自动构建的APK文件了！