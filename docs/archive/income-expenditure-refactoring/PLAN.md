# Phase5 Step2-4, Step2-5 実装計画

## 概要

ExpenditureItemSelectIntegrationTest と IncomeAndExpenditureRegistConfirmIntegrationTest の2テストクラスを作成します。

---

## 1. テスト確認観点の整理

### 既存テスト（Step2-1〜2-3）で確立した観点の反映

| 確認観点 | Step2-4 反映 | Step2-5 反映 |
|----------|-------------|-------------|
| 支出項目名の＞区切り階層表示 | ○ readExpenditureItemActSelectで検証 | - |
| イベントコード・イベント名の確認 | ○ イベント系支出項目選択で検証 | - |
| actionType (NON_UPDATE/ADD/UPDATE/DELETE)の正確な設定 | - | ○ execRegistActionで全パターン検証 |
| DELETE時のセッション上の扱い | - | ○ DB論理削除の確認 |
| 自動生成コードの桁数確認 | - | ○ 収入コード・支出コードの連番発番確認 |
| 金額フォーマット（円マーク、カンマ区切り） | - | ○ 確認画面の一覧表示で検証 |
| transactionSuccessFull設定 | - | ○ キャンセル・登録完了で検証 |
| 具体値による検証（assertNotNull不可） | ○ | ○ |

### Step2-5で新たに必要な観点

| 確認観点 | 内容 |
|----------|------|
| 4テーブルCRUD | INCOME_TABLE, EXPENDITURE_TABLE, SISYUTU_KINGAKU_TABLE, INCOME_AND_EXPENDITURE_TABLE |
| 金額計算の整合性 | 収入合計、支出合計、収支差額の正確性 |
| 支出金額テーブルの親階層更新 | SisyutuKingakuItemHolderによる親コードへの金額伝搬 |
| DELETE_FLG論理削除 | 収入・支出テーブルの論理削除の確認 |
| 必須支出項目チェック | 買い物登録用の8項目チェック（0051×3, 0052, 0050, 0046, 0007, 0047） |
| initFlg判定 | 新規登録(true)と更新(false)で支出予定金額の扱いが変わる |

※テスト観点ガイドラインの別ファイル化は不要と判断。上記観点は計画書のテストケース表に反映済みで網羅できる。Step2完了後に必要があれば改めて検討。

---

## 2. ExpenditureItemSelectIntegrationTest（Step2-4）

### 対象メソッド
1. `readExpenditureAddSelect(LoginUserInfo)` - 支出項目一覧取得
2. `readExpenditureItemActSelect(LoginUserInfo, String)` - 支出項目選択後の詳細取得

### テストケース詳細設計（5個）

| # | テストケース名 | 対象メソッド | テスト観点 |
|---|--------------|------------|----------|
| 1 | 正常系：支出項目選択画面初期表示_支出項目一覧確認 | readExpenditureAddSelect | 支出項目一覧が階層構造で取得される（61件）、メッセージなし |
| 2 | 正常系：支出項目選択_非イベント系Level3項目 | readExpenditureItemActSelect | 電気代(0037)選択→sisyutuItemName="固定費(課税)＞水光熱通費＞電気代"、イベント選択なし |
| 3 | 正常系：支出項目選択_イベント系支出項目 | readExpenditureItemActSelect | コミケ(0061)選択→sisyutuItemName="趣味娯楽＞イベント費＞コミケ"、eventCodeRequired=true、eventSelectList存在、eventCode先頭値設定 |
| 4 | 正常系：支出項目選択_Level2項目 | readExpenditureItemActSelect | 水光熱通費(0004)選択→sisyutuItemName="固定費(課税)＞水光熱通費"、＞区切り2階層確認 |
| 5 | 正常系：支出項目選択_Level1項目 | readExpenditureItemActSelect | 固定費(税金)(0001)選択→sisyutuItemName="固定費(税金)"、＞区切りなし確認 |

### SQLファイル
ExpenditureRegistIntegrationTest.sqlと同等のデータを使用（0001-0061 + EVENT_ITEM_TABLE）。

### 実装方針
- ExpenditureRegistIntegrationTest.sqlをコピーしてExpenditureItemSelectIntegrationTest.sqlとする
- レスポンスの検証はbuild()ではなくフィールド直接検証（ExpenditureItemSelectResponseのgetter/setter経由）
- 支出項目一覧の件数・内容確認はgetSisyutuItemInfoList的なアクセスが不可能な場合、build()→ModelAndViewから取得

---

## 3. IncomeAndExpenditureRegistConfirmIntegrationTest（Step2-5）

### 対象メソッド
1. `readRegistCheckInfo` - 登録内容確認画面表示
2. `readRegistCancelInfo` - 登録キャンセル
3. `execRegistAction` - 収支情報DB登録

### テストケース詳細設計（8個）

| # | テストケース名 | 対象メソッド | テスト観点 |
|---|--------------|------------|----------|
| 1 | 正常系：確認画面表示_収入支出一覧・削除アクション除外確認 | readRegistCheckInfo | 削除アクション設定データが除外される、表示用収入一覧・支出一覧・合計金額の確認 |
| 2 | 正常系：キャンセル_メッセージとリダイレクト設定確認 | readRegistCancelInfo | "YYYY年MM月度の収支登録をキャンセルしました。"メッセージ、transactionSuccessFull設定 |
| 3 | 異常系：キャンセル_不正な対象年月 | readRegistCancelInfo | 不正な年月(例:"abc")指定時の例外確認 |
| 4 | 正常系：新規登録_全テーブルINSERT確認 | execRegistAction | 新規月(202512)への登録、INCOME/EXPENDITURE/SISYUTU_KINGAKU/INCOME_AND_EXPENDITURE全テーブルへのINSERT、金額計算の整合性確認 |
| 5 | 正常系：更新_混合アクション（NON_UPDATE/UPDATE/ADD/DELETE） | execRegistAction | 既存月(202511)の更新、収入・支出の各アクション処理、INCOME_AND_EXPENDITURE_TABLEの金額再計算確認 |
| 6 | 正常系：変更なし_全件NON_UPDATE | execRegistAction | 全セッションデータがNON_UPDATE、"変更箇所がありませんでした"メッセージ確認 |
| 7 | 異常系：収入リスト空エラー | execRegistAction | 空リスト渡し時の例外確認 |
| 8 | 異常系：収入リストnullエラー | execRegistAction | null渡し時の例外確認 |

### テスト4（新規登録）のデータ設計

**セッション収入リスト（2件）**:
- NEW/ADD: kubun=1(給与), amount=350,000
- NEW/ADD: kubun=2(副業), amount=30,000

**セッション支出リスト（8件）** - 必須8項目を網羅:
- NEW/ADD: itemCode=0051, kubun=1(無駄遣いなし), 飲食, amount=10,000
- NEW/ADD: itemCode=0051, kubun=2(無駄遣いB), 飲食B, amount=2,000
- NEW/ADD: itemCode=0051, kubun=3(無駄遣いC), 飲食C, amount=1,000
- NEW/ADD: itemCode=0052, kubun=1, 一人プチ贅沢・外食, amount=5,000
- NEW/ADD: itemCode=0050, kubun=1, 日用消耗品, amount=3,000
- NEW/ADD: itemCode=0046, kubun=1, 被服費, amount=5,000
- NEW/ADD: itemCode=0007, kubun=1, 流動経費, amount=10,000
- NEW/ADD: itemCode=0047, kubun=1, 住居設備, amount=2,000

**検証**:
- INCOME_TABLE: 2件INSERT
- EXPENDITURE_TABLE: 8件INSERT
- SISYUTU_KINGAKU_TABLE: 支出項目+親項目分の件数確認
- INCOME_AND_EXPENDITURE_TABLE: 1件INSERT、金額整合性

### テスト5（更新）のデータ設計

**前提DBデータ（InitIntegrationTest.sqlの202511データを利用）**:
- INCOME_TABLE: code=01(350,000), code=02(30,000)
- EXPENDITURE_TABLE: code=001(item=0037, 12,000), code=002(item=0038, 10,000), code=003(item=0040, 8,000)
- 対応するSISYUTU_KINGAKU_TABLE、INCOME_AND_EXPENDITURE_TABLE

**セッション収入リスト**:
- LOAD/NON_UPDATE: code=01, kubun=1, 350,000（変更なし）
- LOAD/UPDATE: code=02, kubun=2, 50,000（30,000→50,000に更新）
- NEW/ADD: kubun=1, 臨時収入, 20,000（新規追加）

**セッション支出リスト**:
- LOAD/NON_UPDATE: code=001, item=0037, 12,000（変更なし）
- LOAD/UPDATE: code=002, item=0038, 15,000（10,000→15,000に更新）
- LOAD/DELETE: code=003, item=0040, 8,000（削除）
- NEW/ADD: item=0051, kubun=1, 5,000（新規追加）
...（買い物登録チェックを通過するため、残り必須項目も追加）

**注意**: 更新テストではcheckExpenditureAndSisyutuKingakuのチェックを通過する必要があるため、必須8項目すべてがEXPENDITURE_TABLEとSISYUTU_KINGAKU_TABLEに存在する必要がある。SQLデータとセッションデータの組み合わせで8項目を確保する。

### SQLファイル
- IncomeAndExpenditureInitIntegrationTest.sqlをベースに拡張
- 新規登録テスト用：user01/202512月のデータは未登録（空）
- 更新テスト用：user01/202511月の既存データを使用
- SISYUTU_KINGAKU_TABLEの既存データも必要（更新テスト用）

### DB検証方法
- @Autowiredしたリポジトリ（IncomeTableRepository, ExpenditureTableRepository等）でfindBy/countByメソッドを使用
- @Transactional内なのでテスト中のDB変更は検証可能（テスト後に自動ロールバック）

---

## 4. 実装順序

| 順序 | 作業 | 内容 |
|------|------|------|
| 1 | Phase5_Step2_結合テスト作成計画書.md更新 | 4.4, 4.5のテストケース表を詳細化 |
| 2 | ExpenditureItemSelectIntegrationTest.sql作成 | ExpenditureRegistIntegrationTest.sqlベースで作成 |
| 3 | ExpenditureItemSelectIntegrationTest.java作成 | 5テストケース実装 |
| 4 | テスト実行・確認（Step2-4） | 全テストGREEN |
| 5 | IncomeAndExpenditureRegistConfirmIntegrationTest.sql作成 | InitIntegrationTest.sqlベースで拡張 |
| 6 | IncomeAndExpenditureRegistConfirmIntegrationTest.java作成 | 8テストケース実装 |
| 7 | テスト実行・確認（Step2-5） | 全テストGREEN |
| 8 | 全体テスト実行 | 既存36テスト + 新規13テスト = 49テスト全GREEN |

---

## 5. テスト観点ガイドラインについて

現状の `Phase5_Step2_結合テスト作成計画書.md` のテストケース表＋備考欄で、テスト確認観点は十分網羅できていると判断。

理由:
- 各テストクラスの「備考」欄に詳細化した観点を記録済み（レビュー指摘対応含む）
- テストケース名に具体的な確認ポイントを含めている
- 共通パターン（セッション操作、DB検証、階層表示等）は既存テストコードがテンプレートとして機能

**Phase5 Step2完了後に改めて評価** し、他機能の結合テストに再利用する際に不足があれば、ガイドラインドキュメントを作成する。
