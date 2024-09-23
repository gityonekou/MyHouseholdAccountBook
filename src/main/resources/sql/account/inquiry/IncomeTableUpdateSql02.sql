-- 指定の収入情報を収入テーブル:INCOME_TABLEから論理削除します。
UPDATE INCOME_TABLE SET DELETE_FLG = TRUE
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/
      AND INCOME_CODE = /*[# mb:p="dto.syuunyuuCode"]*/ 4 /*[/]*/
