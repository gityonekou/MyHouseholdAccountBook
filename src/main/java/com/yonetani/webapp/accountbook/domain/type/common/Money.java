/**
 * 金額を表す値オブジェクトの抽象基底クラスです。
 * すべての金額系ドメインタイプはこのクラスを継承します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 金額を表す値オブジェクトの抽象基底クラスです。
 *
 * [責務]
 * ・金額の基本的なバリデーション（null、スケール値）
 * ・金額の基本演算（加算、減算）
 * ・金額の表示形式の統一
 *
 * [設計方針]
 * ・不変性：生成後は値を変更できない
 * ・自己検証：不正な値は生成時に検証
 * ・型安全性：サブクラスで具体的な金額の型を表現
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public abstract class Money {

	// 金額の値（スケール2固定: 小数点以下2桁）
	private final BigDecimal value;

	/**
	 *<pre>
	 * 金額の値を検証します。
	 *
	 * [検証内容]
	 * ・null値チェック
	 * ・スケール値チェック（2固定）
	 *
	 * サブクラスで追加の検証が必要な場合は、コンストラクタでこのメソッドを呼び出した後に
	 * 追加の検証を実行してください。
	 *</pre>
	 * @param value 検証対象の金額
	 * @param typeName 金額の型名（エラーメッセージ用）
	 * @throws MyHouseholdAccountBookRuntimeException 検証エラー時
	 *
	 */
	protected static void validate(BigDecimal value, String typeName) {
		// ガード節(null)
		if(value == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目の設定値がnullです。管理者に問い合わせてください。", typeName));
		}
		// ガード節(スケール値が2以外)
		if(value.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目のスケール値が不正です。管理者に問い合わせてください。[scale=%d]",
					typeName, value.scale()));
		}
	}

	/**
	 *<pre>
	 * 金額の加算を行います。
	 * 結果は新しいインスタンスとして返却されます（不変性の維持）。
	 *</pre>
	 * @param other 加算する金額
	 * @return 加算結果のBigDecimal値
	 *
	 */
	protected BigDecimal add(Money other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"加算対象の金額がnullです。管理者に問い合わせてください。");
		}
		return this.value.add(other.value);
	}

	/**
	 *<pre>
	 * 金額の減算を行います。
	 * 結果は新しいインスタンスとして返却されます（不変性の維持）。
	 *</pre>
	 * @param other 減算する金額
	 * @return 減算結果のBigDecimal値
	 *
	 */
	protected BigDecimal subtract(Money other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"減算対象の金額がnullです。管理者に問い合わせてください。");
		}
		return this.value.subtract(other.value);
	}

	/**
	 *<pre>
	 * 金額の比較を行います。
	 *</pre>
	 * @param other 比較対象の金額
	 * @return this < other の場合は負の値、this == other の場合は0、this > other の場合は正の値
	 *
	 */
	public int compareTo(Money other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"比較対象の金額がnullです。管理者に問い合わせてください。");
		}
		return this.value.compareTo(other.value);
	}

	/**
	 *<pre>
	 * 金額が0かどうかを判定します。
	 *</pre>
	 * @return 0の場合true、それ以外の場合false
	 *
	 */
	public boolean isZero() {
		return this.value.compareTo(BigDecimal.ZERO) == 0;
	}

	/**
	 *<pre>
	 * 金額が正の値かどうかを判定します。
	 *</pre>
	 * @return 正の値の場合true、0または負の値の場合false
	 *
	 */
	public boolean isPositive() {
		return this.value.compareTo(BigDecimal.ZERO) > 0;
	}

	/**
	 *<pre>
	 * 金額が負の値かどうかを判定します。
	 *</pre>
	 * @return 負の値の場合true、0または正の値の場合false
	 *
	 */
	public boolean isNegative() {
		return this.value.compareTo(BigDecimal.ZERO) < 0;
	}

	/**
	 *<pre>
	 * 画面表示・入力用に整数値を取得します。
	 * スケール0で四捨五入した整数値を返却します。
	 *
	 * [使用例]
	 * - 画面の入力フィールドへの初期値設定
	 * - 整数での計算が必要な場合
	 *</pre>
	 * @return 整数値（long型）
	 *
	 */
	public long toIntegerValue() {
		return this.value.setScale(0, RoundingMode.HALF_UP).longValue();
	}

	/**
	 *<pre>
	 * 画面入力用に整数の文字列を取得します。
	 * カンマ区切りなしの整数値を返却します。
	 *</pre>
	 * @return 整数値の文字列（例: "10000"）
	 *
	 */
	public String toIntegerString() {
		return String.valueOf(toIntegerValue());
	}

	/**
	 *<pre>
	 * 金額を画面表示用にフォーマットします。
	 * スケール0で四捨五入し、カンマ区切り+円表記の文字列を返却します。
	 *</pre>
	 * @return フォーマット済み文字列（例: "10,000円"）
	 *
	 */
	public String toFormatString() {
		// スケール0で四捨五入
		long roundedValue = this.value.setScale(0, RoundingMode.HALF_UP).longValue();
		// カンマ区切り+円表記
		return String.format("%,d円", roundedValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// 値の文字列表現を返却（デバッグ用）
		return value.toString();
	}
}
