-- 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USER、ユーザテーブル:USERSからユーザ情報の一覧を取得します。
SELECT A.USER_ID, A.NOW_TARGET_YEAR, A.NOW_TARGET_MONTH, A.USER_NAME, B.ENABLED
FROM ACCOUNT_BOOK_USER AS A, USERS AS B
WHERE A.USER_ID = B.USERNAME

