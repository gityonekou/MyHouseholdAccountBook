-- 指定のユーザIDと指定した支払方法表示順以降のデータを条件に支払方法テーブル:PAYMENT_METHOD_TABLEを参照します。
SELECT * FROM PAYMENT_METHOD_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND PAYMENT_METHOD_SORT >= /*[# mb:p="dto.paymentMethodSort"]*/ 2 /*[/]*/
