-- ユーザID,対象年度を条件に収支テーブルを検索して返します。
SELECT * FROM INCOME_AND_EXPENSE_TABLE
WHERE USER_ID =  /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND TARGET_YEAR =  /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/
ORDER BY TARGET_MONTH

