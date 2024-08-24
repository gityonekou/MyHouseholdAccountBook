-- イベントテーブル:EVENT_ITEM_TABLEにデータを追加します。
INSERT INTO EVENT_ITEM_TABLE (USER_ID, EVENT_CODE, SISYUTU_ITEM_CODE, EVENT_NAME, EVENT_DETAIL_CONTEXT, EVENT_START_DATE, EVENT_END_DATE, EVENT_EXIT_FLG)
  VALUES (/*[# mb:p="dto.userId"]*/ 1 /*[/]*/, /*[# mb:p="dto.eventCode"]*/ 2 /*[/]*/, /*[# mb:p="dto.sisyutuItemCode"]*/ 3 /*[/]*/, /*[# mb:p="dto.eventName"]*/ 4 /*[/]*/, 
          /*[# mb:p="dto.eventDetailContext"]*/ 5 /*[/]*/, /*[# mb:p="dto.eventStartDate"]*/ 6 /*[/]*/, /*[# mb:p="dto.eventEndDate"]*/ 7 /*[/]*/,
          /*[# mb:p="dto.eventExitFlg"]*/ 8 /*[/]*/)
