-- 指定のユーザID、対象年月、支出項目コードを条件に支出テーブル:EXPENDITURE_TABLEを参照します。
SELECT * FROM EXPENDITURE_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/
      AND SISYUTU_ITEM_CODE = /*[# mb:p="dto.sisyutuItemCode"]*/ 4 /*[/]*/
