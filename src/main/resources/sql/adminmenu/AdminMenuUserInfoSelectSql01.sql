-- 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USER、ユーザテーブル:USERSから指定ユーザIDのユーザ情報を取得します。
SELECT A.USER_ID, A.NOW_TARGET_YEAR, A.NOW_TARGET_MONTH, A.USER_NAME, B.enabled
FROM ACCOUNT_BOOK_USER AS A, users AS B
WHERE A.USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND A.USER_ID = B.username
