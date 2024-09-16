-- 指定の支出情報を支出テーブル:EXPENDITURE_TABLEから論理削除します。
UPDATE EXPENDITURE_TABLE SET DELETE_FLG = /*[# mb:p="dto.deleteFlg"]*/ 1 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 2 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 3 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 4 /*[/]*/
      AND EXPENDITURE_CODE = /*[# mb:p="dto.sisyutuCode"]*/ 5 /*[/]*/
