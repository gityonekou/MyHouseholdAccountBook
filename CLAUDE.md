# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MyHouseholdAccountBook is a personal household account book web application built with Spring Boot 3.0.7 and Java 17. The project follows Domain-Driven Design (DDD) principles. DDD refactoring (Phase 1-5) is complete, and the project is in active feature development.

**Tech Stack**: Spring Boot 3.0.7, Spring Security 6.0.7, MyBatis 3.0.2, Thymeleaf, Lombok, MariaDB (production), H2 (test)

## Build Commands

```bash
# Run all tests
mvn clean test

# Run a single test class
mvn test -Dtest=ClassName

# Run a specific test method
mvn test -Dtest=ClassName#methodName

# Build without tests
mvn clean install -DskipTests

# Run application (local)
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## Architecture

The project follows DDD layered architecture:

```
src/main/java/com/yonetani/webapp/accountbook/
├── domain/           # Domain layer (business logic)
│   ├── type/         # Value objects (Money types, Codes, Dates)
│   ├── model/        # Domain models (aggregates, entities)
│   ├── repository/   # Repository interfaces
│   ├── service/      # Domain services
│   ├── exception/    # Domain exceptions
│   └── utils/        # Domain utilities
├── application/      # Application layer
│   └── usecase/      # Use cases (application services)
├── presentation/     # Presentation layer
│   ├── controller/   # Spring MVC controllers
│   ├── request/      # Request DTOs (form objects)
│   ├── response/     # Response DTOs
│   ├── session/      # Session-scoped beans
│   └── security/     # Spring Security config
├── infrastructure/   # Infrastructure layer
│   ├── datasource/   # Repository implementations
│   ├── mapper/       # MyBatis mappers
│   └── dto/          # DB entity DTOs
└── common/           # Cross-cutting concerns
```

**Key Design Decisions**:
- Repository interfaces in `domain/repository/`, implementations in `infrastructure/datasource/`
- MyBatis mappers use Thymeleaf SQL templating (`mybatis-thymeleaf`)
- Value objects are immutable records in `domain/type/`
- UseCase classes orchestrate business flows and define transaction boundaries

## Testing

**Test Configuration**:
- Integration tests use `@ActiveProfiles("test")` with H2 in-memory database
- Test data loaded via `@Sql` annotations with UTF-8 encoding
- Test schema: `src/test/resources/sql/initsql/schema_test.sql`

**Integration Test Pattern**:
```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
    "/sql/initsql/schema_test.sql",
    "/path/to/TestData.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
class SomeIntegrationTest {
    // Test methods with @Test and @DisplayName
}
```

**Test Data Design Rules**:
- See `docs/test-data-design-rules.md` for detailed conventions
- Master data codes 0001-0060 are fixed and cannot be deleted/updated
- Test SQL files are placed alongside test classes in resources

## Refactoring History

Phase 1-5 refactoring (DDD準拠リファクタリング) is complete. Documents are archived in `docs/archive/income-expenditure-refactoring/`.

**Phase 5 completed**: `IncomeAndExpenditureRegistUseCase` was split into 5 UseCases:
1. `IncomeAndExpenditureInitUseCase` - Screen initialization
2. `IncomeRegistUseCase` - Income session operations
3. `ExpenditureRegistUseCase` - Expenditure session operations
4. `ExpenditureItemSelectUseCase` - Item selection screen
5. `IncomeAndExpenditureRegistConfirmUseCase` - Registration confirmation/DB update

## Feature Development History

**Feature1.01** (fixed cost management enhancements, `docs/archive/fixedcost/`):
- UseCase分割: `FixedCostInfoManageUseCase` (read) / `FixedCostRegistConfirmUseCase` (write)
- Feature①: Bulk update for same-category fixed costs (`FixedCostRegistConfirmUseCase.execBulkUpdate`)
- Feature②: 3-month rolling total display on fixed cost screen
- Feature③-1: Annual fixed cost summary screen (`FixedCostAnnualSummaryUseCase`)
- Feature③-2: Monthly fixed cost detail screen (`FixedCostMonthlyDetailUseCase`)

**Feature1.02** (monthly account improvements):
- Feature1.02-dev1: Expenditure list view toggle on monthly account screen (`AccountMonthInquiryUseCase`, `docs/archive/account-month-inquiry/`)
- Feature1.02-dev2: 0-yen fixed cost registration support with validation (`FixedCostInfoUpdateForm`, `IncomeAndExpenditureInitUseCase`, `docs/archive/fixedcost/`)

## Documentation

- `docs/ドメイン用語集.md` - Domain glossary (収支/支出項目/固定費/支払方法/銀行口座/システム予約値 etc.); update whenever a feature introduces new domain terms
- `docs/test-data-design-rules.md` - Test data conventions
- `docs/integration-test-guidelines.md` - UseCase integration test detailed guidelines (response verification, assertion rules, session/DB patterns)
- `docs/DDD設計_金額クラス統合判断基準.md` - Money class design decisions
- `docs/specifications/06_コーディング規約.md` - Coding conventions: Java file header update-history format, `@since` versioning rule, encoding/line-ending standard per file type
- `docs/specifications/07_テスト方針書.md` - Project-wide test strategy (coverage targets, naming conventions, quality standards)
- `docs/archive/income-expenditure-refactoring/` - Archived Phase 1-5 refactoring documents
- `docs/archive/fixedcost/` - Archived fixed cost feature documents (Feature1.01: UseCase分割・機能追加, Feature1.02: 0円固定費対応)
- `docs/archive/account-month-inquiry/` - Archived monthly account inquiry feature documents (Feature1.02: 支出別一覧追加)

## Coding Conventions

Full details: `docs/specifications/06_コーディング規約.md` (must-follow, applies across all chats/sessions on this project).

- **Javaファイルヘッダの更新履歴** (java files only; other source files and .js files have no update-history header):
  ```java
  /**
   * クラスの概要説明
   *
   *------------------------------------------------
   * 更新履歴
   * 日付       : version  ブランチ            コメントなど
   * 2026/05/27 : 1.00.00  feature-1.01-dev4   新規作成
   * 2026/08/18 : 1.01.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応
   *
   */
  ```
  Only "日付 : version" is colon-separated; the middle digit increments once per branch (1.00→1.01→…→99→alphabetic) and resets the trailing digit to `.00`; within the same branch, each additional change adds a new line incrementing only the trailing digit (1.01.00→1.01.01→1.01.02…) — same-purpose edits within the same branch may share one line instead of adding a new one per edit, even across different dates (update the line's date to the latest edit when merging). Version resets to X.00.00 when the branch major changes (e.g. `feature-2.00.**`). Add a new line per update; never rewrite past lines.
- **`@since` tag**: `@since 家計簿アプリ(1.03)` — the version part reflects the branch at class **creation** time only, and never changes afterward (update history above is where later changes are tracked).
- **Encoding / line endings** (new and edited files):

  | Type | Encoding | Line ending |
  |---|---|---|
  | java | UTF-8 | CRLF |
  | sql | UTF-8 | LF |
  | css | UTF-8 | LF |
  | js | UTF-8 | LF |
  | html | UTF-8 | CRLF |

## Japanese Conventions

This codebase uses Japanese for:
- Commit messages and documentation
- Test display names (`@DisplayName`)
- Javadoc comments
- Business domain terms (収入=income, 支出=expenditure, 固定費=fixed cost, etc.)
