-- 指定の銀行口座情報で銀行口座テーブル:BANK_ACCOUNT_TABLEの表示順の値を更新します。
UPDATE BANK_ACCOUNT_TABLE SET BANK_ACCOUNT_SORT = /*[# mb:p="dto.bankAccountSort"]*/ 1 /*[/]*/ WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 2 /*[/]*/ AND BANK_ACCOUNT_CODE = /*[# mb:p="dto.bankAccountCode"]*/ 3 /*[/]*/
