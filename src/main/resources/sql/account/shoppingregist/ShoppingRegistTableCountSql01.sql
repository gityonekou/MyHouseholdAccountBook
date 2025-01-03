-- 検索条件(ユーザID、対象年、対象月)に一致する買い物登録情報が何件あるかを取得します。
SELECT COUNT(*) FROM SHOPPING_REGIST_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/

