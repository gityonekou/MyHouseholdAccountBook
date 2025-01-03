-- ユニークキー(ユーザID、対象年、対象月、買い物登録コード)を条件に買い物登録情報テーブル:SHOPPING_REGIST_TABLEを参照します。
SELECT * FROM SHOPPING_REGIST_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/
  AND SHOPPING_REGIST_CODE = /*[# mb:p="dto.shoppingRegistCode"]*/ 4 /*[/]*/
