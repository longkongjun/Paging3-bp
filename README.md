# Paging3 Best Practice (RecyclerView)

该项目演示了在典型三层架构中使用 **Jetpack Paging3 + Room RemoteMediator + RecyclerView** 的最佳实践，重点覆盖以下要点：

- **数据源整合**：`RickAndMortyApi` 通过 Retrofit + Kotlinx Serialization 获取角色数据，同时使用 Room 缓存实体并维护分页 RemoteKeys。
- **远程调度**：`CharacterRemoteMediator` 负责在 `LoadType` 不同阶段增量刷新本地缓存，确保离线可读与失败可重试能力。
- **领域抽象**：`CharacterRepositoryImpl` 将数据映射为领域模型并提供 `Flow<PagingData<Character>>`，上层仅关心 `UseCase`。
- **依赖注入**：Hilt 注入 Retrofit、OkHttp、Room、Repository 等组件，实现可测试的模块化设计。
- **RecyclerView 体验**：`CharacterAdapter` + `CharacterLoadStateAdapter` + `SwipeRefreshLayout` 提供加载中、失败、空态和 footer retry 等状态反馈。

## 模块结构

```
app/
├── data
│   ├── remote (Retrofit API & DTO)
│   ├── local  (Room Entity/Dao/Database)
│   ├── mediator (RemoteMediator)
│   ├── mapper
│   └── repository (RepositoryImpl)
├── domain
│   ├── model
│   ├── repository (接口)
│   └── usecase
├── di (Hilt Modules)
└── ui
    ├── adapter / components / viewmodel
    └── MainActivity + XML 布局
```

## 运行 & 构建

1. **准备 Android SDK**：在项目根目录创建 `local.properties` 并写入 `sdk.dir=/path/to/Android/sdk`。
2. **（可选）Gradle Wrapper 下载问题**：在当前机器上，`./gradlew :app:assembleDebug` 会因为路径含空格而尝试在项目根寻找 `gradle-8.10.2`，导致 `gradle-runtime-api-info` 找不到。若遇到相同现象，可手动解压官方 `gradle-8.10.2-bin.zip` 到项目根或通过 `GRADLE_USER_HOME` 指向无空格路径，再执行 `./gradlew`。
3. **构建命令**：
   ```bash
   ./gradlew :app:assembleDebug
   ```
   若仍受限，可临时使用下载的发行版执行 `./gradle-8.10.2/bin/gradle :app:assembleDebug`，本项目已用该方式完成验证。

## 功能亮点

- `CharacterRemoteMediator` 采用标准的 `withTransaction` 清理策略，充分利用 `RemoteKeys` 追踪上下页。
- RecyclerView 层通过 `ConcatAdapter` 组合 Header/Footer load state，并在 MainActivity 中统一监听 `CombinedLoadStates`，驱动刷新指示、空态/错误展示与重试按钮。
- Hilt 模块化提供 `NetworkModule`、`DatabaseModule`、`RepositoryModule`，默认开启 OkHttp 日志和动态颜色主题。

## 下一步建议

- 增加 `PagingSource`/`RemoteMediator` 单元测试，确保分页边界与失败策略覆盖。
- 接入 `WorkManager` 触发预取或离线同步，或加入 UI 测试验证加载状态切换。
