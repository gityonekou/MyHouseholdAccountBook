-- 固定費テーブル:FIXED_COST_TABLEの情報から指定した固定費の情報を論理削除します。
UPDATE FIXED_COST_TABLE SET DELETE_FLG = TRUE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND FIXED_COST_CODE = /*[# mb:p="dto.fixedCostCode"]*/ 2 /*[/]*/
