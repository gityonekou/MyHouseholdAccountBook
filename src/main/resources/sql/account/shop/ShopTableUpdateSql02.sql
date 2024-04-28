-- 指定の店舗情報で店舗テーブル:SHOP_TABLEの表示順の値を更新します。
UPDATE SHOP_TABLE SET SHOP_SORT = /*[# mb:p="dto.shopSort"]*/ 1 /*[/]*/ WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 2 /*[/]*/ AND SHOP_CODE = /*[# mb:p="dto.shopCode"]*/ 3 /*[/]*/
