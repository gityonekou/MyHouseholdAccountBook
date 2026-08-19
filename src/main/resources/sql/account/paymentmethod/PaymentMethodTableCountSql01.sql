-- 指定のユーザIDに対応する支払方法情報のうち、システム予約帯(990～999)を除いた件数を取得します(新規コード発番用)。
SELECT COUNT(*) FROM PAYMENT_METHOD_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND PAYMENT_METHOD_CODE < '990'
