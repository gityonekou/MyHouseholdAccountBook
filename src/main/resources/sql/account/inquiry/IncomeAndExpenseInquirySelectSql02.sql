-- ユーザID,対象年,対象月を条件に収支テーブルを検索して返します。
SELECT * FROM INCOME_AND_EXPENSE_TABLE
WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/

