-- 指定の支払方法情報で支払方法テーブル:PAYMENT_METHOD_TABLEの表示順の値を更新します。
UPDATE PAYMENT_METHOD_TABLE SET PAYMENT_METHOD_SORT = /*[# mb:p="dto.paymentMethodSort"]*/ 1 /*[/]*/ WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 2 /*[/]*/ AND PAYMENT_METHOD_CODE = /*[# mb:p="dto.paymentMethodCode"]*/ 3 /*[/]*/
