-- 指定のユーザIDで店舗テーブル:SHOP_TABLEを検索します
SELECT COUNT(*) FROM SHOP_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND SHOP_CODE < '900'
