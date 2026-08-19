-- 指定データで支払方法テーブル:PAYMENT_METHOD_TABLEの情報を更新します
UPDATE PAYMENT_METHOD_TABLE SET PAYMENT_METHOD_NAME = /*[# mb:p="dto.paymentMethodName"]*/ 1 /*[/]*/, PAYMENT_METHOD_KUBUN = /*[# mb:p="dto.paymentMethodKubun"]*/ 2 /*[/]*/,
       BANK_ACCOUNT_CODE = /*[# mb:p="dto.bankAccountCode"]*/ 3 /*[/]*/, CLOSING_DAY = /*[# mb:p="dto.closingDay"]*/ 4 /*[/]*/,
       PAYMENT_METHOD_SORT = /*[# mb:p="dto.paymentMethodSort"]*/ 5 /*[/]*/, ENABLE_FLG = /*[# mb:p="dto.enableFlg"]*/ 6 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 7 /*[/]*/ AND PAYMENT_METHOD_CODE = /*[# mb:p="dto.paymentMethodCode"]*/ 8 /*[/]*/
