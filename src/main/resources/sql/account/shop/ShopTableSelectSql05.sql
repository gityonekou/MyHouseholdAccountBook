-- 指定のユーザIDと指定した店舗区分コードのリスト(in条件に指定する店舗区分コード)のデータを条件に店舗テーブル:SHOP_TABLEを参照します。
SELECT * FROM SHOP_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ 
	/*[# th:if="${not #lists.isEmpty(dto.shopKubunCodeList)}"]*/ 
		AND SHOP_KUBUN_CODE IN (/*[# mb:p="dto.shopKubunCodeList"]*/ 2 /*[/]*/)
	/*[/]*/ AND SHOP_SORT < '900'
ORDER BY SHOP_SORT
