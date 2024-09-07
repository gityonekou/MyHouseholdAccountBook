-- ユーザID,対象年度を条件に収支テーブルと支出金額テーブルを検索して返します。
-- 支出金額テーブルは指定の支出項目コード(支出項目テーブルのレベルが1に設定されている項目コード)の値を1レコードにして返します。
SELECT
 A.TARGET_MONTH,
 A.EXPENDITURE_KINGAKU,
 A.INCOME_AND_EXPENDITURE_KINGAKU,
 SUM(CASE WHEN B.SISYUTU_ITEM_CODE='0001' THEN B.SISYUTU_KINGAKU ELSE 0 END) AS JIGYOU_KEIHI_KINGAKU,
 SUM(CASE WHEN B.SISYUTU_ITEM_CODE='0013' THEN B.SISYUTU_KINGAKU ELSE 0 END) AS KOTEI_HIKAZEI_KINGAKU,
 SUM(CASE WHEN B.SISYUTU_ITEM_CODE='0023' THEN B.SISYUTU_KINGAKU ELSE 0 END) AS KOTEI_KAZEI_KINGAKU,
 SUM(CASE WHEN B.SISYUTU_ITEM_CODE='0045' THEN B.SISYUTU_KINGAKU ELSE 0 END) AS IRUI_JYUUKYO_SETUBI_KINGAKU,
 SUM(CASE WHEN B.SISYUTU_ITEM_CODE='0049' THEN B.SISYUTU_KINGAKU ELSE 0 END) AS INSYOKU_NITIYOUHIN_KINGAKU,
 SUM(CASE WHEN B.SISYUTU_ITEM_CODE='0056' THEN B.SISYUTU_KINGAKU ELSE 0 END) AS SYUMI_GOTAKU_KINGAKU,
 SUM(B.SISYUTU_KINGAKU_B) AS SISYUTU_KINGAKU_B
FROM INCOME_AND_EXPENDITURE_TABLE AS A, SISYUTU_KINGAKU_TABLE AS B
WHERE A.USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND A.TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/ AND A.TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/ 
 AND A.USER_ID = B.USER_ID AND A.TARGET_YEAR = B.TARGET_YEAR AND A.TARGET_MONTH = B.TARGET_MONTH AND A.TARGET_MONTH = B.TARGET_MONTH
 AND B.SISYUTU_ITEM_CODE IN ('0001', '0013', '0023', '0045', '0049', '0056')
ORDER BY A.TARGET_MONTH

