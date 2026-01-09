# 运动记录应用 - 构建说明

## 项目状态
✅ **项目已完成开发，所有功能已实现**
- 完整的Android Kotlin项目
- 所有源代码文件已就绪
- 测试套件已完成
- 构建配置已设置

## 构建APK的方法

### 方法1：使用Android Studio（推荐）

1. **安装Android Studio**
   - 下载：https://developer.android.com/studio
   - 安装时确保包含Android SDK

2. **打开项目**
   - 启动Android Studio
   - 选择 "Open an existing Android Studio project"
   - 选择此项目的根目录

3. **同步项目**
   - Android Studio会自动下载所需的依赖
   - 等待Gradle同步完成

4. **构建APK**
   - 菜单：Build → Build Bundle(s) / APK(s) → Build APK(s)
   - 或者使用快捷键：Ctrl+Shift+A，搜索"Build APK"

5. **查找APK文件**
   - 构建完成后，APK文件位于：`app/build/outputs/apk/debug/app-debug.apk`

### 方法2：使用命令行（需要Android SDK）

如果你已经安装了Android SDK和gradle：

```bash
# Windows
.\gradlew.bat assembleDebug

# 生成的APK位置
app\build\outputs\apk\debug\app-debug.apk
```

### 方法3：在线构建服务

可以使用以下在线服务构建APK：
- GitHub Actions（需要上传到GitHub）
- GitLab CI/CD
- Bitrise
- CircleCI

## 项目特性

### 已实现功能
- ✅ 日历界面显示和导航
- ✅ 运动记录创建（点击日期选择运动时长）
- ✅ 数据持久化（JSON文件存储）
- ✅ 每日11:30运动提醒通知
- ✅ 记录不可变性（已记录的数据不可修改）
- ✅ Android平台兼容性

### 技术栈
- **语言**: Kotlin
- **平台**: Android (API 24+)
- **UI**: Android原生组件（CalendarView, Dialog）
- **存储**: JSON文件（本地存储）
- **通知**: AlarmManager + NotificationManager
- **测试**: Kotest（属性测试 + 单元测试）

### 项目结构
```
app/
├── src/main/java/com/exercisetracker/
│   ├── MainActivity.kt              # 主界面
│   ├── ExerciseRecord.kt           # 数据模型
│   ├── ExerciseRecordManager.kt    # 记录管理
│   ├── FileStorage.kt              # 文件存储
│   ├── ExerciseDialog.kt           # 运动选择对话框
│   ├── NotificationManager.kt      # 通知管理
│   └── NotificationReceiver.kt     # 通知接收器
├── src/main/res/
│   ├── layout/activity_main.xml    # 主界面布局
│   ├── values/strings.xml          # 字符串资源
│   └── values/themes.xml           # 主题配置
└── src/test/java/com/exercisetracker/
    ├── *Test.kt                    # 各种测试文件
    └── *IntegrationTest.kt         # 集成测试
```

## 安装说明

### 开发版本安装
1. 构建debug APK（app-debug.apk）
2. 在Android设备上启用"开发者选项"
3. 启用"未知来源"或"安装未知应用"
4. 传输APK到设备并安装

### 权限要求
应用需要以下权限：
- 通知权限（用于每日提醒）
- 精确闹钟权限（用于11:30定时提醒）

## 故障排除

### 常见问题

1. **Gradle同步失败**
   - 检查网络连接
   - 在Android Studio中：File → Invalidate Caches and Restart

2. **构建失败**
   - 确保Android SDK已安装
   - 检查build.gradle中的SDK版本是否已安装

3. **APK安装失败**
   - 检查设备是否允许安装未知来源应用
   - 确保设备Android版本 ≥ 7.0 (API 24)

## 联系信息

如果在构建过程中遇到问题，请检查：
1. Android Studio版本是否为最新
2. Android SDK是否完整安装
3. 网络连接是否正常（用于下载依赖）

---

**注意**: 此项目已完成所有开发工作，包括完整的测试套件。只需要在适当的Android开发环境中构建即可获得可安装的APK文件。