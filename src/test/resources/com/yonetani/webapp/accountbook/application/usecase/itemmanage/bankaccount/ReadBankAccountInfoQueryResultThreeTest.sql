-- 銀行口座テーブル:BANK_ACCOUNT_TABLEにユーザID:TESTUSER001のデータ3件となるデータを登録
INSERT INTO BANK_ACCOUNT_TABLE (USER_ID, BANK_ACCOUNT_CODE, BANK_NAME, BANK_ACCOUNT_MEMO, BANK_ACCOUNT_SORT, ENABLE_FLG) VALUES
  ('TESTUSER001-NotFound', '01', 'テスト銀行０１', null, '01', true),
  ('TESTUSER001', '01', 'テストユーザ銀行０１', '生活費口座', '01', true),
  ('TESTUSER001', '02', 'テストユーザ銀行０２', null, '02', true),
  ('TESTUSER001', '03', 'テストユーザ銀行０３', '無効口座', '03', false);
