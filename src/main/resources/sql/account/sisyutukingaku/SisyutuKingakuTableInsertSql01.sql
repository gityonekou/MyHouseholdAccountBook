-- 支出金額テーブル：SISYUTU_KINGAKU_TABLEにデータを追加します。
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE, PARENT_SISYUTU_ITEM_CODE, SISYUTU_YOTEI_KINGAKU, SISYUTU_KINGAKU, SISYUTU_KINGAKU_B, SISYUTU_KINGAKU_C, SISYUTU_SIHARAI_DATE)
  VALUES (/*[# mb:p="dto.userId"]*/ 1 /*[/]*/, /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/, /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/, /*[# mb:p="dto.sisyutuItemCode"]*/ 4 /*[/]*/, 
          /*[# mb:p="dto.parentSisyutuItemCode"]*/ 5 /*[/]*/, /*[# mb:p="dto.sisyutuYoteiKingaku"]*/ 6 /*[/]*/, /*[# mb:p="dto.sisyutuKingaku"]*/ 7 /*[/]*/, /*[# mb:p="dto.sisyutuKingakuB"]*/ 8 /*[/]*/,
          /*[# mb:p="dto.sisyutuKingakuC"]*/ 9 /*[/]*/, /*[# mb:p="dto.sisyutuSiharaiDate"]*/ 10 /*[/]*/)

