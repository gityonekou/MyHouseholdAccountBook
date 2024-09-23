-- 収支テーブル：INCOME_AND_EXPENDITURE_TABLEの情報を指定の収支情報で更新します。
UPDATE INCOME_AND_EXPENDITURE_TABLE SET INCOME_KINGAKU = /*[# mb:p="dto.incomeKingaku"]*/ 1 /*[/]*/, EXPENDITURE_KINGAKU =  /*[# mb:p="dto.expenditureKingaku"]*/ 2 /*[/]*/,
    INCOME_AND_EXPENDITURE_KINGAKU = /*[# mb:p="dto.incomeAndExpenditureKingaku"]*/ 3 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 4 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 5 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 6 /*[/]*/

