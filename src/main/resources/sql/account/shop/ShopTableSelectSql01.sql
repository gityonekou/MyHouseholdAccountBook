-- 指定のユーザIDで店舗テーブル:SHOP_TABLEを検索します
SELECT * FROM SHOP_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ ORDER BY SHOP_SORT
