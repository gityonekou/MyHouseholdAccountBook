-- 指定のユーザID、イベントコードを条件にイベントテーブル:EVENT_ITEM_TABLEを参照します。
SELECT * FROM EVENT_ITEM_TABLE WHERE USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND EVENT_CODE = /*[# mb:p="dto.eventCode"]*/ 2 /*[/]*/

