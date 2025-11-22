-- 店舗テーブル:SHOP_TABLEにユーザID:testuser01のデータ6件(変更可能分3件、変更不可分3件)となるデータを登録
INSERT INTO SHOP_TABLE (USER_ID, SHOP_CODE, SHOP_KUBUN_CODE, SHOP_NAME, SHOP_SORT) VALUES
  ('TESTUSER001-NotFound', '001', '901', 'テスト店舗０１', '001'),
  ('TESTUSER001', '001', '901', 'テストユーザ登録店舗０１', '001'),
  ('TESTUSER001', '002', '902', 'テストユーザ登録店舗０２', '002'),
  ('TESTUSER001', '003', '903', 'テストユーザ登録店舗０３', '003'),
  ('TESTUSER001', '901', '901', '食品・日用品店舗(その他)', '901'),
  ('TESTUSER001', '902', '902', 'ホームセンター(その他)', '902'),
  ('TESTUSER001', '903', '903', '衣類店舗(その他)', '903'),
  ('TESTUSER001-NotFound', '002', '901', 'テスト店舗０２', '002');
  