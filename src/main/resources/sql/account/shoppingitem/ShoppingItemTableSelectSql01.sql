-- 指定のユーザID、商品コードを条件に商品テーブル:SHOPPING_ITEM_TABLEを参照します。
SELECT * FROM SHOPPING_ITEM_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND SHOPPING_ITEM_CODE = /*[# mb:p="dto.shoppingItemCode"]*/ 2 /*[/]*/
