-- 指定のユーザID、固定費支払月を条件に固定費テーブル:FIXED_COST_TABLEを参照します。
-- 支出項目テーブル:SISYUTU_ITEM_TABLEとJOINし、支出項目の表示順(固定費一覧画面と同じ並び)で返すようにする。
SELECT A.* FROM FIXED_COST_TABLE AS A, SISYUTU_ITEM_TABLE AS B
  WHERE A.USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/
   /*[# th:if="${not #lists.isEmpty(dto.fixedCostShiharaiTukiList)}"]*/
       AND A.FIXED_COST_SHIHARAI_TUKI IN (/*[# mb:p="dto.fixedCostShiharaiTukiList"]*/ 2 /*[/]*/)
   /*[/]*/ AND A.DELETE_FLG IS FALSE AND A.USER_ID = B.USER_ID AND A.SISYUTU_ITEM_CODE = B.SISYUTU_ITEM_CODE
   ORDER BY B.SISYUTU_ITEM_SORT, A.FIXED_COST_SHIHARAI_DAY

