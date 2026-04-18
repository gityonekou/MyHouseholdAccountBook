-- 指定の支出情報を支出テーブル:EXPENDITURE_TABLEから論理削除します。
UPDATE EXPENDITURE_TABLE SET DELETE_FLG = TRUE
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/
      AND EXPENDITURE_CODE = /*[# mb:p="dto.sisyutuCode"]*/ 4 /*[/]*/
