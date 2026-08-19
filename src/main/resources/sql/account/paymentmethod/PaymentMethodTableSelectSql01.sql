-- 指定のユーザIDで支払方法テーブル:PAYMENT_METHOD_TABLEを検索します(全件、システム予約値を含む)
SELECT * FROM PAYMENT_METHOD_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ ORDER BY PAYMENT_METHOD_SORT
