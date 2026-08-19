-- 指定のユーザIDに対応する銀行口座情報が何件あるかを取得します(上限99件チェック用)。
SELECT COUNT(*) FROM BANK_ACCOUNT_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/
