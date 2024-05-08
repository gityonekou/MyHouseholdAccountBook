-- 商品テーブル:SHOPPING_ITEM_TABLEにデータを追加します。
INSERT INTO SHOPPING_ITEM_TABLE (USER_ID, SHOPPING_ITEM_CODE, SHOPPING_ITEM_KUBUN_NAME, SHOPPING_ITEM_NAME, SHOPPING_ITEM_DETAIL_CONTEXT, SHOPPING_ITEM_JAN_CODE, SISYUTU_ITEM_CODE, COMPANY_NAME, STANDARD_SHOP_CODE, STANDARD_PRICE)
  VALUES (/*[# mb:p="dto.userId"]*/ 1 /*[/]*/, /*[# mb:p="dto.shoppingItemCode"]*/ 2 /*[/]*/, /*[# mb:p="dto.shoppingItemKubunName"]*/ 3 /*[/]*/, /*[# mb:p="dto.shoppingItemName"]*/ 4 /*[/]*/, 
          /*[# mb:p="dto.shoppingItemDetailContext"]*/ 5 /*[/]*/, /*[# mb:p="dto.shoppingItemJanCode"]*/ 6 /*[/]*/, /*[# mb:p="dto.sisyutuItemCode"]*/ 7 /*[/]*/, /*[# mb:p="dto.companyName"]*/ 8 /*[/]*/,
          /*[# mb:p="dto.standardShopCode"]*/ 9 /*[/]*/, /*[# mb:p="dto.standardPrice"]*/ 10 /*[/]*/)

