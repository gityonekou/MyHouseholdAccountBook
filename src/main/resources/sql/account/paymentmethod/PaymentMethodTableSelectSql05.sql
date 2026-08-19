-- 指定のユーザIDに対応する支払方法情報のうち、有効かつシステム予約値を除いたものを表示順で取得します(選択肢用)。
SELECT * FROM PAYMENT_METHOD_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND ENABLE_FLG = true AND PAYMENT_METHOD_CODE < '990' ORDER BY PAYMENT_METHOD_SORT
