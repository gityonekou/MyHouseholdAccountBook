-- 指定のユーザIDに対応する銀行口座情報のうち、有効な口座のみを表示順で取得します(選択肢用)。
SELECT * FROM BANK_ACCOUNT_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND ENABLE_FLG = true ORDER BY BANK_ACCOUNT_SORT
