-- 指定データで銀行口座テーブル:BANK_ACCOUNT_TABLEの情報を更新します
UPDATE BANK_ACCOUNT_TABLE SET BANK_NAME = /*[# mb:p="dto.bankName"]*/ 1 /*[/]*/, BANK_ACCOUNT_MEMO = /*[# mb:p="dto.bankAccountMemo"]*/ 2 /*[/]*/,
       BANK_ACCOUNT_SORT = /*[# mb:p="dto.bankAccountSort"]*/ 3 /*[/]*/, ENABLE_FLG = /*[# mb:p="dto.enableFlg"]*/ 4 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 5 /*[/]*/ AND BANK_ACCOUNT_CODE = /*[# mb:p="dto.bankAccountCode"]*/ 6 /*[/]*/
