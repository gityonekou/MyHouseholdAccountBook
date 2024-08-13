-- 指定のユーザID、固定費支払月を条件に固定費テーブル:FIXED_COST_TABLEを参照します。
SELECT * FROM FIXED_COST_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/
   /*[# th:if="${not #lists.isEmpty(dto.fixedCostShiharaiTukiList)}"]*/ 
       AND FIXED_COST_SHIHARAI_TUKI IN (/*[# mb:p="dto.fixedCostShiharaiTukiList"]*/ 2 /*[/]*/)
   /*[/]*/ AND DELETE_FLG IS FALSE
   ORDER BY FIXED_COST_SHIHARAI_DAY

