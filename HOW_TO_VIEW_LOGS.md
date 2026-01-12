# 如何查看Android应用日志

## 方法1: 使用ADB命令（推荐）

### 前提条件
1. 在手机上启用"开发者选项"
2. 启用"USB调试"
3. 电脑上安装ADB工具

### 启用开发者选项步骤
1. 打开手机"设置"
2. 找到"关于手机"或"关于设备"
3. 连续点击"版本号"7次
4. 返回设置，找到"开发者选项"
5. 启用"USB调试"

### 安装ADB工具
- **Windows**: 下载Android SDK Platform Tools
- **Mac**: `brew install android-platform-tools`
- **Linux**: `sudo apt install android-tools-adb`

### 查看日志命令
```bash
# 连接设备后，查看所有日志
adb logcat

# 只查看我们应用的日志
adb logcat | findstr "MainActivity ExerciseDialog"

# 或者使用grep（如果有的话）
adb logcat | grep -E "(MainActivity|ExerciseDialog)"

# 清除之前的日志，然后查看新日志
adb logcat -c
adb logcat | findstr "MainActivity ExerciseDialog"
```

## 方法2: 使用Android Studio

### 步骤
1. 下载并安装Android Studio
2. 用USB线连接手机到电脑
3. 打开Android Studio
4. 点击底部的"Logcat"标签
5. 在过滤器中输入"MainActivity"或"ExerciseDialog"

## 方法3: 使用手机上的日志应用

### 推荐应用
- **Logcat Reader** (免费)
- **aLogcat** (免费)
- **Catlog** (免费)

### 使用步骤
1. 从Google Play下载日志查看应用
2. 给应用授予必要权限
3. 在过滤器中搜索"MainActivity"或"ExerciseDialog"

## 预期的日志内容

### 当您点击日历日期时，应该看到：
```
D/MainActivity: 显示运动选择对话框，日期: 2025-01-09
D/MainActivity: 对话框已调用show方法
D/ExerciseDialog: 创建对话框，日期: 2025-01-09, 选项数量: 4
```

### 当您选择选项时，应该看到：
```
D/ExerciseDialog: 用户选择了选项: 1
D/ExerciseDialog: 选择：运动 20分钟
D/MainActivity: 收到对话框结果: exercised=true, duration=20
```

## 重要提醒

⚠️ **当前状态**: 我添加的调试日志还没有推送到GitHub（网络问题），所以您现在使用的APK可能还没有这些日志。

### 解决方案
1. 等待我推送成功后，重新下载APK
2. 或者我可以先尝试其他修复方法

## 如果没有看到预期日志

### 可能原因1: APK版本问题
- 确认APK是从最新的GitHub Actions构建下载的
- 检查APK的构建时间

### 可能原因2: 日志级别问题
- 尝试查看所有级别的日志：`adb logcat`
- 搜索应用包名：`adb logcat | findstr "com.exercisetracker"`

### 可能原因3: 设备连接问题
- 确认设备已连接：`adb devices`
- 重新连接设备：`adb kill-server && adb start-server`

## 快速测试命令

```bash
# 1. 检查设备连接
adb devices

# 2. 清除日志
adb logcat -c

# 3. 开始监控日志
adb logcat | findstr "MainActivity ExerciseDialog"

# 4. 在手机上操作应用，点击日历日期
# 5. 观察终端输出
```

---
**创建时间**: 2025-01-09
**注意**: 调试版本APK需要等待网络恢复后重新构建