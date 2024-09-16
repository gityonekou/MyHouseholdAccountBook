-- 指定のユーザID、対象年月に対応する支出情報が何件あるかを取得します。
SELECT COUNT(*) FROM EXPENDITURE_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/

