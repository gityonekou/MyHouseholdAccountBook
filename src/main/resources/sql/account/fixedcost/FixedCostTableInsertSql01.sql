-- 固定費テーブル:FIXED_COST_TABLEにデータを追加します。
INSERT INTO FIXED_COST_TABLE (USER_ID, FIXED_COST_CODE, FIXED_COST_NAME, FIXED_COST_DETAIL_CONTEXT, SISYUTU_ITEM_CODE, FIXED_COST_SHIHARAI_TUKI, FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT, SHIHARAI_KINGAKU)
  VALUES (/*[# mb:p="dto.userId"]*/ 1 /*[/]*/, /*[# mb:p="dto.fixedCostCode"]*/ 2 /*[/]*/, /*[# mb:p="dto.fixedCostName"]*/ 3 /*[/]*/, /*[# mb:p="dto.fixedCostDetailContext"]*/ 4 /*[/]*/, 
          /*[# mb:p="dto.sisyutuItemCode"]*/ 5 /*[/]*/, /*[# mb:p="dto.fixedCostShiharaiTuki"]*/ 6 /*[/]*/, /*[# mb:p="dto.fixedCostShiharaiTukiOptionalContext"]*/ 7 /*[/]*/, /*[# mb:p="dto.shiharaiKingaku"]*/ 8 /*[/]*/
          )

