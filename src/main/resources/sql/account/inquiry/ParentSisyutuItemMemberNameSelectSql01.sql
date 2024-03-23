-- 指定のユーザID、親の支出項目コードを条件に支出項目テーブル:SISYUTU_ITEM_TABLEを参照し、親の支出項目に属する支出項目の名称一覧を取得します。
SELECT SISYUTU_ITEM_NAME FROM SISYUTU_ITEM_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND PARENT_SISYUTU_ITEM_CODE = /*[# mb:p="dto.sisyutuItemCode"]*/ 2 /*[/]*/
  AND SISYUTU_ITEM_CODE != PARENT_SISYUTU_ITEM_CODE
