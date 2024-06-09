-- 指定のユーザID、固定費コードを条件に固定費テーブル:FIXED_COST_TABLEを参照します。
SELECT * FROM FIXED_COST_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND FIXED_COST_CODE = /*[# mb:p="dto.fixedCostCode"]*/ 2 /*[/]*/
