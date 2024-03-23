-- 支出項目テーブル:SISYUTU_ITEM_TABLEの支出項目詳細内容項目の値を更新します。
UPDATE SISYUTU_ITEM_TABLE SET SISYUTU_ITEM_DETAIL_CONTEXT =  /*[# mb:p="dto.sisyutuItemDetailContext"]*/ 1 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 2 /*[/]*/ AND SISYUTU_ITEM_CODE = /*[# mb:p="dto.sisyutuItemCode"]*/ 3 /*[/]*/
