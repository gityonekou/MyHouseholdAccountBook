-- ログインユーザ
INSERT INTO USERS (USERNAME, PASSWORD, ENABLED) VALUES
('admin', '{bcrypt}$2a$10$vC.r53zKYPwEXplBYH3mxuZP52r2u3udRcEg9yTUmwYE5yjmoUXyG', true);
INSERT INTO USERS (USERNAME, PASSWORD, ENABLED) VALUES
('user', '{bcrypt}$2a$08$DD5k5hs1PVj0ZNfrq9bFKe3OVSE9cmYsUCuRUL5vFRVxh/6o.COEW', true);
INSERT INTO USERS (USERNAME, PASSWORD, ENABLED) VALUES
('TEST-USER-ID', '{bcrypt}$2a$08$tGkj2w0.K.Iuz3P16S28uO8L.oIjGBD9fyS7/xID3uJU4tnIEarEe', true);
INSERT INTO AUTHORITIES (USERNAME, AUTHORITY) VALUES ('admin', 'ROLE_ADMIN');
INSERT INTO AUTHORITIES (USERNAME, AUTHORITY) VALUES ('admin', 'ROLE_USER');
INSERT INTO AUTHORITIES (USERNAME, AUTHORITY) VALUES ('user', 'ROLE_USER');
INSERT INTO AUTHORITIES (USERNAME, AUTHORITY) VALUES ('TEST-USER-ID', 'ROLE_ADMIN');
INSERT INTO AUTHORITIES (USERNAME, AUTHORITY) VALUES ('TEST-USER-ID', 'ROLE_USER');
-- update USERS set ENABLED = false where USERNAME='admin';
--  家計簿利用ユーザ:ACCOUNT_BOOK_USER
--  INSERT INTO ACCOUNT_BOOK_USER (USER_ID, USER_NAME, NOW_TARGET_YEAR, NOW_TARGET_MONTH) VALUES ('admin', 'アドミンユーザさん', '2023', '09');
INSERT INTO ACCOUNT_BOOK_USER (USER_ID, NOW_TARGET_YEAR, NOW_TARGET_MONTH, USER_NAME) VALUES ('user', '2023', '08', 'テストユーザさん');
INSERT INTO ACCOUNT_BOOK_USER (USER_ID, NOW_TARGET_YEAR, NOW_TARGET_MONTH, USER_NAME) VALUES ('TEST-USER-ID', '2023', '09', '米谷 幸城さん');
-- 支出項目テーブル:SISYUTU_ITEM_TABLE
-- 事業経費
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG) 
	VALUES ('TEST-USER-ID', '0001', '事業経費', '事業経費詳細を入力', '0001', '1','0100000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0002', '固定経費', '固定経費詳細を入力', '0001', '2','0101000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0003', '駐輪場代金', '駐輪場代金詳細を入力', '0002', '3','0101010000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0004', '電車定期券', '電車定期券詳細を入力', '0002', '3','0101020000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0005', 'レンタル代金', 'レンタル代金詳細を入力', '0002', '3','0101030000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0006', '勤務表PDF', '勤務表PDF詳細を入力', '0002', '3','0101040000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0007', '流動経費', '流動経費詳細を入力', '0001', '2','0102000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0008', '文房具', '文房具詳細を入力', '0007', '3','0102010000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0009', '事業設備', '事業設備詳細を入力', '0007', '3','0102020000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0010', 'その他', 'その他詳細を入力', '0007', '3','0102030000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0011', '税金支払い', '税金支払い詳細を入力', '0001', '2','0103000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0012', '消費税', '消費税詳細を入力', '0011', '2','0103010000', false);
-- 固定費(非課税)
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0013', '固定費(非課税)', '固定費(非課税)詳細を入力', '0013', '1','0200000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0014', '社会保険', '社会保険詳細を入力', '0013', '2','0201000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0015', '国民年金保険', '国民年金保険詳細を入力', '0014', '3','0201010000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0016', '健康保険', '健康保険詳細を入力', '0014', '3','0201020000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0017', 'イデコ', 'イデコ詳細を入力', '0013', '2','0202000000', false);
-- 固定費(課税)
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0018', '固定費(課税)', '固定費(課税)詳細を入力', '0018', '1','0300000000', false);
-- 衣類住居設備
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0019', '衣類住居設備', '衣類住居設備詳細を入力', '0019', '1','0400000000', false);
-- 飲食日用品
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0020', '飲食日用品', '飲食日用品詳細を入力', '0020', '1','0500000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0021', '美容費', '美容費詳細を入力', '0020', '2','0501000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0022', '日用消耗品', '日用消耗品詳細を入力', '0020', '2','0502000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0023', '食費', '食費詳細を入力', '0020', '2','0503000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0024', '一人プチ贅沢・外食', '一人プチ贅沢・外食詳細を入力 友人・家族との外食は交際費へ', '0020', '2','0504000000', false);
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0025', '雑貨', '雑貨詳細を入力', '0020', '2','0505000000', false);
-- 趣味娯楽
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE,SISYUTU_ITEM_LEVEL,SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
	VALUES ('TEST-USER-ID', '0026', '趣味娯楽', '趣味娯楽詳細を入力', '0026', '1','0600000000', false);
-- PTテストで'TEST-USER-ID','0030'のデータがないことを確認しているので注意

