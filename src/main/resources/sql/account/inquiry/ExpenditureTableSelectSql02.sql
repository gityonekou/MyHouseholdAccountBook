-- ユニークキー(ユーザID、対象年、対象月、支出コード)を条件に支出テーブルを参照します。
SELECT * FROM EXPENDITURE_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/
      AND EXPENDITURE_CODE = /*[# mb:p="dto.sisyutuCode"]*/ 4 /*[/]*/
