-- 指定のユーザIDで銀行口座テーブル:BANK_ACCOUNT_TABLEを検索します
SELECT * FROM BANK_ACCOUNT_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ ORDER BY BANK_ACCOUNT_SORT
