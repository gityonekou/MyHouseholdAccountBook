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
--  INSERT INTO ACCOUNT_BOOK_USER (USER_ID, USER_NAME, NOW_TARGET_YEAR, NOW_TARGET_MONTH) VALUES ('admin', 'アドミンユーザさん', '2025', '01');
INSERT INTO ACCOUNT_BOOK_USER (USER_ID, NOW_TARGET_YEAR, NOW_TARGET_MONTH, USER_NAME) VALUES ('user', '2023', '08', 'テストユーザさん');
INSERT INTO ACCOUNT_BOOK_USER (USER_ID, NOW_TARGET_YEAR, NOW_TARGET_MONTH, USER_NAME) VALUES ('kouki', '2023', '09', '米谷 幸城さん');
-- 支出項目テーブル:SISYUTU_ITEM_TABLE
INSERT INTO SISYUTU_ITEM_TABLE VALUES
('kouki', '0001','事業経費','事業経費詳細を入力','0001','1','0100000000', false),
('kouki', '0002','固定経費','固定経費詳細を入力','0001','2','0101000000', false),
('kouki', '0003','駐輪場代金','駐輪場代金詳細を入力','0002','3','0101010000', false),
('kouki', '0004','電車定期券','電車定期券詳細を入力','0002','3','0101020000', false),
('kouki', '0005','レンタル代金','レンタル代金詳細を入力','0002','3','0101030000', false),
('kouki', '0006','勤務表PDF','勤務表PDF詳細を入力','0002','3','0101040000', false),
('kouki', '0007','流動経費','流動経費詳細を入力','0001','2','0102000000', false),
('kouki', '0008','文房具','文房具詳細を入力','0007','3','0102010000', false),
('kouki', '0009','事業設備','事業設備詳細を入力','0007','3','0102020000', false),
('kouki', '0010','その他','その他詳細を入力','0007','3','0102990000', false),
('kouki', '0011','租税公課','租税公課(税金支払い)詳細を入力','0001','2','0103000000', false),
('kouki', '0012','消費税','消費税詳細を入力','0011','3','0103010000', false),
('kouki', '0013','固定費(非課税)','固定費(非課税)詳細を入力','0013','1','0200000000', false),
('kouki', '0014','社会保険','社会保険詳細を入力','0013','2','0201000000', false),
('kouki', '0015','国民年金保険','国民年金保険詳細を入力','0014','3','0201010000', false),
('kouki', '0016','健康保険','健康保険詳細を入力','0014','3','0201020000', false),
('kouki', '0017','iDeCo(イデコ)','イデコ詳細を入力','0013','2','0202000000', false),
('kouki', '0018','ふるさと納税','ふるさと納税(控除分)になります。非控除の2000円は別科目です、その他詳細を入力','0013','2','0204000000', false),
('kouki', '0019','保険料控除','保険料控除詳細を入力','0013','2','0205000000', false),
('kouki', '0020','生命保険料控除','生命保険料控除詳細を入力','0019','3','0205010000', false),
('kouki', '0021','その他','固定費(非課税)その他詳細を入力','0013','2','0299000000', false),
('kouki', '0022','寄附金控除','寄附金控除詳細を入力','0021','3','0299010000', false),
('kouki', '0023','固定費(課税)','固定費(課税)詳細を入力','0023','1','0300000000', false),
('kouki', '0024','税金支払い','税金支払い詳細を入力','0023','2','0301000000', false),
('kouki', '0025','住民税','住民税詳細を入力','0024','3','0301010000', false),
('kouki', '0026','所得税','所得税詳細を入力','0024','3','0301020000', false),
('kouki', '0027','保険料','保険料詳細を入力','0023','2','0302000000', false),
('kouki', '0028','生命保険(非課税分)','生命保険(非課税分)詳細を入力','0027','3','0302010000', false),
('kouki', '0029','地代家賃','地代家賃詳細を入力','0023','2','0303000000', false),
('kouki', '0030','家賃','家賃詳細を入力','0029','3','0303010000', false),
('kouki', '0031','積立(投資)','積立(投資)詳細を入力','0023','2','0304000000', false),
('kouki', '0032','積立ＮＩＳＡ','積立ＮＩＳＡ詳細を入力','0031','3','0304010000', false),
('kouki', '0033','積立金','積立金詳細を入力','0023','2','0305000000', false),
('kouki', '0034','翌年税金積立','翌年税金積立詳細を入力','0033','3','0305010000', false),
('kouki', '0035','自由用途積立金','自由用途積立金詳細を入力','0033','3','0305020000', false),
('kouki', '0036','水光熱通費','水光熱通費詳細を入力','0023','2','0306000000', false),
('kouki', '0037','電気代','電気代詳細を入力','0036','3','0306010000', false),
('kouki', '0038','ガス代','ガス代詳細を入力','0036','3','0306020000', false),
('kouki', '0039','通信費','通信費詳細を入力','0036','3','0306030000', false),
('kouki', '0040','水道代','水道代詳細を入力','0036','3','0306040000', false),
('kouki', '0041','NHK','NHK詳細を入力','0036','3','0306050000', false),
('kouki', '0042','その他','固定費(課税)その他詳細を入力','0023','2','0399000000', false),
('kouki', '0043','控除外支払い','控除外支払い詳細を入力','0042','3','0399010000', false),
('kouki', '0044','その他定期支払','その他定期支払詳細を入力','0042','3','0399020000', false),
('kouki', '0045','衣類住居設備','衣類住居設備詳細を入力','0045','1','0400000000', false),
('kouki', '0046','被服費','被服費詳細を入力','0045','2','0401000000', false),
('kouki', '0047','住居設備','住居設備詳細を入力','0045','2','0402000000', false),
('kouki', '0048','その他','衣類住居設備その他詳細を入力','0045','2','0499000000', false),
('kouki', '0049','飲食日用品','飲食日用品詳細を入力','0049','1','0500000000', false),
('kouki', '0050','日用消耗品','日用消耗品詳細を入力','0049','2','0501000000', false),
('kouki', '0051','食費','食費詳細を入力','0049','2','0502000000', false),
('kouki', '0052','一人プチ贅沢・外食','一人プチ贅沢・外食詳細を入力 友人・家族との外食は交際費へ','0049','2','0503000000', false),
('kouki', '0053','雑貨','雑貨詳細を入力','0049','2','0504000000', false),
('kouki', '0054','その他','飲食日用品その他詳細を入力','0049','2','0599000000', false),
('kouki', '0055','趣味娯楽','趣味娯楽詳細を入力','0055','1','0600000000', false),
('kouki', '0056','交際費','交際費詳細を入力','0055','2','0601000000', false),
('kouki', '0057','趣味娯楽費','趣味娯楽費詳細を入力(漫画・雑誌などの経費以外の本はここに登録)','0055','2','0602000000', false),
('kouki', '0058','イベント費','イベント費詳細を入力','0055','2','0603000000', false),
('kouki', '0059','その他','趣味娯楽その他詳細を入力','0055','2','0699000000', false),
('kouki', '0060', '小規模企業共済', '小規模企業共済詳細を入力', '0013', '2','0203000000', false),
('kouki', '0061', 'コミケ', 'コミケイベント詳細を入力', '0058', '3','0603010000', false),
('kouki', '0062', '父東京遠征', '父東京遠征イベント詳細を入力', '0058', '3','0603020000', false),
('kouki', '0063', '実家帰省', '年末年始実家帰省イベント詳細を入力', '0058', '3','0603030000', false);


-- 支出金額テーブル：SISYUTU_KINGAKU_TABLE
INSERT INTO SISYUTU_KINGAKU_TABLE VALUES
	('kouki', '2023', '09', '0001', '0001', 36500.00, 36500.00, null, 0.00, null),
	('kouki', '2023', '09', '0002', '0001', 26500.00, 26500.00, 0.00, null, '20230902'),
	('kouki', '2023', '09', '0007', '0001', 0.00, 10000.00, 0.00, 0.00, '20230903'),
	('kouki', '2023', '09', '0020', '0020', 49170.00, 49170.00, 10840.00, 0.00, null),
	('kouki', '2023', '09', '0021', '0020', 1890.00, 1890.00, 0.00, 0.00, '20230905'),
	('kouki', '2023', '09', '0022', '0020', 3299.00, 5380.00, 350.00, 0.00, '20230906'),
	('kouki', '2023', '09', '0023', '0020', 15469.00, 35100.00, 3690.00, 0.00, '20230907'),
	('kouki', '2023', '09', '0024', '0020', 0.00, 6800.00, 6800.00, 0.00, '20230908'),
	('kouki', '2023', '09', '0013', '0013', 0.00, 2000.00, 0.00, 0.00, null),
	('kouki', '2023', '09', '0014', '0013', 0.00, 2010.00, null, null, null),
	('kouki', '2023', '08', '0013', '0013', 0.00, 2100.00, 0.00, 0.00, null),
	('kouki', '2023', '08', '0014', '0013', 0.00, 2110.00, 0.00, null, null),
	('kouki', '2023', '09', '0018', '0018', 0.00, 3000.00, 0.00, 0.00, null),
	('kouki', '2023', '08', '0018', '0018', 0.00, 3100.00, null, null, null),
	('kouki', '2023', '09', '0019', '0019', 0.00, 4000.00, 0.00, 0.00, null),
	('kouki', '2023', '08', '0019', '0019', 0.00, 4100.00, 3000.00, 0.00, null),
	('kouki', '2023', '08', '0022', '0020', 3270.00, 5380.00, 1350.00, 0.00, '20230906'),
	('kouki', '2023', '09', '0026', '0026', 6000.00, 6000.00, null, 0.00, null),
	('kouki', '2023', '08', '0026', '0026', 6000.00, 6100.00, 0.00, 0.00, null);

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
INSERT INTO FIXED_COST_TABLE (USER_ID, FIXED_COST_CODE, FIXED_COST_NAME, FIXED_COST_DETAIL_CONTEXT, SISYUTU_ITEM_CODE, FIXED_COST_KUBUN, FIXED_COST_SHIHARAI_TUKI, FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT, FIXED_COST_SHIHARAI_DAY, SHIHARAI_KINGAKU, DELETE_FLG) 
	VALUES
 ('kouki', '0001', '飲食(無駄遣いなし)', '月毎の飲食(無駄遣いなし)予定金額', '0051', '2', '00', null, '40', 45600.00, false),
 ('kouki', '0002', '飲食(無駄遣いB)', '月毎の飲食(無駄遣いB)予定金額', '0051', '2', '00', null, '40', 12300.00, false),
 ('kouki', '0003', '飲食(無駄遣いC)', '月毎の飲食(無駄遣いC)予定金額', '0051', '2', '00', null, '40', 6500.00, false),
 ('kouki', '0004', '日用消耗品', '月毎の日用消耗品予定金額', '0050', '2', '00', null, '40', 3500.00, false),
 ('kouki', '0005', '被服費', '月毎の被服費予定金額', '0046', '2', '00', null, '40', 11000.00, false),
 ('kouki', '0006', '住居設備', '月毎の住居設備予定金額', '0047', '2', '00', null, '40', 6700.00, false),
 ('kouki', '0007', '外食', '月毎の外食予定金額', '0052', '2', '00', null, '40', 1500.00, false),
 ('kouki', '0008', '事業流動経費', '月毎の事業流動経費予定金額', '0007', '2', '00', null, '40', 1200.00, false),
 ('kouki', '0009', 'コミケ C014', '【内容詳細】2024年夏コミ', '0061', '2', '06', null, '15', 49800.00, false),
 ('kouki', '0010', '国民年金保険', null, '0015', '1', '00',null, '00', 16980.00, false),
 ('kouki', '0012', '消費税支払(偶数月)', '【内容詳細】偶数月に消費税支払い', '0012', '1', '30',null, '19', 3260.00, false),
 ('kouki', '0013', 'その他任意で不定期の支払１', '【内容詳細】その他任意で不定期の支払１詳細内容', '0009', '1', '40','【支払月任意詳細】その他任意月１', '40', 36380.00, false),
 ('kouki', '0014', 'その他任意で不定期の支払２', null, '0009', '1', '40','【支払月任意詳細】その他任意月２', '40', 22910.00, false);

-- 収支テーブル：INCOME_AND_EXPENDITURE_TABLE
INSERT INTO INCOME_AND_EXPENDITURE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_KINGAKU, WITHDREW_KINGAKU, EXPENDITURE_ESTIMATE_KINGAKU, EXPENDITURE_KINGAKU, INCOME_AND_EXPENDITURE_KINGAKU)
	VALUES ('kouki', '2023', '07', 363000.00, null, 295800.00, 315800.00, 47200.00);
INSERT INTO INCOME_AND_EXPENDITURE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_KINGAKU, WITHDREW_KINGAKU, EXPENDITURE_ESTIMATE_KINGAKU, EXPENDITURE_KINGAKU, INCOME_AND_EXPENDITURE_KINGAKU)
	VALUES ('kouki', '2023', '08', 363000.00, null, 335800.00, 365800.00, -2800.00);
INSERT INTO INCOME_AND_EXPENDITURE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_KINGAKU, WITHDREW_KINGAKU, EXPENDITURE_ESTIMATE_KINGAKU, EXPENDITURE_KINGAKU, INCOME_AND_EXPENDITURE_KINGAKU)
	VALUES ('kouki', '2023', '09', 418000.00, null, 295800.00, 285300.00, 132700.00);
INSERT INTO INCOME_AND_EXPENDITURE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_KINGAKU, WITHDREW_KINGAKU, EXPENDITURE_ESTIMATE_KINGAKU, EXPENDITURE_KINGAKU, INCOME_AND_EXPENDITURE_KINGAKU)
	VALUES ('kouki', '2023', '10', 418000.00, 18700.00, 295800.00, 441400.00, -23400.00);

-- 収入テーブルテストデータ
INSERT INTO INCOME_TABLE (INCOME_KUBUN, TARGET_MONTH, USER_ID, TARGET_YEAR, INCOME_KINGAKU, INCOME_CODE, DELETE_FLG)
 VALUES('1', '08', 'kouki', '2023', 363000.00, '01', false);
INSERT INTO INCOME_TABLE VALUES
 ('kouki', '2023', '07', '01', '1', '０６月分給料', 363000.00, false),
 ('kouki', '2023', '09', '01', '1', '０８月分給料', 418000.00, false),
 ('kouki', '2023', '10', '01', '1', '０９月分給料', 418000.00, false);

-- 支出テーブルテストデータ
INSERT INTO EXPENDITURE_TABLE VALUES
 ('kouki', '2023', '07', '001', '0028', null, 'その他ALLの仮登録(趣味娯楽)', '1', 'その他すべて仮登録(趣味娯楽で入れてます)', null, 295800.00, 315800.00, false),
 ('kouki', '2023', '08', '001', '0018', null, '衣類住居設備仮登録', '1', null, null, 0.00, 4000.00, false),
 ('kouki', '2023', '08', '002', '0028', null, 'その他ALLの仮登録(趣味娯楽)', '1', 'その他すべて仮登録(趣味娯楽で入れてます)', null, 335800.00, 361800.00, false),
 ('kouki', '2023', '09', '001', '0018', null, '衣類住居設備仮登録ALL', '1', null, null, 295800.00, 285300.00, false),
 ('kouki', '2023', '10', '001', '0028', null, 'その他ALLの仮登録(趣味娯楽)', '1', 'その他すべて仮登録(趣味娯楽で入れてます)', null, 295800.00, 441400.00, false);


