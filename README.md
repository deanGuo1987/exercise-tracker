# 运动记录应用 (Exercise Tracker)

一个简洁的Android运动记录应用，帮助用户通过日历界面记录每日运动情况。

## 📱 应用截图预览

- 📅 **月视图日历** - 清晰显示运动记录
- ⏰ **每日提醒** - 11:30准时通知
- 💾 **数据持久化** - 本地存储，永不丢失
- 🔒 **记录不可变** - 保证历史数据准确性

## ✨ 主要功能

### 🗓️ 日历界面
- 月视图显示，支持滑动导航
- 红色标记显示运动记录
- 显示运动时长信息

### 🏃‍♂️ 运动记录
- 点击日期快速记录运动
- 支持20/30/40分钟预设时长
- 可选择"是否运动"

### 📊 运动报表
- 按月和按年查看运动统计
- 运动天数、运动率、运动时长分析
- 连续运动天数统计
- 详细数据可视化展示

### 💾 数据管理
- 本地JSON文件存储
- 支持最多1000条记录
- 应用重装后数据自动恢复

### 🔔 智能提醒
- 每日11:30准时提醒
- 点击通知直接打开应用
- 无法关闭，确保坚持运动

### 🔐 数据安全
- 记录一旦创建不可修改
- 不可删除历史记录
- 保证数据完整性

## 🛠️ 技术规格

- **平台**: Android 7.0+ (API 24+)
- **语言**: Kotlin
- **架构**: MVC模式
- **存储**: 本地JSON文件
- **测试**: 属性测试 + 单元测试 + 集成测试

## 📦 获取APK

### 🥇 方法1: GitHub Actions自动构建（推荐）

**最简单的方法 - 只需10分钟！**

1. **上传到GitHub**
   ```bash
   # 创建GitHub仓库后运行
   .\setup-github.ps1 -RepoUrl "https://github.com/你的用户名/exercise-tracker.git"
   ```

2. **等待自动构建**
   - 访问GitHub仓库的"Actions"页面
   - 等待"Build Android APK"完成（3-5分钟）

3. **下载APK**
   - 在构建完成页面底部找到"Artifacts"
   - 下载"exercise-tracker-debug-apk"

📖 **详细说明**: [QUICK_START.md](QUICK_START.md) | [GITHUB_ACTIONS_SETUP.md](GITHUB_ACTIONS_SETUP.md)

### 🥈 方法2: Android Studio
1. 下载 [Android Studio](https://developer.android.com/studio)
2. 打开此项目
3. Build → Build APK
4. 安装 `app/build/outputs/apk/debug/app-debug.apk`

### 🥉 方法3: 命令行
```bash
./gradlew assembleDebug
```

详细说明请查看 [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)

## 📋 项目状态

✅ **开发完成** - 所有功能已实现  
✅ **测试通过** - 包含6个属性测试和完整集成测试  
✅ **文档完整** - 需求、设计、任务文档齐全  
✅ **构建就绪** - 配置文件已完成  
🚀 **CI/CD就绪** - GitHub Actions自动构建APK  

## 🧪 测试覆盖

### 属性测试 (Property-Based Testing)
- Property 1: 日历显示信息准确性
- Property 2: 日期点击交互一致性  
- Property 3: 运动记录创建完整性
- Property 4: 数据持久化往返一致性
- Property 5: 通知时间精确性
- Property 6: 记录不可变性保证

### 集成测试
- 端到端用户流程测试
- 系统组件集成测试
- 错误处理测试

## 📁 项目结构

```
├── app/src/main/java/com/exercisetracker/
│   ├── MainActivity.kt              # 主界面控制器
│   ├── ReportActivity.kt           # 报表界面控制器
│   ├── ExerciseRecord.kt           # 运动记录数据模型
│   ├── ExerciseReport.kt           # 报表数据模型和生成器
│   ├── ExerciseRecordManager.kt    # 记录管理器
│   ├── FileStorage.kt              # 文件存储服务
│   ├── ExerciseDialog.kt           # 运动选择对话框
│   ├── NotificationManager.kt      # 通知管理器
│   └── NotificationReceiver.kt     # 通知接收器
├── app/src/main/res/               # Android资源文件
├── app/src/test/                   # 测试文件
├── .kiro/specs/                    # 项目规格文档
└── .github/workflows/              # CI/CD配置
```

## 📖 文档

- [构建说明](BUILD_INSTRUCTIONS.md) - 详细构建步骤
- [部署指南](DEPLOYMENT_GUIDE.md) - APK获取和安装
- [报表功能指南](REPORT_FEATURE_GUIDE.md) - 报表功能详细说明
- [需求文档](.kiro/specs/exercise-tracker/requirements.md) - 功能需求
- [设计文档](.kiro/specs/exercise-tracker/design.md) - 技术设计
- [任务列表](.kiro/specs/exercise-tracker/tasks.md) - 开发任务

## 🎯 使用方法

1. **安装应用** - 下载并安装APK到Android设备
2. **授予权限** - 允许通知权限
3. **记录运动** - 点击日历日期选择运动时长
4. **查看历史** - 在日历上查看运动记录标记
5. **查看报表** - 点击"📊 查看报表"按钮查看统计数据
6. **接收提醒** - 每天11:30收到运动提醒

### 📊 报表功能详细说明
- **月度报表**: 查看指定月份的运动统计，包括运动天数、运动率、总时长等
- **年度报表**: 查看整年的运动数据汇总和趋势分析
- **统计指标**: 运动率、平均时长、连续运动天数等关键指标
- **数据可视化**: 彩色卡片展示，清晰直观的数据呈现

详细使用指南请查看 [REPORT_FEATURE_GUIDE.md](REPORT_FEATURE_GUIDE.md)

## 🔧 开发环境

- Android Studio Arctic Fox+
- JDK 17
- Android SDK API 34
- Kotlin 1.9+
- Gradle 8.2+

---

**项目已完成所有开发工作，包括完整的测试套件。只需在Android开发环境中构建即可获得可安装的APK文件。**