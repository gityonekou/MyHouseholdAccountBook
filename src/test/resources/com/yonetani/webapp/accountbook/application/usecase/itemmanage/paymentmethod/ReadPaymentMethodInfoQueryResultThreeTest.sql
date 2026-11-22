-- 銀行口座テーブル:BANK_ACCOUNT_TABLEにユーザID:TESTUSER001のデータを登録
INSERT INTO BANK_ACCOUNT_TABLE (USER_ID, BANK_ACCOUNT_CODE, BANK_NAME, BANK_ACCOUNT_MEMO, BANK_ACCOUNT_SORT, ENABLE_FLG) VALUES
  ('TESTUSER001', '01', 'テストユーザ銀行０１', null, '01', true);

-- 支払方法テーブル:PAYMENT_METHOD_TABLEにユーザID:TESTUSER001のデータ3件(通常)＋システム予約行1件を登録
INSERT INTO PAYMENT_METHOD_TABLE (USER_ID, PAYMENT_METHOD_CODE, PAYMENT_METHOD_NAME, PAYMENT_METHOD_KUBUN,
       BANK_ACCOUNT_CODE, CLOSING_DAY, PAYMENT_METHOD_SORT, ENABLE_FLG, ENABLE_UPDATE_FLG) VALUES
  ('TESTUSER001-NotFound', '001', '現金', '1', null, null, '001', true, true),
  ('TESTUSER001', '001', '現金', '1', null, null, '001', true, true),
  ('TESTUSER001', '002', '〇〇銀行引き落とし', '2', '01', null, '002', true, true),
  ('TESTUSER001', '003', '△△カード', '3', '01', '15', '003', false, true),
  ('TESTUSER001', '999', '支払方法がない', '1', null, null, '999', true, false);
