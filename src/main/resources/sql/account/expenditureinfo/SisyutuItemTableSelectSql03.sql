-- 指定のユーザID、支出項目表示順A、支出項目表示順B(BETWEEN A AND B)を条件に支出項目テーブル:SISYUTU_ITEM_TABLEを参照します。
SELECT * FROM SISYUTU_ITEM_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND SISYUTU_ITEM_SORT BETWEEN /*[# mb:p="dto.sisyutuItemSortA"]*/ 2 /*[/]*/ AND /*[# mb:p="dto.sisyutuItemSortB"]*/ 3 /*[/]*/

