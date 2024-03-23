-- 指定のユーザIDを条件に支出項目テーブル:SISYUTU_ITEM_TABLEを参照します。
SELECT * FROM SISYUTU_ITEM_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ ORDER BY SISYUTU_ITEM_SORT
