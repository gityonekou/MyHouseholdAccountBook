-- 指定のユーザID、銀行口座コードを条件に銀行口座テーブル:BANK_ACCOUNT_TABLEを参照します。
SELECT * FROM BANK_ACCOUNT_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND BANK_ACCOUNT_CODE = /*[# mb:p="dto.bankAccountCode"]*/ 2 /*[/]*/
