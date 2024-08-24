-- 指定のイベント情報でイベントテーブル:EVENT_ITEM_TABLEを更新します。
UPDATE EVENT_ITEM_TABLE SET EVENT_NAME = /*[# mb:p="dto.eventName"]*/ 1 /*[/]*/, EVENT_DETAIL_CONTEXT = /*[# mb:p="dto.eventDetailContext"]*/ 2 /*[/]*/,
    EVENT_START_DATE = /*[# mb:p="dto.eventStartDate"]*/ 3 /*[/]*/, EVENT_END_DATE = /*[# mb:p="dto.eventEndDate"]*/ 4 /*[/]*/
  WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 5 /*[/]*/ AND EVENT_CODE = /*[# mb:p="dto.eventCode"]*/ 6 /*[/]*/
