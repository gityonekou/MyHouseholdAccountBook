-- 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USERの指定ユーザ情報を更新します。
UPDATE ACCOUNT_BOOK_USER 
  SET NOW_TARGET_YEAR = /*[# mb:p="dto.nowTargetYear"]*/ 1 /*[/]*/, NOW_TARGET_MONTH = /*[# mb:p="dto.nowTargetMonth"]*/ 2 /*[/]*/, USER_NAME = /*[# mb:p="dto.userName"]*/ 3 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 4 /*[/]*/