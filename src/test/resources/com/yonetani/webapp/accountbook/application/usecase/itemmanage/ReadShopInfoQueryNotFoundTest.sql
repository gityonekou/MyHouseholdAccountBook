-- 店舗テーブル:SHOP_TABLEにユーザID:testuser01のデータなしとなるデータを登録
INSERT INTO SHOP_TABLE (USER_ID, SHOP_CODE, SHOP_KUBUN_CODE, SHOP_NAME, SHOP_SORT) VALUES
  ('TESTUSER001-NotFound', '001', '901', 'テスト店舗０１', '001'),
  ('TESTUSER001-NotFound', '002', '901', 'テスト店舗０２', '002'),
  ('TESTUSER001-NotFound', '003', '901', 'テスト店舗０３', '003');
  