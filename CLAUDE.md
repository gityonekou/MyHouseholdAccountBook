# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MyHouseholdAccountBook is a personal household account book web application built with Spring Boot 3.0.7 and Java 17. The project follows Domain-Driven Design (DDD) principles and is currently undergoing refactoring to achieve full DDD compliance.

**Tech Stack**: Spring Boot 3.0.7, Spring Security 6.0.7, MyBatis 3.0.2, Thymeleaf, Lombok, MariaDB (production), H2 (test)

## Build Commands

```bash
# Run all tests
mvn clean test

# Run domain layer tests only
mvn clean test -Pdomain-test

# Run phase1 tests only
mvn clean test -Pphase1-test

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

## Current Refactoring (Phase 5)

The project is in Phase 5: Refactoring `IncomeAndExpenditureRegistUseCase` into 5 smaller UseCases:
1. `IncomeAndExpenditureInitUseCase` - Screen initialization
2. `IncomeRegistUseCase` - Income session operations
3. `ExpenditureRegistUseCase` - Expenditure session operations
4. `ExpenditureItemSelectUseCase` - Item selection screen
5. `IncomeAndExpenditureRegistConfirmUseCase` - Registration confirmation/DB update

See `docs/Phase5_計画書.md` for detailed refactoring plan.

## Documentation

- `docs/Phase5_計画書.md` - Current refactoring plan
- `docs/Phase5_Step1_詳細設計書.md` - Detailed design
- `docs/Phase5_Step2_結合テスト作成計画書.md` - Integration test plan
- `docs/Phase5_パッケージリファクタリング設計書.md` - Package refactoring design (new package structure, class migration mapping, rename list)
- `docs/test-data-design-rules.md` - Test data conventions
- `docs/integration-test-guidelines.md` - UseCase integration test detailed guidelines (response verification, assertion rules, session/DB patterns)
- `docs/DDD設計_金額クラス統合判断基準.md` - Money class design decisions
- `07_テスト方針書.md` - Project-wide test strategy (coverage targets, naming conventions, quality standards)

## Japanese Conventions

This codebase uses Japanese for:
- Commit messages and documentation
- Test display names (`@DisplayName`)
- Javadoc comments
- Business domain terms (収入=income, 支出=expenditure, 固定費=fixed cost, etc.)
