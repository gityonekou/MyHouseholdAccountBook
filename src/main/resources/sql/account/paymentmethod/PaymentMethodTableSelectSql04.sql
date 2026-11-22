-- 指定のユーザIDと指定した支払方法表示順A～支払方法表示順B間のデータを条件に支払方法テーブル:PAYMENT_METHOD_TABLEを参照します。
SELECT * FROM PAYMENT_METHOD_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND PAYMENT_METHOD_SORT BETWEEN /*[# mb:p="dto.paymentMethodSortA"]*/ 2 /*[/]*/ AND /*[# mb:p="dto.paymentMethodSortB"]*/ 3 /*[/]*/
