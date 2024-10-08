-- 固定費テーブル:FIXED_COST_TABLE、支出項目テーブル:SISYUTU_ITEM_TABLEから指定のユーザID、支出項目コードを条件に固定費情報を参照します。
SELECT A.FIXED_COST_CODE, A.FIXED_COST_NAME, A.FIXED_COST_DETAIL_CONTEXT, B.SISYUTU_ITEM_NAME, A.FIXED_COST_SHIHARAI_TUKI,
       A.FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT, A.FIXED_COST_SHIHARAI_DAY, A.SHIHARAI_KINGAKU
  FROM FIXED_COST_TABLE AS A, SISYUTU_ITEM_TABLE AS B
  WHERE A.USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND A.SISYUTU_ITEM_CODE = /*[# mb:p="dto.sisyutuItemCode"]*/ 2 /*[/]*/ AND A.DELETE_FLG IS FALSE
    AND A.USER_ID = B.USER_ID AND A.SISYUTU_ITEM_CODE = B.SISYUTU_ITEM_CODE
  ORDER BY B.SISYUTU_ITEM_SORT
