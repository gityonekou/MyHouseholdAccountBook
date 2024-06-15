-- 指定のユーザID、支出項目コードに対応する固定費情報が何件あるかを取得します。
SELECT COUNT(*) FROM FIXED_COST_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND SISYUTU_ITEM_CODE = /*[# mb:p="dto.sisyutuItemCode"]*/ 2 /*[/]*/ AND DELETE_FLG IS FALSE

