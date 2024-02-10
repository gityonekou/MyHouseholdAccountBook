-- 指定データで店舗テーブル:SHOP_TABLEの情報を更新します
UPDATE SHOP_TABLE SET SHOP_KUBUN_CODE = /*[# mb:p="dto.shopKubunCode"]*/ 1 /*[/]*/, SHOP_NAME = /*[# mb:p="dto.shopName"]*/ 2 /*[/]*/, SHOP_SORT = /*[# mb:p="dto.shopSort"]*/ 3 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 4 /*[/]*/ AND SHOP_CODE = /*[# mb:p="dto.shopCode"]*/ 5 /*[/]*/
