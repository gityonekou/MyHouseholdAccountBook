-- 指定のユーザID、対象年月に対応する収入金額合計値を取得します。
SELECT SUM(INCOME_KINGAKU) FROM INCOME_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/
    AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/ AND DELETE_FLG = FALSE

