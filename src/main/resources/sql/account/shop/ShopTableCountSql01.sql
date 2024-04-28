-- 指定のユーザIDに対応する店舗情報のうち、900番未満のものが何件あるかを取得します。
SELECT COUNT(*) FROM SHOP_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND SHOP_CODE < '900'
