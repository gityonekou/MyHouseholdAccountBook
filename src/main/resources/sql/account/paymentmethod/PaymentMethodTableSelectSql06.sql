-- 指定のユーザIDに対応する支払方法情報のうち、有効なもの(システム予約値は除外しない)を表示順で取得します(固定費登録画面専用)。
SELECT * FROM PAYMENT_METHOD_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND ENABLE_FLG = true ORDER BY PAYMENT_METHOD_SORT
