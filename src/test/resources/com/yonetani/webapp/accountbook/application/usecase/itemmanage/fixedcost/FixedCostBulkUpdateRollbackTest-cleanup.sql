-- ロールバックテスト専用クリーンアップSQL
-- 目的：@Transactionalなしのテストクラスでは@Sqlデータがコミットされるため、
--        次のテスト前に全関連テーブルをクリアしてデータをリセットする。
-- 使用場所：FixedCostBulkUpdateRollbackTest の executionPhase = BEFORE_TEST_METHOD / AFTER_TEST_METHOD

-- FK制約を一時無効化して順序を気にせず削除可能にする
SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE FIXED_COST_TABLE;
TRUNCATE TABLE SISYUTU_ITEM_TABLE;
TRUNCATE TABLE ACCOUNT_BOOK_USER;
-- 支払方法・銀行口座（dev1で追加）も、コミット済みデータが後続テストと重複しないよう削除する
TRUNCATE TABLE PAYMENT_METHOD_TABLE;
TRUNCATE TABLE BANK_ACCOUNT_TABLE;

-- FK制約を再有効化
SET REFERENTIAL_INTEGRITY TRUE;
