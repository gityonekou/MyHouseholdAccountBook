-- 家計簿利用ユーザ:ACCOUNT_BOOK_USER ★本番テーブル定義登録済み★
-- DROP TABLE IF EXISTS ACCOUNT_BOOK_USER;
CREATE TABLE IF NOT EXISTS ACCOUNT_BOOK_USER (
	-- ユーザID
	USER_ID         VARCHAR(50),
	-- 現在の対象年
	NOW_TARGET_YEAR CHAR(4),
	-- 現在の対象月
	NOW_TARGET_MONTH     CHAR(2),
	-- ユーザ名
	USER_NAME       VARCHAR(100) NOT NULL,
	-- 複合プライマリキー
	PRIMARY KEY(USER_ID, NOW_TARGET_YEAR, NOW_TARGET_MONTH)
);

-- 支出項目テーブル:SISYUTU_ITEM_TABLE ★本番テーブル定義登録済み★
-- DROP TABLE IF EXISTS SISYUTU_ITEM_TABLE;
CREATE TABLE IF NOT EXISTS SISYUTU_ITEM_TABLE (
	-- ユーザID
	USER_ID         VARCHAR(50),
	-- 支出項目コード
	SISYUTU_ITEM_CODE    CHAR(4),
	-- 支出項目名
	SISYUTU_ITEM_NAME    VARCHAR(15)  NOT NULL,
	-- 支出項目詳細内容
	SISYUTU_ITEM_DETAIL_CONTEXT    VARCHAR(200),
	-- 親支出項目コード
	PARENT_SISYUTU_ITEM_CODE  CHAR(4)  NOT NULL,
	-- 支出項目レベル(1～5)
	SISYUTU_ITEM_LEVEL  CHAR(1) NOT NULL,
	-- 支出項目表示順
	SISYUTU_ITEM_SORT    CHAR(10) NOT NULL,
	-- 更新可否フラグ
	ENABLE_UPDATE_FLG    BOOLEAN NOT NULL,
	-- 複合プライマリキー
	PRIMARY KEY(USER_ID, SISYUTU_ITEM_CODE)
);

-- 支出項目テーブル(BASE):SISYUTU_ITEM_BASE_TABLE ★本番テーブル定義登録済み★
-- DROP TABLE IF EXISTS SISYUTU_ITEM_BASE_TABLE;
CREATE TABLE IF NOT EXISTS SISYUTU_ITEM_BASE_TABLE (
	-- 支出項目コード
	SISYUTU_ITEM_CODE    CHAR(4) PRIMARY KEY,
	-- 支出項目名
	SISYUTU_ITEM_NAME    VARCHAR(15)  NOT NULL,
	-- 支出項目詳細内容
	SISYUTU_ITEM_DETAIL_CONTEXT    VARCHAR(200),
	-- 親支出項目コード
	PARENT_SISYUTU_ITEM_CODE  CHAR(4)  NOT NULL,
	-- 支出項目レベル(1～5)
	SISYUTU_ITEM_LEVEL  CHAR(1) NOT NULL,
	-- 支出項目表示順
	SISYUTU_ITEM_SORT    CHAR(10) NOT NULL
);

-- 支出金額テーブル：SISYUTU_KINGAKU_TABLE ★本番テーブル定義登録済み★
-- DROP TABLE IF EXISTS SISYUTU_KINGAKU_TABLE;
CREATE TABLE IF NOT EXISTS SISYUTU_KINGAKU_TABLE (
	-- ユーザID
	USER_ID         VARCHAR(50),
	-- 対象年
	TARGET_YEAR     CHAR(4),
	-- 対象月
	TARGET_MONTH    CHAR(2),
	-- 支出項目コード
	SISYUTU_ITEM_CODE    CHAR(4),
	-- 支出金額
	SISYUTU_KINGAKU DECIMAL(12, 2) NOT NULL,
	-- 支出金額B
	SISYUTU_KINGAKU_B    DECIMAL(12, 2) NOT NULL,
	-- 支払日
	SIHARAI_DATE    DATE,
	-- 支払い済みフラグ
	CLOSING_FLG  BOOLEAN,
	
	-- 複合プライマリキー
	PRIMARY KEY(USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE)
	-- 外部キー:支出項目コード
	-- INDEX SISYUTU_ITEM_CODE_INDEX(SISYUTU_ITEM_CODE),
	-- CONSTRAINT FK_SISYUTU_ITEM_CODE FOREIGN KEY(USER_ID, SISYUTU_ITEM_CODE) REFERENCES SISYUTU_ITEM_TABLE(USER_ID, SISYUTU_ITEM_CODE)
);
-- 支出金額テーブルインデックス ★本番テーブル定義登録済み★
-- CREATE INDEX SISYUTU_KINGAKU_TABLE_USER_YEAR ON SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR);
-- CREATE INDEX SISYUTU_KINGAKU_TABLE_USER_YEAR_MONTH ON SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH);

-- 収支テーブル：INCOME_AND_EXPENSE_TABLE ★本番テーブル定義登録済み★
-- DROP TABLE IF EXISTS INCOME_AND_EXPENSE_TABLE;
CREATE TABLE IF NOT EXISTS INCOME_AND_EXPENSE_TABLE (
	-- ユーザID
	USER_ID         VARCHAR(50),
	-- 対象年
	TARGET_YEAR     CHAR(4),
	-- 対象月
	TARGET_MONTH    CHAR(2),
	-- 収入金額
	INCOME_KINGAKU  DECIMAL(12, 2) NOT NULL,
	-- 支出金額
	EXPENSE_KINGAKU DECIMAL(12, 2) NOT NULL,
	-- 支出予定金額
	EXPENSE_YOTEI_KINGAKU     DECIMAL(12, 2) NOT NULL,
	-- 収支
	SYUUSI_KINGAKU  DECIMAL(12, 2) NOT NULL,
	-- 衣類住居設備予定金額
	IRUI_JYUUKYO_YOTEI_KINGAKU  DECIMAL(9, 2) NOT NULL,
	-- 飲食日用品予定金額
	INSYOKU_NITIYOUHIN_YOTEI_KINGAKU    DECIMAL(9, 2) NOT NULL,
	-- 趣味娯楽予定金額
	SYUMI_GOTAKU_YOTEI_KINGAKU  DECIMAL(9, 2) NOT NULL,
	
	-- 複合プライマリキー
	PRIMARY KEY(USER_ID, TARGET_YEAR, TARGET_MONTH)
);
-- 収支テーブルインデックス
-- 件数は1ユーザ年12件にしかならないので、このテーブルではインデックス不要(フルスキャンOK)

-- 店舗テーブル:SHOP_TABLE ★本番テーブル定義登録済み★
-- DROP TABLE IF EXISTS SHOP_TABLE;
CREATE TABLE IF NOT EXISTS SHOP_TABLE (
	-- ユーザID
	USER_ID      VARCHAR(50),
	-- 店舗コード
	SHOP_CODE    CHAR(3),
	-- 店舗区分コード
	SHOP_KUBUN_CODE CHAR(3) NOT NULL,
	-- 店舗名
	SHOP_NAME    VARCHAR(50) NOT NULL,
	-- 店舗表示順
	SHOP_SORT    CHAR(3) NOT NULL,
	-- 複合プライマリキー
	PRIMARY KEY(USER_ID, SHOP_CODE)
);

-- 店舗テーブル(BASE):SHOP_BASE_TABLE ★本番テーブル定義登録済み★
-- DROP TABLE IF EXISTS SHOP_BASE_TABLE;
CREATE TABLE IF NOT EXISTS SHOP_BASE_TABLE (
	-- 店舗コード
	SHOP_CODE    CHAR(3) PRIMARY KEY,
	-- 店舗名
	SHOP_NAME    VARCHAR(50)  NOT NULL
);


-- 商品テーブル:SHOPPING_ITEM_TABLE ★本番テーブル定義登録済み★
-- DROP TABLE IF EXISTS SHOPPING_ITEM_TABLE;
CREATE TABLE IF NOT EXISTS SHOPPING_ITEM_TABLE (
	-- ユーザID
	USER_ID         VARCHAR(50),
	-- 商品コード
	SHOPPING_ITEM_CODE   CHAR(5),
	-- 商品区分名
	SHOPPING_ITEM_KUBUN_NAME  VARCHAR(30) NOT NULL,
	-- 商品名
	SHOPPING_ITEM_NAME   VARCHAR(100) NOT NULL,
	-- 商品詳細
	SHOPPING_ITEM_DETAIL_CONTEXT   VARCHAR(300),
	-- 商品JANコード(13桁 or 8桁 or ISBNコード:10桁)
	SHOPPING_ITEM_JAN_CODE    VARCHAR(13) NOT NULL,
	-- 支出項目コード(商品が属する支出項目コード)
	SISYUTU_ITEM_CODE    CHAR(4) NOT NULL,
	-- 会社名
	COMPANY_NAME    VARCHAR(100) NOT NULL,
	-- 基準店舗コード
	STANDARD_SHOP_CODE   CHAR(3),
	-- 基準価格
	STANDARD_PRICE  DECIMAL(8, 2),
	-- 内容量
	-- CAPACITY   SMALLINT UNSIGNED,
	CAPACITY   SMALLINT,
	-- 内容量単位
	CAPACITY_UNIT   CHAR(2),
	-- カロリー
	-- CALORIES   MEDIUMINT UNSIGNED,
	CALORIES   MEDIUMINT,
	
	-- 複合プライマリキー
	PRIMARY KEY(USER_ID, SHOPPING_ITEM_CODE)
	
	-- 外部キー:支出項目コード
	-- INDEX SHOPPING_ITEM_SISYUTU_ITEM_CODE_INDEX(SISYUTU_ITEM_CODE),
	-- CONSTRAINT FK_SHOPPING_ITEM_SISYUTU_ITEM_CODE FOREIGN KEY(USER_ID, SISYUTU_ITEM_CODE) REFERENCES SISYUTU_ITEM_TABLE(USER_ID, SISYUTU_ITEM_CODE)
);


-- 固定費テーブル:FIXED_COST_TABLE ★登録未
-- DROP TABLE IF EXISTS FIXED_COST_TABLE;
CREATE TABLE IF NOT EXISTS FIXED_COST_TABLE (
	-- ユーザID
	USER_ID         VARCHAR(50),
	-- 固定費コード
	FIXED_COST_CODE CHAR(4),
	-- 固定費名
	FIXED_COST_NAME VARCHAR(100) NOT NULL,
	-- 固定費内容詳細
	FIXED_COST_DETAIL_CONTEXT VARCHAR(300),
	-- 支出項目コード(固定費が属する支出項目コード)
	SISYUTU_ITEM_CODE    CHAR(4) NOT NULL,
	-- 固定費区分
	FIXED_COST_KUBUN     CHAR(1) NOT NULL,
	-- 固定費支払月
	FIXED_COST_SHIHARAI_TUKI  CHAR(2) NOT NULL,
	-- 固定費支払月任意詳細
	FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT     VARCHAR(300),
	-- 固定費支払日
	FIXED_COST_SHIHARAI_DAY   CHAR(2) NOT NULL,
	-- 支払金額
	SHIHARAI_KINGAKU     DECIMAL(8, 2) NOT NULL,
	-- 削除フラグ
	DELETE_FLG BOOLEAN,
	
	-- 複合プライマリキー
	PRIMARY KEY(USER_ID, FIXED_COST_CODE)
	
	-- 外部キー:支出項目コード
	-- INDEX FIXED_COST_SISYUTU_ITEM_CODE_INDEX(SISYUTU_ITEM_CODE),
	-- CONSTRAINT FK_FIXED_COST_SISYUTU_ITEM_CODE FOREIGN KEY(USER_ID, SISYUTU_ITEM_CODE) REFERENCES SISYUTU_ITEM_TABLE(USER_ID, SISYUTU_ITEM_CODE)
);

-- イベントテーブル:EVENT_ITEM_TABLE ★登録未
-- DROP TABLE IF EXISTS EVENT_ITEM_TABLE;
CREATE TABLE IF NOT EXISTS EVENT_ITEM_TABLE (
	-- ユーザID
	USER_ID    VARCHAR(50),
	-- イベントコード
	EVENT_CODE CHAR(4),
	-- 支出項目コード(イベントが属する支出項目コード)
	SISYUTU_ITEM_CODE    CHAR(4) NOT NULL,
	-- イベント名
	EVENT_NAME VARCHAR(100) NOT NULL,
	-- イベント内容詳細
	EVENT_DETAIL_CONTEXT VARCHAR(300),
	-- イベント開始日
	EVENT_START_DATE     DATE NOT NULL,
	-- イベント終了日
	EVENT_END_DATE  DATE NOT NULL,
	-- 削除フラグ
	EVENT_EXIT_FLG  BOOLEAN NOT NULL,
	
	-- 複合プライマリキー
	PRIMARY KEY(USER_ID, EVENT_CODE)
);
