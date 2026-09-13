-- 指定データで支払方法テーブル:PAYMENT_METHOD_TABLEの情報を更新します
UPDATE PAYMENT_METHOD_TABLE SET PAYMENT_METHOD_NAME = /*[# mb:p="dto.paymentMethodName"]*/ 1 /*[/]*/, PAYMENT_METHOD_MEMO = /*[# mb:p="dto.paymentMethodMemo"]*/ 2 /*[/]*/,
       PAYMENT_METHOD_KUBUN = /*[# mb:p="dto.paymentMethodKubun"]*/ 3 /*[/]*/,
       BANK_ACCOUNT_CODE = /*[# mb:p="dto.bankAccountCode"]*/ 4 /*[/]*/, CLOSING_DAY = /*[# mb:p="dto.closingDay"]*/ 5 /*[/]*/,
       PAYMENT_METHOD_SORT = /*[# mb:p="dto.paymentMethodSort"]*/ 6 /*[/]*/, ENABLE_FLG = /*[# mb:p="dto.enableFlg"]*/ 7 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 8 /*[/]*/ AND PAYMENT_METHOD_CODE = /*[# mb:p="dto.paymentMethodCode"]*/ 9 /*[/]*/
