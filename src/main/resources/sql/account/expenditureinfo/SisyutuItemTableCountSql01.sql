-- 指定のユーザIDに対応する支出項目情報が何件あるかを取得します
SELECT COUNT(*) FROM SISYUTU_ITEM_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/

