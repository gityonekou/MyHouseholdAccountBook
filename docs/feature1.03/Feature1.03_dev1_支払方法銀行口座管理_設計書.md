# Feature1.03-dev1: 支払方法・銀行口座管理 設計書

## 改訂履歴

| 版 | 内容 |
|----|------|
| 初版 | 支払方法マスタ・銀行口座マスタ・既存3画面拡張の設計 |
| 改訂1 | レビュー指摘①（銀行口座コード桁数 CHAR(3)→CHAR(2)）を反映 |
| 改訂2 | [電子マネー対応の設計インプット](Claude Codeへの作業インプット/Feature1.03_dev1_payment-method-emoney-design-input.md) を反映。支払方法種別に「電子マネー（前払い式）」を追加、`PaymentMethodKubun` をenum化して`requiresAccount()`/`requiresClosingDay()`述語に業務ルールを集約、銀行口座を種別により任意化（確認事項①が解消） |
| 改訂3 | [支払方法固定値の設計指針](Claude Codeへの作業インプット/Feature1_03_dev1_支払方法固定値_設計指針.md) を反映。支払方法コードにシステム予約帯（990〜999）を導入して`isSystemReserved()`に判定を集約、`findByUserId()`/`findSelectableByUserId()`の使い分け、ユーザー入力経路でのシステム予約値の拒否バリデーション、表示変換（予約値→「－」）の一元化、`ShoppingAggregateSpecification`（買い物集計8項目の判定）を新規追加。あわせて新たな確認事項⑤が発生（5.5節参照）。dev5向けの設計指針（`Feature1_03_dev5_集計8項目見直し_設計指針.md`）は本devのスコープ外のため本書には反映せず、要件定義書・メモリにのみ参照ポインタを残す |
| 改訂4 | `Feature1.03_dev1_支払方法銀行口座管理_設計書_レビュー指摘.md`（指摘①〜⑦・確認事項②③④⑤への回答）を反映。集計開始日を1〜28日に変更（指摘①）、`BankAccount.enableUpdateFlg`を削除（指摘②）、`BankAccountCode`/`PaymentMethodCode`を`Identifier`継承、`BankAccountSort`/`PaymentMethodSort`用に共通基底`SortOrder`を新設（指摘③）、`PaymentMethodInfoComponent`をリクエスト単位1回のバッチ解決方式に変更しN+1回のSQL発行を回避（指摘④）、支払方法マスタ管理画面のJSを外部ファイル化・実装テンプレート名を確定（指摘⑤）、`FixedCostBulkUpdateForm`及び固定費関連5画面への支払方法列追加を新規設計（指摘⑥⑦）。**確認事項②③④⑤すべてに回答を得て解消**、残る確認事項なし |
| 改訂5 | `Feature1_03_dev1_設計書_要件突合レビュー.md`の指摘A・Mを反映。**指摘A**：買い物集計8項目は`IncomeAndExpenditureRegistConfirmUseCase`による自動生成ではなく、**固定費として登録し「固定費→収支登録」の引き継ぎ機能でEXPENDITURE_TABLEへ伝播する運用**であることが確認により判明。固定費登録画面（5.4.1節）のみ支払方法プルダウンに「支払方法がない」を含める新規メソッド`findEnabledByUserId()`/`getFixedCostPaymentMethodOptions()`を追加し、同画面の予約値拒否バリデーションを撤回。買い物登録による金額調整（`ExpenditureItem.addSisyutuKingaku()`等）は他フィールドをコピーするだけなので追加設計不要（実装時の対応漏れ防止の注記のみ追加）。収支登録画面（5.5節）は変更なし（固定費由来の未保存新規行にもdisabled表示の対象を明示的に拡張）。**指摘M**：銀行口座マスタに選択肢取得用の`findSelectableByUserId()`と`PaymentMethodInfoComponent.getBankAccountOptions()`を追加し、支払方法マスタ管理画面の銀行口座プルダウンに配線 |
| 改訂6 | 突合レビュー指摘Nを反映。10章備考に**dev2への申し送り事項**を追加：要件4.3（口座別支払確認）は銀行名を軸とする集計表だが、支払方法種別「現金」「電子マネー（前払い式）」は銀行口座を持たない（`BANK_ACCOUNT_CODE=NULL`）ため集計軸に乗らない。銀行口座マスタへのダミーシステム行追加は採用しない方針を維持しつつ、これらの支出の扱い（独立行として表示 or 対象外）はdev2の設計時に決定することを明記。なお本devの設計・実装内容そのものへの変更は無し（指摘Nは「dev1のミスではない」との評価付き） |
| 改訂7 | 突合レビュー指摘Oを反映。**要件定義書側の記載間違いと確認**（ユーザー確認済み）：3.1節「無効フラグで一覧から非表示」は各登録画面の選択肢（プルダウン）を指す記述であり、マスタ管理画面自体の一覧から完全に消す仕様ではなかった。要件定義書の銀行口座マスタ・支払方法マスタ両方の該当箇所を「各登録画面の選択肢に表示しない。マスタ管理画面自体の一覧には無効状態がわかる形で表示を残す」に訂正。設計書側（5.1節・5.2節）にもこの解釈を明記し、5.2節に欠けていた「無効化した支払方法の一覧表示方針（無効バッジ付きで表示）」を追記 |
| 改訂8 | 突合レビュー指摘C・F・B・D・E・G・H〜Lを反映。**C**：店舗マスタのデフォルト支払方法にも予約値拒否バリデーションを追加。**F**：「不要なのに入力されている」値をUseCase側で`null`に正規化する方式を採用（バリデーション追加より堅牢と判断）。**B**：5.7節を`createResolver()`方式に修正（初版は行ごとの単発解決のままで指摘④の対応が骨抜きになっていた）。**D**：訂正フォームのdisabled表示条件を`isSystemReserved()`単独から「現在値が選択肢に含まれるか」に一般化し、無効化された支払方法・銀行口座を参照する既存行にも適用（5.2・5.4.1・5.5・5.6節）。**E**：`requiresAccount()`/`requiresClosingDay()`の判定結果をHTML`<option>`のdata属性経由でJSに渡す方式にし、JS側の種別コード直書きを排除。**G**：表示順シフト処理が予約帯（990〜999）に踏み込まないことを3.6節に明記。**H**：予約帯の開始番号が店舗マスタ（900〜）と異なる点をチェックリストに明記。**I**：システム行名称は要件3.1の文言「支払方法がない」のまま据え置き（一覧非表示化により推奨名称採用の必要性が下がったため）。**J**：`Feature1_03_dev1_支払方法固定値_設計指針.md`のdev5引き継ぎリストに`ENABLE_UPDATE_FLG`の要否判断を追加。**K**：3.9節の型名・定数名の実装時確認事項を8章に明記。**L**：dev1時点でCSV出力機能自体が存在しないことを確認し対応不要と結論 |
| 改訂9 | `Feature1_03_dev1_設計書_要件突合レビュー_2次確認.md`（1次レビュー対応の検証で新規判明した指摘T〜Z）を反映。**指摘Wの調査結果**：`FIXED_COST_TABLE`に支出区分の専用カラムは存在せず、既存メソッド`ExpenditureCategory.from(FixedCostName)`（固定費名の文字列マッチ）で導出している。**指摘T**：6.1節のデータ移行手順に、対象8項目に対応する`FIXED_COST_TABLE`行への`999`設定ステップを追加（既存の`EXPENDITURE_TABLE`行のみ変換すると、翌月の固定費引き継ぎで不整合が再発するため）。**指摘U**：11章チェックリスト2項目目を「固定費登録画面は意図的に選択肢へ含める」と明記する文言に修正。**指摘V**：固定費登録画面の予約値バリデーションを「全面撤回」から「対象8項目の固定費のときのみ`999`を許容する条件付きバリデーション」に変更（`ExpenditureCategory.from(FixedCostName)`＋`ShoppingAggregateSpecification.isSatisfiedBy()`で判定）。**指摘X**：`Feature1_03_dev1_支払方法固定値_設計指針.md`の2.3・2.4に固定費登録の例外注記を追加。**指摘Y**：3.9節「用途」に指摘T・Vの2用途を追加（計4用途）。**指摘Z**：1次レビューファイルの指摘A本文に対応状況を追記漏れしていたため追加。指摘J・Sは実際には対応済みだったことを確認。**本ラウンドはレビュー側の指摘ミスなし**（T〜Zすべて妥当な指摘） |
| 改訂10 | `Feature1_03_dev1_設計書_要件突合レビュー_3次確認.md`（2次レビュー対応の検証で新規判明した指摘AA〜AE）を反映。**指摘AA（重大）**：`@AssertTrue`メソッドが域内型の`from()`ファクトリを直接呼ぶと、不正値送信時に例外が検証エラーに変換されず500エラーになる問題を修正。ユーザー提案の代替案（例外を投げない`tryFrom(String): Optional<T>`入口を型に追加する方式）を採用し、レガシー型（`FixedCostName`・`ExpenditureItemCode`等）はAPIを変更せず共通ユーティリティ`domain/type/common/SafeDomainFactory.tryCreate(Supplier<T>)`で包み、新規型（`PaymentMethodCode`・`PaymentMethodKubun`）には`tryFrom(String)`を追加（3.2節・3.5節）。5.2・5.3・5.4.1・5.6節の計4箇所の`@AssertTrue`を`tryFrom`/`SafeDomainFactory`ベースに書き換え、あわせて5.4.1節のガード節に欠けていた`fixedCostName`のnullチェックを追加（通常操作での500エラー原因）。**指摘AB**：6.1節ステップ3の`FIXED_COST_TABLE`移行を、`FIXED_COST_NAME`文字列マッチをSQL側で再現する方式から、候補抽出（SELECT）→目視確認→`FIXED_COST_CODE`明示列挙（UPDATE）の方式に変更し、ロジック複製によるSQL改修漏れリスクを回避。**指摘AC**：`FIXED_COST_NAME`文字列マッチへの新規依存箇所（5.4.1節バリデーション・6.1節移行判定の2箇所）をdev5への申し送りとして10章備考・`Feature1_03_dev1_支払方法固定値_設計指針.md`4章に追加。**指摘AD**：3.9節に`ShoppingAggregateSpecification`がSpring管理外（`new`で都度生成）であることを明記。8章に`FixedCostInfoUpdateForm`の`sisyutuItemCode`/`fixedCostName`フィールド実在確認済みである旨を追記（設計変更なし）。**指摘AE**：指摘J・S・X時点の`Feature1_03_dev1_支払方法固定値_設計指針.md`・`ドメイン用語集.md`への反映が実ファイルに存在することを確認済み（レビューファイル側に注記） |
| 改訂11 | `Feature1_03_dev1_設計書_要件突合レビュー_4次確認.md`（3次レビュー対応の検証で新規判明した指摘AF〜AJ）のうち、実装着手前に片付けるべき指摘AF・AGを反映。**指摘AF**：`Feature1_03_dev1_支払方法固定値_設計指針.md`5章のチェックリストが指摘U・V・Hの内容と未同期だった問題を解消。設計指針5章は「dev1着手時点の方針の記録」として以後更新しない位置づけとし、11章冒頭に「dev1完了時の実際のチェックは本節を正とする」旨を明記（設計指針側にも対応する注記を追加）。**指摘AG**：3次確認レビューファイルの指摘AC対応状況欄に「固定費名変更リスクを設計指針側の追記に含めた」という誤った記載があったことが判明（実際には未反映）。5.4.1節のHTML補足文の箇所に、固定費名の「無駄遣いB」「無駄遣いC」の文言変更で集計区分が画面エラーなく変わりうる旨の注意書きを追加し、3次確認レビューファイル側の誤記載も訂正した。**指摘AH（`Optional.ofNullable()`化）・AI（設計指針の残る未同期4点）・AJ（用語集再検証）はユーザー確認により実装フェーズで対応することとし、本改訂では設計書・設計指針への追加修正を行わない**（4次確認レビューが最終ラウンドである旨の確認を得た） |

## 1. 概要

### 1.1 目的

現金・口座振替・クレジットカード・デビットカード・電子マネー（前払い式）などの支払方法をユーザーごとに登録できるようにし、固定費登録・収支登録・買い物登録の各画面で支払方法を選択できるようにする。あわせて支払方法が引き落とされる銀行口座をマスタ管理する。

本devの成果物は dev2（クレカポイント減算・口座別支払確認）・dev4（店舗・支払方法別買い物照会）・dev5（支出8項目の見直し）の前提データとなる。

### 1.2 対象ブランチ

`feature-1.03-dev1`

### 1.3 対象画面・クラス一覧

| 区分 | 画面/クラス | 種別 |
|------|------------|------|
| 新規 | 銀行口座マスタ管理画面 | 画面（Controller/UseCase/Form/Response/HTML） |
| 新規 | 支払方法マスタ管理画面 | 画面（Controller/UseCase/Form/Response/HTML） |
| 拡張 | 店舗マスタ管理画面（`ShopInfoManageUseCase`） | デフォルト支払方法項目追加 |
| 拡張 | 固定費登録画面（`FixedCostRegistConfirmUseCase`／`FixedCostInquiryUseCase`／`FixedCostMonthlyDetailUseCase`） | 支払方法選択追加、一括更新（`FixedCostBulkUpdateForm`）関連画面含む固定費関連5画面への支払方法列表示追加（5.4節） |
| 拡張 | 収支登録画面（支出側）（`ExpenditureRegistUseCase`, `IncomeAndExpenditureInitUseCase`, `IncomeAndExpenditureRegistConfirmUseCase`） | 支払方法選択追加、固定費からの引き継ぎ、システム予約値の訂正フォーム表示制御 |
| 拡張 | 買い物登録（簡易タイプ）画面（`SimpleShoppingRegistUseCase`） | 支払方法選択追加、店舗のデフォルト支払方法自動設定 |
| 拡張 | 月別収支照会画面 支出別タブ（`AccountMonthInquiryUseCase`） | 「支払方法」「引落先口座」列追加 |
| 拡張 | ユーザー初期化処理（`AdminMenuUserInfoUseCase`） | 「支払方法がない」システム行の登録 |

> **前提**：`ShoppingRegist.html`（フル版買い物登録）は現状スタブ実装（`ShoppingRegistUseCase.read()` が空レスポンスを返すのみ、POST未実装）のため、本devでの「買い物登録」対応スコープは実装済みの**簡易タイプ**（`SimpleShoppingRegist*`）のみを対象とする。フル版は将来実装時に本devと同じ方針で追随する。

---

## 2. データ設計

### 2.1 新規テーブル

#### 2.1.1 銀行口座マスタテーブル：`BANK_ACCOUNT_TABLE`

```sql
CREATE TABLE IF NOT EXISTS BANK_ACCOUNT_TABLE (
    -- ユーザID
    USER_ID              VARCHAR(50),
    -- 銀行口座コード
    BANK_ACCOUNT_CODE    CHAR(2),
    -- 銀行名
    BANK_NAME             VARCHAR(50) NOT NULL,
    -- 口座メモ(口座のエイリアス)
    BANK_ACCOUNT_MEMO     VARCHAR(100),
    -- 銀行口座表示順
    BANK_ACCOUNT_SORT     CHAR(2) NOT NULL,
    -- 有効/無効フラグ(trueの時は有効、falseの時は無効)
    ENABLE_FLG            BOOLEAN NOT NULL,

    -- 複合プライマリキー
    PRIMARY KEY(USER_ID, BANK_ACCOUNT_CODE)
);
```

> **レビュー反映**：1ユーザーが登録する銀行口座数は現実的に999件も想定されないため、`BANK_ACCOUNT_CODE`・`BANK_ACCOUNT_SORT`は当初案の`CHAR(3)`から`CHAR(2)`（1〜99、上限99件）に変更した。

- 表示順・コード発番は店舗マスタ（`SHOP_TABLE`/`ShopInfoManageUseCase`）と全く同じ運用（3.1参照）とするため、`ENABLE_UPDATE_FLG` のようなシステム行保護は不要（システム固定行を持たないため）。
- 削除は実装しない（要件どおり無効フラグ方式）。
- 2.1.2 で後述のとおり、支払方法種別「現金」「電子マネー（前払い式）」は銀行口座の紐づけが不要になったため、銀行口座マスタに「口座なし」のようなシステム予約行は不要となった（本節は初版からの変更点。旧版で検討していた確認事項①は本改訂で解消済み）。

#### 2.1.2 支払方法マスタテーブル：`PAYMENT_METHOD_TABLE`

```sql
CREATE TABLE IF NOT EXISTS PAYMENT_METHOD_TABLE (
    -- ユーザID
    USER_ID                  VARCHAR(50),
    -- 支払方法コード
    PAYMENT_METHOD_CODE      CHAR(3),
    -- 支払方法名
    PAYMENT_METHOD_NAME      VARCHAR(50) NOT NULL,
    -- 支払方法種別(1:現金 2:口座振替 3:クレジットカード 4:デビットカード 5:電子マネー(前払い式))
    PAYMENT_METHOD_KUBUN     CHAR(1) NOT NULL,
    -- 銀行口座コード(種別により要否が異なる。口座振替/クレジットカード/デビットカードは必須、現金/電子マネー(前払い式)はNULL可)
    BANK_ACCOUNT_CODE        CHAR(2),
    -- 集計開始日(クレジットカードのみ。1～28)
    CLOSING_DAY              CHAR(2),
    -- 支払方法表示順
    PAYMENT_METHOD_SORT      CHAR(3) NOT NULL,
    -- 有効/無効フラグ(trueの時は有効、falseの時は無効)
    ENABLE_FLG               BOOLEAN NOT NULL,
    -- 更新可否フラグ(trueの時は更新可能、falseの時は更新不可。「支払方法がない」システム行はfalse)
    ENABLE_UPDATE_FLG        BOOLEAN NOT NULL,

    -- 複合プライマリキー
    PRIMARY KEY(USER_ID, PAYMENT_METHOD_CODE),

    -- 外部キー:銀行口座コード(NULL可のFK。値がある場合のみ整合性を検証)
    INDEX PAYMENT_METHOD_BANK_ACCOUNT_CODE_INDEX(BANK_ACCOUNT_CODE),
    CONSTRAINT FK_PAYMENT_METHOD_BANK_ACCOUNT_CODE FOREIGN KEY(USER_ID, BANK_ACCOUNT_CODE) REFERENCES BANK_ACCOUNT_TABLE(USER_ID, BANK_ACCOUNT_CODE)
);
```

> **改訂**：[電子マネー（前払い式）対応の設計インプット](Claude Codeへの作業インプット/Feature1.03_dev1_payment-method-emoney-design-input.md) の決定事項を反映し、`BANK_ACCOUNT_CODE` を `NOT NULL` から `NULL可` に変更した。種別ごとの要否は「現金・電子マネー（前払い式）＝不要、口座振替・クレジットカード・デビットカード＝必須」であり、この判定はコード中に種別値の分岐を書き散らさず `PaymentMethodKubun`（3.5節）の `requiresAccount()` に集約する。

- `ENABLE_UPDATE_FLG` は `SISYUTU_ITEM_TABLE` と同じ設計思想（明示的フラグでシステム行を保護）を採用する。要件は「店舗マスタの変更不可システム行と同様」と表現しているが、店舗マスタの実装は「コード/表示順が900番台なら一覧上不可視にする」という UI 上のソフトな慣習であり、サーバー側の更新拒否は行っていない（`ShopInfoManageUseCase` 参照）。支払方法マスタは固定費・収支・買い物の必須項目として毎回参照される基準データであるため、より堅牢な `ENABLE_UPDATE_FLG` 方式を採用する（4.2.2 参照）。
- 「支払方法がない」システム行：`PAYMENT_METHOD_CODE = "999"` 固定値、`PAYMENT_METHOD_KUBUN = "1"（現金）` を仮設定、`BANK_ACCOUNT_CODE = NULL`（現金種別は銀行口座が不要になったため）、`ENABLE_UPDATE_FLG=false`。

> **設計上の確認事項①（解消）**：初版では「支払方法がない」行が参照するダミーの銀行口座（`BANK_ACCOUNT_CODE="999"`＝「口座なし」システム行）をどう保護するかを論点としていたが、電子マネー対応で `現金` 種別の銀行口座が不要（NULL可）になったことで、`BANK_ACCOUNT_CODE = NULL` を設定すればよいだけになり、この論点自体が解消した。銀行口座マスタにシステム行を追加する必要はない（2.1.1参照）。

#### システム予約コード帯（990〜999）

[支払方法固定値の設計指針](Claude Codeへの作業インプット/Feature1_03_dev1_支払方法固定値_設計指針.md) に基づき、`PAYMENT_METHOD_CODE` に**予約帯 `990`〜`999`** を設ける（店舗マスタの「コード帯を予約してシステム行を表現する」考え方と同一パターン。店舗マスタは`900`〜だが、支払方法マスタは登録数が少ないため`990`〜の10件を予約する）。

- 「支払方法がない」＝`999`（予約帯の中の1件）
- 通常のユーザー登録は`1`〜`989`の範囲で発番する（`PaymentMethodInfoForm`・採番ロジックの上限は`989`とする。5.2参照）
- `990`という値そのものをコード中に直書きしない。`PaymentMethodCode`（3.5節）に判定述語 `isSystemReserved()` を持たせ、呼び出し側は述語のみを参照する

```java
// PaymentMethodCode は Identifier を継承する（3.2節・3.5節参照。ここでは追加述語のみ抜粋）
@EqualsAndHashCode(callSuper = true)
public class PaymentMethodCode extends Identifier {
    private static final String RESERVED_FROM = "990";
    private static final String NOT_APPLICABLE = "999"; // 「支払方法がない」

    private PaymentMethodCode(String value) { super(value); }
    public static PaymentMethodCode from(String value) { /* Identifier.validate + 3桁数字チェック(BankAccountCodeと同型) */ }

    /** このコードがシステム予約帯（990～999）に該当するか */
    public boolean isSystemReserved() {
        return getValue().compareTo(RESERVED_FROM) >= 0;
    }
    /** このコードが「支払方法がない」を表す固定値か */
    public boolean isNotApplicable() {
        return NOT_APPLICABLE.equals(getValue());
    }
}
```

**理由**：`999`という値をコード中の各所に直書きすると、dev5でこのシステム予約値を削除する際に参照箇所を洗い出すのが困難になる。判定を1箇所（値オブジェクトの述語メソッド）に集約しておけば、dev5での影響範囲調査が `isSystemReserved()`／`isNotApplicable()` の参照箇所を辿るだけで済む。

### 2.2 既存テーブル拡張

| テーブル | 追加カラム | NULL可否 | 用途 |
|---------|-----------|---------|------|
| `SHOP_TABLE` | `DEFAULT_PAYMENT_METHOD_CODE CHAR(3)` | NULL可 | 店舗のデフォルト支払方法（任意） |
| `FIXED_COST_TABLE` | `PAYMENT_METHOD_CODE CHAR(3) NOT NULL` | NOT NULL | 固定費の支払方法 |
| `EXPENDITURE_TABLE` | `PAYMENT_METHOD_CODE CHAR(3) NOT NULL` | NOT NULL | 支出の支払方法（8項目は「支払方法がない」固定値） |
| `SHOPPING_REGIST_TABLE` | `PAYMENT_METHOD_CODE CHAR(3) NOT NULL` | NOT NULL | 買い物登録の支払方法 |

- `INCOME_TABLE` への追加は行わない（2.3参照）。
- 各カラムに `PAYMENT_METHOD_TABLE(USER_ID, PAYMENT_METHOD_CODE)` への外部キー制約を追加する（`SHOP_TABLE.DEFAULT_PAYMENT_METHOD_CODE` はNULL許容FK）。

```sql
ALTER TABLE SHOP_TABLE ADD DEFAULT_PAYMENT_METHOD_CODE CHAR(3);
ALTER TABLE SHOP_TABLE ADD CONSTRAINT FK_SHOP_DEFAULT_PAYMENT_METHOD_CODE
    FOREIGN KEY(USER_ID, DEFAULT_PAYMENT_METHOD_CODE) REFERENCES PAYMENT_METHOD_TABLE(USER_ID, PAYMENT_METHOD_CODE);

ALTER TABLE FIXED_COST_TABLE ADD PAYMENT_METHOD_CODE CHAR(3) NOT NULL AFTER SISYUTU_ITEM_CODE;
ALTER TABLE FIXED_COST_TABLE ADD CONSTRAINT FK_FIXED_COST_PAYMENT_METHOD_CODE
    FOREIGN KEY(USER_ID, PAYMENT_METHOD_CODE) REFERENCES PAYMENT_METHOD_TABLE(USER_ID, PAYMENT_METHOD_CODE);

ALTER TABLE EXPENDITURE_TABLE ADD PAYMENT_METHOD_CODE CHAR(3) NOT NULL AFTER SISYUTU_ITEM_CODE;
ALTER TABLE EXPENDITURE_TABLE ADD CONSTRAINT FK_EXPENDITURE_PAYMENT_METHOD_CODE
    FOREIGN KEY(USER_ID, PAYMENT_METHOD_CODE) REFERENCES PAYMENT_METHOD_TABLE(USER_ID, PAYMENT_METHOD_CODE);

ALTER TABLE SHOPPING_REGIST_TABLE ADD PAYMENT_METHOD_CODE CHAR(3) NOT NULL AFTER SHOP_CODE;
ALTER TABLE SHOPPING_REGIST_TABLE ADD CONSTRAINT FK_SHOPPING_REGIST_PAYMENT_METHOD_CODE
    FOREIGN KEY(USER_ID, PAYMENT_METHOD_CODE) REFERENCES PAYMENT_METHOD_TABLE(USER_ID, PAYMENT_METHOD_CODE);
```

> 既存テーブルへの `NOT NULL` カラム追加は既存データ件数分の移行が必須となる（2.4節・7節）。ALTER実行順序は「①カラムをNULL可で追加 → ②移行スクリプトでデータ設定 → ③NOT NULL制約を付与」の3段階とする。

### 2.3 設計上の確認事項②：収支登録（収入側）に支払方法は不要と解釈

要件定義書 3.2 の表は画面名を「収支登録」としているが、実際に支払方法が必要になるのは支出側のみと判断した。根拠：

- 3.3「支払方法に紐づかない支出データの扱い」・4.3「口座別支払確認」など、本機能全体が一貫して**支出（お金が出ていく側）**の口座紐づけを扱っている。収入（入金）を対象とする記述はない。
- 「固定費から収支を生成するタイミングでは支払方法を引き継ぐ」の対象は `IncomeAndExpenditureInitUseCase.readInitInfo()` が `FixedCostList` から生成する **支出登録情報（`ExpenditureRegistItem`）のみ**であり、収入側（`IncomeRegistItem`）は固定費から生成されない。
- `IncomeItemForm`／`IncomeRegistItem` に支払方法を追加すると、「積立金取崩」などの収入区分でも支払方法選択が必須になり、UI・要件との整合が取りづらい。

このため本設計書では **`INCOME_TABLE` 拡張なし、`IncomeItemForm`/`IncomeRegistUseCase` 拡張なし** として進める。

> **設計上の確認事項②（解消・回答あり）**：上記の解釈で正しいとの回答を得た。支払方法の追加対象は `src/main/resources/templates/account/regist/IncomeAndExpenditureRegist.html` の**支出登録エリア（104〜183行目）のみ**。なお紙芝居 `\AccountBook\MonthEndClosingProcess_1.html` 等にはdev3（積立金残高管理）の内容が反映されているが、**dev1のスコープは「収支登録」のみ**であり、dev3相当の紙芝居の内容は対象外。

### 2.4 コードテーブル追加

`MyHouseholdAccountBookContent` の `CODE_DEFINES_*` は次の空き番号 `"008"` を使用する。

```java
/** コード定義区分(支払方法種別:008) */
public static final String CODE_DEFINES_PAYMENT_METHOD_KUBUN = "008";
/** コード定義:支払方法種別で現金(1)を選択時 */
public static final String PAYMENT_METHOD_KUBUN_CASH_SELECTED_VALUE = "1";
/** コード定義:支払方法種別で口座振替(2)を選択時 */
public static final String PAYMENT_METHOD_KUBUN_TRANSFER_SELECTED_VALUE = "2";
/** コード定義:支払方法種別でクレジットカード(3)を選択時 */
public static final String PAYMENT_METHOD_KUBUN_CREDIT_SELECTED_VALUE = "3";
/** コード定義:支払方法種別でデビットカード(4)を選択時 */
public static final String PAYMENT_METHOD_KUBUN_DEBIT_SELECTED_VALUE = "4";
/** コード定義:支払方法種別で電子マネー(前払い式)(5)を選択時 */
public static final String PAYMENT_METHOD_KUBUN_PREPAID_EMONEY_SELECTED_VALUE = "5";
/** 支払方法コード:支払方法がない(999) */
public static final String PAYMENT_METHOD_CODE_NONE_VALUE = "999";
```

> **改訂**：電子マネー対応で種別値`5`を追加した。`BANK_ACCOUNT_CODE_NONE_VALUE`（銀行口座コード:口座なし）は、`BANK_ACCOUNT_CODE`がNULL可になったことで不要になったため削除した。

`codetable.csv`（Git管理外、各環境の `accountbook.property.codetable-file-path` が指す実ファイル）に以下を追記する必要がある。**この作業はコード変更だけでは完結しないため、local/test/prod 各環境のファイル更新を別途実施すること。**

```
#支払方法種別(008)
008,1,現金
008,2,口座振替
008,3,クレジットカード
008,4,デビットカード
008,5,電子マネー（前払い式）
```

### 2.5 ユーザー初期化（システム行の投入）

`AdminMenuUserInfoUseCase.execAction()` の新規ユーザー登録分岐（既存の `SISYUTU_ITEM_TABLE`・`SHOP_TABLE` シード処理と同じ `@Transactional` ブロック内）に、以下のシード処理を追加する。BASE テーブル経由ではなく、固定・単一のシステム行のみのためハードコードで直接 `add()` する（`SISYUTU_ITEM_BASE_TABLE`/`SHOP_BASE_TABLE` のような可変マスタ一括アップロードの仕組みは不要）。

> **改訂**：`BANK_ACCOUNT_CODE` がNULL可になったため、銀行口座マスタへのシステム行投入は不要になった（初版では「口座なし」行を追加していたが削除）。支払方法マスタのシステム行のみを投入する。
>
> **突合レビュー指摘I（マスタ名称）への回答**：[支払方法固定値の設計指針](Claude Codeへの作業インプット/Feature1_03_dev1_支払方法固定値_設計指針.md) 2.5は、DB直接参照時の可読性のため`"(システム予約) 支払方法なし"`のような文言を推奨しているが、本devでは要件3.1の文言に合わせて**`"支払方法がない"`のまま**とする（不採用）。理由：確認事項③の回答により本システム行は支払方法マスタ管理画面の一覧にも表示されなくなった（5.2節）ため、画面上の視認性向上という推奨の主目的は該当しなくなった。DB直接参照時の可読性は多少下がるが、要件定義書との文言の一致を優先する。

```java
// 支払方法マスタ:システム行「支払方法がない」を追加
PaymentMethod nonePaymentMethod = PaymentMethod.from(
        accountBookUser.getUserId().getValue(),
        MyHouseholdAccountBookContent.PAYMENT_METHOD_CODE_NONE_VALUE, // "999"
        "支払方法がない",
        MyHouseholdAccountBookContent.PAYMENT_METHOD_KUBUN_CASH_SELECTED_VALUE, // "1"仮設定(現金=銀行口座不要のため下記bankAccountCodeはnullでよい)
        null, // BANK_ACCOUNT_CODE
        null, // CLOSING_DAY
        MyHouseholdAccountBookContent.PAYMENT_METHOD_CODE_NONE_VALUE, // sort="999"
        true,  // ENABLE_FLG
        false); // ENABLE_UPDATE_FLG
int paymentAddCount = paymentMethodTableRepository.add(nonePaymentMethod);
if(paymentAddCount != 1) { throw new MyHouseholdAccountBookRuntimeException(...); }
```

---

## 3. ドメイン層設計

パッケージ構成は既存の `account/shop` パターンを踏襲する。

### 3.1 新規：`domain/model/account/bankaccount/BankAccount.java`

`Shop.java` と同型（`@RequiredArgsConstructor(PRIVATE) @Getter @ToString @EqualsAndHashCode`）。

| フィールド | 型 |
|-----------|-----|
| `userId` | `UserId` |
| `bankAccountCode` | `BankAccountCode` |
| `bankName` | `BankName` |
| `bankAccountMemo` | `BankAccountMemo`（null許容） |
| `bankAccountSort` | `BankAccountSort` |
| `enableFlg` | `EnableFlg`（共通ドメインタイプとして新規作成） |

ファクトリ：`BankAccount.from(userId, bankAccountCode, bankName, bankAccountMemo, bankAccountSort, enableFlg)`（全て `String`/`boolean` 引数）

> **レビュー指摘②反映**：`BankAccount` に `enableUpdateFlg` を持たせていたが、2.1.1のとおり銀行口座マスタはシステム行を持たない設計のため `ENABLE_UPDATE_FLG`（更新可否フラグ）自体が不要と判断し削除した。`BANK_ACCOUNT_TABLE` のDDL（2.1.1）にも元々このカラムは含めていない（DDLとドメインモデルが一致していなかった初版の不整合を解消）。

`domain/model/account/bankaccount/BankAccountInquiryList.java`：`ShopInquiryList` と同型（`List<BankAccount>` ラッパー）。

### 3.2 新規：`domain/type/account/bankaccount/`

> **レビュー指摘③反映**：初版は `ShopCode`／`ShopSort` を桁数違いでコピーする設計だったが、`ShopCode`／`ShopSort` はDDD未対応（リファクタリング未着手）の旧コードであるため、これらを新規クラスのテンプレートにしない。DDD対応済みの `FixedCostCode`（`domain/type/account/fixedcost/`）と、新規に切り出す表示順の共通基底クラスをテンプレートとする。

| クラス | 制約 |
|--------|------|
| `BankAccountCode` | `domain/type/common/Identifier`（`FixedCostCode` と同じ抽象基底）を継承する2桁数字。`from(int count)`／`getNewCode(int)` も同様に用意 |
| `BankAccountSort` | 新規共通基底 `domain/type/common/SortOrder`（下記）を継承する2桁数字。`from(int sort)` あり |
| `BankName` | 非空・50文字以内 |
| `BankAccountMemo` | null許容・100文字以内（`FixedCostDetailContext` 型の null 許容パターンを踏襲） |

`domain/type/common/EnableFlg.java`（新規・共通）：`EnableUpdateFlg` と同型の `boolean` ラッパー。「有効/無効フラグ」の意味を持つ値として `ENABLE_UPDATE_FLG`（更新可否）とは別概念で管理する。

#### `BankAccountCode`（`Identifier` 継承）

`FixedCostCode.java`（`domain/type/account/fixedcost/`）と全く同じ構造で実装する。

```java
@EqualsAndHashCode(callSuper = true)
public class BankAccountCode extends Identifier {
    private BankAccountCode(String value) {
        super(value);
    }
    public static BankAccountCode from(String bankAccountCode) {
        Identifier.validate(bankAccountCode, "銀行口座コード");
        if (bankAccountCode.length() != 2) {
            throw new MyHouseholdAccountBookRuntimeException("銀行口座コードは2桁で指定してください。管理者に問い合わせてください。code=" + bankAccountCode);
        }
        try {
            Integer.parseInt(bankAccountCode);
        } catch (NumberFormatException e) {
            throw new MyHouseholdAccountBookRuntimeException("銀行口座コードは数値で指定してください。管理者に問い合わせてください。code=" + bankAccountCode);
        }
        return new BankAccountCode(bankAccountCode);
    }
    public static BankAccountCode from(int count) {
        return new BankAccountCode(String.format("%02d", count));
    }
    public static String getNewCode(int count) {
        return BankAccountCode.from(count).getValue();
    }
}
```

（`getValue()`／`toString()`／`equals()`／`hashCode()` は `Identifier` から継承。`@Getter` は `Identifier` 側にあるため `BankAccountCode` 自体には付与不要）

`PaymentMethodCode`（3.5節）も同じ理由で `Identifier` を継承する形に合わせる（3桁版。既存の `isSystemReserved()`／`isNotApplicable()` はサブクラス独自メソッドとして追加する）。

#### `domain/type/common/SortOrder.java`（新規共通基底クラス）

現状 `domain/type/common/` には「表示順」を表す共通基底クラスが存在せず、`ShopSort`（DDD未対応）と `ExpenditureItemSortOrder`（`from()`にバリデーションが一切ない）がそれぞれ独立かつ非一貫な実装になっている。`Identifier` と同じ設計思想で新設する。

```java
/**
 * 表示順を表す値オブジェクトの共通基底クラスです。
 * 表示順系のドメインタイプはこのクラスを継承します。
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public abstract class SortOrder {
    private final String value;

    /** null・空文字チェックのみ行う共通ガード。桁数・数値チェックは各サブクラスの from() で行う */
    protected static void validate(String value, String typeName) {
        if (value == null) {
            throw new MyHouseholdAccountBookRuntimeException(typeName + "の値がnullです。管理者に問い合わせてください。");
        }
        if (!StringUtils.hasLength(value)) {
            throw new MyHouseholdAccountBookRuntimeException(typeName + "の値が未設定です。管理者に問い合わせてください。");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
```

`BankAccountSort`（2桁）・`PaymentMethodSort`（3桁）は、`FixedCostCode`が`Identifier`を継承するのと同じパターンで `SortOrder` を継承する。**`SortOrder` 自体の単体テストも新規作成すること**（レビュー指摘③）。

```java
@EqualsAndHashCode(callSuper = true)
public class BankAccountSort extends SortOrder {
    private BankAccountSort(String value) { super(value); }
    public static BankAccountSort from(String sort) {
        SortOrder.validate(sort, "銀行口座表示順");
        if (sort.length() != 2) { throw new MyHouseholdAccountBookRuntimeException(...); }
        try { Integer.parseInt(sort); } catch (NumberFormatException e) { throw new MyHouseholdAccountBookRuntimeException(...); }
        return new BankAccountSort(sort);
    }
    public static BankAccountSort from(int sort) {
        return new BankAccountSort(String.format("%02d", sort));
    }
}
```

既存の `ShopSort`／`ExpenditureItemSortOrder` を `SortOrder` 継承に揃えるリファクタリングは、既存クラスへの影響範囲調査が必要になるため**本devのスコープ外**とする（新規に作る2クラスのみ `SortOrder` を継承する）。

#### `domain/type/common/SafeDomainFactory.java`（新規共通ユーティリティ。突合レビュー指摘AA対応）

**背景**：5.2・5.3・5.4.1・5.6節で追加する`@AssertTrue`は、業務ルールの重複を避けるため（指摘E参照）ドメインタイプの`from()`を呼んで判定する設計にしている。しかし各ドメインタイプの`from()`は「不正な形式の値が渡されたら`MyHouseholdAccountBookRuntimeException`を投げる」という**不変条件違反＝例外**の意味論を持つ（`Identifier.validate()`・`PaymentMethodKubun.from()`など）。Bean Validationは`@AssertTrue`メソッド内で発生した例外をバリデーションエラーに変換しないため、そのまま伝播すると**バリデーションエラー画面ではなく500エラー**になってしまう。

**方針**：`from()`の意味論（内部的な再構築等、値が正しいことが前提の呼び出し元では例外を投げてよい）は変更せず、**プレゼンテーション層（Formの`@AssertTrue`）など「値が不正かもしれない」ことを前提に呼び出す箇所専用の安全な入口**を用意する。既存ドメイン型（`FixedCostName`・`ExpenditureItemCode`等、Feature1.03以前から存在するもの）のAPIは変更せず、呼び出し側で本ユーティリティを介して包む。今回新規作成する型（`PaymentMethodCode`・`PaymentMethodKubun`）は、呼び出し側の記述を簡潔にするため、内部で本ユーティリティを使う`tryFrom(String)`を自身に追加する。

```java
package com.yonetani.webapp.accountbook.domain.type.common;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * ドメインタイプのfrom()系ファクトリメソッド呼び出しを、例外ではなくOptionalで安全に扱うためのユーティリティです。
 * 値が不正な形式かもしれない外部入力（Formのバリデーション等）を検証する場合に使用します。
 * from()自体の「不変条件違反=例外」という意味論は変更しません。
 */
public final class SafeDomainFactory {
    private SafeDomainFactory() {}

    /**
     * 指定したファクトリ処理を実行し、成功した場合はOptionalで結果を返します。
     * MyHouseholdAccountBookRuntimeExceptionが発生した場合はOptional.empty()を返します。
     */
    public static <T> Optional<T> tryCreate(Supplier<T> factory) {
        try {
            return Optional.of(factory.get());
        } catch (MyHouseholdAccountBookRuntimeException e) {
            return Optional.empty();
        }
    }
}
```

**新規型への`tryFrom()`追加例**（`PaymentMethodCode`。`PaymentMethodKubun`も同様のパターン）：

```java
public static Optional<PaymentMethodCode> tryFrom(String value) {
    return SafeDomainFactory.tryCreate(() -> PaymentMethodCode.from(value));
}
```

**既存型（`FixedCostName`・`ExpenditureItemCode`）の呼び出し側での使用例**（5.4.1節）：

```java
Optional<FixedCostName> name = SafeDomainFactory.tryCreate(() -> FixedCostName.from(fixedCostName));
```

既存の`@AssertTrue`（`FixedCostInfoUpdateForm.isNeedCheckShiharaiTukiOptionalContext()`等）はドメインタイプを一切呼ばず`Objects.equals()`等の生値比較のみで構成されており、本ユーティリティは必要としていなかった（突合レビュー指摘AAの調査で確認）。本ユーティリティは、業務ルールの集約のためにあえてドメインタイプを呼ぶ**新しいパターン**として導入するものであり、既存の書き方を置き換えるものではない。

### 3.3 新規：`domain/repository/account/bankaccount/BankAccountTableRepository.java`

`ShopTableRepository` と同じ構成だが、銀行口座マスタはシステム行（900番台の予約帯）を持たないため `countByIdAndLessThanNineHundred` 相当のメソッド名は使わず、単純な `countById`（1〜99件の上限チェック用）とする。

> **突合レビュー指摘M反映**：初版は「支払方法マスタの銀行口座選択でも参照されるため、よく使う口座を上位に表示できるようにする」（要件3.1）という要求に対応する**選択肢取得メソッドが欠落**していた。`findById(SearchQueryUserId)` は全件取得（無効行も含む）のみで、支払方法マスタ管理画面（5.2節）の銀行口座プルダウンが「有効な口座のみ・表示順ソート」（5.1節が既に約束している挙動）を実現する手段がなかった。支払方法マスタの `findSelectableByUserId()`（3.6節）と対になる `findSelectableByUserId()` を追加する。

```java
int add(BankAccount data);
int update(BankAccount data);
int updateBankAccountSort(BankAccount data);
BankAccountInquiryList findById(SearchQueryUserId userId);
BankAccountInquiryList findById(SearchQueryUserIdAndBankAccountSort search);
BankAccountInquiryList findById(SearchQueryUserIdAndBankAccountSortBetweenAB search);
BankAccount findById(SearchQueryUserIdAndBankAccountCode search);
int countById(SearchQueryUserId userId); // 上限99件チェック用
// プルダウン用(ENABLE_FLG=trueのみ、表示順ソート)。支払方法マスタ管理画面の銀行口座選択で使用(3.8節参照)
BankAccountInquiryList findSelectableByUserId(SearchQueryUserId userId);
```

新規 `SearchQuery*` クラス4つを `domain/model/searchquery/` に追加（`SearchQueryUserIdAndShopSort` 等と同型）。

### 3.4 新規：`domain/model/account/paymentmethod/PaymentMethod.java`

| フィールド | 型 |
|-----------|-----|
| `userId` | `UserId` |
| `paymentMethodCode` | `PaymentMethodCode` |
| `paymentMethodName` | `PaymentMethodName` |
| `paymentMethodKubun` | `PaymentMethodKubun`（新規ドメインタイプ、3.5節参照） |
| `bankAccountCode` | `BankAccountCode`（3.2で定義した型を再利用、**null許容**） |
| `closingDay` | `ClosingDay`（null許容の新規ドメインタイプ） |
| `paymentMethodSort` | `PaymentMethodSort` |
| `enableFlg` | `EnableFlg` |
| `enableUpdateFlg` | `EnableUpdateFlg` |

ファクトリ：`PaymentMethod.from(...)`（全 `String`/`boolean` 引数、`bankAccountCode`・`closingDay` はnull許容）。

`PaymentMethodKubun` の値により `bankAccountCode`／`closingDay` の要否が変わるバリデーションは **Form側の `@AssertTrue`**（3.5節の `requiresAccount()`／`requiresClosingDay()` 述語を参照して判定）で行い、ドメイン層は単体の制約（`ClosingDay` は 01～28 の2桁数字 or null。レビュー指摘①：29〜31日を起算日とする実在のクレジットカードはないため対象外）のみを持つ（`FixedCostInfoUpdateForm` の相互バリデーションパターンを踏襲）。

`domain/model/account/paymentmethod/PaymentMethodInquiryList.java`：同型ラッパー。

### 3.5 新規：`domain/type/account/paymentmethod/`

| クラス | 制約 |
|--------|------|
| `PaymentMethodCode` | `domain/type/common/Identifier` を継承する3桁数字（`BankAccountCode`と同じ`FixedCostCode`パターン。3.2節参照）。システム予約帯（990〜999）判定述語 `isSystemReserved()`／`isNotApplicable()` をサブクラス独自メソッドとして追加で持つ（詳細は2.1.2節）。**`tryFrom(String)`（`Optional<PaymentMethodCode>`を返す安全な入口。3.2節`SafeDomainFactory`参照。突合レビュー指摘AA対応）も持つ** |
| `PaymentMethodName` | 非空・50文字以内 |
| `PaymentMethodKubun` | 下記参照（**enum**） |
| `ClosingDay` | null許容。値ありの場合は "01"～"28" の2桁数字（レビュー指摘①） |
| `PaymentMethodSort` | `domain/type/common/SortOrder` を継承する3桁数字（`BankAccountSort`と同じパターン。3.2節参照） |

#### `PaymentMethodKubun`（enum・種別ごとの業務ルールを集約）

[電子マネー対応の設計インプット](Claude Codeへの作業インプット/Feature1.03_dev1_payment-method-emoney-design-input.md) 4.2 の指示に従い、「銀行口座の要否」を種別ごとの `if` 分岐で書き散らさず、`PaymentMethodKubun` 自身に述語メソッドとして持たせる。既存の `ExpenditureCategory`（`domain/type/account/expenditure/`、`toDisplayLabel()` を自身が持つ設計）と同じ考え方を踏襲し、単純な文字列ラッパーではなく実体を持つ enum として実装する。

```java
public enum PaymentMethodKubun {
    CASH("1", false, false),
    BANK_TRANSFER("2", true, false),
    CREDIT_CARD("3", true, true),
    DEBIT_CARD("4", true, false),
    PREPAID_EMONEY("5", false, false);
    // 注:「前払い式」であることをenum名に残す(要件どおり)。ELECTRONIC_MONEYのような曖昧な名前にしない。
    // 将来、後払い型電子マネーを独立種別化する場合でもPREPAID_EMONEYの名前と意味は変わらない想定。

    private final String value;
    private final boolean requiresAccount;
    private final boolean requiresClosingDay;

    public static PaymentMethodKubun from(String value) { ... } // 未定義値は例外
    // 未定義値でも例外を投げない安全な入口(突合レビュー指摘AA対応。3.2節SafeDomainFactory参照)
    public static Optional<PaymentMethodKubun> tryFrom(String value) {
        return SafeDomainFactory.tryCreate(() -> PaymentMethodKubun.from(value));
    }
    public String getValue() { return value; }
    /** この支払方法種別が銀行口座の紐づけを必須とするか */
    public boolean requiresAccount() { return requiresAccount; }
    /** この支払方法種別が集計開始日の設定を必須とするか(現状はクレジットカードのみ) */
    public boolean requiresClosingDay() { return requiresClosingDay; }
}
```

| 種別 | コード値 | `requiresAccount()` | `requiresClosingDay()` |
|------|---------|---------------------|------------------------|
| 現金 | 1 | false | false |
| 口座振替 | 2 | true | false |
| クレジットカード | 3 | true | true |
| デビットカード | 4 | true | false |
| 電子マネー（前払い式） | 5 | false | false |

呼び出し側（Form のバリデーション・UseCase・画面制御JS）はこの2つの述語のみを参照する。表示名（日本語ラベル）は既存の慣習どおり `codetable.csv`（`CODE_DEFINES_PAYMENT_METHOD_KUBUN`）側で管理し、`PaymentMethodKubun` enum 自体には持たせない（業務ルールの集約と表示ラベルの管理を分離する）。**画面制御JSはJavaのenumを直接参照できないため、UseCase側で述語を評価した結果をHTML（`<option>`のdata属性）経由でJSに渡し、JS側は種別コードを直書きしない**（突合レビュー指摘E。詳細は5.2節）。

### 3.6 新規：`domain/repository/account/paymentmethod/PaymentMethodTableRepository.java`

`BankAccountTableRepository` と同型の `add/update/updateSort/findById(各種SearchQuery)` に加え、[支払方法固定値の設計指針](Claude Codeへの作業インプット/Feature1_03_dev1_支払方法固定値_設計指針.md) 2.3 の指示に従い、**取得メソッドを用途別に2系統**用意する。UseCase側で「予約値を除外するif」を個別に書かせず、呼び出すメソッド自体で区別させる。

```java
int add(PaymentMethod data);
int update(PaymentMethod data);
int updateSort(PaymentMethod data);
// 表示・JOIN用(全件、システム予約値を含む)。支払方法マスタ管理画面の一覧表示、名称解決などに使用
PaymentMethodInquiryList findByUserId(SearchQueryUserId userId);
PaymentMethodInquiryList findById(SearchQueryUserIdAndPaymentMethodSort search);
PaymentMethodInquiryList findById(SearchQueryUserIdAndPaymentMethodSortBetweenAB search);
PaymentMethod findById(SearchQueryUserIdAndPaymentMethodCode search);
// プルダウン用(ENABLE_FLG=trueかつシステム予約値を除外、表示順ソート)。収支登録・買い物登録・
// 店舗マスタのデフォルト支払方法選択などユーザーが選択操作を行う画面で使用(3.8節PaymentMethodInfoComponent経由)
PaymentMethodInquiryList findSelectableByUserId(SearchQueryUserId userId);
// プルダウン用(ENABLE_FLG=trueのみ、システム予約値は除外しない、表示順ソート)。固定費登録画面専用(5.4.1節。
// 突合レビュー指摘A対応。買い物集計8項目を固定費として登録する際に「支払方法がない」を選ぶ必要があるため)
PaymentMethodInquiryList findEnabledByUserId(SearchQueryUserId userId);
// 新規コード発番用。予約帯(990~999)を除いた件数をカウントする
int countByIdAndLessThanReserved(SearchQueryUserId userId);
```

`findSelectableByUserId()` を使うべき画面（[支払方法固定値の設計指針](Claude Codeへの作業インプット/Feature1_03_dev1_支払方法固定値_設計指針.md) 2.3 記載の一覧のうち、固定費登録を除いたdev1スコープ分）：収支登録（支出側）／買い物登録（簡易タイプ）／店舗マスタ管理（デフォルト支払方法の選択）。**固定費登録画面のみ`findEnabledByUserId()`を使う**（5.4.1節・突合レビュー指摘A）。支払方法マスタ管理画面自体の一覧表示は `findByUserId()`（予約行も表示するが、5.2節のとおり更新は`ENABLE_UPDATE_FLG`で制限）を使う。

> **突合レビュー指摘G反映（表示順シフトと予約行の関係の明示）**：`PaymentMethodInfoForm.paymentMethodSort`は`@Min(1) @Max(989)`（5.2節）で予約帯（990〜999）を避けているが、これは**新規登録・単票更新時の入力値チェック**にすぎない。`ShopInfoManageUseCase`を踏襲した表示順シフト処理（既存データの表示順を±1する一括更新。5.2節）が、`findById(SearchQueryUserIdAndPaymentMethodSortBetweenAB)`で取得する範囲に予約行（`PAYMENT_METHOD_SORT="999"`）を巻き込まない保証がどこにも明記されていなかったため、以下のとおり明示する。
> - シフト処理が対象とする範囲（`BetweenAB`検索の`B`側の上限値）は**常に`989`を超えないよう`PaymentMethodInfoManageUseCase`側で保証する**（ユーザー入力値が`@Max(989)`で既に`989`以下に制限されているため、シフト範囲もこの上限を超えることはない設計になるが、実装時にこの前提を崩さないこと）。
> - `findEnabledByUserId()`（固定費登録用、5.4.1節）はシフト処理の対象外の読み取り専用メソッドであり、シフト処理そのものには関与しない。
> - `updateSort(PaymentMethod data)`はシステム予約行（`ENABLE_UPDATE_FLG=false`）に対しては呼び出さない（5.2節の確認事項③対応どおり、予約行は表示順を含めすべて更新不可のため、シフト処理の一括更新ループからも除外する）。

### 3.7 既存クラス拡張

| クラス | 変更内容 |
|--------|---------|
| `Shop.java` | フィールド `defaultPaymentMethodCode: PaymentMethodCode`（null許容）追加。`Shop.from(...)` に引数追加 |
| `FixedCost.java` | フィールド `paymentMethodCode: PaymentMethodCode` 追加。`FixedCost.from(...)`（2種）・`updateBulkUpdateItem()` に引数追加 |
| `ExpenditureItem.java` | フィールド `paymentMethodCode: PaymentMethodCode` 追加。`ExpenditureItem.createExpenditureItem(...)` に引数追加（`ExpenditureRegistItem` 経由で取得）。**`addSisyutuKingaku(ExpenditureAmount)` および同種の減算メソッド（`ExpenditureItem.java:192-222`付近）も `paymentMethodCode` を現在値のままコピーするよう修正すること**（突合レビュー指摘A。`SimpleShoppingRegistUseCase`が買い物登録のたびにこれらのメソッドで金額のみを増減させてEXPENDITURE_TABLEを更新するため、対応漏れがあると買い物登録の都度、支払方法の値が消える） |
| `ShoppingRegist.java` | フィールド `paymentMethodCode: PaymentMethodCode` 追加。`ShoppingRegist.createShoppingRegist(...)` に引数追加 |
| `presentation/session/ExpenditureRegistItem.java` | フィールド `paymentMethodCode: String` 追加。`from(...)` に引数追加（Serializable） |

`AccountMonthInquiryExpenditureList.ExpenditureRow`（`domain/model/account/inquiry/`）にも `paymentMethodCode`／`bankAccountCode` フィールドを追加し、`ExpenditureRow.from(ExpenditureItem item)` で `item.getPaymentMethodCode()` から設定する。ただし支払方法名・口座名への変換（コード→名称）はドメイン層では行わず、UseCase層で `PaymentMethodInfoComponent`（3.8）を介して行う。

### 3.8 新規：`application/usecase/account/component/PaymentMethodInfoComponent.java`

固定費登録・収支登録・買い物登録・月別収支照会など複数のUseCaseから共通利用する「支払方法コード→表示名／銀行口座名」解決ロジックを1箇所に集約するコンポーネント。`CodeTableItemComponent` と同じ `@Component` パターン。

> **レビュー指摘④反映**：初版は `getPaymentMethodName(userId, code)`／`getBankAccountName(userId, code)` を行ごとに呼び出す設計だったが、これだと一覧1行あたり最大2回DBアクセスが発生し、100明細表示時に最大200回のSQL発行になってしまう。ユーザー単位の支払方法マスタ・銀行口座マスタは（990〜999のシステム予約帯を除いても）せいぜい数十件規模であり全件メモリに載せても問題にならない規模のため、**画面表示の起点で1回だけ全件取得し、以降はメモリ上のMapで解決するリゾルバオブジェクト**に設計変更する。`CodeTableItemComponent` がコードテーブル全体を起動時に1度だけ読み込んでメモリ保持するのと同じ考え方を、リクエスト単位（画面表示単位）で行う。

```java
// 画面表示の起点(UseCaseのread系メソッド)で1回だけ呼び出し、以降は戻り値のリゾルバをループ内で使い回す。
// 内部で PaymentMethodTableRepository.findByUserId(検索条件) と BankAccountTableRepository.findById(検索条件) を
// それぞれ1回ずつ(合計2回)呼び出し、Map<PaymentMethodCode, PaymentMethod> / Map<BankAccountCode, BankAccount> を構築する。
PaymentMethodNameResolver createResolver(UserId userId);

// ログインユーザの選択可能な支払方法一覧を選択ボックス用に取得(内部でfindSelectableByUserId()を使用。予約値を含まない)
List<OptionItem> getPaymentMethodOptions(UserId userId);
// ログインユーザの選択可能な銀行口座一覧を選択ボックス用に取得(内部でBankAccountTableRepository.findSelectableByUserId()を使用。無効行を含まない)
// 支払方法マスタ管理画面(5.2節)の銀行口座選択で使用する(突合レビュー指摘M)
List<OptionItem> getBankAccountOptions(UserId userId);
```

`getBankAccountOptions()` は責務としては「銀行口座マスタの選択肢生成」であり、本来は独立した`BankAccountInfoComponent`に切り出す方が単一責任の原則に沿うが、`PaymentMethodInfoComponent`は既に`createResolver()`内で`BankAccountTableRepository`を参照しており、支払方法マスタ管理画面という単一の利用箇所しか持たない小さなメソッドのために新規コンポーネントを作る非対称さを避けるため、本devでは同居させる。

```java
// PaymentMethodInfoComponent の内部クラス(またはpackage-privateな別クラス)。
// 1度構築すれば、以降の名称解決はすべてメモリ上のMap参照のみでDBアクセスを伴わない。
public class PaymentMethodNameResolver {
    // 支払方法コード→支払方法名 解決
    public String getPaymentMethodName(PaymentMethodCode code) {
        // システム予約値(999)はマスタを引かず「－」を返す。マスタ上の名称(支払方法がない)をそのまま画面に出さない
        if (code.isSystemReserved()) {
            return "－";
        }
        // ... Map<PaymentMethodCode, PaymentMethod> から解決(DBアクセスなし)
    }
    // 支払方法コード→銀行口座名 解決
    public String getBankAccountName(PaymentMethodCode code) {
        if (code.isSystemReserved()) {
            return "－";
        }
        // 対応する支払方法のbankAccountCodeがnull(現金・電子マネー(前払い式))の場合も「－」を返す
        // ... Map<BankAccountCode, BankAccount> から解決(DBアクセスなし)
    }
}
```

[支払方法固定値の設計指針](Claude Codeへの作業インプット/Feature1_03_dev1_支払方法固定値_設計指針.md) 2.5 の指示に従い、**表示変換ロジックは `PaymentMethodNameResolver` の2メソッドのみに集約**する（Thymeleaf側や各UseCaseに個別の変換処理を書かない）点は初版から変更しない。

対象箇所（[支払方法固定値の設計指針](Claude Codeへの作業インプット/Feature1_03_dev1_支払方法固定値_設計指針.md) 2.5）：月別収支照会（支出別タブ）の「支払方法」「引落先口座」列（5.7節）、固定費関連一覧の「支払方法」列（5.4節）。いずれも `AccountMonthInquiryUseCase.convertExpenditureList()`／`FixedCostInquiryUseCase` 側で `createResolver()` を一覧生成の直前に1回呼び、`stream().map(...)` のループ内では `resolver.getPaymentMethodName(...)` のみを呼ぶ形にする。将来のdev4買い物照会一覧の「支払方法」列も同じ仕組みを再利用する想定。

> **突合レビュー指摘L（CSV出力）への回答**：設計指針2.5は変換対象に「将来的なCSV出力等」も挙げているが、`src/main/java`配下を確認したところ**dev1時点でCSV出力機能は本アプリに一切存在しない**（該当するController/UseCaseなし）。したがって本devでの対応事項はなく、将来CSV出力機能が追加される際に同じ`PaymentMethodNameResolver`を再利用する、という方針の言及のみに留める。

呼び出し頻度が低い単発の解決（1件だけ表示するような画面）向けに、これまでどおりの単発解決メソッド（`getPaymentMethodName(UserId, PaymentMethodCode)`／`getBankAccountName(UserId, PaymentMethodCode)`、内部で `createResolver()` を都度呼ぶだけの薄いラッパー）も残すが、**一覧・複数行を扱う画面では必ず `createResolver()` を使うこと**をUseCase実装時のルールとする。

### 3.9 新規：`ShoppingAggregateSpecification`（買い物集計8項目の判定仕様）

[支払方法固定値の設計指針](Claude Codeへの作業インプット/Feature1_03_dev1_支払方法固定値_設計指針.md) 2.6 の指示に従い、「買い物集計8項目」（要件定義書 3.3・7.3。[ドメイン用語集](../ドメイン用語集.md) 参照）の判定を1箇所に集約する。既存の `ShoppingRegistExpenditureItemComponent` は8項目それぞれを個別のフィールド・個別メソッドとして持つ実装（フィールド名の直書きが8か所に分散）になっているため、まずこの `Specification` を切り出し、`ShoppingRegistExpenditureItemComponent` はこれに委譲する形にリファクタリングする。

**配置候補**：`domain/model/account/expenditure/ShoppingAggregateSpecification.java`（Specificationパターンはドメイン層の知識のため）。

> **Spring管理外であることの明示（突合レビュー指摘AD）**：本クラスは`@Component`を付与せずSpring管理外の単純な値オブジェクトとして扱う。DIコンテナ経由の注入は行わず、利用側（5.4.1節のForm `@AssertTrue`など）で都度`new ShoppingAggregateSpecification()`する（5.4.1節コード例参照）。状態を持たない・依存性がないため、DIによるテスト容易性のメリットが乏しく、Form側でのnewを許容する。

```java
public class ShoppingAggregateSpecification {
    // 支出項目コード＋支出区分(無駄遣い区分)の組み合わせ8組を1箇所に列挙する
    private static final Set<Pair<ExpenditureItemCode, ExpenditureCategory>> TARGETS = Set.of(
        Pair.of(ExpenditureItemCode.from(SISYUTU_ITEM_CODE_INSYOKU_VALUE), ExpenditureCategory.NON_WASTED),   // 食費(無駄遣いなし)
        Pair.of(ExpenditureItemCode.from(SISYUTU_ITEM_CODE_INSYOKU_VALUE), ExpenditureCategory.WASTED_B),      // 食費(無駄遣いB)
        Pair.of(ExpenditureItemCode.from(SISYUTU_ITEM_CODE_INSYOKU_VALUE), ExpenditureCategory.WASTED_C),      // 食費(無駄遣いC)
        Pair.of(ExpenditureItemCode.from(SISYUTU_ITEM_CODE_NITIYOU_SYOUMOUHIN_VALUE), ExpenditureCategory.NON_WASTED), // 日用消耗品
        Pair.of(ExpenditureItemCode.from(SISYUTU_ITEM_CODE_HIFUKU_VALUE), ExpenditureCategory.NON_WASTED),     // 衣類・クリーニング・靴
        Pair.of(ExpenditureItemCode.from(SISYUTU_ITEM_CODE_JYUUKYO_SETUBI_VALUE), ExpenditureCategory.NON_WASTED), // 住居設備
        Pair.of(ExpenditureItemCode.from(SISYUTU_ITEM_CODE_GAISYOKU_VALUE), ExpenditureCategory.NON_WASTED),   // 外食
        Pair.of(ExpenditureItemCode.from(SISYUTU_ITEM_CODE_RYUUDOU_KEIHI_VALUE), ExpenditureCategory.NON_WASTED) // 仕事(流動経費)
    );

    /** 指定の支出項目コード・支出区分の組み合わせが買い物集計8項目に該当するか */
    public boolean isSatisfiedBy(ExpenditureItemCode itemCode, ExpenditureCategory category) {
        return TARGETS.contains(Pair.of(itemCode, category));
    }
}
```

（`ExpenditureItemCode`/`ExpenditureCategory`の8組の実際の値は `ShoppingRegistExpenditureItemComponent` 既存コードの定数定義と完全に一致させること。現状の識別が項目名の文字列 `【更新不可予定額(アプリ更新)】` に依存している箇所があれば、このSpecificationに置き換える）

**用途**：
- dev1：6.1節の既存データ変換スクリプトで、`EXPENDITURE_TABLE` のどの行に「支払方法がない」を設定すべきかの判定に使用する（従来の「`ShoppingRegistExpenditureItemComponent`の対象8項目」という表現をこのSpecificationに置き換える）
- dev1：6.1節の既存データ変換スクリプトで、`FIXED_COST_TABLE` のどの固定費行に「支払方法がない」を設定すべきかの判定にも使用する（**突合レビュー指摘T・Y**。`FIXED_COST_TABLE`には支出区分の専用カラムがないため、`ExpenditureCategory.from(FixedCostName)`で固定費名から支出区分を導出したうえで本Specificationに渡す。3.9節末尾・6.1節参照）
- dev1：5.4.1節の固定費登録フォームの条件付きバリデーション（システム予約値`999`を許容するのは対象8項目の固定費のときのみとする判定。**突合レビュー指摘V・Y**）
- dev1：5.5節の訂正フォーム表示制御（支払方法コードがシステム予約値の行はdisabled表示にする。厳密には`PaymentMethodCode.isSystemReserved()`のみで判定でき本Specificationは不要だが、対象8項目かどうかをログ・テストで確認したい場合に利用できる）
- dev5：データ作成ロジックの見直し・訂正操作の制限の両方で同じ判定が必要になるため、dev1で作成したこのSpecificationをそのまま再利用する（[dev5設計指針](Claude Codeへの作業インプット/Feature1_03_dev5_集計8項目見直し_設計指針.md) 参照。ただし本devのスコープはdev1側の利用のみ）

> **用途が増えたことについて（突合レビュー指摘Y）**：当初はdev1の6.1節・5.5節の2用途のみだったが、指摘T・Vへの対応で5.4.1節・6.1節（FIXED_COST_TABLE側）の2用途が追加され、合計4用途になった。判定を1箇所に集約する（設計指針2.6）意図が効いてくる箇所が増えたということであり、本Specificationを`ShoppingRegistExpenditureItemComponent`から確実に切り出す価値がより高まったと言える。

---

## 4. インフラ層設計

`infrastructure/datasource`・`infrastructure/mapper`・`infrastructure/dto`・SQLテンプレート（`src/main/resources/sql/`）はすべて `account/shop` パターンをそのまま踏襲する（3層構成、mybatis-thymeleafのファイル参照型 `@Insert("sql/xxx/YyySql01.sql")`）。

### 4.1 新規追加ファイル一覧

| 層 | 銀行口座 | 支払方法 |
|----|---------|---------|
| Mapper | `infrastructure/mapper/account/bankaccount/BankAccountTableMapper.java` | `infrastructure/mapper/account/paymentmethod/PaymentMethodTableMapper.java` |
| DataSource | `infrastructure/datasource/account/bankaccount/BankAccountTableDataSource.java` | `infrastructure/datasource/account/paymentmethod/PaymentMethodTableDataSource.java` |
| DTO | `infrastructure/dto/account/bankaccount/BankAccountReadWriteDto.java` | `infrastructure/dto/account/paymentmethod/PaymentMethodReadWriteDto.java` |
| SQL | `src/main/resources/sql/account/bankaccount/*.sql` | `src/main/resources/sql/account/paymentmethod/*.sql` |

SQLファイルは `ShopTable*Sql*.sql` の番号付け規約（`InsertSql01`／`UpdateSql01`(本体更新)／`UpdateSql02`(表示順のみ)／`SelectSql01`〜`SelectSql0N`／`CountSql01`）をそのまま流用する。支払方法マスタは以下の2本を追加する。
- `findSelectableByUserId` 用の `SelectSql0N`（`WHERE ENABLE_FLG = true AND PAYMENT_METHOD_CODE < '990' ORDER BY PAYMENT_METHOD_SORT`）
- `countByIdAndLessThanReserved` 用の `CountSql02`（`WHERE PAYMENT_METHOD_CODE < '990'`）

銀行口座マスタは以下の1本を追加する（突合レビュー指摘M）。
- `findSelectableByUserId` 用の `SelectSql0N`（`WHERE ENABLE_FLG = true ORDER BY BANK_ACCOUNT_SORT`）

### 4.2 既存インフラの拡張

| クラス | 変更内容 |
|--------|---------|
| `ShopTableMapper` / `ShopReadWriteDto` / `ShopTableDataSource` / SQL | `DEFAULT_PAYMENT_METHOD_CODE` カラムを Insert/Update/Select に追加 |
| `FixedCostTableMapper` / `FixedCostReadWriteDto` / `FixedCostTableDataSource` / SQL | `PAYMENT_METHOD_CODE` カラムを Insert/Update/Select に追加 |
| `ExpenditureTableMapper` / `ExpenditureReadWriteDto` / `ExpenditureTableDataSource` / SQL | 同上 |
| `ShoppingRegistTableMapper` / `ShoppingRegistReadWriteDto` / `ShoppingRegistTableDataSource` / SQL | 同上 |

（各ファイル名は実装着手時に対象パッケージ配下で最終確認する。命名規則は `Shop*` と同一）

---

## 5. プレゼンテーション層設計

### 5.1 銀行口座マスタ管理画面（新規）

`ShopInfoManageController`／`ShopInfoForm`／`ShopInfoManageResponse`／`ShopInfoManageUseCase`／`ShopInfoManage.html` を1:1でなぞって新規作成する。

**ルーティング**：`/myhacbook/managebaseinfo/bankaccountinfo/`

| メソッド | パス | 内容 |
|---------|------|------|
| GET | `/initload/` | 初期表示 |
| GET | `/updateload?bankAccountCode=...` | 更新対象選択 |
| POST | `/update/` | 登録・更新 |
| GET | `/updateComplete/` | 完了後リダイレクト表示 |

**`BankAccountInfoForm`**

```java
private String action;
private String bankAccountCode;
private String bankAccountSortBefore;
@NotBlank @Size(min=1, max=50) private String bankName;
@Size(max=100) private String bankAccountMemo; // 任意項目
@Min(1) @Max(99) private Integer bankAccountSort;
private Boolean enableFlg; // チェックボックス(デフォルトtrue)
```

**`BankAccountInfoManageUseCase`**：`ShopInfoManageUseCase` の表示順シフトロジック（`execAction()` の件数キャップ・表示順の再配置）を踏襲するが、上限値は店舗マスタの900件ではなく**99件**（2.1.1参照）。店舗マスタと異なり「区分」選択がない分シンプルになる。

**`BankAccountInfoManageResponse`**：`ShopListItem` 相当の `BankAccountListItem`（`bankAccountCode, bankName, bankAccountMemo, bankAccountSort, enableFlg`）。`nonEditShopList` 相当の仕組みは持たない（銀行口座マスタはシステム行を持たない設計のため。2.1.2参照）。無効フラグの行は一覧上「無効」バッジ付きで表示するが選択（登録画面のプルダウン）には出さない。

> **突合レビュー指摘O反映**：要件3.1の「無効フラグで一覧から非表示」は、**マスタ管理画面自体の一覧ではなく、各登録画面の選択肢（プルダウン）を指す**という解釈で実装する（要件定義書側もこの解釈に合わせて訂正済み）。銀行口座マスタ管理画面自体の一覧からは無効化した口座を完全に消さない。理由：一覧から完全に消すと再有効化する手段がなくなるため。

**HTML**：新規テンプレート `templates/itemmanage/BankAccountInfoManage.html`（`ShopInfoManage.html` に「口座メモ」テキスト欄・「有効/無効」チェックボックスを追加した構成、`ShopInfoManage.html`と同じく`templates/itemmanage/`直下に配置）。一覧はコード・銀行名・口座メモ・表示順・状態の列を持つリスト。表示/非表示を切り替えるような条件付きJSは不要な画面のため、JSファイルの新規追加はなし。

### 5.2 支払方法マスタ管理画面（新規）

**ルーティング**：`/myhacbook/managebaseinfo/paymentmethodinfo/`（銀行口座マスタと同じエンドポイント構成）

**`PaymentMethodInfoForm`**

```java
private String action;
private String paymentMethodCode;
private String paymentMethodSortBefore;
@NotBlank @Size(min=1, max=50) private String paymentMethodName;
@NotBlank private String paymentMethodKubun;      // 1～5
private String bankAccountCode;                    // 種別により要否が異なる(下記AssertTrueで判定。現金・電子マネー(前払い式)は不要)
private String closingDay;                          // クレカのみ必須(下記AssertTrueで判定)
@Min(1) @Max(989) private Integer paymentMethodSort; // 予約帯990~999は対象外(2.1.2参照)
private Boolean enableFlg;
// enableUpdateFlgは保持しない(確認事項③回答により、システム行はフォームに読み込まれること自体がない。2.1.2/5.2参照)

@AssertTrue(message = "選択した支払方法種別では、銀行口座の指定が必要です。")
private boolean isBankAccountValid() {
    // 未入力、または不正な形式値(tryFrom()がempty)の場合はここでは素通しする
    // (未入力は@NotBlank側、不正形式はPaymentMethodKubunの型自体を検証するAssertTrue等で別途弾かれる想定。
    //  本メソッドの責務は「値が正当な種別であることを前提に、口座要否との整合を見る」ことに限定する)
    return PaymentMethodKubun.tryFrom(paymentMethodKubun)
            .map(kubun -> !kubun.requiresAccount() || StringUtils.hasLength(bankAccountCode))
            .orElse(true);
}

@AssertTrue(message = "クレジットカードを選択した場合、集計開始日は必須です。")
private boolean isClosingDayValid() {
    return PaymentMethodKubun.tryFrom(paymentMethodKubun)
            .map(kubun -> !kubun.requiresClosingDay() || StringUtils.hasLength(closingDay))
            .orElse(true);
}
```

（突合レビュー指摘AA反映。`PaymentMethodKubun.from()`の直接呼び出しをやめ`tryFrom()`＋`Optional`に変更したことで、`paymentMethodKubun`が不正な形式の値でも例外が伝播せず500エラーにならない。不正な種別値自体を弾く責務は`@NotBlank`と`PaymentMethodKubun`が扱う値の集合をコード定義（`codetable.csv`）由来のプルダウンに限定している画面設計側にあるため、本メソッドでは「素通し（true）」としてよい）

種別ごとの判定を `PAYMENT_METHOD_KUBUN_CREDIT_SELECTED_VALUE` のような文字列比較で `Form` に書き散らすと、種別追加のたびに分岐の追加漏れが起きうる（[電子マネー対応の設計インプット](Claude Codeへの作業インプット/Feature1.03_dev1_payment-method-emoney-design-input.md) 4.2 の指摘）。そのため `PaymentMethodKubun`（3.5節）の `requiresAccount()`／`requiresClosingDay()` 述語を参照する形に統一する。

> **突合レビュー指摘F反映**：上記2つの`@AssertTrue`は「必須なのに未入力」のみを検査しており、「不要なのに入力されている」（例：種別＝現金なのに`bankAccountCode`が送信された）を弾かない。JSで非表示にした欄のvalueがクリアされずに送信される、といった通常操作でも起こりうるケースであり、`BANK_ACCOUNT_CODE`に「現金なのに銀行口座が紐づいている」データが混入すると、dev2の口座別支払確認（要件4.3）で二重計上の原因になりうる。レビューでは(a)専用のバリデーション追加、(b)UseCase側で不要な値を`null`に正規化、の2案が提示されたが、**(b)の方が堅牢**（JS側の実装ミスやブラウザの挙動に依存せず、サーバー側で必ず整合性が取れる）なため、こちらを採用する。`PaymentMethodInfoManageUseCase`側で、`PaymentMethod`ドメインを生成する直前に以下の正規化を行う。

```java
// requiresAccount()=falseの種別は、フォームに何が送信されていてもbankAccountCodeをnullにする
String normalizedBankAccountCode = kubun.requiresAccount() ? inputForm.getBankAccountCode() : null;
// requiresClosingDay()=falseの種別は、フォームに何が送信されていてもclosingDayをnullにする
String normalizedClosingDay = kubun.requiresClosingDay() ? inputForm.getClosingDay() : null;
```

**`PaymentMethodInfoManageUseCase`**：`ShopInfoManageUseCase` の表示順シフトロジックを踏襲する。

> **設計上の確認事項③（解消・回答あり）**：「支払方法がない」システム行はすべての項目を更新不可とし、かつ**一覧表示にも出さない**（更新後の紙芝居 `ManagePaymentMethod.html` の登録済み一覧に予約行が含まれていない状態が正）。したがって `ENABLE_UPDATE_FLG` によるフィールド単位のdisabled制御（初版の設計）は不要になり、実装は以下のとおりシンプルになる。

- **一覧表示**：`PaymentMethodInfoManageUseCase` の一覧生成では `findByUserId()`（全件）ではなく、システム予約行を除いた一覧を組み立てる。具体的には `findByUserId()` の結果を `PaymentMethodCode.isSystemReserved()` でフィルタしてから画面に渡す（3.6節の `isSystemReserved()` 述語をここでも再利用。専用のリポジトリメソッドは追加しない）。
- **更新対象選択（`updateload?paymentMethodCode=999`への直接アクセス）**：一覧に出ないため通常操作では起こらないが、URL直叩き等の不正操作に備え、`PaymentMethodInfoManageUseCase.readPaymentMethodInfo(user, paymentMethodCode)` で対象データの `ENABLE_UPDATE_FLG=false` を検知した場合はエラーメッセージを設定して一覧表示に戻す（更新自体を許可しない。`ExpenditureItemInfoManageUseCase`のようなdisabled表示による編集画面は用意しない）。
- **`ENABLE_UPDATE_FLG` カラム自体は維持**：DB上のシステム行保護（サーバー側での更新拒否）は引き続き `ENABLE_UPDATE_FLG` で行う（2.1.2参照）。UI側で一覧非表示にすることと、DB側で更新を拒否することは別レイヤの防御であり、両方実装する。
- **無効化（`ENABLE_FLG=false`）した支払方法の一覧表示**（突合レビュー指摘O反映）：システム予約行（上記）とは別の話として、ユーザーが無効化した通常の支払方法は、銀行口座マスタ管理画面（5.1節）と同じ方針で**一覧上「無効」バッジ付きで表示**する（再有効化できるように）。ただし各登録画面のプルダウン（`findSelectableByUserId()`）には出さない。要件3.1の「無効フラグで一覧から非表示」は、マスタ管理画面自体の一覧ではなく各登録画面の選択肢を指す解釈で統一する（要件定義書側もこの解釈に合わせて訂正済み）。

**HTML**：新規テンプレート `templates/itemmanage/PaymentMethodInfoManage.html`（`templates/itemmanage/`直下、`ShopInfoManage.html`と同じ配置）。登録・更新フォームは紙芝居 `ManagePaymentMethod.html` に準拠する（支払方法種別 `<select>` の下に「電子マネーでも、紐づけたクレジットカードから支払いを行う場合（クレジットカード払い）は『クレジットカード』として登録してください。『電子マネー(前払い式)』は、電子マネーの残高から支払った場合のみ選択します。」という補足文を配置）。銀行口座欄・集計開始日欄は、種別＝現金／電子マネー（前払い式）のとき銀行口座欄を非表示、種別＝クレジットカードのときのみ集計開始日欄を表示するJSで制御する（紙芝居の`toggleAccountAndClosingDayAreas()`と同等のロジック）。一覧は予約行を含まない（前述のとおり）。

> **突合レビュー指摘E反映**：3.5節は「呼び出し側（Form のバリデーション・UseCase・**画面制御JS**）は`requiresAccount()`／`requiresClosingDay()`の2述語のみを参照する」としているが、JSはJavaのenumメソッドを直接呼べないため、紙芝居の`toggleAccountAndClosingDayAreas()`のようにJS側で種別コード（`'1'`,`'3'`,`'5'`等）を直書きすると、Java側の述語とJS側の分岐が二重管理になり、種別追加時にJSの修正漏れが再発しうる（電子マネー対応（改訂2）が防ごうとした問題そのもの）。これを避けるため、**サーバー側で述語を評価した結果をHTMLの`<option>`要素に埋め込み、JSは属性値のみを参照する**方式にする（5.6節`SimpleShoppingRegist.js`の`data-default-payment-method`と同じ手法）。

```html
<option th:each="item : ${paymentMethodKubunOptions}"
        th:value="${item.value}" th:text="${item.text}"
        th:attr="data-requires-account=${item.requiresAccount}, data-requires-closing-day=${item.requiresClosingDay}">
</option>
```

```javascript
// PaymentMethodInfoManage.js（レビュー指摘⑤で分離した外部ファイル）
function toggleAccountAndClosingDayAreas() {
    var select = document.getElementById('paymentMethodKubun');
    var selectedOption = select.options[select.selectedIndex];
    var requiresAccount = selectedOption.dataset.requiresAccount === 'true';
    var requiresClosingDay = selectedOption.dataset.requiresClosingDay === 'true';
    document.getElementById('bankAccountArea').style.display = requiresAccount ? 'block' : 'none';
    document.getElementById('creditCardArea').style.display = requiresClosingDay ? 'block' : 'none';
}
```

`paymentMethodKubunOptions`（`OptionItem`を拡張した`PaymentMethodKubunOptionItem`等、`value`/`text`に加え`requiresAccount`/`requiresClosingDay`の`boolean`を持つDTO）は、`PaymentMethodInfoManageUseCase`が`PaymentMethodKubun`の全値を`requiresAccount()`／`requiresClosingDay()`で評価して生成する（`codetable.csv`から表示名を、`PaymentMethodKubun` enumから2つの述語値を、それぞれ取得して合成する）。**種別を追加する際は`PaymentMethodKubun` enumに定数を1つ追加するだけでよく、JS側の修正は不要になる。**

銀行口座 `<select>` の選択肢は `PaymentMethodInfoManageUseCase` が `PaymentMethodInfoComponent.getBankAccountOptions(userId)`（3.8節・突合レビュー指摘M）から取得し、先頭に「設定しない」の空値オプションを追加してレスポンスに設定する（有効な口座のみ・表示順ソート。無効化した口座は選択肢に出ない）。

- **無効化された銀行口座を参照する既存の支払方法を編集する場合（突合レビュー指摘D）**：既存の支払方法が参照している銀行口座がその後無効化されていると、更新フォームを開いた時点で現在値が銀行口座の選択肢に存在しない状態になる。5.5節と同じ「現在値が選択肢に含まれるか」判定でdisabled表示＋隠しフィールド併記を適用する。

> **レビュー指摘⑤反映**：紙芝居 `ManagePaymentMethod.html` はHTMLファイル内に直接JSを記載しているが、実装（`PaymentMethodInfoManage.html`）ではJSを別ファイル `src/main/resources/static/js/itemmanage/PaymentMethodInfoManage.js` に分離する（`SimpleShoppingRegist.js`が`static/js/account/regist/`配下に分離されているのと同じ方針。指定パスは`static/js/itemmanage/`直下）。

### 5.3 店舗マスタ管理画面の拡張

- `ShopInfoForm` に `defaultPaymentMethodCode`（任意、`@NotBlank`なし）を追加。
- `ShopInfoManageUseCase.createShopInfoManageResponse()` で支払方法選択肢（`PaymentMethodInfoComponent.getPaymentMethodOptions()`、先頭に「設定しない」の空値オプション）をレスポンスに追加。
- `ShopInfoManageResponse.ShopListItem` に `defaultPaymentMethodName`（表示用に解決済み文字列）を追加。
- `ShopInfoManage.html`：登録フォームに支払方法 `<select>` を追加、一覧テーブルに「デフォルト支払方法」列を追加。
- **サーバー側バリデーション**（突合レビュー指摘C反映）：`defaultPaymentMethodCode` は`findSelectableByUserId()`ベースのプルダウンのためシステム予約値は通常出ないが、他の3画面（固定費登録・収支登録・買い物登録）と同様、リクエスト改変への備えとして `ShopInfoForm` に `@AssertTrue` を追加する。**任意項目**のため、値が入力されている場合のみ検査する（未入力＝「設定しない」は許容）。

```java
@AssertTrue(message = "デフォルト支払方法にシステム予約値は指定できません。")
private boolean isDefaultPaymentMethodCodeValid() {
    if (!StringUtils.hasLength(defaultPaymentMethodCode)) {
        return true; // 任意項目のため未入力は許容
    }
    // tryFrom()により、不正な形式値でも例外が伝播せず500エラーにならない(突合レビュー指摘AA反映)
    return PaymentMethodCode.tryFrom(defaultPaymentMethodCode)
            .map(code -> !code.isSystemReserved())
            .orElse(true);
}
```

### 5.4 固定費登録画面の拡張

#### 5.4.1 登録・更新フォーム（`FixedCostInfoUpdateForm`）

> **突合レビュー指摘A反映（運用確認により判明した重要事項）**：買い物集計8項目（3.9節）は`IncomeAndExpenditureRegistConfirmUseCase`が自動生成するものではなく、**固定費として固定費登録画面から登録する運用**（例：`IncomeAndExpenditureInitIntegrationTest_FixedCost_202510_AllRequired.sql`にある8件の固定費データ）である。毎月`IncomeAndExpenditureInitUseCase.readInitInfo()`が当月分の固定費を`ExpenditureRegistItem`として展開し（既存の「固定費から収支を生成する」仕組み）、そのまま`IncomeAndExpenditureRegistConfirmUseCase.execRegistAction()`でEXPENDITURE_TABLEにINSERTされる。**つまりEXPENDITURE_TABLE行への「支払方法がない」の設定は、固定費登録画面で支払方法＝「支払方法がない」を選んで固定費を登録した時点で決まり、固定費からの引き継ぎ（後述）でそのままEXPENDITURE_TABLEまで伝播する。** その後の買い物登録（`SimpleShoppingRegistUseCase`）による金額調整は`ExpenditureItem.addSisyutuKingaku()`等の差分更新のみで、他のフィールド（`paymentMethodCode`含む）は現在値のままコピーされるため、新たな設計対応は不要（フィールド追加時にこれらのメソッドが`paymentMethodCode`もコピーするよう実装すればよい、という実装レベルの注意点のみ）。
>
> この理解にもとづき、**固定費登録画面のプルダウンにのみ「支払方法がない」を選択肢として含める**（他の画面は5.5節のとおり除外を維持）。

- `FixedCostInfoUpdateForm` に `@NotBlank private String paymentMethodCode;` を追加。
- `FixedCostRegistConfirmUseCase.createFixedCost()`（`:327-349`）で `FixedCost.from(...)` に `inputForm.getPaymentMethodCode()` を渡す。
- 画面表示情報生成箇所（`FixedCostInquiryUseCase` 側、既存の支払月・支払日コンボ生成と同様の箇所）で `PaymentMethodInfoComponent.getPaymentMethodOptions()` ではなく、**新設する `PaymentMethodInfoComponent.getFixedCostPaymentMethodOptions(UserId)`**（`PaymentMethodTableRepository.findEnabledByUserId()`ベース。`ENABLE_FLG=true`のみで絞り込み、`isSystemReserved()`による除外は行わない。表示順ソートのため「支払方法がない」はソート値`999`により選択肢の末尾に表示される）を呼び出しプルダウンを設定する。対象画面：`templates/itemmanage/fixedcost/FixedCostInfoManageUpdate.html`（紙芝居 `ManageFixedCostUpd.html`）。
  - `PaymentMethodTableRepository` に `PaymentMethodInquiryList findEnabledByUserId(SearchQueryUserId userId)` を追加（3.6節）。`findSelectableByUserId()`との違いは`isSystemReserved()`による除外を行わない点のみ。
  - HTML側に「『支払方法がない』は買い物集計8項目専用です。通常の固定費では選択しないでください。」という`<p class="form-text">`の補足文を配置する（固定費登録画面でのみ表示。要件3.3・7.3の対象8項目という運用ルールをUI上でも示すため）。
  - **【固定費名変更時の注意・突合レビュー指摘AG】**：対象8項目の支出区分（無駄遣い区分）は固定費名の文字列マッチ（`FIXED_COST_NAME`が「無駄遣いB」「無駄遣いC」を含むかで判定。指摘W）で導出される。そのため固定費名を変更すると支出区分が変わりうるが、`SISYUTU_ITEM_CODE=0051`（食費）は3区分すべてが対象8項目に含まれるため`isPaymentMethodCodeValid()`のバリデーションは変更後も通過し、**画面上エラーが出ないまま翌月以降の引き継ぎで集計先（無駄遣い区分）が変わる**。上記のHTML補足文に「固定費名の『無駄遣いB』『無駄遣いC』の文言を削除・変更すると集計区分が変わります」の一文を追加し、対象8項目の固定費名を編集する際の注意を促す。この挙動自体はFeature1.03以前から存在する`ExpenditureCategory.from(FixedCostName)`の仕様であり、dev1が作り込んだものではないため優先度は高くないが、指摘Wの調査で初めて可視化されたため記録として残す。
- **サーバー側バリデーション（突合レビュー指摘V反映：全面撤回→条件付き許容に変更）**：固定費登録画面は「支払方法がない」の入力経路として正当だが、初版の設計（予約値拒否バリデーションを一切設けない）は**対象8項目以外の通常の固定費にも`999`を設定できてしまう**副作用がある。プルダウンの末尾（ソート値`999`）に常時表示されるため誤選択の余地があり、誤設定は毎月の引き継ぎで`EXPENDITURE_TABLE`に伝播し、月別収支照会では「－」表示になり支払方法未設定なのか集計レコードなのか区別がつかなくなる、dev2の口座別支払確認でどの銀行の行にも計上されない、dev5の逆変換スクリプトが誤変換する、といった問題がある。そこで**条件付きバリデーション**にする：`paymentMethodCode`がシステム予約値の場合、その固定費の支出項目コード・固定費名（から導出される支出区分）が`ShoppingAggregateSpecification`（3.9節）の対象8項目に一致するときのみ許容し、一致しない場合はバリデーションエラーとする。

```java
@AssertTrue(message = "「支払方法がない」は買い物集計8項目専用の固定費にのみ設定できます。")
private boolean isPaymentMethodCodeValid() {
    // paymentMethodCode・sisyutuItemCode・fixedCostNameの3つとも、このメソッドが参照する可能性がある
    // (@AssertTrueは他フィールドの@NotBlank違反があっても実行されるため、3つとも明示的にガードする。
    //  fixedCostNameのガード漏れが突合レビュー指摘AAで指摘された)
    if (!StringUtils.hasLength(paymentMethodCode)
            || !StringUtils.hasLength(sisyutuItemCode)
            || !StringUtils.hasLength(fixedCostName)) {
        return true; // 各フィールドの@NotBlank側で検出
    }
    // tryFrom()により、不正な形式値でも例外が伝播せず500エラーにならない(突合レビュー指摘AA反映)
    Optional<PaymentMethodCode> code = PaymentMethodCode.tryFrom(paymentMethodCode);
    if (code.isEmpty() || !code.get().isSystemReserved()) {
        return true; // 形式不正、または通常の支払方法はここでは判定不要(形式不正自体はpaymentMethodCode専用の別チェックで弾く想定)
    }
    // システム予約値が選択された場合のみ、対象8項目かどうかを判定する。
    // FixedCostName・ExpenditureItemCodeは既存(Feature1.03以前)の型でtryFrom()を持たないため、
    // SafeDomainFactory.tryCreate()で直接包む(3.2節参照)
    Optional<FixedCostName> name = SafeDomainFactory.tryCreate(() -> FixedCostName.from(fixedCostName));
    Optional<ExpenditureItemCode> itemCode = SafeDomainFactory.tryCreate(() -> ExpenditureItemCode.from(sisyutuItemCode));
    if (name.isEmpty() || itemCode.isEmpty()) {
        return true; // 形式不正はsisyutuItemCode/fixedCostNameそれぞれの既存チェックで弾かれる想定
    }
    ExpenditureCategory category = ExpenditureCategory.from(name.get());
    // ShoppingAggregateSpecificationはSpring管理外の単純な値オブジェクトのため、他Formの@AssertTrueと同様その場でnewする
    return new ShoppingAggregateSpecification().isSatisfiedBy(itemCode.get(), category);
}
```

`ExpenditureCategory.from(FixedCostName)`（`domain/type/account/expenditure/ExpenditureCategory.java:94-102`。Feature1.03以前から存在する既存メソッド。固定費名に「無駄遣いB」／「無駄遣いC」の文字列が含まれるかで支出区分を導出し、どちらも含まなければ「無駄遣いなし」を返す）を使えば、`ShoppingAggregateSpecification.isSatisfiedBy(itemCode, category)`をそのまま呼べるため、支出項目コード単独の部分判定メソッドを新設する必要はない（指摘Wの調査結果）。他の3画面（収支登録・買い物登録・店舗マスタ）は指摘Cの対応（一律拒否）のまま維持する。

> **突合レビュー指摘AA対応の要点**：初版は`paymentMethodCode`・`sisyutuItemCode`のみガードしており`fixedCostName`が未ガードだった。`fixedCostName`が空のまま登録ボタンを押すと`FixedCostName.from("")`が例外を投げ、「固定費名は必須です」というバリデーションメッセージの代わりに**500エラーが表示される**という、通常操作で踏みうるバグだった。ガード節に`fixedCostName`を追加し、あわせて全ての`from()`呼び出しを`tryFrom()`／`SafeDomainFactory.tryCreate()`経由に変更したことで、想定外の形式不正値が混入しても500エラーにならない。
- **無効化された支払方法を参照する既存行の訂正（突合レビュー指摘D）**：本画面は`findEnabledByUserId()`ベースのため予約値（`999`）自体は選択肢に含まれるが、**ユーザーが無効化（`ENABLE_FLG=false`）した支払方法**は選択肢から消える。既存の固定費が無効化済みの支払方法を参照している場合、更新フォームを開いた時点で現在値が選択肢に存在しない状態になるため、5.5節と同じ「現在値が選択肢に含まれるか」判定でdisabled表示＋隠しフィールド併記を適用する。

#### 5.4.2 一括更新フォーム（`FixedCostBulkUpdateForm`）（レビュー指摘⑥反映）

初版は `FixedCostInfoUpdateForm`（単票の登録・更新）のみを対象としていたが、固定費登録には同一支出項目の固定費をまとめて更新する**一括更新フロー**（Feature1.01 Feature①）が別途存在し、専用の `FixedCostBulkUpdateForm` を使う。この一括更新フローも支払方法がらみで影響を受けるため、以下のとおり設計を追加する。

**`FixedCostBulkUpdateForm.java`（既存）の構成**（変更なし）：
```java
private String baseFixedCostCode;
@NotBlank private String shiharaiDay;
@NotNull @Min(1) private Integer shiharaiKingaku;
@NotEmpty private List<String> checkedFixedCostCodeList;
```

- **一括更新の対象に支払方法は含めない**：`FixedCostBulkUpdateForm`に`paymentMethodCode`は追加しない。一括更新は「支払日」「支払金額」の2項目のみをチェック対象の全固定費に一律適用する機能であり、各固定費の支払方法（銀行口座・カード）は個体ごとに異なりうるため、一括で書き換える対象には含めない。
- `FixedCost.updateBulkUpdateItem(String fixedCostPaymentDay, Integer fixedCostPaymentAmount)`（`FixedCost.java:162-174`）はシグネチャ変更なし。返却する新しい`FixedCost`は他の全フィールド（`paymentMethodCode`含む）を**元の値のまま**コピーする（既存の`sisyutuItemCode`等と同じ扱い。実装時、コピー漏れがないか特に注意する）。
- **表示のみ対応**：一括更新の対象選択画面では、どの固定費がどの支払方法かをユーザーが確認できるよう、一覧に「支払方法」列を表示する（5.4.3参照）。

#### 5.4.3 固定費関連の一覧・選択画面への「支払方法」列追加（レビュー指摘⑦反映）

固定費登録に関わる以下5画面（紙芝居ファイル→実装テンプレート／レスポンスDTOの対応）すべてに「支払方法」列を追加する。

| # | 紙芝居 | 実装テンプレート | Response / 行DTO | UseCase |
|---|--------|-----------------|-------------------|---------|
| 1 | `ManageFixedCost.html` | `templates/itemmanage/fixedcost/FixedCostInfoManageInit.html` | `FixedCostInfoManageInitResponse`（`AbstractFixedCostItemListResponse.FixedCostItem`を使用） | `FixedCostInquiryUseCase.readInitInfo()` |
| 2 | `ManageFixedCost2_addCheck.html` | 上記と同一（新規登録済み確認表示。`GET /addload`→`readRegisteredFixedCostInfoBySisyutuItem()`） | 同上（`FixedCostItem`を再利用） | `FixedCostInquiryUseCase.readRegisteredFixedCostInfoBySisyutuItem()` |
| 3 | `ManageFixedCostActSelect.html` | `templates/itemmanage/fixedcost/FixedCostInfoManageActSelect.html` | `FixedCostInfoManageActSelectResponse`（`SelectFixedCostInfo`／`SiblingFixedCostItem`） | `FixedCostInquiryUseCase.readActSelectItemInfo()` |
| 4 | `ManageFixedCostActSelect2_BulkUpd.html` | `templates/itemmanage/fixedcost/FixedCostBulkUpdate.html` | `FixedCostBulkUpdateResponse`（`BulkUpdateTargetItem`） | `FixedCostInquiryUseCase.readBulkUpdateInfo()` |
| 5 | `FixedCostMonthlyDetail.html` | `templates/itemmanage/fixedcost/FixedCostMonthlyDetail.html` | `FixedCostMonthlyDetailResponse`（`FixedCostItem`を再利用） | `FixedCostMonthlyDetailUseCase.readMonthlyDetail()` |

> #2（`ManageFixedCost2_addCheck.html`）は実装上どの画面に対応するか初版調査時点でやや曖昧だった（新規登録確認用の専用テンプレートが存在せず、#1の一覧画面が「登録済み確認」状態を兼ねている）。上表の対応で問題なければこのまま進めるが、認識が異なる場合はご指摘ください。

**DTOごとの変更**：
- `AbstractFixedCostItemListResponse.FixedCostItem`（`presentation/response/itemmanage/fixedcost/AbstractFixedCostItemListResponse.java:67-102`。#1・#2・#5で共用）に `paymentMethodName`（解決済み文字列）フィールドを追加し、`from(...)`ファクトリに引数を追加する。この1クラスの変更で#1・#2・#5の3画面をカバーできる。
- `FixedCostInfoManageActSelectResponse.SelectFixedCostInfo`／`.SiblingFixedCostItem`（#3）にそれぞれ `paymentMethodName` を追加。
- `FixedCostBulkUpdateResponse.BulkUpdateTargetItem`（#4）に `paymentMethodName` を追加（5.4.2のとおり表示のみ、一括更新の対象にはしない）。
- 各UseCase（`FixedCostInquiryUseCase`／`FixedCostMonthlyDetailUseCase`）は、一覧生成の直前に `PaymentMethodInfoComponent.createResolver(userId)`（3.8節・レビュー指摘④反映後の仕様）を1回呼び出し、`stream().map(...)` のループ内では `resolver.getPaymentMethodName(...)` のみを呼ぶ（N+1回のSQL発行を避ける）。
- 対象テンプレート4ファイル（`FixedCostInfoManageInit.html`／`FixedCostInfoManageActSelect.html`／`FixedCostBulkUpdate.html`／`FixedCostMonthlyDetail.html`）のテーブルに「支払方法」列を追加。

> `FixedCostAnnualSummary.html`／`FixedCostAnnualSummaryUseCase`（年間固定費合計画面）は今回のレビュー指摘に含まれていないため対象外とする。年間の合計表示画面であり個々の固定費の支払方法までは表示しない想定だが、必要であれば別途ご指摘ください。

### 5.5 収支登録画面（支出側）の拡張

前提：2.3節の解釈（収入側は対象外）に基づく。

- `ExpenditureItemForm` に `@NotBlank private String paymentMethodCode;` を追加。
- `presentation/session/ExpenditureRegistItem` に `paymentMethodCode` フィールドを追加（3.7参照）。
- `ExpenditureRegistUseCase.createExpenditureRegistItem()`（`:457-482`）で form ⇄ session item 変換に `paymentMethodCode` を追加。
- `ExpenditureItem.createExpenditureItem()` で `ExpenditureRegistItem.getPaymentMethodCode()` を `PaymentMethodCode` に変換し `EXPENDITURE_TABLE.PAYMENT_METHOD_CODE` へ設定。
- **固定費からの引き継ぎ**：`IncomeAndExpenditureInitUseCase.readInitInfo()`（`:162-186`）の `ExpenditureRegistItem.from(...)` 呼び出しに `domain.getPaymentMethodCode().getValue()`（`FixedCost` 由来）を追加する。**これが買い物集計8項目に「支払方法がない」を設定する唯一の経路である**（5.4.1節・突合レビュー指摘A参照。8項目は固定費として登録され、この引き継ぎ処理を通じて毎月EXPENDITURE_TABLEへ伝播する）。
- 支出追加・訂正フォーム（`ExpenditureItemForm` を使う画面）に支払方法 `<select>` を追加。プルダウンは他の全画面と同様 `findSelectableByUserId()` ベースとし、**「支払方法がない」は例外なく表示しない（選択不可）**。買い物集計8項目は固定費登録画面（5.4.1節）経由でのみ「支払方法がない」を設定でき、収支登録画面から新規に直接追加する経路では選ばせない。

> **設計上の確認事項⑤（解消・回答あり、突合レビュー指摘Aを受けて記述を修正）**：ExpenditureItemFormのプルダウンは予約値『支払方法がない』を表示しない（選択できない）でよい、との回答を得た。当初「買い物集計8項目の自動登録の仕組みはdev1のスコープ外」としていたが、運用確認の結果、**自動登録の実体は既存の「固定費→収支登録」引き継ぎ機能（上記）そのもの**であることが判明した。8項目は固定費として登録し（5.4.1節）、支払方法「支払方法がない」を固定費側で設定しておけば、毎月の収支登録初期表示時にそのまま引き継がれるため、収支登録画面側のプルダウンで予約値を選ばせる必要はない。**すべての支払方法プルダウン共通で予約値は非表示**（固定費登録画面を除く）とする方針は変更なし。
>
> **実装上の補足（技術的な留意点。突合レビュー指摘D反映で判定条件を一般化）**：プルダウンの選択肢に予約値を含めない場合、現在値が`999`（システム予約値）である行を訂正フォームで開くと、`<select>`の現在値が選択肢一覧に存在しないという状態になる。この状態でブラウザが未選択またはリストの先頭値を暗黙的に表示すると、ユーザーが支払方法欄を一切操作しなくても、保存時に意図せず`999`から別の値に書き換わってしまう恐れがある。
>
> **同じ事故は、システム予約値だけでなく「ユーザーが後から無効化（`ENABLE_FLG=false`）した支払方法」を参照する既存行を開いた場合にも起こる**（`findSelectableByUserId()`は`ENABLE_FLG=true`の行のみを返すため、無効化された支払方法もプルダウンの選択肢から消える）。そのため判定条件は `isSystemReserved()` 単独ではなく、**「現在値の支払方法コードが `findSelectableByUserId()` の結果（選択可能な一覧）に含まれるか」に一般化**する。含まれない場合（＝システム予約値、または無効化された支払方法を参照している場合のいずれか）に限り、支払方法欄を`ExpenditureItemInfoManageUpdate.html`と同じdisabled表示＋隠しフィールド併記パターン（`th:unless`側の分岐）で表示し、値をそのまま維持したまま送信する。**この判定は「DBに保存済みの行を訂正フォームで開く」場合だけでなく、「固定費から引き継いでまだ未保存のセッション上の新規行（`DATA_TYPE_NEW`）を収支登録確認画面で表示する」場合にも同じ条件で適用する**（固定費由来の新規行は生成された時点で既に`paymentMethodCode=999`を持っているため）。対象外の行（＝選択肢に含まれる支払方法を参照している通常の支出）は常に通常の選択可能なプルダウンとする。この対応は「訂正操作の制限」（要件3.3で dev1〜dev4 では行わないとされているもの。主に金額の訂正を指す）とは異なり、プルダウンに存在しない値をどう表示するかという技術的な帰結として支払方法欄のみに適用するものであり、金額欄・他の項目には一切影響しない。
>
> **同型の問題が存在する他の画面（突合レビュー指摘D）**：固定費登録の更新フォーム（5.4.1節。ただしこちらは`findEnabledByUserId()`ベースで予約値自体は含むため、無効化された支払方法を参照するケースのみ該当）、買い物登録の更新（5.6節の`action=update`）、支払方法マスタ管理画面自体の銀行口座選択（5.2節。無効化された銀行口座を参照している既存の支払方法を編集する場合）。**いずれも同じ「現在値が選択肢に含まれるか」判定を適用し、含まれない場合はdisabled表示＋隠しフィールド併記とする**（5.4.1節・5.6節・5.2節にそれぞれ追記）。

### 5.6 買い物登録（簡易タイプ）画面の拡張

- `SimpleShoppingRegistInfoForm` に `@NotBlank private String paymentMethodCode;` を追加。
- `SimpleShoppingRegistUseCase.createResponse()`（`:753-789`）で支払方法選択肢（`PaymentMethodInfoComponent.getPaymentMethodOptions()`、`findSelectableByUserId()`ベース）を追加取得しレスポンスに設定。
- **サーバー側バリデーション**：5.4節と同様、`SimpleShoppingRegistInfoForm` に `@AssertTrue` を追加する。買い物登録は1回の買い物実績を単一の支払方法で記録する画面であり、5.5節のような「システム予約値を選ばせる必要があるケース」は存在しないため、例外なく拒否してよい。

```java
@AssertTrue(message = "支払方法にシステム予約値は指定できません。")
private boolean isPaymentMethodCodeValid() {
    if (!StringUtils.hasLength(paymentMethodCode)) {
        return true; // 未入力は@NotBlank側で検出
    }
    // tryFrom()により、不正な形式値でも例外が伝播せず500エラーにならない(突合レビュー指摘AA反映)
    return PaymentMethodCode.tryFrom(paymentMethodCode)
            .map(code -> !code.isSystemReserved())
            .orElse(true);
}
```
- `ShoppingRegist.createShoppingRegist(userId, inputForm)` で `PAYMENT_METHOD_CODE` を設定。
- **店舗のデフォルト支払方法自動設定**：G節の調査のとおり、店舗選択はサーバー往復を伴わない（店舗区分変更のみ往復）。そのためクライアントサイドJSで対応する。
  - 店舗 `<option>` 要素に `data-default-payment-method` 属性を埋め込む（`th:attr="data-default-payment-method=${item.defaultPaymentMethodCode}"`）。
  - `SimpleShoppingRegist.js` に店舗 `<select>` の `change` イベントハンドラを追加し、選択された `<option>` の `data-default-payment-method` が空でなければ支払方法 `<select>` の値をそれに設定する。
  - **新規登録時のみ**自動設定し、更新（`action=update`、DBロード済みデータ）の場合は登録済みの実際の支払方法を優先し自動上書きしない（`registInfoForm.getAction()` で判定）。
  - ユーザーが自動設定後に手動で支払方法を変更することは妨げない（あくまで初期値のヒント）。
- **無効化された支払方法を参照する既存行の訂正（突合レビュー指摘D）**：更新（`action=update`）でDBロードした買い物登録データの支払方法が、その後ユーザーによって無効化（`ENABLE_FLG=false`）されている場合、5.5節と同じ「現在値が選択肢に含まれるか」判定でdisabled表示＋隠しフィールド併記を適用する。

### 5.7 月別収支照会画面（支出別タブ）の拡張

`docs/archive/account-month-inquiry/Feature1.02_支出別一覧追加_設計書.md` で追加された仕組みへの追加変更。

- `AccountMonthInquiryResponse.ExpenditureRow` に `paymentMethodName`・`bankAccountName`（解決済み文字列）フィールドを追加。
- `AccountMonthInquiryUseCase.convertExpenditureList()` の**呼び出し元**（`execRead()`等、一覧生成の起点）で `PaymentMethodInfoComponent.createResolver(userId)`（3.8節・レビュー指摘④）を**1回だけ**呼び出し、`convertExpenditureList()` にはそのリゾルバを引数で渡す。`convertExpenditureList()` 内の `stream().map(...)` ループでは `resolver.getPaymentMethodName(...)`／`resolver.getBankAccountName(...)` のみを呼び出し、個々の行ごとにDBアクセスしない（**突合レビュー指摘B**：初版はここが行ごとの単発解決メソッド呼び出しのままになっており、指摘④のバッチ解決方針に反していた。月別収支照会は明細行数が最も多くなりうる画面であるため、この修正を欠かすと指摘④の対応が実質的に無効化される）。
- `AccountMonth.html` の支出別テーブル（`:189-213`）にヘッダ「支払方法」「引落先口座」を追加、`td` に `item.paymentMethodName`／`item.bankAccountName` を出力。

| 現行 | 変更後 |
|------|--------|
| 支出名(区分) / 支払日 / 支出金額 / 訂正 / 支出詳細 | 支出名(区分) / **支払方法** / 支払日 / 支出金額 / **引落先口座** / 訂正 / 支出詳細 |

（要件定義書3.3の列順「支出名(区分)/支払方法/支払日/支出金額/引落先口座/訂正/詳細」に合わせる）

---

## 6. データ移行設計

### 6.1 既存DBデータへの支払方法設定（要件3.4）

`FIXED_COST_TABLE`・`EXPENDITURE_TABLE`・`SHOPPING_REGIST_TABLE` への `PAYMENT_METHOD_CODE NOT NULL` カラム追加に伴い、既存データへの一括設定が必要。

**方針（SQLスクリプトベース）**：

1. 各ユーザーの支払方法マスタに、少なくとも「現金」1件と「支払方法がない」システム行（`PAYMENT_METHOD_CODE=999`）が存在する状態を用意する（ユーザーごとに手動 or 簡易スクリプトで初期の支払方法・銀行口座を最低1件ずつ投入）。**実行順序に注意**：支払方法マスタへのシステム予約行INSERTを、下記2.以降の支出・収支・固定費データのUPDATEより先に実行すること。逆順ではFK制約違反になる。
2. `EXPENDITURE_TABLE` のうち、`(SISYUTU_ITEM_CODE, EXPENDITURE_KUBUN)` の組み合わせが `ShoppingAggregateSpecification`（3.9節）の対象8項目に一致する行 → 「支払方法がない」固定値を設定。判定条件はSpecificationと完全に一致させ、別途SQL側で独自の条件を書かない。
3. **`FIXED_COST_TABLE` のうち、対象8項目に対応する固定費行 → 「支払方法がない」固定値を設定**（突合レビュー指摘T対応）。改訂5で判明したとおり、この8項目は固定費として登録され「固定費→収支登録」の引き継ぎ機能で毎月`EXPENDITURE_TABLE`へ伝播する運用のため、既存の`EXPENDITURE_TABLE`行だけを2.で変換しても、参照元の`FIXED_COST_TABLE`行を変換し忘れると、**翌月の収支登録初期表示時に固定費側の値（他ステップで設定された値）が引き継がれてしまい、移行直後の月と翌月以降とで支払方法が食い違うサイレントな不整合**が生じる。
   - `FIXED_COST_TABLE` には`ShoppingAggregateSpecification`が要求する支出区分（無駄遣い区分）に相当する専用カラムが存在しない（指摘W参照）。区分の判定は既存の`ExpenditureCategory.from(FixedCostName)`（`domain/type/account/expenditure/ExpenditureCategory.java:94-102`。Feature1.03以前から存在する固定費名の文字列マッチによる支出区分導出ロジック）と同じ規則に基づくが、**この文字列マッチ規則をSQL側（UPDATE文のWHERE句）でそのまま再現することはしない**（突合レビュー指摘AB対応）。移行対象は全ユーザー合計でも高々8項目×ユーザー数程度の少件数であり、文字列マッチをSQLに複製すると、`ExpenditureCategory`側のロジック変更時にSQLの改修漏れが生じるリスクがある。代わりに以下の**候補抽出→目視確認→コード明示列挙**の手順を取る：
     1. `SELECT FIXED_COST_CODE, FIXED_COST_NAME, SISYUTU_ITEM_CODE FROM FIXED_COST_TABLE WHERE SISYUTU_ITEM_CODE IN (対象6つの支出項目コード)` で候補行を抽出する。
     2. 抽出結果を**目視で確認**し、`FIXED_COST_NAME`から対象8項目（支出項目コード＋無駄遣い区分の組み合わせ）に該当する行であることを1件ずつ確定させる。抽出件数が要件7.3の8項目（ユーザーごと）と一致することを確認し、想定外の固定費が混入していないかもあわせて確認する。
     3. UPDATE文には、2.で確定した`FIXED_COST_CODE`の値を**明示列挙**する（`WHERE FIXED_COST_CODE IN ('xxxx', 'yyyy', ...)`）。SQL側で`FIXED_COST_NAME`の文字列マッチ条件を独自に組み立てない。
   - 対象6つの支出項目コード（`0051`は3区分すべてが対象、`0052`/`0050`/`0046`/`0007`/`0047`はNON_WASTEDのみ対象）は`ShoppingRegistExpenditureItemComponent`の定数定義と完全に一致させる。
4. 上記以外の `EXPENDITURE_TABLE`・`FIXED_COST_TABLE`・`SHOPPING_REGIST_TABLE` の行 → ルールベースで自動設定を試みる（例：家賃など特定の固定費名 → 指定の口座振替、特定店舗コード → 現金、など）。ルールはユーザーの実データを確認しながら個別に作成する。
5. ルールに一致しない残りの行 → 「現金」をデフォルト設定。
6. 手修正が必要な件数（5.の対象件数、業務的に誤りが許容できない口座振替系のデータなど）を集計し、件数次第でSQL手修正 or Admin画面拡張の要否を判断する（要件9節の持ち越し事項）。

変換スクリプトはベースラインとして保存し、dev5で「支払方法がない」を削除する際の逆変換の参照元にする（[支払方法固定値の設計指針](Claude Codeへの作業インプット/Feature1_03_dev1_支払方法固定値_設計指針.md) 3節）。

> **設計上の確認事項④（解消・回答あり）**：ステップ6の判断（バッチ/Admin画面が必要か）は実データに対して4.の自動設定ルールを試算しないと確定できないため、**製造・テストが完了した後、本番適用前に本番環境のDBデータを確認してから、本節（6.データ移行設計）で必要となる詳細ロジックを実装する**進め方でよい、との回答を得た。したがって6章の詳細スクリプト実装（6.1のステップ2〜6の具体的なSQL。突合レビュー指摘Tで追加したステップ3の`FIXED_COST_TABLE`変換を含む）は、8章の実装順序では**最終工程（本番適用の直前）に位置づける**（14.の位置づけを変更。8章参照）。dev1のコーディング・テストそのものは、既存データの内容によらず進められる（6.2のマイグレーション手順の1〜3、5〜6＝テーブル作成・カラム追加・制約付与は先に実施でき、6.1・6.2の4＝実データへのルールベース設定のみを本番適用直前に回す）。

### 6.2 マイグレーション実行順序

1. `BANK_ACCOUNT_TABLE`・`PAYMENT_METHOD_TABLE` 作成
2. 全既存ユーザーに対し「支払方法がない」システム行（`BANK_ACCOUNT_CODE=NULL`）を投入（6.1の1.と合わせ、最低限の「現金」相当の支払方法も投入）
3. `SHOP_TABLE.DEFAULT_PAYMENT_METHOD_CODE`・`FIXED_COST_TABLE.PAYMENT_METHOD_CODE`（NULL可）・`EXPENDITURE_TABLE.PAYMENT_METHOD_CODE`（NULL可）・`SHOPPING_REGIST_TABLE.PAYMENT_METHOD_CODE`（NULL可）を追加
4. 6.1のデータ移行スクリプトを実行
5. `FIXED_COST_TABLE`・`EXPENDITURE_TABLE`・`SHOPPING_REGIST_TABLE` の `PAYMENT_METHOD_CODE` に `NOT NULL` 制約を付与
6. 外部キー制約を追加

---

## 7. テスト方針

### 7.1 ドメイン層（Unit）

- `BankAccountCode`・`BankAccountSort`・`BankName`・`BankAccountMemo`・`PaymentMethodCode`・`PaymentMethodName`・`ClosingDay`・`PaymentMethodSort`・`EnableFlg` 各ドメインタイプのバリデーション境界値テスト
- `PaymentMethodKubun`（enum）：`from()` の全コード値網羅、`requiresAccount()`／`requiresClosingDay()` を5種別分すべて検証（電子マネー（前払い式）が `requiresAccount()=false` であることを含む）
- `BankAccount`／`PaymentMethod` の `from()` テスト（`bankAccountCode`・`closingDay` がnullのケースを含む）
- `PaymentMethod` の `closingDay`／`bankAccountCode` null許容パターンのテスト
- `PaymentMethodCode.isSystemReserved()`／`isNotApplicable()`：境界値テスト（989=false, 990=true, 999=true かつ isNotApplicable=true, 991等の予約帯内だが999でない値=isSystemReserved true/isNotApplicable false）
- `ShoppingAggregateSpecification.isSatisfiedBy()`：買い物集計8項目に一致する8パターンすべてでtrue、要件定義書7.3の項目数（8件）と一致することを検証。該当しない組み合わせ（例：食費以外の項目＋無駄遣い区分）でfalseになることも確認
- `SortOrder`（共通基底クラス）：`validate()`のnull・空文字ガードのテスト（レビュー指摘③）
- `BankAccountCode`／`PaymentMethodCode`が`Identifier`の`validate()`ガード（null・空文字）を継承していることのテスト、`BankAccountSort`／`PaymentMethodSort`が`SortOrder`の`validate()`ガードを継承していることのテスト
- `SafeDomainFactory.tryCreate()`：正常値で`Optional`に値が入ること、`MyHouseholdAccountBookRuntimeException`をスローする`Supplier`に対しては例外を伝播させず`Optional.empty()`を返すことの確認（突合レビュー指摘AA）。`PaymentMethodCode.tryFrom()`／`PaymentMethodKubun.tryFrom()`：不正値で`Optional.empty()`、正常値で`Optional`に値が入ることの確認

### 7.2 UseCase統合テスト

- `BankAccountInfoManageUseCaseIntegrationTest`（`ShopInfoManageUseCaseIntegrationTest` を雛形に、新規・更新・表示順シフト・99件超エラーを検証）
- `PaymentMethodInfoManageUseCaseIntegrationTest`（同上＋「支払方法がない」行の更新拒否、一覧表示に予約行が含まれないことの確認、クレカ選択時の集計開始日必須チェック、現金・電子マネー（前払い式）選択時に銀行口座省略で登録できることの確認、口座振替・クレジットカード・デビットカード選択時に銀行口座未指定でバリデーションエラーになることの確認、**無効化した銀行口座が銀行口座選択肢に出ないこと・選択肢が表示順でソートされていることの確認（突合レビュー指摘M）**、**種別＝現金／電子マネー（前払い式）で銀行口座コード・種別＝現金／口座振替／デビットカードで集計開始日を送信しても、登録後は`null`に正規化されていることの確認（突合レビュー指摘F）**、**無効化された銀行口座を参照する既存の支払方法を更新フォームで開いた場合にdisabled表示となり値が維持されることの確認（突合レビュー指摘D）**、**表示順シフト実行後もシステム予約行（`999`）の`PAYMENT_METHOD_SORT`が変化しないことの確認（突合レビュー指摘G）**）
- `ShopInfoManageUseCaseIntegrationTest`：デフォルト支払方法の設定・任意省略パターンの追加、システム予約値を送信した場合にバリデーションエラーになることの確認（突合レビュー指摘C）
- `FixedCostRegistConfirmUseCaseIntegrationTest`：支払方法必須チェック・登録内容へのマッピング検証の追加、`execBulkUpdate()`実行後も各行の`paymentMethodCode`が変更されず維持されることの確認（レビュー指摘⑥）、**対象8項目の支出項目コード＋固定費名（例：`sisyutuItemCode=0051`・`fixedCostName`に「無駄遣いB」を含む）で`999`を送信した場合は登録できること、対象8項目以外の支出項目コードで`999`を送信した場合はバリデーションエラーになることの確認（突合レビュー指摘V）**
- `FixedCostInquiryUseCaseIntegrationTest`：`readInitInfo()`／`readActSelectItemInfo()`／`readBulkUpdateInfo()`の各レスポンスに支払方法名が含まれることの確認（レビュー指摘⑦）、`FixedCostInfoUpdateForm`の支払方法プルダウンに「支払方法がない」が選択肢として含まれることの確認（`findEnabledByUserId()`ベース。突合レビュー指摘A・5.4.1節）、無効化された支払方法を参照する既存の固定費を更新フォームで開いた場合にdisabled表示となり値が維持されることの確認（突合レビュー指摘D）
- `FixedCostMonthlyDetailUseCaseIntegrationTest`：支払方法名表示の追加確認
- `IncomeAndExpenditureInitUseCaseIntegrationTest`：固定費→収支登録の支払方法引き継ぎ検証の追加。特に、支払方法「支払方法がない」を設定した固定費（買い物集計8項目相当）から生成された`ExpenditureRegistItem`が`paymentMethodCode=999`を保持することの確認（突合レビュー指摘A）
- `IncomeAndExpenditureRegistConfirmUseCaseIntegrationTest`：EXPENDITURE_TABLEへの支払方法保存検証の追加。固定費由来の`paymentMethodCode=999`の行がそのままINSERTされることの確認（突合レビュー指摘A）
- `ExpenditureRegistUseCaseIntegrationTest`：支払方法プルダウンの選択肢にシステム予約値が含まれないことの確認、支払方法コードがシステム予約値の行（DBロード済み・固定費由来の未保存セッション行の両方）を収支登録画面で表示した場合にdisabled表示となり値が維持されることの確認（5.5節・突合レビュー指摘A）、支払方法コードが無効化済み（`ENABLE_FLG=false`）の行を訂正フォームで開いた場合も同様にdisabled表示となることの確認（突合レビュー指摘D）
- `SimpleShoppingRegistUseCaseIntegrationTest`：支払方法必須チェック・デフォルト支払方法反映（サーバー側で解決した選択肢に含まれるか）の追加、システム予約値を送信した場合にバリデーションエラーになることの確認、**買い物登録（新規・更新）による金額調整後もEXPENDITURE_TABLE行の`paymentMethodCode`（`999`を含む）が変化しないことの確認（突合レビュー指摘A。`ExpenditureItem.addSisyutuKingaku()`等のフィールドコピー漏れ検知）**、無効化された支払方法を参照する既存の買い物登録データを更新フォームで開いた場合にdisabled表示となり値が維持されることの確認（突合レビュー指摘D）
- `AccountMonthInquiryIntegrationTest`：支出別タブの支払方法・引落先口座名表示の追加、支払方法コードがシステム予約値の行が「－」表示になることの確認
- `PaymentMethodInfoComponentTest`（新規）：`createResolver()`が1回のDB呼び出し（`findByUserId()`・`BankAccountTableRepository.findById(SearchQueryUserId)`各1回）で複数コードを解決できることの確認（レビュー指摘④。モックのリポジトリ呼び出し回数を検証）

### 7.3 Controller統合テスト

- `BankAccountInfoManageControllerIntegrationTest`・`PaymentMethodInfoManageControllerIntegrationTest`（新規、`ShopInfoManageUseCaseIntegrationTest` 相当のController版があれば同様に作成）
- 既存の `ShopInfoManageControllerIntegrationTest`・固定費／収支登録／簡易買い物登録の各Controller統合テストに、支払方法必須項目のバリデーションエラーケースを追加
- **`tryFrom`ベースの`@AssertTrue`が例外ではなくバリデーションエラーを返すことの確認（突合レビュー指摘AA）**：`PaymentMethodInfoForm`・`ShopInfoForm`・`FixedCostInfoUpdateForm`・`SimpleShoppingRegistInfoForm`の4フォームそれぞれに対し、`paymentMethodKubun`／`paymentMethodCode`／`sisyutuItemCode`にコード体系上不正な値（マスタ非存在のコード等）を送信した場合に、500エラーではなく`@AssertTrue`のバリデーションエラーメッセージを伴う200応答（再表示）になることを確認する。あわせて`FixedCostInfoUpdateForm`は`fixedCostName`が未入力（空文字）の状態で`paymentMethodCode=999`を送信しても500にならず、通常の`@NotBlank`エラーとして扱われることを確認する（改訂9時点でガード節に`fixedCostName`のnullチェックが漏れていたことが原因の500エラーの再発防止）

### 7.4 テストデータ

`docs/test-data-design-rules.md` の規約に従い、`schema_test.sql` に新規2テーブルのDDLを追加し、各テストの `@Sql` データに `BANK_ACCOUNT_TABLE`／`PAYMENT_METHOD_TABLE` の最小データ（現金・支払方法がない、を含む）を用意する。既存の固定費・収支・買い物登録関連テストSQLは全て `PAYMENT_METHOD_CODE` 列の追加に伴う更新が必要になる（影響範囲が広いため、テストSQL一覧を洗い出したうえで一括更新する）。

確認事項④の回答（8章参照）のとおり、`schema_test.sql` 側の `FIXED_COST_TABLE`・`EXPENDITURE_TABLE`・`SHOPPING_REGIST_TABLE` の `PAYMENT_METHOD_CODE` は最初から `NOT NULL` で追加してよい（本番の実データ移行が完了していなくても、テスト環境は完全に独立しているため影響しない）。

---

## 8. 実装順序

1. `domain/type/common/SortOrder` 共通基底クラス新規作成（レビュー指摘③。単体テストも同時に作成）
2. `EnableFlg` 共通ドメインタイプ新規作成
3. 銀行口座マスタ：ドメイン層（`BankAccountCode`は`Identifier`継承、`BankAccountSort`は`SortOrder`継承。型・モデル・リポジトリIF）→ インフラ層（Mapper/DTO/DataSource/SQL）→ プレゼンテーション層（Form/Response/UseCase/Controller/HTML）
4. 支払方法マスタ：同上（銀行口座マスタ完了後。FK依存のため）。`PaymentMethodCode`は`Identifier`継承＋`isSystemReserved()`、`PaymentMethodSort`は`SortOrder`継承。`findByUserId()`と`findSelectableByUserId()`の使い分けを含む
5. コードテーブル追加（`CODE_DEFINES_PAYMENT_METHOD_KUBUN`定数、`codetable.csv`更新）
6. ユーザー初期化処理拡張（`AdminMenuUserInfoUseCase`）とシステム行投入確認
7. `PaymentMethodInfoComponent` 新規作成（`createResolver()`によるバッチ解決・表示変換の一元化を含む。レビュー指摘④）
8. `ShoppingRegistExpenditureItemComponent` から `ShoppingAggregateSpecification` を切り出すリファクタリング。**着手時に以下を確認すること（突合レビュー指摘K）**：(a) 3.9節の型名・定数名（`ExpenditureItemCode`／`ExpenditureCategory`／`SISYUTU_ITEM_CODE_INSYOKU_VALUE`等）は仮記載であり、既存コードベースの実際の型名・定数名（ローマ字命名の揺れを含む）と完全に一致させること、(b) `Pair`（8組の組み合わせの保持に使用）がクラスパス上で利用可能か（Apache Commons Lang3 / Spring Framework 等の依存有無）を確認し、なければ専用の小さな値オブジェクトを新設すること
9. 店舗マスタ拡張（デフォルト支払方法）
10. 固定費登録画面拡張（`FixedCostInfoUpdateForm`は`findEnabledByUserId()`ベースで「支払方法がない」を選択肢に含める・予約値拒否バリデーションは設けない。`FixedCostBulkUpdateForm`関連画面への支払方法列追加、`AbstractFixedCostItemListResponse.FixedCostItem`等4種のDTO変更。突合レビュー指摘A・レビュー指摘⑥⑦・5.4節参照）。**フィールド存在確認（突合レビュー指摘AD）**：5.4.1節の`isPaymentMethodCodeValid()`が参照する`sisyutuItemCode`（`@NotBlank`）・`fixedCostName`（`@NotBlank @Size(1,100)`）は既存の`FixedCostInfoUpdateForm`に実在することをソース確認済み（既存の`isNeedCheckShiharaiTukiOptionalContext()`等と同じフィールドセット）。設計変更は不要
11. 収支登録画面（支出側）拡張、固定費引き継ぎ対応（買い物集計8項目の`paymentMethodCode`はここで固定費から伝播する。突合レビュー指摘A）、システム予約値の訂正フォーム表示制御（disabled+hidden併記。DBロード済み行・固定費由来の未保存新規行の両方が対象。5.5節参照）
12. 買い物登録（簡易タイプ）画面拡張、デフォルト支払方法JS対応、サーバー側の予約値拒否バリデーションを含む。あわせて`ExpenditureItem.addSisyutuKingaku()`等の金額増減メソッドが`paymentMethodCode`を現在値のままコピーすることを確認・修正する（突合レビュー指摘A・3.7節参照）
13. 月別収支照会（支出別タブ）列追加
14. テスト追加・全件グリーン確認（`schema_test.sql`は`PAYMENT_METHOD_CODE`等を`NOT NULL`で追加してよい。実データの移行が未実施でもテスト環境には影響しない）
15. **（本番適用直前に実施）** 既存データ移行スクリプト作成・件数確認（確認事項④の回答のとおり、14.完了後・本番適用前に本番DBを確認してから着手する）
16. **（本番適用直前に実施）** 本番環境への `NOT NULL` 制約・外部キー制約の付与

---

## 9. 設計上の確認事項まとめ（実装着手前にユーザー確認）

| # | 内容 | 該当節 | 状態 |
|---|------|--------|------|
| ~~①~~ | ~~銀行口座マスタのシステム行「口座なし」保護方式~~ | 2.1.2 | **解消済み**：電子マネー対応で現金・電子マネー種別の銀行口座がNULL可になったため、システム行自体が不要になった |
| ~~②~~ | ~~収支登録画面の支払方法必須化は支出側のみで良いか~~ | 2.3 | **回答あり**：支出登録エリア（`IncomeAndExpenditureRegist.html`104〜183行目）のみが対象で正しい |
| ~~③~~ | ~~「支払方法がない」システム行の表示順・有効フラグの変更を許容するか~~ | 5.2 | **回答あり**：全項目更新不可、かつ支払方法マスタ管理画面の一覧にも表示しない |
| ~~④~~ | ~~既存データ移行のルールベース自動設定〜手修正の閾値・実現方式~~ | 6.1 | **回答あり**：製造・テスト完了後、本番適用前に本番DBを確認してから詳細ロジックを実装する進め方でよい |
| ~~⑤~~ | ~~収支登録（支出側）で買い物集計8項目の「支払方法がない」をどう入力させるか~~ | 5.5 | **回答あり**：全画面共通でプルダウンに予約値を表示しない。8項目の運用はユーザー側で対応（要件3.3どおり） |

---

## 10. 備考・制約事項

- フル版買い物登録（`ShoppingRegist.html`／`ShoppingRegistUseCase`）は現状未実装のスタブのため、本devのスコープ外。将来実装時は簡易タイプと同じ拡張方針を適用する。
- `codetable.csv` はGit管理外の外部ファイルのため、コード変更のみでは支払方法種別の選択肢は反映されない。local/test/prod 各環境のファイル更新を別途実施すること。
- dev2（クレカポイント減算・口座別支払確認）は本devで登録される支払方法マスタの「クレジットカード種別・集計開始日・銀行口座紐づけ」データを前提とするため、本devの完了後に着手する。
- **【dev2への申し送り・突合レビュー指摘N】口座別支払確認（要件4.3）の集計軸と、現金・電子マネー（前払い式）の銀行口座なし設計の関係**：要件4.3の口座別支払確認は「銀行名」を行の軸とする表（口座振替／クレジットカード／現金＋その他の列を持つ）だが、支払方法種別「現金」「電子マネー（前払い式）」は`BANK_ACCOUNT_CODE = NULL`（銀行口座を持たない）ため、これらの支出はどの銀行の行にも属さない。初版では「支払方法がない」行が参照するダミー銀行口座（`BANK_ACCOUNT_CODE="999"`＝「口座なし」システム行）を検討していたが、電子マネー対応（確認事項①の解消）でこの受け皿は廃止した。**本devでは銀行口座マスタにダミーのシステム行を追加する方式は採用しない**（この判断は変更しない）。dev2の設計時に、現金・電子マネーの支出を「（口座なし）」等の独立行として表示するか、口座別集計の対象外（別枠で合計表示等）とするかを決定すること。要件4.3の表自体には現金・電子マネーの受け皿が明記されていない点も、dev2の設計時に要件定義書側の記述を補うことが望ましい。
- dev5で「支払方法がない」固定値を削除する計画があるため、本devでは `ENABLE_UPDATE_FLG` による保護に留め、削除不可を強制する実装（DBトリガー等）までは行わない。dev5の設計指針は `docs/feature1.03/Claude Codeへの作業インプット/Feature1_03_dev5_集計8項目見直し_設計指針.md` に存在するが、**本devのスコープ外のため本書には反映しない**。dev5着手時に読み込むこと（要件定義書7章にも参照ポインタあり）。
- 電子マネー（前払い式）はチャージ金額・残高を本アプリの管理対象外とする（[電子マネー対応の設計インプット](Claude Codeへの作業インプット/Feature1.03_dev1_payment-method-emoney-design-input.md) 2.1）。将来チャージ・残高管理を導入する場合は、支払方法種別に属性を追加するのではなく、資産アカウント（資金移動）を独立した集約として切り出す方針とする（同ドキュメント5節）。
- 用語の定義は [ドメイン用語集](../ドメイン用語集.md) を参照。
- **【dev5への申し送り・突合レビュー指摘AC】`FIXED_COST_NAME`文字列マッチへの依存が新たに増えた**：`FIXED_COST_TABLE`に支出区分（無駄遣い区分）専用カラムが存在しないという既存の制約（指摘W）自体はFeature1.03以前から存在するが、本devではこの制約を前提にした`ExpenditureCategory.from(FixedCostName)`（固定費名の文字列マッチ）への依存箇所が新たに2つ増えた：(1) 5.4.1節`FixedCostInfoUpdateForm.isPaymentMethodCodeValid()`のバリデーション判定、(2) 6.1節ステップ3の既存データ移行時の対象8項目確定（ただしSQL側では文字列マッチを再現せず候補抽出→目視確認→コード明示列挙とした。突合レビュー指摘AB参照）。dev5で対象8項目のデータ構造を根本見直しする際は、`FIXED_COST_TABLE`に支出区分の専用カラムを追加し、これら2箇所の文字列マッチ依存を解消することを推奨する。同内容を[支払方法固定値の設計指針](Claude Codeへの作業インプット/Feature1_03_dev1_支払方法固定値_設計指針.md) 4章にも追記済み。なお、この文字列マッチ依存によりdev1リリース後・dev5着手前の期間に実際に起こりうる運用上の注意点（固定費名変更で集計先が画面エラーなく変わりうること）は、dev5への申し送りとは性質が別のためここではなく5.4.1節に記載した（突合レビュー指摘AG）。

---

## 11. dev1完了時チェックリスト（支払方法固定値関連）

[支払方法固定値の設計指針](Claude Codeへの作業インプット/Feature1_03_dev1_支払方法固定値_設計指針.md) 5節のチェックリストをベースに、dev1設計時に生じた例外・補足（指摘U・V・H）を反映したものを以下に示す。**dev1完了時の実際のチェックは本節を正とする**（設計指針5節側は着手時点の方針の記録として残し、以後更新しない。突合レビュー指摘AF）。

- [ ] `999`（および予約帯990〜999を表す値）の直書きがコード中に存在しない（`PaymentMethodCode.isSystemReserved()`／`isNotApplicable()` 経由で判定している）
- [ ] 画面のプルダウンに「支払方法がない」が表示される箇所がない（収支登録・買い物登録・店舗マスタのデフォルト支払方法選択で確認。**固定費登録画面のみ意図的に選択肢へ含める**（5.4.1節・突合レビュー指摘A）ため対象外。突合レビュー指摘U対応）。収支登録（支出側）・固定費登録・買い物登録・支払方法マスタ管理（銀行口座選択）の各訂正/更新フォームで、現在値が選択肢に含まれない行（システム予約値、または無効化された支払方法・銀行口座を参照している場合）はdisabled表示になっており、通常のプルダウンとしては表示されないことも確認する（5.2・5.4.1・5.5・5.6節、突合レビュー指摘D参照）
- [ ] リクエスト改変で `999`（または990〜998などの予約帯内の値）を送信した場合に、収支登録・買い物登録・**店舗マスタ管理（デフォルト支払方法）**の各画面でバリデーションエラーになる（突合レビュー指摘C反映）。**固定費登録画面は対象8項目の固定費に限り`999`を許容する条件付きバリデーションのため、対象8項目以外の支出項目コードで`999`を送信した場合はバリデーションエラーになることを確認する**（突合レビュー指摘V反映）
- [ ] 支払方法コードのシステム予約帯（990〜999）の判定が1箇所（`PaymentMethodCode`）に集約されている（**突合レビュー指摘H**：店舗マスタの予約帯は900〜999だが、支払方法マスタは登録数が少ないため990〜999と意図的に開始番号を変えている。「同一ルール」とは"コード帯を予約してシステム行を表現する"という**パターン**が同じという意味であり、数値そのものが同じという意味ではない）
- [ ] 対象8項目の判定（`ShoppingAggregateSpecification`）が1箇所に集約され、要件定義書7.3と件数（8件）が一致している
- [ ] 表示変換 `999 → －` が `PaymentMethodInfoComponent` の2メソッドに集約されている
- [ ] 既存データ変換スクリプトの実行順序（支払方法マスタへのシステム予約行INSERT → 支出・収支データのUPDATE）がFK制約を満たしている
