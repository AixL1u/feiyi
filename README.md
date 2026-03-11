# 非遗传承管理系统

一个基于 Spring Boot + Vue 3 的非物质文化遗产传承管理平台，提供非遗项目展示、传承人管理、文创产品销售、活动预约等功能。

## 项目简介

本系统旨在保护和传承非物质文化遗产，通过数字化平台展示非遗项目、传承人信息，并提供文创产品销售和文化活动预约服务。

### 主要功能

- 非遗项目管理：分类展示、详情介绍、多媒体展示
- 传承人管理：传承人信息、作品展示、技艺传承
- 文创商城：产品展示、购物车、订单管理、地址管理
- 活动预约：文化活动发布、在线预约、预约管理
- 留言互动：用户留言、评论管理
- 后台管理：数据统计、内容管理、用户管理、操作日志

## 技术栈

### 后端技术

- Spring Boot 3.2.0
- Spring Security + JWT 认证
- MyBatis Plus 3.5.5
- MySQL 8.0
- Lombok
- Hutool 工具库
- Apache POI (Excel 导入导出)

### 前端技术

- Vue 3.2
- Vue Router 4
- Pinia 状态管理
- Element Plus UI 框架
- Axios HTTP 客户端
- ECharts 数据可视化
- Vite 构建工具

## 项目结构

```
feiyi/
├── src/main/java/com/example/feiyi/
│   ├── annotation/          # 自定义注解
│   ├── aspect/             # AOP 切面
│   ├── common/             # 公共类
│   ├── config/             # 配置类
│   ├── controller/         # 控制器
│   ├── entity/             # 实体类
│   ├── mapper/             # MyBatis Mapper
│   ├── service/            # 服务层
│   └── FeiyiApplication.java
├── frontend/
│   ├── src/
│   │   ├── api/           # API 接口
│   │   ├── assets/        # 静态资源
│   │   ├── router/        # 路由配置
│   │   ├── views/         # 页面组件
│   │   │   ├── admin/    # 后台管理页面
│   │   │   └── front/    # 前台展示页面
│   │   ├── App.vue
│   │   └── main.js
│   └── package.json
├── sql/
│   └── init.sql           # 数据库初始化脚本
└── pom.xml
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+

### IDE 配置（IntelliJ IDEA）

1. 配置 Maven 设置（重要！）
   - File → Settings → Build, Execution, Deployment → Build Tools → Maven
   - Maven home path: 设置为你的 Maven 安装目录（如 `L:\Maven\apache-maven-3.9.8`）
   - User settings file: 指向你的 Maven settings.xml 文件
   - Local repository: 设置为 `L:\Maven\apache-maven-3.9.8\mvn_repository`
   - 点击 Apply 和 OK

2. 安装 Lombok 插件
   - File → Settings → Plugins
   - 搜索 "Lombok" 并安装
   - 重启 IDE

3. 启用注解处理器
   - File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - 勾选 "Enable annotation processing"

4. 刷新 Maven 项目
   - 右键点击项目根目录的 pom.xml
   - 选择 Maven → Reload Project
   - 或者点击 Maven 工具窗口的刷新按钮

5. 清理 IDE 缓存（如果问题仍然存在）
   - File → Invalidate Caches / Restart
   - 选择 "Invalidate and Restart"

## 常见问题

#### Maven 权限错误 (AccessDeniedException)

如果遇到 `java.nio.file.AccessDeniedException: C:\Users\admin` 错误：

1. 检查 IntelliJ IDEA 的 Maven 配置是否正确
2. 确保 Local repository 路径存在且有写入权限
3. 项目已包含 `.mvn/maven.config` 文件，指定了正确的仓库路径
4. 重新加载 Maven 项目

#### 前端启动时的警告和错误

1. **Vue feature flag 警告**：已在 `vite.config.js` 中配置，可以忽略

2. **403 Forbidden 错误**：
   - 错误：`GET http://localhost:5173/api/auth/info 403 (Forbidden)`
   - 原因：这是正常的！在未登录状态下，前端尝试获取用户信息会被拒绝
   - 解决：登录后即可正常访问
   - 这不影响系统使用，只是前端的自动检查机制

3. **npm 警告**：
   - `npm warn old lockfile`：package-lock.json 版本较旧，npm 会自动更新
   - 可以安全忽略，不影响功能

#### 数据库连接问题

如果遇到数据库连接错误：

1. 确保 MySQL 服务正在运行
2. 检查 `application.yml` 中的数据库配置
3. 确认数据库 `feiyi` 已创建并导入了初始化脚本
4. 验证用户名和密码是否正确

### 后端启动

1. 确保 MySQL 服务已启动

2. 创建数据库并导入初始化脚本

**方式1：使用 PowerShell（推荐）**

```powershell
# 创建数据库
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS feiyi DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"

# 导入初始化脚本
Get-Content sql/init.sql -Encoding UTF8 | mysql -u root -proot feiyi --default-character-set=utf8mb4
```

**方式2：使用 MySQL 命令行**

```bash
# 登录 MySQL
mysql -u root -proot

# 在 MySQL 命令行中执行
source L:/Code/feiyi/sql/init.sql
```

3. 配置文件说明

配置文件 `src/main/resources/application.yml` 已配置为：
- 用户名：root
- 密码：root
- 数据库：feiyi

如果你的 MySQL 配置不同，请修改相应的配置。

4. 启动后端服务

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端启动

1. 安装依赖

```bash
cd frontend
npm install
```

如果遇到 PowerShell 执行策略错误，使用：

```bash
cd frontend
cmd /c npm install
```

2. 启动开发服务器

```bash
npm run dev
```

或者：

```bash
cmd /c npm run dev
```

前端服务将在 `http://localhost:5173` 启动

3. 构建生产版本

```bash
npm run build
```

构建后的文件将在 `frontend/dist` 目录

## 默认账号

系统初始化后会自动创建以下账号：

- 超级管理员：`god` / `root`
- 管理员：`admin` / `admin123`
- 传承人：`inheritor1` / `admin123`
- 普通用户：`user1` / `admin123`

## API 文档

后端 API 基础路径：`http://localhost:8080/api`

### 主要接口模块

- `/auth` - 认证接口（登录、注册）
- `/admin` - 后台管理接口
- `/heritage` - 非遗项目接口
- `/inheritor` - 传承人接口
- `/product` - 产品接口
- `/order` - 订单接口
- `/activity` - 活动接口
- `/message` - 留言接口

## 功能截图

（可根据实际情况添加项目截图）

## 开发说明

### 后端开发

- 使用 MyBatis Plus 进行数据库操作
- 使用 JWT 进行身份认证
- 使用 AOP 记录操作日志
- 统一返回结果封装在 `Result` 类中

### 前端开发

- 使用 Vue 3 Composition API
- 使用 Pinia 进行状态管理
- 使用 Element Plus 组件库
- API 请求统一封装在 `src/api/index.js`

## 部署说明

### 后端部署

```bash
# 打包
mvnw.cmd clean package

# 运行
java -jar target/feiyi-0.0.1-SNAPSHOT.jar
```

### 前端部署

```bash
# 构建
cd frontend
npm run build

# 将 dist 目录部署到 Web 服务器（如 Nginx）
```

## 许可证

本项目仅供学习交流使用。

## 联系方式

如有问题或建议，欢迎提交 Issue。
