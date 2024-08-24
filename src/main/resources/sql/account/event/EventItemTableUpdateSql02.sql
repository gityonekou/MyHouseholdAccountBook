-- 指定のイベント情報でイベントテーブル:EVENT_ITEM_TABLEを更新します。
UPDATE EVENT_ITEM_TABLE SET EVENT_EXIT_FLG = TRUE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND EVENT_CODE = /*[# mb:p="dto.eventCode"]*/ 2 /*[/]*/

