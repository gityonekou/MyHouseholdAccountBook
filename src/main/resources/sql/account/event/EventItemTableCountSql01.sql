-- 指定のユーザIDに対応するイベント情報が何件あるかを取得します。
SELECT COUNT(*) FROM EVENT_ITEM_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/

