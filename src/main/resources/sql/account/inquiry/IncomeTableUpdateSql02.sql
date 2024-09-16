-- 指定の収入情報を収入テーブル:INCOME_TABLEから論理削除します。
UPDATE INCOME_TABLE SET DELETE_FLG = /*[# mb:p="dto.deleteFlg"]*/ 1 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 2 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 3 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 4 /*[/]*/
      AND INCOME_CODE = /*[# mb:p="dto.syuunyuuCode"]*/ 5 /*[/]*/
