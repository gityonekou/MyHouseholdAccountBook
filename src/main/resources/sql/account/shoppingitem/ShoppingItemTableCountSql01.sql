-- 指定のユーザIDに対応する商品情報が何件あるかを取得します。
SELECT COUNT(*) FROM SHOPPING_ITEM_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/

