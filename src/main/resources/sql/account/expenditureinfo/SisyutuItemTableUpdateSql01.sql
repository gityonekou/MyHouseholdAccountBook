-- 支出項目テーブル:SISYUTU_ITEM_TABLEの情報を指定の支出項目情報で更新します。
UPDATE SISYUTU_ITEM_TABLE SET SISYUTU_ITEM_NAME = /*[# mb:p="dto.sisyutuItemName"]*/ 1 /*[/]*/, SISYUTU_ITEM_DETAIL_CONTEXT =  /*[# mb:p="dto.sisyutuItemDetailContext"]*/ 2 /*[/]*/,
    PARENT_SISYUTU_ITEM_CODE = /*[# mb:p="dto.parentSisyutuItemCode"]*/ 3 /*[/]*/, SISYUTU_ITEM_LEVEL = /*[# mb:p="dto.sisyutuItemLevel"]*/ 4 /*[/]*/,
    SISYUTU_ITEM_SORT = /*[# mb:p="dto.sisyutuItemSort"]*/ 5 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 6 /*[/]*/ AND SISYUTU_ITEM_CODE = /*[# mb:p="dto.sisyutuItemCode"]*/ 7 /*[/]*/
