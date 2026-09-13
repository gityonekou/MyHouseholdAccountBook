-- 支払方法テーブル:PAYMENT_METHOD_TABLEにデータを追加します。
INSERT INTO PAYMENT_METHOD_TABLE (USER_ID, PAYMENT_METHOD_CODE, PAYMENT_METHOD_NAME, PAYMENT_METHOD_MEMO, PAYMENT_METHOD_KUBUN,
       BANK_ACCOUNT_CODE, CLOSING_DAY, PAYMENT_METHOD_SORT, ENABLE_FLG, ENABLE_UPDATE_FLG)
VALUES (/*[# mb:p="dto.userId"]*/ 1 /*[/]*/, /*[# mb:p="dto.paymentMethodCode"]*/ 2 /*[/]*/, /*[# mb:p="dto.paymentMethodName"]*/ 3 /*[/]*/,
        /*[# mb:p="dto.paymentMethodMemo"]*/ 4 /*[/]*/, /*[# mb:p="dto.paymentMethodKubun"]*/ 5 /*[/]*/, /*[# mb:p="dto.bankAccountCode"]*/ 6 /*[/]*/, /*[# mb:p="dto.closingDay"]*/ 7 /*[/]*/,
        /*[# mb:p="dto.paymentMethodSort"]*/ 8 /*[/]*/, /*[# mb:p="dto.enableFlg"]*/ 9 /*[/]*/, /*[# mb:p="dto.enableUpdateFlg"]*/ 10 /*[/]*/)
