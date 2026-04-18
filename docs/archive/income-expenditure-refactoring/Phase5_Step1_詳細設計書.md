# Phase 5 - Step 1: 詳細設計書

## 1. 現状分析

### 1.1 IncomeAndExpenditureRegistUseCaseの概要

**クラス情報**:
- **パッケージ**: `com.yonetani.webapp.accountbook.application.usecase.account.regist`
- **ファイルパス**: `src/main/java/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureRegistUseCase.java`
- **行数**: **1726行** (Phase5計画書の予想1200行を大幅に超過)
- **publicメソッド数**: **17個**

**依存コンポーネント**:
```java
// コンポーネント (4個)
private final CodeTableItemComponent codeTableItem;
private final SisyutuItemComponent sisyutuItemComponent;
private final SisyutuKingakuItemHolderComponent sisyutuKingakuItemHolderComponent;
private final ShoppingRegistExpenditureAndSisyutuKingakuComponent checkComponent;

// リポジトリ (6個)
private final FixedCostTableRepository fixedCostRepository;
private final EventItemTableRepository eventRepository;
private final IncomeTableRepository incomeRepository;
private final ExpenditureTableRepository expenditureRepository;
private final IncomeAndExpenditureTableRepository incomeAndExpenditureRepository;
private final SisyutuKingakuTableRepository sisyutuKingakuTableRepository;
```

**合計依存数**: 10個のコンポーネント/リポジトリに依存

---

## 2. メソッド一覧と責務分類

### 2.1 グループ1: 収支登録・更新画面（メイン画面） - 4メソッド

| # | メソッド名 | 行番号 | 責務 | 戻り値 |
|---|-----------|-------|------|--------|
| 1 | `readInitInfo` | 135 | 新規登録時の画面初期表示（固定費から自動生成） | IncomeAndExpenditureRegistResponse |
| 2 | `readUpdateInfo` | 247 | 更新時の画面初期表示（DB登録済み情報取得） | IncomeAndExpenditureRegistResponse |
| 3 | `readIncomeAndExpenditureInfoList` | 338 | 収入・支出一覧の再表示 | IncomeAndExpenditureRegistResponse |
| 4 | `readRegistCheckErrorSetInfo` | 1041 | 内容確認時の入力不備エラー処理（収入未登録） | IncomeAndExpenditureRegistResponse |

**主要な処理内容**:
- `readInitInfo`: 固定費テーブルから対象月に合致する固定費を取得し、支出情報としてセッションと画面表示データに設定。収入情報は空を設定する
- `readUpdateInfo`: DBから既存の収支情報を取得し、セッションと画面表示データに設定
- `readIncomeAndExpenditureInfoList`: セッションの収入・支出リストを画面表示用に再構築
- `readRegistCheckErrorSetInfo`: 「内容確認」ボタン押下時に収入情報が未登録の場合、エラーメッセージを設定して収支登録・更新画面（メイン画面）を再表示

---

### 2.2 グループ2: 収入操作（セッション更新） - 4メソッド

| # | メソッド名 | 行番号 | 責務 | 戻り値 |
|---|-----------|-------|------|--------|
| 4 | `readIncomeAddSelect` | 360 | 収入新規追加選択画面表示 | IncomeAndExpenditureRegistResponse |
| 5 | `readIncomeUpdateSelect` | 392 | 収入更新選択画面表示 | IncomeAndExpenditureRegistResponse |
| 6 | `readIncomeUpdateBindingErrorSetInfo` | 453 | 収入更新時のバインディングエラー処理 | IncomeAndExpenditureRegistResponse |
| 7 | `execIncomeAction` | 480 | 収入の追加・更新・削除（セッションのみ） | IncomeAndExpenditureRegistResponse |

**主要な処理内容**:
- `readIncomeAddSelect`: 新規収入追加用のフォームを生成し画面表示
- `readIncomeUpdateSelect`: 指定された収入コードの情報を取得し更新フォーム表示
- `readIncomeUpdateBindingErrorSetInfo`: バリデーションエラー時のフォーム再設定
- `execIncomeAction`: ACTION_TYPE（ADD/UPDATE/DELETE）に応じてセッションの収入リストを操作

---

### 2.3 グループ3: 支出操作（セッション更新） - 4メソッド

| # | メソッド名 | 行番号 | 責務 | 戻り値 |
|---|-----------|-------|------|--------|
| 8 | `readExpenditureUpdateSelect` | 636 | 支出更新選択画面表示 | IncomeAndExpenditureRegistResponse |
| 9 | `readExpenditureUpdateBindingErrorSetInfo` | 708 | 支出更新時のバインディングエラー処理 | IncomeAndExpenditureRegistResponse |
| 10 | `execExpenditureAction` | 735 | 支出の追加・更新・削除（セッションのみ） | IncomeAndExpenditureRegistResponse |
| 11 | `readNewExpenditureItem` | 962 | 選択した支出項目の新規支出情報表示 | IncomeAndExpenditureRegistResponse |

**主要な処理内容**:
- `readExpenditureUpdateSelect`: 指定された支出コードの情報を取得し更新フォーム表示
- `readExpenditureUpdateBindingErrorSetInfo`: バリデーションエラー時のフォーム再設定
- `execExpenditureAction`: ACTION_TYPE（ADD/UPDATE/DELETE）に応じてセッションの支出リストを操作
- `readNewExpenditureItem`: 支出項目選択後の新規支出情報フォーム生成

---

### 2.4 グループ4: 支出項目選択画面 - 2メソッド

| # | メソッド名 | 行番号 | 責務 | 戻り値 |
|---|-----------|-------|------|--------|
| 12 | `readExpenditureAddSelect` | 875 | 支出項目選択画面初期表示 | ExpenditureItemSelectResponse |
| 13 | `readExpenditureItemActSelect` | 895 | 支出項目選択後の確認画面表示 | ExpenditureItemSelectResponse |

**主要な処理内容**:
- `readExpenditureAddSelect`: 支出項目一覧を取得し選択画面表示
- `readExpenditureItemActSelect`: 選択された支出項目の詳細情報を表示

**注目点**: このグループのみ戻り値が `ExpenditureItemSelectResponse` で他と異なる

---

### 2.5 グループ5: 収支登録内容確認画面 - 1メソッド

| # | メソッド名 | 行番号 | 責務 | 戻り値 |
|---|-----------|-------|------|--------|
| 14 | `readRegistCheckInfo` | 1018 | 登録内容確認画面表示 | IncomeAndExpenditureRegistCheckResponse |

**主要な処理内容**:
- `readRegistCheckInfo`: セッションの収入・支出情報を確認画面用に整形して表示

**注目点**: 確認画面を表示する唯一のメソッド

---

### 2.6 グループ6: トランザクション完了処理 - 2メソッド

| # | メソッド名 | 行番号 | 責務 | 戻り値 |
|---|-----------|-------|------|--------|
| 15 | `readRegistCancelInfo` | 1065 | 登録キャンセル時のトランザクション完了 | IncomeAndExpenditureRegistCheckResponse |
| 16 | `execRegistAction` | 1092 | 収支情報の登録・更新後のトランザクション完了（4テーブル更新） | IncomeAndExpenditureRegistCheckResponse |

**主要な処理内容**:
- `readRegistCancelInfo`: キャンセルメッセージを設定し、`setTransactionSuccessFull()`で月次収支照会画面へのリダイレクト情報を設定
- `execRegistAction`: 4テーブル（収支、収入、支出、支出金額）への登録・更新後、`setTransactionSuccessFull()`でトランザクション完了を設定

**共通点**: 両メソッドとも`setTransactionSuccessFull()`を呼び出して月次収支照会画面にリダイレクト

**重要**:
- `execRegistAction`は`@Transactional`アノテーションが必須
- `execRegistAction`は実際のDB更新を行う唯一のメソッド

---

## 3. 分割方針の決定

### 3.1 採用する分割方針

**Phase5計画書の「分割案1: 画面責務による分割」を採用**

理由:
1. ✅ 画面単位で責務が明確
2. ✅ トランザクション境界が`RegistConfirmUseCase`に集約される
3. ✅ 過度な細分化を避け、適切な粒度を維持
4. ✅ 現在のメソッド分類がすでに画面単位になっている

### 3.2 分割後のクラス構成

#### 分割後クラス1: **IncomeAndExpenditureInitUseCase** (収支登録・更新画面初期表示)

**責務**: 収支登録・更新画面の初期表示、固定費からの自動生成、入力確認時のエラー処理

**移行対象メソッド** (4メソッド):
```
グループ1: 収支登録・更新画面（メイン画面） - 4メソッド
  - readInitInfo
  - readUpdateInfo
  - readIncomeAndExpenditureInfoList
  - readRegistCheckErrorSetInfo
```

**推定行数**: 約300-400行

**依存コンポーネント/リポジトリ**:
- CodeTableItemComponent
- SisyutuKingakuItemHolderComponent
- FixedCostTableRepository
- EventItemTableRepository
- IncomeTableRepository
- ExpenditureTableRepository

---

#### 分割後クラス2: **IncomeRegistUseCase** (収入登録操作)

**責務**: 収入の追加・更新・削除（セッション操作）

**移行対象メソッド** (4メソッド):
```
グループ2: 収入操作（セッション更新） - 4メソッド
  - readIncomeAddSelect
  - readIncomeUpdateSelect
  - readIncomeUpdateBindingErrorSetInfo
  - execIncomeAction
```

**推定行数**: 約350-450行

**依存コンポーネント/リポジトリ**:
- CodeTableItemComponent

---

#### 分割後クラス3: **ExpenditureRegistUseCase** (支出登録操作)

**責務**: 支出の追加・更新・削除（セッション操作）

**移行対象メソッド** (4メソッド):
```
グループ3: 支出操作（セッション更新） - 4メソッド
  - readExpenditureUpdateSelect
  - readExpenditureUpdateBindingErrorSetInfo
  - execExpenditureAction
  - readNewExpenditureItem
```

**推定行数**: 約400-500行

**依存コンポーネント/リポジトリ**:
- CodeTableItemComponent
- SisyutuItemComponent
- SisyutuKingakuItemHolderComponent
- ShoppingRegistExpenditureAndSisyutuKingakuComponent (checkComponent)

---

#### 分割後クラス4: **ExpenditureItemSelectUseCase** (支出項目選択画面)

**責務**: 支出項目選択画面の表示、支出項目の選択処理

**移行対象メソッド** (2メソッド):
```
グループ4: 支出項目選択画面 - 2メソッド
  - readExpenditureAddSelect
  - readExpenditureItemActSelect
```

**推定行数**: 約100-150行（最も小さい）

**依存コンポーネント/リポジトリ**:
- SisyutuItemComponent

**注目点**:
- 独立性が高く、他のグループとの依存が少ない
- 戻り値が `ExpenditureItemSelectResponse` で他と異なる

---

#### 分割後クラス5: **IncomeAndExpenditureRegistConfirmUseCase** (収支登録確認・完了処理)

**責務**: 登録内容確認画面の表示、トランザクション完了処理（DB登録・更新、キャンセル）

**移行対象メソッド** (3メソッド):
```
グループ5: 収支登録内容確認画面 - 1メソッド
  - readRegistCheckInfo

グループ6: トランザクション完了処理 - 2メソッド
  - readRegistCancelInfo
  - execRegistAction (重要: @Transactional)
```

**推定行数**: 約370-450行

**依存コンポーネント/リポジトリ**:
- CodeTableItemComponent
- SisyutuKingakuItemHolderComponent
- IncomeTableRepository
- ExpenditureTableRepository
- IncomeAndExpenditureTableRepository
- SisyutuKingakuTableRepository

**重要な注意点**:
- `execRegistAction` メソッドは `@Transactional` アノテーションが必須
- 4テーブル（収支、収入、支出、支出金額）への更新処理を含む
- トランザクション境界の適切な設定が必要

---

## 4. 依存関係の分析

### 4.1 コンポーネント/リポジトリの使用状況

| コンポーネント/リポジトリ | InitUseCase | IncomeUseCase | ExpenditureUseCase | ItemSelectUseCase | ConfirmUseCase |
|------------------------|-------------|---------------|-------------------|-------------------|----------------|
| CodeTableItemComponent | ✅ | ✅ | ✅ | ❌ | ✅ |
| SisyutuItemComponent | ❌ | ❌ | ✅ | ✅ | ❌ |
| SisyutuKingakuItemHolderComponent | ✅ | ❌ | ✅ | ❌ | ✅ |
| ShoppingRegistExpenditureAndSisyutuKingakuComponent | ❌ | ❌ | ✅ | ❌ | ❌ |
| FixedCostTableRepository | ✅ | ❌ | ❌ | ❌ | ❌ |
| EventItemTableRepository | ✅ | ❌ | ❌ | ❌ | ❌ |
| IncomeTableRepository | ✅ | ❌ | ❌ | ❌ | ✅ |
| ExpenditureTableRepository | ✅ | ❌ | ❌ | ❌ | ✅ |
| IncomeAndExpenditureTableRepository | ❌ | ❌ | ❌ | ❌ | ✅ |
| SisyutuKingakuTableRepository | ❌ | ❌ | ❌ | ❌ | ✅ |

**分析結果**:
- `IncomeRegistUseCase` が最も依存が少ない（1個のみ）
- `ExpenditureItemSelectUseCase` も依存が少ない（1個のみ）
- `IncomeAndExpenditureInitUseCase` は画面初期表示のため依存が多い（6個）
- `ExpenditureRegistUseCase` はセッション操作＋検証で依存あり（4個）
- `IncomeAndExpenditureRegistConfirmUseCase` はDB更新系リポジトリを中心に依存（6個）

---

## 5. リファクタリング実施順序の確定

### 5.1 推奨実施順序

Phase5計画書に従い、以下の順序で実施します:

```
Step 1: 現状分析と設計確定 (本ドキュメント) ← 現在ここ
  ↓
Step 2: 結合テスト作成 (最重要) ← 次のステップ
  ↓
Step 3: ExpenditureItemSelectUseCase の分離 (依存が少なく独立性が高い)
  ↓
Step 4: IncomeRegistUseCase の分離 (依存が少なくシンプル)
  ↓
Step 5: ExpenditureRegistUseCase の分離 (セッション操作のみ)
  ↓
Step 6: IncomeAndExpenditureRegistConfirmUseCase の分離 (DB更新処理を含むため慎重に)
  ↓
Step 7: IncomeAndExpenditureInitUseCase へのリネーム
  ↓
Step 8: 共通処理のコンポーネント化 (オプション)
```

**分離順序の理由**:
1. **Step 3**: `ExpenditureItemSelectUseCase` は依存が最小（1個）で独立性が高く、戻り値も異なるため最初に分離しやすい
2. **Step 4**: `IncomeRegistUseCase` は依存が最小（1個）でシンプル、分離パターンを確立できる
3. **Step 5**: `ExpenditureRegistUseCase` はセッション操作のみで、IncomeRegistUseCaseの分離パターンを活用できる
4. **Step 6**: `IncomeAndExpenditureRegistConfirmUseCase` はDB更新処理（@Transactional）を含むため最も慎重に実施
5. **Step 7**: 残ったクラスを適切な名称にリネーム

---

## 6. リスク分析

### 6.1 主要リスクと対策

| リスク | 影響度 | 発生確率 | 対策 |
|--------|--------|---------|------|
| セッション管理の不整合 | 高 | 中 | 収入・支出セッションリスト操作のテストを徹底 |
| トランザクション境界の誤り | 高 | 低 | `@Transactional` 設定の確認とロールバックテスト |
| Controller修正漏れ | 中 | 中 | Grepで全参照箇所を検索し一覧化 |
| クラス間の予期しない依存 | 中 | 低 | 分離前に依存関係図を作成 |
| 1726行の大規模クラスによる分割の複雑化 | 高 | 中 | 小さなステップで進め、各Step完了時にテストGREEN確認 |

### 6.2 特に注意すべき点

1. **セッションの収入・支出リスト操作**:
   - `execIncomeAction` と `execExpenditureAction` がセッションを直接操作
   - 分離後もセッション操作が正しく動作することを確認

2. **トランザクション管理**:
   - `execRegistAction` のみが `@Transactional` で4テーブル更新
   - 分離後も同じトランザクション境界を維持

3. **固定費からの自動生成ロジック**:
   - `readInitInfo` メソッド内の複雑なロジック
   - 固定費取得条件（毎月、指定月、奇数月/偶数月、任意月）の正確性

---

## 7. 次のステップ: Step 2 の準備

### 7.1 Step 2 で作成する結合テストの範囲

**Phase5 Step 2** では、リファクタリング前の動作を保証するテストを作成します。

#### 作成する結合テストクラス (5つ + Controller層1つ = 合計6つ)

**アプリケーション層テスト (5つ)**:

1. **IncomeAndExpenditureInitIntegrationTest** (収支登録・更新画面初期表示) ✅ **実装完了（14テストケース）**
   - 新規登録時の画面表示（固定費自動生成、10月/11月パターン、セッション/画面表示分離）
   - 新規登録時の必須登録データチェック（未登録/全登録/一部登録パターン）
   - 更新時の画面表示（DB登録済み情報、セッション/画面表示分離、支払日NULL検証）
   - 更新時の異常系・境界系（収入テーブルなし→例外、支出テーブルなし→空リスト）
   - 収入・支出一覧の再表示
   - 入力確認時のエラー処理（収入未登録）

2. **IncomeRegistIntegrationTest** (収入登録操作) ✅ **実装完了（10テストケース）**
   - 収入の追加選択画面表示（フォーム初期値確認）
   - 収入の更新選択画面表示（正常系/異常系）
   - 収入のバインディングエラー処理（入力値保持確認）
   - 収入の追加・更新・削除（データタイプによる動作分岐、異常系含む）

3. **ExpenditureRegistIntegrationTest** (支出登録操作) ✅ **実装完了（11テストケース）**
   - 支出の更新選択画面表示（正常系/異常系）
   - 支出のバインディングエラー処理（入力値保持確認）
   - 新規支出項目表示（正常系/異常系：イベント必須未設定）
   - 支出の追加・更新・削除（データタイプによる動作分岐、異常系含む）

4. **ExpenditureItemSelectIntegrationTest** (支出項目選択画面)
   - 支出項目一覧の表示
   - 支出項目選択後の確認表示

5. **IncomeAndExpenditureRegistConfirmIntegrationTest** (登録確認・完了処理)
   - 登録内容確認画面の表示
   - 登録キャンセル処理
   - DB登録処理（4テーブル更新）

**Controller層テスト (1つ)**:

6. **IncomeAndExpenditureRegistControllerIntegrationTest** (Controller層結合テスト)
   - 分割後の5つのUseCaseが正しく結合されているかを検証

### 7.2 テスト実施基準

**Step 2 完了基準**:
- ✅ 6つの結合テストクラスが作成されている
  - アプリケーション層テスト: 5つ
  - Controller層テスト: 1つ
- ✅ すべてのテストがGREEN
- ✅ 主要なビジネスシナリオがカバーされている
- ✅ エッジケース（エラー処理、バリデーション）がテストされている
- ✅ DB更新処理が正しく検証されている
- ✅ トランザクションのロールバックが確認されている

**重要**: Step 2 完了前にリファクタリング（Step 3以降）に進まないこと

---

## 8. 成果物

### 8.1 本ドキュメントで確定した内容

1. ✅ 現状分析完了
   - クラス行数: 1726行
   - publicメソッド数: 17個
   - 依存コンポーネント/リポジトリ: 10個

2. ✅ メソッド分類完了
   - グループ1: 収支登録・更新画面 (4メソッド)
   - グループ2: 収入操作 (4メソッド)
   - グループ3: 支出操作 (4メソッド)
   - グループ4: 支出項目選択画面 (2メソッド)
   - グループ5: 収支登録内容確認画面 (1メソッド)
   - グループ6: トランザクション完了処理 (2メソッド)

3. ✅ 分割方針確定（5つのUseCaseに分割）
   - IncomeAndExpenditureInitUseCase (4メソッド、約300-400行)
   - IncomeRegistUseCase (4メソッド、約350-450行)
   - ExpenditureRegistUseCase (4メソッド、約400-500行)
   - ExpenditureItemSelectUseCase (2メソッド、約100-150行)
   - IncomeAndExpenditureRegistConfirmUseCase (3メソッド、約370-450行)

4. ✅ 実施順序確定
   - Step 2: 結合テスト作成（次のステップ）- 6つのテストクラス
   - Step 3: ExpenditureItemSelectUseCase 分離
   - Step 4: IncomeRegistUseCase 分離
   - Step 5: ExpenditureRegistUseCase 分離
   - Step 6: IncomeAndExpenditureRegistConfirmUseCase 分離
   - Step 7: IncomeAndExpenditureInitUseCase へのリネーム
   - Step 8: 共通処理のコンポーネント化（オプション）

---

## 9. 更新履歴

| 版数 | 更新日 | 更新内容 |
|------|--------|---------|
| 1.00.00 | 2026-01-10 | 初版作成（Phase5 Step1 完了） |

---

**ステータス**: ✅ Step 1 完了
**次のアクション**: Step 2（結合テスト作成）の開始
