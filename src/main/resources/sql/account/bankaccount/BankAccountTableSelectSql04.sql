-- 指定のユーザIDと指定した銀行口座表示順A～銀行口座表示順B間のデータを条件に銀行口座テーブル:BANK_ACCOUNT_TABLEを参照します。
SELECT * FROM BANK_ACCOUNT_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND BANK_ACCOUNT_SORT BETWEEN /*[# mb:p="dto.bankAccountSortA"]*/ 2 /*[/]*/ AND /*[# mb:p="dto.bankAccountSortB"]*/ 3 /*[/]*/
