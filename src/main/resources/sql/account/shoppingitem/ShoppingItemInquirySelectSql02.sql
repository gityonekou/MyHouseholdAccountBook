-- 商品テーブル:SHOPPING_ITEM_TABLE、支出項目テーブル:SISYUTU_ITEM_TABLE、店舗テーブル:SHOP_TABLEから指定の商品検索条件を条件に商品情報を検索します。
SELECT A.SHOPPING_ITEM_CODE, A.SHOPPING_ITEM_KUBUN_NAME, A.SHOPPING_ITEM_NAME, A.SHOPPING_ITEM_DETAIL_CONTEXT, A.SHOPPING_ITEM_JAN_CODE, B.SISYUTU_ITEM_NAME,
       A.COMPANY_NAME, C.SHOP_NAME, A.STANDARD_PRICE
  FROM SHOPPING_ITEM_TABLE AS A
       INNER JOIN SISYUTU_ITEM_TABLE AS B
           ON A.USER_ID = B.USER_ID AND A.SISYUTU_ITEM_CODE = B.SISYUTU_ITEM_CODE
       LEFT OUTER JOIN SHOP_TABLE AS C
           ON A.USER_ID = C.USER_ID  AND A.STANDARD_SHOP_CODE = C.SHOP_CODE
  WHERE A.USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ 
      /*[# th:if="${dto.shoppingItemKubunName} != null"]*/
          /*[# mb:bind="patternShoppingItemKubunName=|%${#likes.escapeWildcard(dto.shoppingItemKubunName)}%|" /]*/
          AND A.SHOPPING_ITEM_KUBUN_NAME LIKE /*[# mb:p="patternShoppingItemKubunName"]*/ 2 /*[/]*/
      /*[/]*/
      /*[# th:if="${dto.shoppingItemName} != null"]*/
          /*[# mb:bind="patternShoppingItemName=|%${#likes.escapeWildcard(dto.shoppingItemName)}%|" /]*/
          AND A.SHOPPING_ITEM_NAME       LIKE /*[# mb:p="patternShoppingItemName"]*/ 3 /*[/]*/
      /*[/]*/
      /*[# th:if="${dto.companyName} != null"]*/
         /*[# mb:bind="patternCompanyName=|%${#likes.escapeWildcard(dto.companyName)}%|" /]*/
         AND A.COMPANY_NAME             LIKE /*[# mb:p="patternCompanyName"]*/ 4 /*[/]*/
      /*[/]*/
  ORDER BY A.SHOPPING_ITEM_CODE DESC
