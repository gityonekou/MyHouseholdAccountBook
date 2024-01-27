-- ログインユーザ
INSERT INTO USERS (USERNAME, PASSWORD, ENABLED) VALUES
('admin', '{bcrypt}$2a$10$vC.r53zKYPwEXplBYH3mxuZP52r2u3udRcEg9yTUmwYE5yjmoUXyG', true);
INSERT INTO USERS (USERNAME, PASSWORD, ENABLED) VALUES
('user', '{bcrypt}$2a$08$DD5k5hs1PVj0ZNfrq9bFKe3OVSE9cmYsUCuRUL5vFRVxh/6o.COEW', true);
INSERT INTO USERS (USERNAME, PASSWORD, ENABLED) VALUES
('kouki', '{bcrypt}$2a$08$tGkj2w0.K.Iuz3P16S28uO8L.oIjGBD9fyS7/xID3uJU4tnIEarEe', true);
INSERT INTO AUTHORITIES (USERNAME, AUTHORITY) VALUES ('admin', 'ROLE_ADMIN');
INSERT INTO AUTHORITIES (USERNAME, AUTHORITY) VALUES ('admin', 'ROLE_USER');
INSERT INTO AUTHORITIES (USERNAME, AUTHORITY) VALUES ('user', 'ROLE_USER');
INSERT INTO AUTHORITIES (USERNAME, AUTHORITY) VALUES ('kouki', 'ROLE_ADMIN');
INSERT INTO AUTHORITIES (USERNAME, AUTHORITY) VALUES ('kouki', 'ROLE_USER');
-- update USERS set ENABLED = false where USERNAME='admin';
--  家計簿利用ユーザ:ACCOUNT_BOOK_USER
--  INSERT INTO ACCOUNT_BOOK_USER (USER_ID, USER_NAME, NOW_TARGET_YEAR, NOW_TARGET_MONTH) VALUES ('admin', 'アドミンユーザさん', '2023', '09');
INSERT INTO ACCOUNT_BOOK_USER (USER_ID, NOW_TARGET_YEAR, NOW_TARGET_MONTH, USER_NAME) VALUES ('user', '2023', '08', 'テストユーザさん');
INSERT INTO ACCOUNT_BOOK_USER (USER_ID, NOW_TARGET_YEAR, NOW_TARGET_MONTH, USER_NAME) VALUES ('kouki', '2023', '09', '米谷 幸城さん');
-- 支出項目テーブル:SISYUTU_ITEM_TABLE
-- 事業経費
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT) 
	VALUES ('kouki', '0100000000', '事業経費', '事業経費詳細を入力', '0100000000', '1','0100000000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0101000000', '固定経費', '固定経費詳細を入力', '0100000000', '2','0101000000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0101010000', '駐輪場代金', '駐輪場代金詳細を入力', '0101000000', '3','0101010000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0101020000', '電車定期券', '電車定期券詳細を入力', '0101000000', '3','0101020000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0101030000', 'レンタル代金', 'レンタル代金詳細を入力', '0101000000', '3','0101030000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0101040000', '勤務表PDF', '勤務表PDF詳細を入力', '0101000000', '3','0101040000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0102000000', '流動経費', '流動経費詳細を入力', '0100000000', '2','0102000000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0102010000', '文房具', '文房具詳細を入力', '0102000000', '3','0102010000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0102020000', '事業設備', '事業設備詳細を入力', '0102000000', '3','0102020000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0102030000', 'その他', 'その他詳細を入力', '0102000000', '3','0102030000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0103000000', '税金支払い', '税金支払い詳細を入力', '0100000000', '2','0103000000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0103010000', '消費税', '消費税詳細を入力', '0103000000', '2','0103010000');
-- 固定費(非課税)
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0200000000', '固定費(非課税)', '固定費(非課税)詳細を入力', '0200000000', '1','0200000000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0201000000', '社会保険', '社会保険詳細を入力', '0200000000', '2','0201000000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0201010000', '国民年金保険', '国民年金保険詳細を入力', '0201000000', '3','0201010000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0201020000', '健康保険', '健康保険詳細を入力', '0201000000', '3','0201020000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0202000000', 'イデコ', 'イデコ詳細を入力', '0200000000', '2','0202000000');
-- 固定費(課税)
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0300000000', '固定費(課税)', '固定費(課税)詳細を入力', '0300000000', '1','0300000000');
-- 衣類住居設備
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0400000000', '衣類住居設備', '衣類住居設備詳細を入力', '0400000000', '1','0400000000');
-- 飲食日用品
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0500000000', '飲食日用品', '飲食日用品詳細を入力', '0500000000', '1','0500000000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0501000000', '美容費', '美容費詳細を入力', '0500000000', '2','0501000000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0502000000', '日用消耗品', '日用消耗品詳細を入力', '0500000000', '2','0502000000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0503000000', '食費', '食費詳細を入力', '0500000000', '2','0503000000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0504000000', '一人プチ贅沢・外食', '一人プチ贅沢・外食詳細を入力 友人・家族との外食は交際費へ', '0500000000', '2','0504000000');
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0505000000', '雑貨', '雑貨詳細を入力', '0500000000', '2','0505000000');
	
-- 趣味娯楽
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT)
	VALUES ('kouki', '0600000000', '趣味娯楽', '趣味娯楽詳細を入力', '0600000000', '1','0600000000');

-- 支出金額テーブル：SISYUTU_KINGAKU_TABLE
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0100000000', 36500.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0101000000', 26500.00, 0.00, '20230902', true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0102000000', 10000.00, 0.00, '20230903', true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0500000000', 49170.00, 10840.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0501000000', 1890.00, 0.00, '20230905', false);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0502000000', 5380.00, 350.00, '20230906', false);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0503000000', 35100.00, 3690.00, '20230907', true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0504000000', 6800.00, 6800.00, '20230908', true);

-- ------------test data add
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0200000000', 2000.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0201000000', 2010.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '08', '0200000000', 2100.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '08', '0201000000', 2110.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0300000000', 3000.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '08', '0300000000', 3100.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0400000000', 4000.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '08', '0400000000', 4100.00, 3000.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '08', '0502000000', 5380.00, 1350.00, '20230906', false);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0600000000', 6000.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '08', '0600000000', 6100.00, 0.00, null, true);
	
-- 収支テーブル：INCOME_AND_EXPENSE_TABLE
INSERT INTO INCOME_AND_EXPENSE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_KINGAKU, EXPENSE_KINGAKU,EXPENSE_YOTEI_KINGAKU, SYUUSI_KINGAKU, IRUI_JYUUKYO_YOTEI_KINGAKU,INSYOKU_NITIYOUHIN_YOTEI_KINGAKU,SYUMI_GOTAKU_YOTEI_KINGAKU)
	VALUES ('kouki', '2023', '07', 363000.00, 315800.00, 295800.00, 47200.00, 0.00, 0.00, 0.00);
INSERT INTO INCOME_AND_EXPENSE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_KINGAKU, EXPENSE_KINGAKU,EXPENSE_YOTEI_KINGAKU, SYUUSI_KINGAKU, IRUI_JYUUKYO_YOTEI_KINGAKU,INSYOKU_NITIYOUHIN_YOTEI_KINGAKU,SYUMI_GOTAKU_YOTEI_KINGAKU)
	VALUES ('kouki', '2023', '08', 363000.00, 365800.00, 335800.00, -2800.00, 0.00, 0.00, 0.00);
INSERT INTO INCOME_AND_EXPENSE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_KINGAKU, EXPENSE_KINGAKU,EXPENSE_YOTEI_KINGAKU, SYUUSI_KINGAKU, IRUI_JYUUKYO_YOTEI_KINGAKU,INSYOKU_NITIYOUHIN_YOTEI_KINGAKU,SYUMI_GOTAKU_YOTEI_KINGAKU)
	VALUES ('kouki', '2023', '09', 363000.00, 285300.00, 295800.00, 77700.00, 0.00, 0.00, 0.00);
INSERT INTO INCOME_AND_EXPENSE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_KINGAKU, EXPENSE_KINGAKU,EXPENSE_YOTEI_KINGAKU, SYUUSI_KINGAKU, IRUI_JYUUKYO_YOTEI_KINGAKU,INSYOKU_NITIYOUHIN_YOTEI_KINGAKU,SYUMI_GOTAKU_YOTEI_KINGAKU)
	VALUES ('kouki', '2023', '10', 363000.00, 386400.00, 295800.00, -23400.00, 0.00, 0.00, 0.00);
