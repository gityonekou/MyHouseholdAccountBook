-- 指定のユーザID、支払方法コードを条件に支払方法テーブル:PAYMENT_METHOD_TABLEを参照します。
SELECT * FROM PAYMENT_METHOD_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND PAYMENT_METHOD_CODE = /*[# mb:p="dto.paymentMethodCode"]*/ 2 /*[/]*/
