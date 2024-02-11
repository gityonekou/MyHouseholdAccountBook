-- 指定ユーザの権限情報を取得します
SELECT authority FROM authorities WHERE username = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/
