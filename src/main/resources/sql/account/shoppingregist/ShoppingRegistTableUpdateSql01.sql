-- 買い物登録情報テーブル:SHOPPING_REGIST_TABLEの情報を指定の買い物登録情報で更新します。
UPDATE SHOPPING_REGIST_TABLE
  SET SHOP_KUBUN_CODE = /*[# mb:p="dto.shopKubunCode"]*/ 1 /*[/]*/, SHOP_CODE =  /*[# mb:p="dto.shopCode"]*/ 2 /*[/]*/, SHOPPING_DATE = /*[# mb:p="dto.shoppingDate"]*/ 3 /*[/]*/,
      SHOPPING_REMARKS = /*[# mb:p="dto.shoppingRemarks"]*/ 4 /*[/]*/, SHOPPING_FOOD_EXPENSES =  /*[# mb:p="dto.shoppingFoodExpenses"]*/ 5 /*[/]*/, SHOPPING_FOOD_TAX_EXPENSES = /*[# mb:p="dto.shoppingFoodTaxExpenses"]*/ 6 /*[/]*/,
      SHOPPING_FOODB_EXPENSES = /*[# mb:p="dto.shoppingFoodBExpenses"]*/ 7 /*[/]*/, SHOPPING_FOODB_TAX_EXPENSES =  /*[# mb:p="dto.shoppingFoodBTaxExpenses"]*/ 8 /*[/]*/, SHOPPING_FOODC_EXPENSES = /*[# mb:p="dto.shoppingFoodCExpenses"]*/ 9 /*[/]*/,
      SHOPPING_FOODC_TAX_EXPENSES = /*[# mb:p="dto.shoppingFoodCTaxExpenses"]*/ 10 /*[/]*/, SHOPPING_DINE_OUT_EXPENSES =  /*[# mb:p="dto.shoppingDineOutExpenses"]*/ 11 /*[/]*/, SHOPPING_DINE_OUT_TAX_EXPENSES = /*[# mb:p="dto.shoppingDineOutTaxExpenses"]*/ 12 /*[/]*/,
      SHOPPING_CONSUMER_GOODS_EXPENSES = /*[# mb:p="dto.shoppingConsumerGoodsExpenses"]*/ 13 /*[/]*/, SHOPPING_CONSUMER_GOODS_TAX_EXPENSES =  /*[# mb:p="dto.shoppingConsumerGoodsTaxExpenses"]*/ 14 /*[/]*/, SHOPPING_CLOTHES_EXPENSES = /*[# mb:p="dto.shoppingClothesExpenses"]*/ 15 /*[/]*/,
      SHOPPING_CLOTHES_TAX_EXPENSES = /*[# mb:p="dto.shoppingClothesTaxExpenses"]*/ 16 /*[/]*/, SHOPPING_WORK_EXPENSES =  /*[# mb:p="dto.shoppingWorkExpenses"]*/ 17 /*[/]*/, SHOPPING_WORK_TAX_EXPENSES = /*[# mb:p="dto.shoppingWorkTaxExpenses"]*/ 18 /*[/]*/,
      SHOPPING_HOUSE_EQUIPMENT_EXPENSES = /*[# mb:p="dto.shoppingHouseEquipmentExpenses"]*/ 19 /*[/]*/, SHOPPING_HOUSE_EQUIPMENT_TAX_EXPENSES = /*[# mb:p="dto.shoppingHouseEquipmentTaxExpenses"]*/ 20 /*[/]*/, SHOPPING_COUPON_PRICE = /*[# mb:p="dto.shoppingCouponPrice"]*/ 21 /*[/]*/,
      TOTAL_PURCHASE_PRICE = /*[# mb:p="dto.totalPurchasePrice"]*/ 22 /*[/]*/, TAX_TOTAL_PURCHASE_PRICE =  /*[# mb:p="dto.taxTotalPurchasePrice"]*/ 23 /*[/]*/, SHOPPING_TOTAL_AMOUNT = /*[# mb:p="dto.shoppingTotalAmount"]*/ 24 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 25 /*[/]*/ AND TARGET_YEAR = /*[# mb:p="dto.targetYear"]*/ 26 /*[/]*/ AND TARGET_MONTH = /*[# mb:p="dto.targetMonth"]*/ 27 /*[/]*/
      AND SHOPPING_REGIST_CODE = /*[# mb:p="dto.shoppingRegistCode"]*/ 28 /*[/]*/
