-- 指定のユーザID、店舗コードを条件に店舗テーブル:SHOP_TABLEを参照します。
SELECT * FROM SHOP_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND SHOP_CODE = /*[# mb:p="dto.shopCode"]*/ 2 /*[/]*/
