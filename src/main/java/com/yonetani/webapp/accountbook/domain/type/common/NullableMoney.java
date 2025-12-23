/**
 * null値を許容する金額を表す値オブジェクトの抽象基底クラスです。
 * null許容金額系ドメインタイプはこのクラスを継承します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/22 : 1.00.00  新規作成
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
 * null値を許容する金額を表す値オブジェクトの抽象基底クラスです。
 *
 * [責務]
 * ・null許容金額の基本的なバリデーション（スケール値、マイナス値）
 * ・null安全な金額の基本演算（加算、減算、乗算）
 * ・null値を考慮した金額の表示形式の統一
 *
 * [設計方針]
 * ・不変性：生成後は値を変更できない
 * ・自己検証：不正な値は生成時に検証（null値は許容）
 * ・型安全性：サブクラスで具体的な金額の型を表現
 * ・null安全性：null値を含む演算を安全に処理
 *
 * [Moneyクラスとの違い]
 * ・Moneyクラス：null非許容、null値はエラー
 * ・NullableMoneyクラス：null許容、null値は0として扱う演算を提供
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
public abstract class NullableMoney {

	// 金額の値（スケール2固定: 小数点以下2桁、null許容）
	private final BigDecimal value;

	/**
	 *<pre>
	 * 金額の値を検証します。
	 *
	 * [検証内容]
	 * ・スケール値チェック（2固定）- null値の場合はチェック不要
	 * ・マイナス値チェック - null値の場合はチェック不要
	 *
	 * [非検証内容]
	 * ・null値チェック - null値を許容するため検証しない
	 *
	 * サブクラスで追加の検証が必要な場合は、コンストラクタでこのメソッドを呼び出した後に
	 * 追加の検証を実行してください。
	 *</pre>
	 * @param value 検証対象の金額（null許容）
	 * @param typeName 金額の型名（エラーメッセージ用）
	 * @throws MyHouseholdAccountBookRuntimeException 検証エラー時
	 *
	 */
	protected static void validate(BigDecimal value, String typeName) {
		// null値の場合は検証不要
		if(value == null) {
			return;
		}
		// ガード節(スケール値が2以外)
		if(value.scale() != 2) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目のスケール値が不正です。管理者に問い合わせてください。[scale=%d]",
					typeName, value.scale()));
		}
		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(value) > 0) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目の設定値がマイナスです。管理者に問い合わせてください。[value=%s]",
					typeName, value.toString()));
		}
	}

	/**
	 *<pre>
	 * 金額の加算を行います（null値伝播方式）。
	 * null安全な演算を提供します。どちらかの値がnullの場合、null以外の値を返します。
	 * 両方nullの場合はnullを返します。
	 * 結果は新しいインスタンスとして返却されます（不変性の維持）。
	 *</pre>
	 * @param other 加算する金額
	 * @return 加算結果のBigDecimal値。両方nullの場合はnull
	 *
	 */
	protected BigDecimal add(NullableMoney other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"加算対象の金額がnullです。管理者に問い合わせてください。");
		}
		// thisがnullの場合はotherの値を返却（nullの可能性あり）
		if(this.value == null) {
			return other.value;
		}
		// otherがnullの場合はthisの値を返却
		if(other.value == null) {
			return this.value;
		}
		// 両方とも値がある場合は加算
		return this.value.add(other.value);
	}

	/**
	 *<pre>
	 * 金額の加算を行います（null値を0として扱う方式）。
	 * null値は0として扱い、必ず非null値を返却します。
	 * 結果は新しいインスタンスとして返却されます（不変性の維持）。
	 *</pre>
	 * @param other 加算する金額
	 * @return 加算結果のBigDecimal値（非null）
	 *
	 */
	protected BigDecimal addTreatingNullAsZero(NullableMoney other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"加算対象の金額がnullです。管理者に問い合わせてください。");
		}
		// 両方の値をnull安全に取得して加算
		return this.getNullSafeValue().add(other.getNullSafeValue());
	}

	/**
	 *<pre>
	 * 金額の減算を行います（null値エラー方式）。
	 * null安全な演算を提供します。
	 *
	 * [動作仕様]
	 * ・減算対象の値(other)がnullの場合: thisの値を返却（this - 0と同義）
	 * ・thisの値がnullの場合: 例外をスロー（null値から減算することは、マイナス値になる可能性があるため不正とする）
	 * ・両方とも値がある場合: 減算結果を返却
	 *
	 * 結果は新しいインスタンスとして返却されます（不変性の維持）。
	 *</pre>
	 * @param other 減算する金額
	 * @return 減算結果のBigDecimal値
	 * @throws MyHouseholdAccountBookRuntimeException thisの値がnullの場合
	 *
	 */
	protected BigDecimal subtract(NullableMoney other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"減算対象の金額がnullです。管理者に問い合わせてください。");
		}
		// thisがnullの場合、減算後の値がマイナスとなる計算を実施したことになるのでデータ不正
		if(this.value == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"null値の金額から減算することはできません。管理者に問い合わせてください。[減算値=" + other.toString() + "]");
		}
		// otherがnullの場合はthisの値を返却（this - 0）
		if(other.value == null) {
			return this.value;
		}
		// 両方とも値がある場合は減算
		return this.value.subtract(other.value);
	}

	/**
	 *<pre>
	 * 金額の減算を行います（null値を0として扱う方式）。
	 * null値は0として扱い、必ず非null値を返却します。
	 * 結果は新しいインスタンスとして返却されます（不変性の維持）。
	 *</pre>
	 * @param other 減算する金額
	 * @return 減算結果のBigDecimal値（非null）
	 *
	 */
	protected BigDecimal subtractTreatingNullAsZero(NullableMoney other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"減算対象の金額がnullです。管理者に問い合わせてください。");
		}
		// 両方の値をnull安全に取得して減算
		return this.getNullSafeValue().subtract(other.getNullSafeValue());
	}

	/**
	 *<pre>
	 * 金額の値(BigDecimal)を返します。値がnullの場合、値0のBigDecimalを返します。
	 *</pre>
	 * @return 金額の値(BigDecimal)。値がnullの場合、値0のBigDecimal
	 *
	 */
	public BigDecimal getNullSafeValue() {
		// valueがnullの場合、値0のBigDecimalを返却
		if(value == null) {
			return BigDecimal.ZERO.setScale(2);
		}
		return value;
	}

	/**
	 *<pre>
	 * 金額の比較を行います。
	 * null値は0として扱います。
	 *</pre>
	 * @param other 比較対象の金額
	 * @return this < other の場合は負の値、this == other の場合は0、this > other の場合は正の値
	 *
	 */
	public int compareTo(NullableMoney other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"比較対象の金額がnullです。管理者に問い合わせてください。");
		}
		return this.getNullSafeValue().compareTo(other.getNullSafeValue());
	}

	/**
	 *<pre>
	 * 金額が0かどうかを判定します。
	 * null値は0として扱います。
	 *</pre>
	 * @return 0の場合true、それ以外の場合false
	 *
	 */
	public boolean isZero() {
		return this.getNullSafeValue().compareTo(BigDecimal.ZERO) == 0;
	}

	/**
	 *<pre>
	 * 金額が正の値かどうかを判定します。
	 * null値は0として扱います。
	 *</pre>
	 * @return 正の値の場合true、0または負の値の場合false
	 *
	 */
	public boolean isPositive() {
		return this.getNullSafeValue().compareTo(BigDecimal.ZERO) > 0;
	}

	/**
	 *<pre>
	 * 金額が負の値かどうかを判定します。
	 * null値は0として扱います。
	 *</pre>
	 * @return 負の値の場合true、0または正の値の場合false
	 *
	 */
	public boolean isNegative() {
		return this.getNullSafeValue().compareTo(BigDecimal.ZERO) < 0;
	}

	/**
	 *<pre>
	 * 金額がnullかどうかを判定します。
	 *</pre>
	 * @return null値の場合true、それ以外の場合false
	 *
	 */
	public boolean isNull() {
		return this.value == null;
	}

	/**
	 *<pre>
	 * 画面表示・入力用に整数値を取得します。
	 * スケール0で四捨五入した整数値を返却します。
	 * null値の場合は0を返却します。
	 *
	 * [使用例]
	 * - 画面の入力フィールドへの初期値設定
	 * - 整数での計算が必要な場合
	 *</pre>
	 * @return 整数値（long型）。null値の場合は0
	 *
	 */
	public long toIntegerValue() {
		return this.getNullSafeValue().setScale(0, RoundingMode.HALF_UP).longValue();
	}

	/**
	 *<pre>
	 * 画面入力用に整数の文字列を取得します。
	 * カンマ区切りなしの整数値を返却します。
	 * null値の場合は"0"を返却します。
	 *</pre>
	 * @return 整数値の文字列（例: "10000"）。null値の場合は"0"
	 *
	 */
	public String toIntegerString() {
		return String.valueOf(toIntegerValue());
	}

	/**
	 *<pre>
	 * 金額を画面表示用にフォーマットします。
	 * スケール0で四捨五入し、カンマ区切り+円表記の文字列を返却します。
	 * null値の場合は空文字列を返却します。
	 *</pre>
	 * @return フォーマット済み文字列（例: "10,000円"）。null値の場合は空文字列
	 *
	 */
	public String toFormatString() {
		// null値の場合は空文字列を返却
		if(value == null) {
			return "";
		}
		// スケール0で四捨五入
		long roundedValue = value.setScale(0, RoundingMode.HALF_UP).longValue();
		// カンマ区切り+円表記
		return String.format("%,d円", roundedValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// 値の文字列表現を返却（デバッグ用）
		// null値の場合は空文字列を返却
		if(value == null) {
			return "";
		}
		return value.toString();
	}
}
