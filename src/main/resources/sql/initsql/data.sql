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
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG) 
	VALUES ('kouki', '0001', '事業経費', '事業経費詳細を入力', '0001', '1','0100000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0002', '固定経費', '固定経費詳細を入力', '0001', '2','0101000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0003', '駐輪場代金', '駐輪場代金詳細を入力', '0002', '3','0101010000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0004', '電車定期券', '電車定期券詳細を入力', '0002', '3','0101020000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0005', 'レンタル代金', 'レンタル代金詳細を入力', '0002', '3','0101030000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0006', '勤務表PDF', '勤務表PDF詳細を入力', '0002', '3','0101040000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0007', '流動経費', '流動経費詳細を入力', '0001', '2','0102000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0008', '文房具', '文房具詳細を入力', '0007', '3','0102010000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0009', '事業設備', '事業設備詳細を入力', '0007', '3','0102020000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0010', 'その他', 'その他詳細を入力', '0007', '3','0102030000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0011', '税金支払い', '税金支払い詳細を入力', '0001', '2','0103000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0012', '消費税', '消費税詳細を入力', '0011', '2','0103010000', false);
-- 固定費(非課税)
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0013', '固定費(非課税)', '固定費(非課税)詳細を入力', '0013', '1','0200000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0014', '社会保険', '社会保険詳細を入力', '0013', '2','0201000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0015', '国民年金保険', '国民年金保険詳細を入力', '0014', '3','0201010000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0016', '健康保険', '健康保険詳細を入力', '0014', '3','0201020000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0017', 'イデコ', 'イデコ詳細を入力', '0013', '2','0202000000', false);
-- 固定費(課税)
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0018', '固定費(課税)', '固定費(課税)詳細を入力', '0018', '1','0300000000', false);
-- 衣類住居設備
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0019', '衣類住居設備', '衣類住居設備詳細を入力', '0019', '1','0400000000', false);
-- 飲食日用品
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0020', '飲食日用品', '飲食日用品詳細を入力', '0020', '1','0500000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0021', '美容費', '美容費詳細を入力', '0020', '2','0501000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0022', '日用消耗品', '日用消耗品詳細を入力', '0020', '2','0502000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0023', '食費', '食費詳細を入力', '0020', '2','0503000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0024', '一人プチ贅沢・外食', '一人プチ贅沢・外食詳細を入力 友人・家族との外食は交際費へ', '0020', '2','0504000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0025', '雑貨', '雑貨詳細を入力', '0020', '2','0505000000', false);
	
-- 趣味娯楽
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0026', '趣味娯楽', '趣味娯楽詳細を入力', '0026', '1','0600000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0027', '交際費', '交際費詳細を入力', '0026', '2','0601000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0028', '趣味娯楽費', '趣味娯楽費詳細を入力', '0026', '2','0602000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0029', 'イベント', 'イベント詳細を入力', '0026', '2','0603000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0030', 'コミケ', 'コミケイベント詳細を入力', '0029', '3','0603010000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0031', '父東京遠征', '父東京遠征イベント詳細を入力', '0029', '3','0603020000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0032', '実家帰省', '年末年始実家帰省イベント詳細を入力', '0029', '3','0603030000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('kouki', '0033', 'その他', '趣味娯楽その他詳細を入力', '0026', '2','0699000000', false);


-- 支出金額テーブル：SISYUTU_KINGAKU_TABLE
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0001', 36500.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0002', 26500.00, 0.00, '20230902', true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0007', 10000.00, 0.00, '20230903', true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0020', 49170.00, 10840.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0021', 1890.00, 0.00, '20230905', false);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0022', 5380.00, 350.00, '20230906', false);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0023', 35100.00, 3690.00, '20230907', true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0024', 6800.00, 6800.00, '20230908', true);

-- ------------test data add
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0013', 2000.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0014', 2010.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '08', '0013', 2100.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '08', '0014', 2110.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0018', 3000.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '08', '0018', 3100.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0019', 4000.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '08', '0019', 4100.00, 3000.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '08', '0022', 5380.00, 1350.00, '20230906', false);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '09', '0026', 6000.00, 0.00, null, true);
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE,SISYUTU_KINGAKU,SISYUTU_KINGAKU_B, SIHARAI_DATE, CLOSING_FLG)
	VALUES ('kouki', '2023', '08', '0026', 6100.00, 0.00, null, true);
	
-- 収支テーブル：INCOME_AND_EXPENSE_TABLE
INSERT INTO INCOME_AND_EXPENSE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_KINGAKU, EXPENSE_KINGAKU,EXPENSE_YOTEI_KINGAKU, SYUUSI_KINGAKU, IRUI_JYUUKYO_YOTEI_KINGAKU,INSYOKU_NITIYOUHIN_YOTEI_KINGAKU,SYUMI_GOTAKU_YOTEI_KINGAKU)
	VALUES ('kouki', '2023', '07', 363000.00, 315800.00, 295800.00, 47200.00, 0.00, 0.00, 0.00);
INSERT INTO INCOME_AND_EXPENSE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_KINGAKU, EXPENSE_KINGAKU,EXPENSE_YOTEI_KINGAKU, SYUUSI_KINGAKU, IRUI_JYUUKYO_YOTEI_KINGAKU,INSYOKU_NITIYOUHIN_YOTEI_KINGAKU,SYUMI_GOTAKU_YOTEI_KINGAKU)
	VALUES ('kouki', '2023', '08', 363000.00, 365800.00, 335800.00, -2800.00, 0.00, 0.00, 0.00);
INSERT INTO INCOME_AND_EXPENSE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_KINGAKU, EXPENSE_KINGAKU,EXPENSE_YOTEI_KINGAKU, SYUUSI_KINGAKU, IRUI_JYUUKYO_YOTEI_KINGAKU,INSYOKU_NITIYOUHIN_YOTEI_KINGAKU,SYUMI_GOTAKU_YOTEI_KINGAKU)
	VALUES ('kouki', '2023', '09', 363000.00, 285300.00, 295800.00, 77700.00, 0.00, 0.00, 0.00);
INSERT INTO INCOME_AND_EXPENSE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_KINGAKU, EXPENSE_KINGAKU,EXPENSE_YOTEI_KINGAKU, SYUUSI_KINGAKU, IRUI_JYUUKYO_YOTEI_KINGAKU,INSYOKU_NITIYOUHIN_YOTEI_KINGAKU,SYUMI_GOTAKU_YOTEI_KINGAKU)
	VALUES ('kouki', '2023', '10', 363000.00, 386400.00, 295800.00, -23400.00, 0.00, 0.00, 0.00);



-- 店舗テーブルテストデータ
INSERT INTO SHOP_TABLE (USER_ID, SHOP_CODE, SHOP_KUBUN_CODE, SHOP_NAME, SHOP_SORT) VALUES ('kouki', '001', '901', 'エイヴィ', '001');
INSERT INTO SHOP_TABLE (USER_ID, SHOP_CODE, SHOP_KUBUN_CODE, SHOP_NAME, SHOP_SORT) VALUES ('kouki', '002', '901', 'OK', '002');
INSERT INTO SHOP_TABLE (USER_ID, SHOP_CODE, SHOP_KUBUN_CODE, SHOP_NAME, SHOP_SORT) VALUES ('kouki', '003', '907', 'イオン', '003');
INSERT INTO SHOP_TABLE (USER_ID, SHOP_CODE, SHOP_KUBUN_CODE, SHOP_NAME, SHOP_SORT) VALUES ('kouki', '004', '901', 'コンビニ', '004');
INSERT INTO SHOP_TABLE (USER_ID, SHOP_CODE, SHOP_KUBUN_CODE, SHOP_NAME, SHOP_SORT) VALUES ('kouki', '005', '901', 'ロピア', '005');
INSERT INTO SHOP_TABLE (USER_ID, SHOP_CODE, SHOP_KUBUN_CODE, SHOP_NAME, SHOP_SORT) VALUES ('kouki', '006', '901', 'いなげや', '006');
INSERT INTO SHOP_TABLE (USER_ID, SHOP_CODE, SHOP_KUBUN_CODE, SHOP_NAME, SHOP_SORT) VALUES ('kouki', '007', '907', 'ドン・キホーテ', '007');
INSERT INTO SHOP_TABLE (USER_ID, SHOP_CODE, SHOP_KUBUN_CODE, SHOP_NAME, SHOP_SORT) VALUES ('kouki', '008', '903', 'ユニクロ', '008');
INSERT INTO SHOP_TABLE (USER_ID, SHOP_CODE, SHOP_KUBUN_CODE, SHOP_NAME, SHOP_SORT) VALUES ('kouki', '009', '902', 'カインズホーム', '009');
INSERT INTO SHOP_TABLE (USER_ID, SHOP_CODE, SHOP_KUBUN_CODE, SHOP_NAME, SHOP_SORT) VALUES ('kouki', '010', '905', 'クリエイト', '010');
INSERT INTO SHOP_TABLE (USER_ID, SHOP_CODE, SHOP_KUBUN_CODE, SHOP_NAME, SHOP_SORT) VALUES ('kouki', '011', '904', '靴流通センター', '011');


-- 固定費テストデータ
INSERT INTO FIXED_COST_TABLE (USER_ID, FIXED_COST_CODE, FIXED_COST_NAME, FIXED_COST_DETAIL_CONTEXT, SISYUTU_ITEM_CODE, FIXED_COST_SHIHARAI_TUKI, FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT, FIXED_COST_SHIHARAI_DAY, SHIHARAI_KINGAKU, DELETE_FLG) 
	VALUES
 ('kouki', '0001', 'コミケ C014', '【内容詳細】2024年夏コミ', '0028', '06', null, '15', 49800.00, false),
 ('kouki', '0002', '国民年金保険', null, '0015', '00',null, '00', 16980.00, false),
 ('kouki', '0003', '散髪(奇数月)', '【内容詳細】奇数月に散髪', '0021', '20',null, '03', 16980.00, false),
 ('kouki', '0004', '消費税支払(偶数月)', '【内容詳細】偶数月に消費税支払い', '0012', '30',null, '19', 3260.00, false),
 ('kouki', '0005', 'その他任意で不定期の支払１', '【内容詳細】その他任意で不定期の支払１詳細内容', '0009', '40','【支払月任意詳細】その他任意月１', '40', 36380.00, false),
 ('kouki', '0006', 'その他任意で不定期の支払２', null, '0009', '40','【支払月任意詳細】その他任意月２', '40', 22910.00, false);
 
 
