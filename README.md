# Network Tool Plus

一个 Minecraft NeoForge 模组，为 AE2 **网络工具** 添加自动收集、扩容升级槽和工具箱增强功能。

## 功能

- **收集模式** — 潜行 + 右键切换。开启后工具显示附魔光效，并自动将背包中的 AE2 升级卡吸入工具
- **自动收卡** — 工具放在背包或 Curios 饰品栏中均生效。每 10 tick 扫描一次背包，自动将升级卡移入工具
- **21 格升级槽** — 从原版 9 格扩展至 21 格（7×3 网格），配备自定义 GUI 纹理
- **工具箱定向填充** — 在机器 GUI 中从工具箱 Shift+点击升级卡时，直接填充升级槽；不兼容的卡片留在原位。终端 GUI 保留原行为
- **工具槽位锁定** — 打开任意 AE2 菜单时自动锁定背包中的网络工具，防止 Shift+点击误移动
- **Curios 集成** —（可选）可将网络工具放入 Curios 饰品栏，收集模式和自动收卡均正常生效

## 依赖

| 依赖 | 版本 | 必需 |
|---|---|---|
| Minecraft | 26.1.2 | 是 |
| NeoForge | 26.1.2.99+ | 是 |
| AE2 | 26.1.11-beta+ | 是 |
| Curios | 15.0.0+26.1.2 | 否 |

## 安装

1. 为 Minecraft 26.1.2 安装 NeoForge 26.1.2.99+
2. 安装 AE2 26.1.11-beta+
3. 将模组 jar（`network_tool_plus-26.1.4.jar`）放入 `mods` 文件夹
4.（可选）安装 Curios 15.0.0+26.1.2 以获得饰品栏支持

## 使用

1. 手持网络工具，潜行 + 右键切换收集模式（工具出现附魔光效）
2. 背包中的升级卡会自动被吸入工具
3. 打开任意 AE2 机器 GUI，工具的升级槽会显示在工具箱面板中（7×3 而非 3×3）

## 构建

```powershell
cd 26.1.2
$env:JAVA_HOME='C:/Users/Admin/.gradle/jdks/eclipse_adoptium-25-amd64-windows.2'  # JDK 25
./gradlew.bat build   # 产物 build/libs/network_tool_plus-26.1.3.jar
```

需 JDK 25 / Gradle 9.2.1。

## 许可

GNU LGPL 3.0
