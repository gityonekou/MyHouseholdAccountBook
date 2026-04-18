-- 指定のユーザID、対象年月を条件に収入テーブル:INCOME_TABLEを参照します。
SELECT * FROM INCOME_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/
      AND DELETE_FLG = FALSE
