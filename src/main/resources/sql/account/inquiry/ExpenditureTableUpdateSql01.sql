-- 支出テーブル:EXPENDITURE_TABLEの情報を指定の支出情報で更新します。
UPDATE EXPENDITURE_TABLE SET EXPENDITURE_NAME = /*[# mb:p="dto.sisyutuName"]*/ 1 /*[/]*/, EXPENDITURE_KUBUN =  /*[# mb:p="dto.sisyutuKubun"]*/ 2 /*[/]*/,
    EXPENDITURE_DETAIL_CONTEXT = /*[# mb:p="dto.sisyutuDetailContext"]*/ 3 /*[/]*/, SIHARAI_DATE =  /*[# mb:p="dto.shiharaiDate"]*/ 4 /*[/]*/,
    EXPENDITURE_KINGAKU =  /*[# mb:p="dto.sisyutuKingaku"]*/ 5 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 6 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 7 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 8 /*[/]*/
      AND EXPENDITURE_CODE = /*[# mb:p="dto.sisyutuCode"]*/ 9 /*[/]*/
