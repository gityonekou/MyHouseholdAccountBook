-- 商品テーブル:SHOPPING_ITEM_TABLEの情報を指定の商品情報で更新します。
UPDATE SHOPPING_ITEM_TABLE SET SHOPPING_ITEM_KUBUN_NAME = /*[# mb:p="dto.shoppingItemKubunName"]*/ 1 /*[/]*/, SHOPPING_ITEM_NAME =  /*[# mb:p="dto.shoppingItemName"]*/ 2 /*[/]*/,
    SHOPPING_ITEM_DETAIL_CONTEXT = /*[# mb:p="dto.shoppingItemDetailContext"]*/ 3 /*[/]*/, SHOPPING_ITEM_JAN_CODE = /*[# mb:p="dto.shoppingItemJanCode"]*/ 4 /*[/]*/,
    COMPANY_NAME = /*[# mb:p="dto.companyName"]*/ 5 /*[/]*/, STANDARD_SHOP_CODE = /*[# mb:p="dto.standardShopCode"]*/ 6 /*[/]*/,
    STANDARD_PRICE = /*[# mb:p="dto.standardPrice"]*/ 7 /*[/]*/, CAPACITY = /*[# mb:p="dto.capacity"]*/ 8 /*[/]*/,
    CAPACITY_UNIT = /*[# mb:p="dto.capacityUnit"]*/ 9 /*[/]*/, CALORIES = /*[# mb:p="dto.calories"]*/ 10 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 11 /*[/]*/ AND SHOPPING_ITEM_CODE = /*[# mb:p="dto.shoppingItemCode"]*/ 12 /*[/]*/
