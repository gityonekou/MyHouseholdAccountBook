-- 銀行口座テーブル:BANK_ACCOUNT_TABLEにデータを追加します。
INSERT INTO BANK_ACCOUNT_TABLE (USER_ID, BANK_ACCOUNT_CODE, BANK_NAME, BANK_ACCOUNT_MEMO, BANK_ACCOUNT_SORT, ENABLE_FLG)
VALUES (/*[# mb:p="dto.userId"]*/ 1 /*[/]*/, /*[# mb:p="dto.bankAccountCode"]*/ 2 /*[/]*/, /*[# mb:p="dto.bankName"]*/ 3 /*[/]*/,
        /*[# mb:p="dto.bankAccountMemo"]*/ 4 /*[/]*/, /*[# mb:p="dto.bankAccountSort"]*/ 5 /*[/]*/, /*[# mb:p="dto.enableFlg"]*/ 6 /*[/]*/)
