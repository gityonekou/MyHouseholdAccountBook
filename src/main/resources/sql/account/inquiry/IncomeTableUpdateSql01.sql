-- 収入テーブル:INCOME_TABLEの情報を指定の収入情報で更新します。
UPDATE INCOME_TABLE SET INCOME_KUBUN = /*[# mb:p="dto.syuunyuuKubun"]*/ 1 /*[/]*/, INCOME_DETAIL_CONTEXT =  /*[# mb:p="dto.syuunyuuDetailContext"]*/ 2 /*[/]*/,
    INCOME_KINGAKU = /*[# mb:p="dto.syuunyuuKingaku"]*/ 3 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 4 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 5 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 6 /*[/]*/
      AND INCOME_CODE = /*[# mb:p="dto.syuunyuuCode"]*/ 7 /*[/]*/
