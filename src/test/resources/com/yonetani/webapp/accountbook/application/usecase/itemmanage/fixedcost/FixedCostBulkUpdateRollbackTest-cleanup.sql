-- ロールバックテスト専用クリーンアップSQL
-- 目的：@Transactionalなしのテストクラスでは@Sqlデータがコミットされるため、
--        次のテスト前に全関連テーブルをクリアしてデータをリセットする。
-- 使用場所：FixedCostBulkUpdateRollbackTest の executionPhase = BEFORE_TEST_METHOD / AFTER_TEST_METHOD

-- FK制約を一時無効化して順序を気にせず削除可能にする
SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE FIXED_COST_TABLE;
TRUNCATE TABLE SISYUTU_ITEM_TABLE;
TRUNCATE TABLE ACCOUNT_BOOK_USER;

-- FK制約を再有効化
SET REFERENTIAL_INTEGRITY TRUE;
