# Phase 5 - Step 8: 詳細設計書（コードクオリティ向上）

## 1. 概要

### 1.1 目的

Step 3〜7のUseCase分離（1クラス→5クラス）完了後に全UseCaseを精読した結果、
以下の改善点が明らかになった。本設計書ではこれら5つのSub-Stepの実施方針を定める。

### 1.2 対象クラス（分離後の5UseCase）

| クラス | パッケージ |
|--------|-----------|
| `IncomeAndExpenditureInitUseCase` | `application.usecase.account.regist` |
| `IncomeRegistUseCase` | `application.usecase.account.regist` |
| `ExpenditureRegistUseCase` | `application.usecase.account.regist` |
| `ExpenditureItemSelectUseCase` | `application.usecase.account.regist` |
| `IncomeAndExpenditureRegistConfirmUseCase` | `application.usecase.account.regist` |

### 1.3 Sub-Step一覧

| Sub-Step | 内容 | 優先度 | 難易度 |
|----------|------|--------|--------|
| **8-1** | `setIncomeAndExpenditureInfoList` の共通コンポーネント化 | 🔴 高 | 低 |
| **8-2** | `SisyutuKubun.isWastedBOrC()` 追加（無駄遣いB/C判定のドメイン化） | 🔴 高 | 低 |
| **8-3** | `execRegistAction` の privateメソッド分割（358行メソッドの可読性向上） | 🟡 中 | 中 |
| **8-4** | 仮登録コード採番ロジックのドメインサービス化 | 🔵 低 | 中 |
| **8-5** | `execIncomeAction` / `execExpenditureAction` のリファクタリング | 🔵 低 | 中 |

### 1.4 合格基準

全Sub-Step完了後、`mvn clean test` で **678件以上のテストがGREEN** であること。

---

## 2. Sub-Step 8-1: setIncomeAndExpenditureInfoList の共通コンポーネント化

### 2.1 現状（問題点）

`setIncomeAndExpenditureInfoList` private メソッドが **4つのUseCaseに完全重複** して存在する。

| UseCase | 重複メソッド |
|---------|-------------|
| `IncomeAndExpenditureInitUseCase` | `setIncomeAndExpenditureInfoList` |
| `IncomeRegistUseCase` | `setIncomeAndExpenditureInfoList` |
| `ExpenditureRegistUseCase` | `setIncomeAndExpenditureInfoList` |
| `IncomeAndExpenditureRegistConfirmUseCase` | `setIncomeAndExpenditureInfoList` |

メソッドの内容：セッションの収入登録情報・支出登録情報のリストを、
画面表示用リスト（収入一覧・支出一覧）に変換して合計金額を計算し、レスポンスに設定する。

**問題**: 同一ロジックが4箇所に散在しており、変更時に4箇所を同時修正する必要がある。

### 2.2 改修方針

新規Springコンポーネント `IncomeAndExpenditureRegistListComponent` を作成し、
4つのUseCaseの重複メソッドをこのコンポーネントに集約する。

### 2.3 新規クラス設計

**クラス名**: `IncomeAndExpenditureRegistListComponent`

**配置パッケージ**: `com.yonetani.webapp.accountbook.common.component`

**ファイルパス**: `src/main/java/com/yonetani/webapp/accountbook/common/component/IncomeAndExpenditureRegistListComponent.java`

**依存フィールド**:
```java
private final CodeTableItemComponent codeTableItem;
private final SisyutuItemComponent sisyutuItemComponent;
```

**公開メソッド**:
```java
/**
 * セッションの収入登録情報・支出登録情報をもとに、
 * 画面表示する収入一覧情報・支出一覧情報を生成してレスポンスに設定する。
 */
public void setIncomeAndExpenditureInfoList(
    UserId userId,
    List<IncomeRegistItem> incomeRegistItemList,
    List<ExpenditureRegistItem> expenditureRegistItemList,
    AbstractIncomeAndExpenditureRegistResponse response)
```

**実装内容**: 4つのUseCaseに重複している `setIncomeAndExpenditureInfoList` の処理をそのまま移植する。

### 2.4 修正対象クラス

各UseCaseから以下を実施する：
1. `private void setIncomeAndExpenditureInfoList(...)` メソッドを削除
2. フィールドに `IncomeAndExpenditureRegistListComponent registListComponent;` を追加
3. 削除したメソッド呼び出し箇所を `registListComponent.setIncomeAndExpenditureInfoList(...)` に置き換え

**影響クラス（4つ）**:
- `IncomeAndExpenditureInitUseCase`
- `IncomeRegistUseCase`
- `ExpenditureRegistUseCase`
- `IncomeAndExpenditureRegistConfirmUseCase`

### 2.5 実施順序

1. `IncomeAndExpenditureRegistListComponent.java` 新規作成
2. `IncomeAndExpenditureInitUseCase` 修正
3. `IncomeRegistUseCase` 修正
4. `ExpenditureRegistUseCase` 修正
5. `IncomeAndExpenditureRegistConfirmUseCase` 修正
6. `mvn clean test` でGREEN確認

---

## 3. Sub-Step 8-2: SisyutuKubun.isWastedBOrC() 追加

### 3.1 現状（問題点）

「支出区分が無駄遣いBまたはC」という判定が、UseCase内で以下のように繰り返されている。

```java
// 4つのUseCaseで重複（setIncomeAndExpenditureInfoList内）
SisyutuKubun checkSisyutuKubun = SisyutuKubun.from(session.getExpenditureKubun());
if(SisyutuKubun.isWastedB(checkSisyutuKubun) || SisyutuKubun.isWastedC(checkSisyutuKubun)) {
```

「無駄遣いB または 無駄遣いC」という組み合わせ判定は **ドメイン知識** であり、
ドメイン型 `SisyutuKubun` 自身が知っているべき情報。

### 3.2 改修方針

`SisyutuKubun` にインスタンスメソッド `isWastedBOrC()` を追加する。

**注意**: Step 8-1 完了後に実施する（Step 8-1 でコンポーネント化した箇所も含めて修正するため）。

### 3.3 追加メソッド設計

**対象ファイル**: `src/main/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/SisyutuKubun.java`

```java
/**
 * 支出区分が「無駄遣い（軽度）」または「無駄遣い（重度）」かどうかを判定します。
 * @return 無駄遣いB または 無駄遣いCの場合true、それ以外はfalse
 */
public boolean isWastedBOrC() {
    return SisyutuKubun.isWastedB(this) || SisyutuKubun.isWastedC(this);
}
```

### 3.4 修正対象クラス

Step 8-1 で作成した `IncomeAndExpenditureRegistListComponent` の判定箇所を置き換える。

**変更前**:
```java
SisyutuKubun checkSisyutuKubun = SisyutuKubun.from(session.getExpenditureKubun());
if(SisyutuKubun.isWastedB(checkSisyutuKubun) || SisyutuKubun.isWastedC(checkSisyutuKubun)) {
```

**変更後**:
```java
SisyutuKubun checkSisyutuKubun = SisyutuKubun.from(session.getExpenditureKubun());
if(checkSisyutuKubun.isWastedBOrC()) {
```

---

## 4. Sub-Step 8-3: execRegistAction の privateメソッド分割

### 4.1 現状（問題点）

`IncomeAndExpenditureRegistConfirmUseCase.execRegistAction` が **358行** の巨大メソッドであり、
以下の5つの異なる処理責務が混在している。

| 処理 | 概算行数 | 内容 |
|------|---------|------|
| ① 初期登録フラグ判定 | 約10行 | DBを参照して新規/更新を判定 |
| ② 収入レコード処理 | 約100行 | 収入情報のINSERT/UPDATE/DELETE |
| ③ 支出レコード処理 | 約130行 | 支出情報のINSERT/UPDATE/DELETE |
| ④ 支出金額テーブル更新 | 約20行 | SisyutuKingakuTableの更新 |
| ⑤ 収支テーブル更新 | 約70行 | IncomeAndExpenditureTableの更新 |

### 4.2 改修方針

`execRegistAction` はトランザクション境界を保ちながら、処理①〜⑤を
**private メソッドへ委譲** する形にリファクタリングする。
外部インターフェース（メソッドシグネチャ・戻り値・`@Transactional`）は変更しない。

### 4.3 分割設計

```java
@Transactional
public IncomeAndExpenditureRegistCheckResponse execRegistAction(
        LoginUserInfo user, String targetYearMonthStr,
        List<IncomeRegistItem> incomeRegistItemList,
        List<ExpenditureRegistItem> expenditureRegistItemList) {

    // 前処理・入力検証
    ...（現状の入力検証ロジック）

    // ① 初期登録フラグ判定
    boolean initFlg = isInitialRegistration(userId, targetYearMonth);

    // ② 収入レコード処理
    processIncomeRegistration(userId, targetYearMonth, incomeRegistItemList, initFlg, ...);

    // ③ 支出レコード処理
    processExpenditureRegistration(userId, targetYearMonth, expenditureRegistItemList, initFlg, ...);

    // ④ 支出金額テーブル更新
    updateSisyutuKingakuTable(userId, targetYearMonth);

    // ⑤ 収支テーブル更新・レスポンス生成
    return updateIncomeAndExpenditureTable(userId, targetYearMonth, initFlg, ...);
}
```

**分割するprivateメソッド**:

| メソッド名 | 処理 |
|-----------|------|
| `isInitialRegistration(UserId, TargetYearMonth)` | ① 初期登録フラグ判定 |
| `processIncomeRegistration(...)` | ② 収入レコードのCRUD |
| `processExpenditureRegistration(...)` | ③ 支出レコードのCRUD |
| `updateSisyutuKingakuTable(...)` | ④ 支出金額テーブル更新 |
| `updateIncomeAndExpenditureTable(...)` | ⑤ 収支テーブル更新 |

**注意**: このリファクタリングは `execRegistAction` の内部構造のみを変更する。
トランザクション境界・外部から呼び出されるシグネチャは一切変更しない。

---

## 5. Sub-Step 8-4: 仮登録コード採番ロジックのドメインサービス化

### 5.1 現状（問題点）

仮登録コードの採番ロジックが3つのUseCaseに散在している。

| UseCase | メソッド | 用途 | パターン |
|---------|---------|------|---------|
| `IncomeAndExpenditureInitUseCase` | `readInitInfo` | 固定費→支出変換時の一括採番 | `yyyyMMddHHmmss` + 3桁連番 |
| `IncomeRegistUseCase` | `execIncomeAction` | 収入1件追加時の採番 | `yyyyMMddHHmmssSSS` |
| `ExpenditureRegistUseCase` | `execExpenditureAction` | 支出1件追加時の採番 | `yyyyMMddHHmmssSSS` |

採番ルール（タイムスタンプ形式、桁数）は **ドメイン知識** であり、UseCase層に実装するのは不適切。

### 5.2 改修方針

新規ドメインサービス `TemporaryCodeGenerator` を作成し、採番ロジックを一元管理する。

### 5.3 新規クラス設計

**クラス名**: `TemporaryCodeGenerator`

**配置パッケージ**: `com.yonetani.webapp.accountbook.domain.service.account.regist`

**ファイルパス**: `src/main/java/com/yonetani/webapp/accountbook/domain/service/account/regist/TemporaryCodeGenerator.java`

**メソッド設計**:

```java
/**
 * 仮登録用コード（単体採番）を生成します。
 * 形式: yyyyMMddHHmmssSSS（17桁）
 */
public static String generate() {
    return DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
}

/**
 * 仮登録用コード（連番採番）のベース部分（yyyyMMddHHmmss, 14桁）を生成します。
 * 呼び出し元が `String.format("%s%03d", base, count)` で完成させる。
 */
public static String generateBase() {
    return DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
}
```

**注意**: `@Service` アノテーションは不要。staticメソッドのユーティリティクラスとして実装する。

### 5.4 修正対象クラス

| UseCase | 変更前 | 変更後 |
|---------|--------|--------|
| `IncomeAndExpenditureInitUseCase` | `DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())` | `TemporaryCodeGenerator.generateBase()` |
| `IncomeRegistUseCase` | `DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now())` | `TemporaryCodeGenerator.generate()` |
| `ExpenditureRegistUseCase` | `DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now())` | `TemporaryCodeGenerator.generate()` |

---

## 6. Sub-Step 8-5: execIncomeAction / execExpenditureAction のリファクタリング

### 6.1 現状（問題点）

`IncomeRegistUseCase.execIncomeAction`（約142行）と
`ExpenditureRegistUseCase.execExpenditureAction`（約130行）が、
ADD/UPDATE/DELETE の3段階 if-else-if 分岐を UseCase 内で直接実装している。

各分岐が30〜50行程度のロジックを持ち、メソッド全体が見通しにくい。

### 6.2 改修方針

Strategyパターンは依存関係の複雑化・テストコード修正リスクが高いため採用しない。
**privateメソッドへの分割** により可読性を向上させる。

### 6.3 分割設計（IncomeRegistUseCase）

```java
public IncomeAndExpenditureRegistResponse execIncomeAction(...) {
    // アクション判定・委譲のみを担う（約20行）
    if (Objects.equals(inputForm.getAction(), ACTION_TYPE_ADD)) {
        return execIncomeAdd(user, targetYearMonth, incomeRegistItemList, inputForm);
    } else if (Objects.equals(inputForm.getAction(), ACTION_TYPE_UPDATE)) {
        return execIncomeUpdate(user, targetYearMonth, incomeRegistItemList, inputForm);
    } else if (Objects.equals(inputForm.getAction(), ACTION_TYPE_DELETE)) {
        return execIncomeDelete(user, targetYearMonth, incomeRegistItemList, inputForm);
    } else {
        throw ...;
    }
}

private IncomeAndExpenditureRegistResponse execIncomeAdd(...) { /* 追加処理 */ }
private IncomeAndExpenditureRegistResponse execIncomeUpdate(...) { /* 更新処理 */ }
private IncomeAndExpenditureRegistResponse execIncomeDelete(...) { /* 削除処理 */ }
```

### 6.4 分割設計（ExpenditureRegistUseCase）

同様に `execExpenditureAction` を以下に分割する：

```java
private IncomeAndExpenditureRegistResponse execExpenditureAdd(...)    { /* 追加処理 */ }
private IncomeAndExpenditureRegistResponse execExpenditureUpdate(...) { /* 更新処理 */ }
private IncomeAndExpenditureRegistResponse execExpenditureDelete(...) { /* 削除処理 */ }
```

**注意**: このリファクタリングは内部構造のみを変更する。
外部から呼び出されるメソッドシグネチャは変更しない。

---

## 7. 実施順序とコミット計画

```
Step 8-1完了 → コミット (setIncomeAndExpenditureInfoList の共通コンポーネント化)
Step 8-2完了 → コミット (SisyutuKubun.isWastedBOrC() 追加)
Step 8-3完了 → コミット (execRegistAction の privateメソッド分割)
Step 8-4完了 → コミット (仮登録コード採番のドメインサービス化)
Step 8-5完了 → コミット (execIncomeAction / execExpenditureAction リファクタリング)
```

各Step完了時に `mvn clean test` を実行し、678件以上のテストがGREENであることを確認する。

---

## 8. 更新履歴

| 版数 | 更新日 | 更新内容 |
|------|--------|---------|
| 1.00.00 | 2026-02-26 | 初版作成 |
