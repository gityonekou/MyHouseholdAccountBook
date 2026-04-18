# Phase 5: 収支登録ユースケースのリファクタリング計画書

## 1. 概要

### 1.1 目的

Phase 4までに完了した値オブジェクト（Money、DateValue系）のリファクタリング成果を活用し、ユースケース層の責務分離と保守性向上を実現します。

### 1.2 背景

**Phase 4完了時の状況**:

Phase 4では以下の成果を得ました：
- クラス名改善: SisyutuKingakuB/C/BC → WasteExpenditure系
- 日付クラス統合: EventDate統合、ShoppingDate改善
- 不要クラス削除: IncomingAmount削除
- DDD設計基準の文書化

**現状の課題**:

現在、`IncomeAndExpenditureRegistUseCase`クラスが以下のすべての責務を担当しており、単一責任原則（SRP）に違反しています：

| 責務 | メソッド例 | 問題点 |
|------|----------|--------|
| 収支登録・更新画面表示 | `readInitInfo`, `readUpdateInfo` | 画面表示ロジックが混在 |
| 収支登録内容確認画面表示 | `readRegistCheckInfo` | 確認画面の責務が混在 |
| 収支の登録・更新処理 | `execRegistAction` | DB更新処理が混在 |
| セッション管理 | `execIncomeAction`, `execExpenditureAction` | セッション操作が各所に分散 |
| 支出項目選択画面表示 | `readExpenditureAddSelect` | 別画面の責務が混在 |

### 1.3 スコープ

**Phase 5の対象**:
- **対象クラス**: `IncomeAndExpenditureRegistUseCase`
- **対象パッケージ**: `com.yonetani.webapp.accountbook.application.usecase.account.regist`

**実施内容**:
1. 現状分析: メソッド一覧と責務の洗い出し
2. 責務分離の設計: UseCaseクラスの分割方針決定
3. **結合テスト作成: リファクタリング前の動作を保証するテストの作成**
4. リファクタリング実施: クラス分割と移行
5. テスト実施: リファクタリング後の動作確認（作成したテストが通ることを確認）

---

## 2. 現状分析

### 2.1 IncomeAndExpenditureRegistUseCaseの概要

**クラス情報**:
- **パッケージ**: `com.yonetani.webapp.accountbook.application.usecase.account.regist`
- **行数**: 約1200行（推定）
- **メソッド数**: 17個のpublicメソッド

**依存コンポーネント**:
```java
// コンポーネント
private final CodeTableItemComponent codeTableItem;
private final SisyutuItemComponent sisyutuItemComponent;
private final SisyutuKingakuItemHolderComponent sisyutuKingakuItemHolderComponent;
private final ShoppingRegistExpenditureAndSisyutuKingakuComponent checkComponent;

// リポジトリ
private final FixedCostTableRepository fixedCostRepository;
private final EventItemTableRepository eventRepository;
private final IncomeTableRepository incomeRepository;
private final ExpenditureTableRepository expenditureRepository;
private final IncomeAndExpenditureTableRepository incomeAndExpenditureRepository;
private final SisyutuKingakuTableRepository sisyutuKingakuTableRepository;
```

### 2.2 メソッド一覧と責務分類

#### グループ1: 収支登録・更新画面（メイン画面）

| メソッド名 | 責務 | 概要 |
|-----------|------|------|
| `readInitInfo` | 画面初期表示 | 新規登録時の画面表示情報取得（固定費から自動生成） |
| `readUpdateInfo` | 画面初期表示 | 更新時の画面表示情報取得（DB登録済み情報） |
| `readIncomeAndExpenditureInfoList` | 画面再表示 | 収入・支出一覧の再表示 |

#### グループ2: 収入操作（セッション更新）

| メソッド名 | 責務 | 概要 |
|-----------|------|------|
| `readIncomeAddSelect` | 画面表示 | 収入新規追加選択画面表示 |
| `readIncomeUpdateSelect` | 画面表示 | 収入更新選択画面表示 |
| `readIncomeUpdateBindingErrorSetInfo` | エラー処理 | 収入更新時のバインディングエラー処理 |
| `execIncomeAction` | セッション操作 | 収入の追加・更新・削除（セッションのみ） |

#### グループ3: 支出操作（セッション更新）

| メソッド名 | 責務 | 概要 |
|-----------|------|------|
| `readExpenditureUpdateSelect` | 画面表示 | 支出更新選択画面表示 |
| `readExpenditureUpdateBindingErrorSetInfo` | エラー処理 | 支出更新時のバインディングエラー処理 |
| `execExpenditureAction` | セッション操作 | 支出の追加・更新・削除（セッションのみ） |
| `readNewExpenditureItem` | 画面表示 | 選択した支出項目の新規支出情報表示 |

#### グループ4: 支出項目選択画面

| メソッド名 | 責務 | 概要 |
|-----------|------|------|
| `readExpenditureAddSelect` | 画面表示 | 支出項目選択画面初期表示 |
| `readExpenditureItemActSelect` | 画面表示 | 支出項目選択後の確認画面表示 |

#### グループ5: 収支登録内容確認画面

| メソッド名 | 責務 | 概要 |
|-----------|------|------|
| `readRegistCheckInfo` | 画面表示 | 登録内容確認画面表示 |

#### グループ6: トランザクション完了処理

| メソッド名 | 責務 | 概要 |
|-----------|------|------|
| `readRegistCancelInfo` | トランザクション完了 | 登録キャンセル時のトランザクション完了 |
| `execRegistAction` | DB更新とトランザクション完了 | 収支情報の登録・更新（4テーブル更新）とトランザクション完了 |

**注**: `readRegistCheckErrorSetInfo`はグループ1（収支登録・更新画面）に分類されます。

---

## 3. 責務分離の方針

### 3.1 基本方針

**DDDのレイヤードアーキテクチャに基づく責務分離**:

```
┌─────────────────────────────────────────┐
│ Presentation Layer (Controller)         │
│ - リクエスト受付、レスポンス返却         │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ Application Layer (UseCase)             │
│ - ビジネスフロー調整                     │
│ - トランザクション境界                   │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ Domain Layer (Model, Repository)        │
│ - ビジネスロジック                       │
│ - ドメインルール                         │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ Infrastructure Layer (Repository実装)    │
│ - DB接続、外部システム連携               │
└─────────────────────────────────────────┘
```

### 3.2 分割方針

**画面と責務単位でUseCaseを分離**:

現在の`IncomeAndExpenditureRegistUseCase`を以下の**5つ**に分割します：

#### 採用する分割方針: 画面と責務による分割（5分割）

| UseCase名 | 責務 | 対象メソッド | 推定行数 |
|-----------|------|------------|---------|
| **IncomeAndExpenditureInitUseCase**<br>（収支登録・更新画面初期表示） | ・収支登録・更新画面の初期表示<br>・固定費からの自動生成<br>・入力確認時のエラー処理 | `readInitInfo`<br>`readUpdateInfo`<br>`readIncomeAndExpenditureInfoList`<br>`readRegistCheckErrorSetInfo` | 約300-400行 |
| **IncomeRegistUseCase**<br>（収入登録操作） | ・収入の追加・更新・削除（セッション操作） | `readIncomeAddSelect`<br>`readIncomeUpdateSelect`<br>`readIncomeUpdateBindingErrorSetInfo`<br>`execIncomeAction` | 約350-450行 |
| **ExpenditureRegistUseCase**<br>（支出登録操作） | ・支出の追加・更新・削除（セッション操作） | `readExpenditureUpdateSelect`<br>`readExpenditureUpdateBindingErrorSetInfo`<br>`execExpenditureAction`<br>`readNewExpenditureItem` | 約400-500行 |
| **ExpenditureItemSelectUseCase**<br>（支出項目選択画面） | ・支出項目選択画面の表示<br>・支出項目の選択処理 | `readExpenditureAddSelect`<br>`readExpenditureItemActSelect` | 約100-150行 |
| **IncomeAndExpenditureRegistConfirmUseCase**<br>（収支登録確認・完了処理） | ・登録内容確認画面の表示<br>・トランザクション完了処理（DB登録・更新、キャンセル） | `readRegistCheckInfo`<br>`readRegistCancelInfo`<br>`execRegistAction` | 約370-450行 |

**5分割を採用する理由**:
- ✅ 各クラスの行数が適切な範囲（300-500行）に収まる
- ✅ 単一責任原則に沿った明確な責務分離
- ✅ 保守性・可読性の向上
- ✅ 1つの大きなInputUseCaseではなく、画面初期表示と収入/支出操作を分離することでさらに理解しやすくなる

### 3.3 共通処理の抽出

**共通処理コンポーネント化**:

以下の共通処理は別途コンポーネントとして抽出を検討：

| コンポーネント名 | 責務 | 抽出理由 |
|----------------|------|---------|
| **IncomeAndExpenditureSessionManager** | セッション管理（収入・支出リスト操作） | 複数UseCaseで使用 |
| **FixedCostAutoGenerator** | 固定費からの収支自動生成 | ビジネスロジックの分離 |
| **IncomeAndExpenditureValidator** | 収支データ検証 | バリデーションロジックの集約 |

---

## 4. リファクタリング手順

### 4.1 Phase 5 - Step 1: 現状分析と設計確定

**目的**: 詳細な分析と設計の確定

**実施内容**:
1. 全メソッドの詳細分析（引数、戻り値、依存関係）
2. 分割後のクラス設計詳細化
3. 移行リスクの洗い出し
4. テスト戦略の策定

**成果物**:
- `Phase5_Step1_詳細設計書.md`

**リスク**: 低（分析・設計フェーズ）

---

### 4.2 Phase 5 - Step 2: 結合テストの作成（重要）

**目的**: リファクタリング前の動作を保証するテストの作成

**重要性**:
- リファクタリングは「**外部から見た振る舞いを変えずに内部構造を改善すること**」
- リファクタリング前にテストを作成し、リファクタリング後も同じテストが通ることを確認することで、安全性を担保
- **テストファースト**のアプローチでリスクを最小化

**実施内容**:
1. 既存の結合テストの確認（存在する場合）
2. 分離後の5つのUseCaseに対応する結合テスト作成（アプリケーション層）
   - `IncomeAndExpenditureInitIntegrationTest`（収支登録・更新画面初期表示）
   - `IncomeRegistIntegrationTest`（収入登録操作）
   - `ExpenditureRegistIntegrationTest`（支出登録操作）
   - `ExpenditureItemSelectIntegrationTest`（支出項目選択画面）
   - `IncomeAndExpenditureRegistConfirmIntegrationTest`（登録確認・完了処理）
3. Controller層の結合テスト作成
   - `IncomeAndExpenditureRegistControllerIntegrationTest`（Controller層結合テスト）
4. 主要なビジネスシナリオのテストケース作成
   - 新規登録フロー（固定費自動生成含む）
   - 更新フロー
   - 収入追加・更新・削除
   - 支出追加・更新・削除
   - 登録確認・DB更新
5. テスト実行とカバレッジ確認

**成果物**:
- 6つの結合テストクラス（アプリケーション層5つ + Controller層1つ）
- テストカバレッジレポート

**リスク**: 中（テスト作成には時間がかかるが、後のリスク低減に不可欠）

**重要な注意点**:
- このステップ完了前にリファクタリングに進まないこと
- すべてのテストがGREENになることを確認してから次へ進む

---

### 4.3 Phase 5 - Step 3: ExpenditureItemSelectUseCaseの分離

**目的**: 比較的独立した支出項目選択画面を先行分離

**前提条件**: Step 2の結合テストがすべてGREEN

**実施内容**:
1. `ExpenditureItemSelectUseCase`クラス作成
2. 2メソッドの移行（`readExpenditureAddSelect`, `readExpenditureItemActSelect`）
3. Controllerの修正
4. **Step 2で作成したテスト実行**（リファクタリング後も同じ動作を確認）

**合格基準**: `ExpenditureItemSelectIntegrationTest`がすべてGREEN

**リスク**: 低（メソッド数が少なく独立性が高い）

---

### 4.4 Phase 5 - Step 4: IncomeRegistUseCaseの分離

**目的**: 収入登録操作の分離

**前提条件**: Step 3完了、すべてのテストがGREEN

**実施内容**:
1. `IncomeRegistUseCase`クラス作成
2. 4メソッドの移行（収入操作関連）
3. Controllerの修正
4. **Step 2で作成したテスト実行**（リファクタリング後も同じ動作を確認）

**合格基準**: `IncomeRegistIntegrationTest`がすべてGREEN

**リスク**: 低（依存が少なくシンプル）

---

### 4.5 Phase 5 - Step 5: ExpenditureRegistUseCaseの分離

**目的**: 支出登録操作の分離

**前提条件**: Step 4完了、すべてのテストがGREEN

**実施内容**:
1. `ExpenditureRegistUseCase`クラス作成
2. 4メソッドの移行（支出操作関連）
3. Controllerの修正
4. **Step 2で作成したテスト実行**（リファクタリング後も同じ動作を確認）

**合格基準**: `ExpenditureRegistIntegrationTest`がすべてGREEN

**リスク**: 低（IncomeRegistUseCaseの分離パターンを活用できる）

---

### 4.6 Phase 5 - Step 6: IncomeAndExpenditureRegistConfirmUseCaseの分離

**目的**: DB更新処理の分離とトランザクション境界の明確化

**前提条件**: Step 5完了、すべてのテストがGREEN

**実施内容**:
1. `IncomeAndExpenditureRegistConfirmUseCase`クラス作成
2. 3メソッドの移行（確認画面表示、トランザクション完了処理）
3. トランザクション設定の確認（`@Transactional`）
4. Controllerの修正
5. **Step 2で作成したテスト実行**（リファクタリング後も同じ動作を確認）

**合格基準**: `IncomeAndExpenditureRegistConfirmIntegrationTest`がすべてGREEN

**リスク**: 中（DB更新処理を含むため慎重な移行が必要）

---

### 4.7 Phase 5 - Step 7: IncomeAndExpenditureInitUseCaseへのリネーム

**目的**: 残ったクラスの責務明確化

**前提条件**: Step 6完了、すべてのテストがGREEN

**実施内容**:
1. `IncomeAndExpenditureRegistUseCase` → `IncomeAndExpenditureInitUseCase`にリネーム
2. クラスのJavadoc更新
3. Controllerの修正
4. **全テスト実施**（6つの結合テストすべて実行）

**合格基準**: 全結合テストがGREEN

**リスク**: 低（リネームのみ）

---

### 4.8 Phase 5 - Step 8: コードクオリティ向上（詳細リファクタリング）

**目的**: Step 3〜7のUseCase分離で明らかになった重複・混在ロジックを整理し、コードの品質をさらに向上させる

**前提条件**: Step 7完了、すべてのテストがGREEN

**背景**:
Step 7完了後に5つのUseCaseを精読した結果、当初計画では気づかなかった以下の改善点が判明した。
詳細は `docs/Phase5_Step8_詳細設計書.md` を参照。

**実施内容（5 Sub-Step）**:

| Sub-Step | 内容 | 優先度 | 難易度 |
|----------|------|--------|--------|
| **8-1** | `setIncomeAndExpenditureInfoList` の共通コンポーネント化（4UseCase完全重複） | 🔴 高 | 低 |
| **8-2** | `SisyutuKubun.isWastedBOrC()` 追加（無駄遣いB/C判定のドメイン化） | 🔴 高 | 低 |
| **8-3** | `execRegistAction` の privateメソッド分割（358行メソッドの可読性向上） | 🟡 中 | 中 |
| **8-4** | 仮登録コード採番ロジックのドメインサービス化 | 🔵 低 | 中 |
| **8-5** | `execIncomeAction` / `execExpenditureAction` のリファクタリング | 🔵 低 | 高 |

**合格基準**: 全テストがGREEN（678件以上）

**リスク**: 低（既存テストで動作保証される）

---

## 5. 期待成果

### 5.1 定量的成果

**クラス構成**:
- リファクタリング前: 1クラス（約1726行）
- リファクタリング後: 5クラス（各300-500行程度）

**責務の明確化**:
- 画面と責務単位で明確な分離
- トランザクション境界の明確化
- テストの容易性向上
- 各クラスの責務が一目で理解できる

### 5.2 定性的成果

**保守性の向上**:
- 単一責任原則の適用
- クラスサイズの適正化（300-500行目安）
- 変更影響範囲の限定化

**可読性の向上**:
- クラス名から責務が明確
- メソッド数の適正化（1クラス5-10メソッド）
- ビジネスフローの理解容易性

**拡張性の向上**:
- 新機能追加時の影響範囲限定
- テストコードの追加容易性
- 将来的なマイクロサービス化への対応

---

## 6. リスクと対策

### 6.1 リスク一覧

| リスク | 影響度 | 発生確率 | 対策 |
|--------|--------|---------|------|
| セッション管理の不整合 | 高 | 中 | 移行前後でセッション操作の動作確認を徹底 |
| トランザクション境界の誤り | 高 | 低 | @Transactional設定の確認とDBロールバックテスト |
| Controller修正漏れ | 中 | 中 | Grepで全参照箇所を検索し一覧化 |
| テストカバレッジ不足 | 中 | 中 | 移行前に既存の動作確認テストを実施 |
| 分割後のクラス間依存 | 低 | 低 | 分割設計時に依存関係を明確化 |

### 6.2 ロールバック計画

各Stepでコミットを分け、問題発生時は前Stepに戻せる構成とします。

```
Step 1完了 → コミット (設計確定)
Step 2完了 → コミット (結合テスト作成完了) ← 重要なマイルストーン
Step 3完了 → コミット (ExpenditureItemSelectUseCase分離)
Step 4完了 → コミット (IncomeRegistUseCase分離)
Step 5完了 → コミット (ExpenditureRegistUseCase分離)
Step 6完了 → コミット (RegistConfirmUseCase分離)
Step 7完了 → コミット (リネーム完了)
Step 8完了 → コミット (コンポーネント化完了・オプション)
```

**重要**: Step 2（結合テスト作成）完了時点でテストがすべてGREENであることを確認し、コミットします。これがリファクタリングの安全網となります。

---

## 7. 実施スケジュール

| Step | タスク | 優先度 | 見積もり工数 | 備考 |
|------|--------|--------|-------------|------|
| Step 1 | 現状分析と設計確定 | 高 | 中 | 設計書作成 |
| **Step 2** | **結合テスト作成** | **最高** | **大** | **リファクタリング前の必須作業（6テストクラス作成）** |
| Step 3 | ExpenditureItemSelectUseCase分離 | 高 | 小 | テストGREENを確認 |
| Step 4 | IncomeRegistUseCase分離 | 高 | 小 | テストGREENを確認 |
| Step 5 | ExpenditureRegistUseCase分離 | 高 | 小 | テストGREENを確認 |
| Step 6 | RegistConfirmUseCase分離 | 高 | 中 | テストGREENを確認 |
| Step 7 | クラスリネーム | 中 | 小 | テストGREENを確認 |
| Step 8 | コードクオリティ向上（詳細リファクタリング） | 高 | 中〜大 | 5 Sub-Step実施、詳細は詳細設計書参照 |

**推奨実施順序**:
1. **Step 1（設計確定）を最優先で実施**
2. **Step 2（結合テスト作成）を必ず完了させる** ← これなしでリファクタリングに進まない
3. Step 3-6を順次実施（各Step完了後、テストGREENを確認）
4. Step 7でリファクタリング完了
5. Step 8でコードクオリティを向上（詳細は`docs/Phase5_Step8_詳細設計書.md`参照）

**テストファーストの原則**:
- リファクタリング = 外部から見た振る舞いは変えずに内部構造を改善
- Step 2で作成したテストがStep 3-7のリファクタリング後もGREENになることで、振る舞いが保たれていることを証明

---

## 8. 更新履歴

| 版数 | 更新日 | 更新内容 |
|------|--------|---------|
| 1.00.00 | 2026-01-02 | 初版作成 |
| 1.01.00 | 2026-01-02 | Step 2「結合テスト作成」を追加、実施順序を修正 |
| 1.02.00 | 2026-01-12 | 5分割方針に更新（3分割→5分割）、Step数を8に拡張 |
| 1.03.00 | 2026-02-26 | Step 8をコードクオリティ向上（詳細リファクタリング）に更新、5 Sub-Step化 |

---

**作成日**: 2026-01-02
**最終更新日**: 2026-01-02
**作成者**: DDD Refactoring Project Phase 5
**ステータス**: 🔄計画中
