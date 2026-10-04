-- 銀行口座テーブル：BANK_ACCOUNT_TABLE
INSERT INTO BANK_ACCOUNT_TABLE VALUES
	('TESTUSER001', '01', 'みんなのテスト＠銀行', 'みんなのテスト＠銀行 めも①', '01', true),
	('TESTUSER001', '02', 'てすと２銀行', 'テスト銀行②めも', '02', true);
-- 支払方法テーブル：PAYMENT_METHOD_TABLE
INSERT INTO PAYMENT_METHOD_TABLE VALUES
	('TESTUSER001', '001', '現金', NULL, '1', NULL, NULL, '001', true, true),
	('TESTUSER001', '002', '○○クレジットカード', 'スペシャルポイント = 合計金額 × 9.5％ ÷ 5\n当月ポイント=スペシャルポイント＋基本の1000円1ポイント', '3', '02', '16', '002', true, true),
	('TESTUSER001', '003', 'みんなのテスト＠銀行　口座振替', NULL, '2', '01', NULL, '003', true, true),
	('TESTUSER001', '999', '支払方法がない', NULL, '1', null, null, '999', true, false);
	
-- 店舗テーブル:SHOP_TABLEにユーザID:testuser01のデータ6件(変更可能分3件、変更不可分3件)となるデータを登録
INSERT INTO SHOP_TABLE (USER_ID, SHOP_CODE, SHOP_KUBUN_CODE, SHOP_NAME, SHOP_SORT, DEFAULT_PAYMENT_METHOD_CODE) VALUES
  ('TESTUSER001-NotFound', '001', '901', 'テスト店舗０１', '001', null),
  ('TESTUSER001', '001', '901', 'テストユーザ登録店舗０１', '001', '003'),
  ('TESTUSER001', '002', '902', 'テストユーザ登録店舗０２', '002', null),
  ('TESTUSER001', '003', '903', 'テストユーザ登録店舗０３', '003', null),
  ('TESTUSER001', '901', '901', '食品・日用品店舗(その他)', '901', null),
  ('TESTUSER001', '902', '902', 'ホームセンター(その他)', '902', null),
  ('TESTUSER001', '903', '903', '衣類店舗(その他)', '903', null),
  ('TESTUSER001-NotFound', '002', '901', 'テスト店舗０２', '002', null);
  