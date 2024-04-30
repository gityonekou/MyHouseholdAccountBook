-- 指定のユーザID、支出項目コードを条件に商品テーブル:SHOPPING_ITEM_TABLEを参照します。
SELECT * FROM SHOPPING_ITEM_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND SISYUTU_ITEM_CODE = /*[# mb:p="dto.sisyutuItemCode"]*/ 2 /*[/]*/
