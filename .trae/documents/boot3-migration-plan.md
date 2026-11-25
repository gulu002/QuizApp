# Spring Boot 2.7.18 → 3.5.16 迁移计划（Java 25 兼容）

## Context

项目 `quiz-app-backend` 的 `pom.xml` 已将 `java.version` 设为 25，但 Spring Boot 2.7.18 内嵌的 Spring Framework 5.3 无法解析 Java 25 字节码（class file major version 69），导致测试与运行时组件扫描崩溃（`Unsupported class file major version 69`）。经确认选择升级到 **Spring Boot 3.5.16**（3.x 最终版，官方声明兼容 Java 25，迁移面最小）。构建环境已就绪：Maven 3.9.16 + JDK 25（`D:\java\current`），Lombok 1.18.42 + `-proc:full` 已配置且必须保留。

所有 javax 使用面已 grep 核实（见下），风险点均已定位到具体文件行。

## 实施步骤

### 步骤 1：pom.xml（backend/pom.xml）
- L10：parent `<version>2.7.18</version>` → `<version>3.5.16</version>`
- L54-59：删除 `mysql-connector-j` 的 `<version>9.3.0</version>`，交由 Boot 3.5 BOM 管理
- **保留不动**：`java.version=25`、`lombok.version=1.18.42`、`<proc>full</proc>`、jjwt 0.11.5、poi-ooxml 5.2.5、spring-boot-maven-plugin 的 lombok exclude

### 步骤 2：javax → jakarta 批量替换（仅三类，禁止全局替换 javax）
| 规则 | 涉及文件（已核实） |
|---|---|
| `javax.persistence` → `jakarta.persistence` | entity 包 11 个实体（均为 L3 `import javax.persistence.*;`）+ `service/impl/AdminServiceImpl.java` L17、`service/impl/QuestionServiceImpl.java` L19（`javax.persistence.criteria.Predicate`） |
| `javax.validation` → `jakarta.validation` | dto 包 5 个文件：`LoginDTO`、`RegisterDTO`、`SubmitAnswerDTO`、`FinishSessionDTO`、`PracticeStartDTO` |
| `javax.servlet` → `jakarta.servlet` | 仅 `security/JwtAuthenticationFilter.java` L3-6 |

**绝不替换**（JDK 自带包，非 Jakarta EE）：
- `security/JwtUtil.java` L10 `javax.crypto.SecretKey`
- `config/DataSourceLogger.java` L10 `javax.sql.DataSource`

### 步骤 3：SecurityConfig 重写（config/SecurityConfig.java L28-38）
现有代码用的是 Security 5 旧式链式 DSL（`csrf().disable()`、`antMatchers`、`.and()`），在 Security 6 中已删除，整段替换为 lambda DSL：

```java
http
    .csrf(csrf -> csrf.disable())
    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/auth/**", "/api/public/**").permitAll()
        .anyRequest().authenticated())
    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
```

### 步骤 4：application.yml 调整
- `src/main/resources/application.yml` L24：删除 `dialect: org.hibernate.dialect.MySQL8Dialect`（Hibernate 6 已移除该类，自动检测 MySQLDialect）；L25 `format_sql` 保留
- `src/test/resources/application.yml` L13：删除 `dialect: org.hibernate.dialect.H2Dialect` 行，靠自动检测（保留亦可，Hibernate 6 仍有 H2Dialect）
- 其余键（devtools/jackson/jwt/logging/server.port）Boot 3 兼容，不动

### 步骤 5：确认无需改动的部分（防过度迁移）
- `PageRequest.of`/`Sort.by`/`@Query`/Specification：Spring Data 3 API 兼容
- jjwt 0.11.5 `Jwts.parserBuilder()`：不依赖 jakarta 命名空间
- 5 个测试类（`@SpringBootTest` RANDOM_PORT，无 `@MockBean`）：无需改
- `CorsConfig`、`PasswordConfig`、`Result`、`GlobalExceptionHandler`：无需改
- 实体上的 `@CreationTimestamp`/`@UpdateTimestamp`/`@GeneratedValue(IDENTITY)`：Hibernate 6 保留

## 已知风险与应对

1. **H2 + `columnDefinition="json"`**（`entity/Question.java` L44，最高风险）：Boot 3.5 管理的 H2 版本更严格，测试建表/插入可能报 cast 错误。若步骤 6 测试失败：优先在测试 yml 的 H2 URL 追加 `NON_KEYWORDS=VALUE` 无效后，再考虑测试 profile 覆盖为 TEXT 或改用 `@JdbcTypeCode(SqlTypes.JSON)`
2. **生产 MySQL 的 `ddl-auto: update` 漂移**：Hibernate 6 类型映射变化可能生成 ALTER。首次连生产库启动前临时开 `show-sql: true` 观察
3. **Security 6 默认拒绝语义**：确认 `/api/auth/**`、`/api/public/**` 之外的公开端点无遗漏（当前 controller 前缀均被 `anyRequest().authenticated()` 覆盖，符合预期）

## 验证清单（按顺序执行）

1. `mvn clean compile`（backend 目录，JAVA_HOME=D:\java\current）——暴露 jakarta 替换遗漏
2. `mvn dependency:tree -Dincludes=com.mysql`——connector 由 BOM 解析为 9.x
3. `mvn clean install`——5 个 `@SpringBootTest` 通过（H2 建表 + JSON 列风险在此暴露）
4. 启动等价验证：步骤 3 的 RANDOM_PORT 测试已真实拉起 Tomcat + Security 过滤链；如需额外确认，用 H2 内存库参数 `mvn spring-boot:run` 启动观察 DataSourceLogger 输出
5. 冒烟（可选，需本地 MySQL）：`POST /api/auth/login` 返回 401/400 而非 500

## Critical Files
- f:\workbase\QuizApp\backend\pom.xml
- f:\workbase\QuizApp\backend\src\main\java\com\quizapp\config\SecurityConfig.java
- f:\workbase\QuizApp\backend\src\main\java\com\quizapp\security\JwtAuthenticationFilter.java
- f:\workbase\QuizApp\backend\src\main\resources\application.yml
- f:\workbase\QuizApp\backend\src\main\java\com\quizapp\entity\Question.java
- entity 包 11 个实体 + dto 包 5 个 DTO + service/impl 2 个文件（批量 import 替换）
