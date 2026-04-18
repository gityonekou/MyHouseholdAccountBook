-- 収入テーブル：INCOME_TABLEにデータを追加します。
INSERT INTO INCOME_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_CODE, INCOME_KUBUN, INCOME_DETAIL_CONTEXT, INCOME_KINGAKU, DELETE_FLG)
  VALUES (/*[# mb:p="dto.userId"]*/ 1 /*[/]*/, /*[# mb:p="dto.targetYear"]*/ 2 /*[/]*/, /*[# mb:p="dto.targetMonth"]*/ 3 /*[/]*/, /*[# mb:p="dto.syuunyuuCode"]*/ 4 /*[/]*/, 
          /*[# mb:p="dto.syuunyuuKubun"]*/ 5 /*[/]*/, /*[# mb:p="dto.syuunyuuDetailContext"]*/ 6 /*[/]*/, /*[# mb:p="dto.syuunyuuKingaku"]*/ 7 /*[/]*/, /*[# mb:p="dto.deleteFlg"]*/ 8 /*[/]*/)

