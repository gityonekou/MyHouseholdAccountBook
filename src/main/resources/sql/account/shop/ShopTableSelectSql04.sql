-- 指定のユーザIDと指定した店舗表示順A～店舗表示順B間のデータを条件に店舗テーブル:SHOP_TABLEを参照します。
SELECT * FROM SHOP_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND SHOP_SORT BETWEEN /*[# mb:p="dto.shopSortA"]*/ 2 /*[/]*/ AND /*[# mb:p="dto.shopSortB"]*/ 3 /*[/]*/
