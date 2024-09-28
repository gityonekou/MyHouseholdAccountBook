-- 固定費テーブル:FIXED_COST_TABLEの情報を指定の固定費情報で更新します。
UPDATE FIXED_COST_TABLE SET FIXED_COST_NAME = /*[# mb:p="dto.fixedCostName"]*/ 1 /*[/]*/, FIXED_COST_DETAIL_CONTEXT = /*[# mb:p="dto.fixedCostDetailContext"]*/ 2 /*[/]*/, FIXED_COST_KUBUN = /*[# mb:p="dto.fixedCostKubun"]*/ 3 /*[/]*/, 
    FIXED_COST_SHIHARAI_TUKI = /*[# mb:p="dto.fixedCostShiharaiTuki"]*/ 4 /*[/]*/, FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT = /*[# mb:p="dto.fixedCostShiharaiTukiOptionalContext"]*/ 5 /*[/]*/,
    FIXED_COST_SHIHARAI_DAY = /*[# mb:p="dto.fixedCostShiharaiDay"]*/ 6 /*[/]*/, SHIHARAI_KINGAKU = /*[# mb:p="dto.shiharaiKingaku"]*/ 7 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 8 /*[/]*/ AND FIXED_COST_CODE = /*[# mb:p="dto.fixedCostCode"]*/ 9 /*[/]*/
