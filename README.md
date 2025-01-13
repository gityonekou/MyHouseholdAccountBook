# MyHouseholdAccountBook
マイ家計簿のWebアプリケーションです。<br>
SpringBootを使って作成しています。<br>
各種、詳細設計書も作成予定です。<br>
<br>
最初のベータA版の作成完了しました。（作成期間：2023年8月～2024年12月末(長かった。。)<br>
　：土日など休日を使って作成：java main で17Kとなりました。<br>
　ベータA版：今管理しているExcelの家計簿を登録できる機能まで<br>
<br>
 excel家計簿とこのアプリで並走して、問題なさそうなら本格運用していきます。<br>
 <br>
(2025年～)<br>
本内容取込を優先するので、テストコードは簡易な2機能を優先して作成し、残りは後回しとする<br>
①今読んでいる本の内容を取り込み<br>
　・良いコード、悪いコード<br>
　・デザインパターン(良いコード、悪コードの後に読む）<br>
　・・・その他、いろいろ追加可能性。。
②本格的にテストケースコードを作成していく（規模的に、いつまでかかるか、2026年はじめまでかかるかも。。）<br>
③上記終了後に、買い物登録の以下残を作成<br>
　・イベントに応じた買い物を登録できるようにする<br>
　・買い物登録(買い物商品の詳細をわかるようにする)<br>
　・買い物登録の残は携帯(アンドロイド側）のアプリもkotlinで作るなど、、やることはいろいろある<br>
<br>
<br>
・SpringBoot:3.0.7<br>
・Spring Security:6.0.7<br>
・Mybatis:2.2.2<br>
<br>
A版:1.00：(2023/06/03)～2023/08/26～2025/01/03) java mainステップ数(17K)<br>
A版:1.01：小規模改善取込(A版までの小規模改善取込完了)<br>
以降はB版として修正
B版:2.00：<br>
　・2機能のテストケース作成(下記本内容取り込みでソース構成変わる予定なので、コントローラー⇒DBまでで簡単な機能2つを先行してテストケース作成する(あくまで勉強用)<br>
　　バグあれば、修正<br>
　・今読んでいる本の内容を取り込み<br>
　・イベントの買い物機能追加(夏～冬あたり)<br>
　・テストケース作成(？？？あたり)<br>
C版:3.00：買い物残機能を作成<br>
　・買い物登録(買い物商品の詳細をわかるように)<br>
<br>
1.00版作成後、詳細設計を作成予定だが、時間あるかどうか。。（今ある紙芝居だけになるかもしれない。。）<br>
<br>
派生プロジェクト<br>
　買い物登録の一部機能はアンドロイドアプリ(kotlin)として作成<br>
<br>
<br>
コーディング前の動作確認用サンプルプログラムは以下にて作成してからこちらに実装しています。<br>
「SpringWebSample」<br>
<br>
