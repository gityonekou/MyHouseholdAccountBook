-- 指定のユーザID、支出項目コードを条件に支出項目テーブル:SISYUTU_ITEM_TABLEを参照します。
SELECT * FROM SISYUTU_ITEM_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND SISYUTU_ITEM_CODE = /*[# mb:p="dto.sisyutuItemCode"]*/ 2 /*[/]*/
