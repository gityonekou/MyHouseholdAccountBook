-- 支出項目テーブル:SISYUTU_ITEM_TABLEにデータを追加します。
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, SISYUTU_ITEM_DETAIL_CONTEXT, PARENT_SISYUTU_ITEM_CODE, SISYUTU_ITEM_LEVEL, SISYUTU_ITEM_SORT, ENABLE_UPDATE_FLG)
  VALUES (/*[# mb:p="dto.userId"]*/ 1 /*[/]*/, /*[# mb:p="dto.sisyutuItemCode"]*/ 2 /*[/]*/, /*[# mb:p="dto.sisyutuItemName"]*/ 3 /*[/]*/, /*[# mb:p="dto.sisyutuItemDetailContext"]*/ 4 /*[/]*/, 
          /*[# mb:p="dto.parentSisyutuItemCode"]*/ 5 /*[/]*/, /*[# mb:p="dto.sisyutuItemLevel"]*/ 6 /*[/]*/, /*[# mb:p="dto.sisyutuItemSort"]*/ 7 /*[/]*/, /*[# mb:p="dto.enableUpdateFlg"]*/ 8 /*[/]*/)

