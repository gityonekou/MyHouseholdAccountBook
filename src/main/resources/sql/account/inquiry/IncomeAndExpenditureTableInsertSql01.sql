-- 収支テーブル：INCOME_AND_EXPENDITURE_TABLEにデータを追加します。
INSERT INTO INCOME_AND_EXPENDITURE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_KINGAKU, WITHDREW_KINGAKU, EXPENDITURE_ESTIMATE_KINGAKU, EXPENDITURE_KINGAKU, INCOME_AND_EXPENDITURE_KINGAKU)
  VALUES (/*[# mb:p="dto.userId"]*/ 1 /*[/]*/, /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/, /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/, /*[# mb:p="dto.incomeKingaku"]*/ 4 /*[/]*/, 
          /*[# mb:p="dto.withdrewKingaku"]*/ 5 /*[/]*/, /*[# mb:p="dto.expenditureEstimateKingaku"]*/ 6 /*[/]*/, /*[# mb:p="dto.expenditureKingaku"]*/ 7 /*[/]*/, /*[# mb:p="dto.incomeAndExpenditureKingaku"]*/ 8 /*[/]*/)
