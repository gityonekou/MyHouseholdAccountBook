-- 指定のユーザIDと指定した店舗表示順以降のデータを条件に店舗テーブル:SHOP_TABLEを参照します。
SELECT * FROM SHOP_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND SHOP_SORT BETWEEN /*[# mb:p="dto.shopSort"]*/ 2 /*[/]*/ AND '899'
