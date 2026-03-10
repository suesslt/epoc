# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

epoc is an economic simulation system for modeling market dynamics, financial accounting, product lifecycles, and stochastic variables (normal distributions). It has two components:

- **epoc** (Java/Maven): Business simulation and accounting engine
- **EpocServer** (Swift/Vapor): REST API exposing the simulation system with PostgreSQL persistence

## Build & Test Commands

```bash
# Build Java component
./mvnw compile

# Build Swift server
cd EpocServer && swift build

# Run Swift server tests
cd EpocServer && swift test

# Run Java tests
./mvnw test
```

## Architecture

### EpocServer (Swift/Vapor)

- **Vapor** web framework with **Fluent** ORM + PostgreSQL
- SPM package (Swift 5.9+, macOS 14+)
- Depends on Score package via local path (`../../score`)

#### Key Types
| Type | Role |
|------|------|
| `Storage` | Persistence state management |
| `ProductLifecycle` | Product lifecycle modeling |
| `FinancialAccounting` | Accounting engine |
| `Booking` / `DebitCreditAmount` | Double-entry accounting entries |
| `EpocCalendar` | Simulation calendar |
| `NormalDistribution` | Stochastic variable modeling |

#### ScoreCompatibility.swift
Bridges Java-style API to Swift-style Score API:
- `Money.of()`, `Money.parse()` factory methods
- `add()`, `subtract()`, `multiply()`, `divide()` arithmetic
- `Currency.getInstance()`, `currencyCode`, `fractionDigits`
- `Percent.scale`, `toBigDecimal`

### epoc (Java/Maven)
- Java 19+, Spring Boot, Jasper Reports
- Custom `jore` framework dependency
- Domain-driven design with accounting and simulation modules

## Score Package — Shared Base Classes

EpocServer depends on the [Score](../score) package via local SPM dependency (`../../score`).

**Current usage**: `import Score` for `Money`, `Currency`, `Percent` in financial accounting and simulation.

### Available Types

| Type | Module | Description |
|------|--------|-------------|
| `Money` | Score | Currency-safe monetary amounts with `Decimal` precision. Arithmetic enforces matching currencies. |
| `Currency` | Score | ISO 4217 enum with 180+ currencies, decimal places, and localized names. |
| `Percent` | Score | Percentage as factor (e.g. `0.10` = 10%). |
| `FXRate` | Score | Bid/ask exchange rates with conversion methods. |
| `VATCalculation` | Score | VAT split (net/gross) with inclusive/exclusive handling. |
| `YearMonth` | Score | Year-month value type for monthly periods. |
| `DayCountRule` | Score | Financial day count conventions (ACT/360, ACT/365, 30/360). |
| `InterestCalculationRule` | Score | Interest accrual calculation rules. |
| `ServicePipeline` | Score | Async middleware chain for service operations. |
| `ServiceError` | Score | Typed errors (notFound, validation, businessRule, etc.). |
| `CSVExportable` | Score | Protocol for CSV row export. |
| `IBANValidator` | Score | ISO 13616 IBAN validation. |
| `SCORReferenceGenerator` | Score | ISO 11649 creditor reference with Mod 97. |
| `ErrorHandler` | ScoreUI | Observable error state management for SwiftUI. |
| `PDFRenderer` | ScoreUI | UIKit-based PDF generation. |
| `.errorAlert()` | ScoreUI | SwiftUI modifier for error alert presentation. |

```swift
import Score  // Money, Currency, Percent, etc.
```
