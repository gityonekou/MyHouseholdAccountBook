-- 指定のユーザID、商品JANコードに対応する商品情報が何件あるかを取得します。
SELECT COUNT(*) FROM SHOPPING_ITEM_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND SHOPPING_ITEM_JAN_CODE = /*[# mb:p="dto.shoppingItemJanCode"]*/ 2 /*[/]*/

