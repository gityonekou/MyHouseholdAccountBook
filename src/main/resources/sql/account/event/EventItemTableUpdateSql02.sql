-- 指定のイベント情報をイベントテーブル:EVENT_ITEM_TABLEから論理削除します。
UPDATE EVENT_ITEM_TABLE SET EVENT_EXIT_FLG = TRUE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND EVENT_CODE = /*[# mb:p="dto.eventCode"]*/ 2 /*[/]*/

