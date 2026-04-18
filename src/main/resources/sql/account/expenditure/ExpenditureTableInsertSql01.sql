-- 支出テーブル:EXPENDITURE_TABLEにデータを追加します。
INSERT INTO EXPENDITURE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, EXPENDITURE_CODE, SISYUTU_ITEM_CODE, EVENT_CODE, EXPENDITURE_NAME, EXPENDITURE_KUBUN, EXPENDITURE_DETAIL_CONTEXT, SIHARAI_DATE, EXPENDITURE_ESTIMATE_KINGAKU, EXPENDITURE_KINGAKU, DELETE_FLG)
  VALUES (/*[# mb:p="dto.userId"]*/ 1 /*[/]*/, /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/, /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/, /*[# mb:p="dto.sisyutuCode"]*/ 4 /*[/]*/, 
          /*[# mb:p="dto.sisyutuItemCode"]*/ 5 /*[/]*/, /*[# mb:p="dto.eventCode"]*/ 6 /*[/]*/, /*[# mb:p="dto.sisyutuName"]*/ 7 /*[/]*/, /*[# mb:p="dto.sisyutuKubun"]*/ 8 /*[/]*/,
          /*[# mb:p="dto.sisyutuDetailContext"]*/ 9 /*[/]*/, /*[# mb:p="dto.shiharaiDate"]*/ 10 /*[/]*/, /*[# mb:p="dto.sisyutuYoteiKingaku"]*/ 11 /*[/]*/, /*[# mb:p="dto.sisyutuKingaku"]*/ 12 /*[/]*/,
          /*[# mb:p="dto.deleteFlg"]*/ 13 /*[/]*/)

