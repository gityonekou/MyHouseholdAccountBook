-- 指定のユーザIDと指定した店舗区分コードのデータを条件に店舗テーブル:SHOP_TABLEを参照します。
SELECT * FROM SHOP_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND SHOP_KUBUN_CODE = /*[# mb:p="dto.shopKubunCode"]*/ 2 /*[/]*/
ORDER BY SHOP_SORT
