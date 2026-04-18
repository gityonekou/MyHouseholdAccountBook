-- ========================================
-- 収支登録・更新画面初期表示機能 統合テスト用データ
-- 固定費テーブルデータ（12月固定費テスト用：202512）
-- ========================================
--
-- [テスト観点]
-- 2025年21月検索時に取得される固定費パターンなしを確認
-- 対象となるFIXED_COST_SHIHARAI_TUKI: '08', '09', '10', '11'
--
-- [テストデータ設計]
-- - FIXED_COST_SHIHARAI_TUKI='08'（8月）: 電気代
-- - FIXED_COST_SHIHARAI_TUKI='09'（9月）: ガス代
-- - FIXED_COST_SHIHARAI_TUKI='10'（10月）: 水道代
-- - FIXED_COST_SHIHARAI_TUKI='11'（11月）: 家賃
--
-- ========================================

INSERT INTO FIXED_COST_TABLE (USER_ID, FIXED_COST_CODE, FIXED_COST_NAME, FIXED_COST_DETAIL_CONTEXT, SISYUTU_ITEM_CODE, FIXED_COST_KUBUN, FIXED_COST_SHIHARAI_TUKI, FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT, FIXED_COST_SHIHARAI_DAY, SHIHARAI_KINGAKU, DELETE_FLG)
	VALUES
 ('user01', '0001', '電気代', '電気代詳細', '0001', '1', '08', null, '31', 10000.00, false),
 ('user01', '0002', 'ガス代', 'ガス代詳細', '0001', '2', '09', null, '40', 8000.00, false),
 ('user01', '0003', '水道代', '水道代詳細', '0001', '1', '10', null, '20', 5000.00, false),
 ('user01', '0004', '家賃', '家賃支払詳細', '0004', '2', '11', null, '00', 62000.00, false);

-- ========================================
-- テストデータ作成完了
-- ========================================
