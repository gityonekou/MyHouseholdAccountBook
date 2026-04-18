-- 支出金額テーブル：SISYUTU_KINGAKU_TABLEの情報を指定の支出金額情報で更新します。
UPDATE SISYUTU_KINGAKU_TABLE SET SISYUTU_KINGAKU = /*[# mb:p="dto.sisyutuKingaku"]*/ 1 /*[/]*/, SISYUTU_KINGAKU_B =  /*[# mb:p="dto.sisyutuKingakuB"]*/ 2 /*[/]*/,
    SISYUTU_KINGAKU_C = /*[# mb:p="dto.sisyutuKingakuC"]*/ 3 /*[/]*/, SISYUTU_SIHARAI_DATE =  /*[# mb:p="dto.sisyutuSiharaiDate"]*/ 4 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 5 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 6 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 7 /*[/]*/
      AND SISYUTU_ITEM_CODE = /*[# mb:p="dto.sisyutuItemCode"]*/ 8 /*[/]*/
