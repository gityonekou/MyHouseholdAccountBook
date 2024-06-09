-- 指定のユーザIDに対応する固定費情報が何件あるかを取得します。
SELECT COUNT(*) FROM FIXED_COST_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/

