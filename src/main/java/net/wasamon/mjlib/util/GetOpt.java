package net.wasamon.mjlib.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 引数の解析を行い、その結果を保持するクラス。<br>
 * GetOpt("hvf:", "prefix:", args)などとして利用<br>
 * 最初の引数で一文字のオプションを、第二引数ではロングオプションを設定。<br>
 * ":"を末尾につけることで、そのオプションは引数をとることができる。<br>
 * この引数は空白または"="で識別される。<br>
 * また、オプションに"::"をつけると、その後の文字列すべてを、そのオプションの引数として処理する。<br>
 * - や -- の次、また、-ではじまらない文字列からを引数として保持する。<br>
 *
 * @version $Id: GetOpt.java,v 1.3 2004/05/24 05:24:35 miyo Exp $
 * @author Takefumi MIYOSHI
 *
 */
public class GetOpt {

	private String[] args;

	private NamedArrayList opts;

	/** 引数つきのオプションを保持するのリスト */
	private NamedArrayList opt_with_arg;

	/** 引数つきではないオプションフラグのリスト */
	private NamedArrayList opt_flag;

	/** 解析の結果不明だったリスト */
	private ArrayList unknown;

	private NamedObject opt_with_arg_rest = new NamedObject("");

	private boolean result = true;

	/**
	 * コンストラクタ
	 *
	 * @param sp
	 *            解析したい一文字オプションの連続(-vとか)
	 * @param lps
	 *            解析したいロングオプションのcommma separate羅列
	 * @param ptn
	 *            解析すべき文字列の配列
	 */
	public GetOpt(String sp, String lps, String ptn[]) {
		this(sp, lps, ptn, 0);
	}

	/**
	 * コンストラクタ
	 *
	 * @param sp
	 *            解析したい一文字オプションの連続(-vとか)
	 * @param lps
	 *            解析したいロングオプションのcommma separate羅列
	 * @param ptn
	 *            解析すべき文字列の配列
	 * @param offset
	 *            解析すべき文字のオフセット
	 */
	public GetOpt(String sp, String lps, String ptn[], int offset) {
		args = new String[0];
		opt_with_arg = new NamedArrayList();
		opt_flag = new NamedArrayList();
		opts = new NamedArrayList();
		unknown = new ArrayList();

		StringTokenizer st = new StringTokenizer(lps, ",");
		String lp[] = new String[st.countTokens()];
		for (int i = 0; i < lp.length; i++) {
			lp[i] = st.nextToken();
		}

		makeOptList(sp, lp);

		analyze(ptn, offset);

	}

	/**
	 * デバッグ用コンストラクタ
	 *
	 */
	public GetOpt(String sp, String lps, String ptn[], boolean flag) {
		this(sp, lps, ptn);

		String[] o = getAllOpt();
		String[] a = getArgs();

		for (int i = 0; i < o.length; i++) {
			System.out.println("Option " + o[i]);
			if (flag(o[i])) {
				try {
					System.out.println(" Value=" + getValue(o[i]));
				} catch (GetOptException e) {
					System.out.println(" Value=");
				}
			}
		}
		if (a != null) {
			for (int i = 0; i < a.length; i++)
				System.out.println("Argument " + a[i]);
		}
	}

	/**
	 * 引数すべてに対し指定したパターンがあるかどうか判定する
	 *
	 * @param ptn
	 *            引数の配列
	 * @param offset
	 *            解析する引数のオフセット
	 *
	 * @TODO もっといいアルゴリズムに
	 */
	private void analyze(String[] ptn, int offset) {
		int i = offset;
		for (i = offset; i < ptn.length; i++) {
			if (ptn[i].charAt(0) != '-') {
				break;
			}
			if ((ptn[i].equals("-") == true) || (ptn[i].equals("--") == true)) {
				break;
			}

			if (opt_with_arg_rest.equals(ptn[i].substring(1))) {
				String flag = ptn[i].substring(1);
				String rest = "";
				i += 1;
				while (true) {
					rest += ptn[i];
					if (i == ptn.length - 1) {
						break;
					} else {
						rest += " ";
						i += 1;
					}
				}
				opts.add(new AssocPair(flag, rest));
			}

			if (ptn[i].charAt(0) == '-') {
				if (ptn[i].charAt(1) == '-') {
					i += analy_longopt(ptn[i].substring(2), ptn, i);
				} else {
					i += analy_shortopt(ptn[i].substring(1), ptn, i);
				}
			}
		}

		args = setArgs(ptn, i);
	}

	/**
	 * 与えられたショートオプションとロングオプションから 引数解析のためのリストを生成する
	 */
	private boolean makeOptList(String sp, String[] lp) {
		int i = 0;
		while (i < sp.length()) {
			if (sp.length() > (i + 1) && sp.charAt(i + 1) == ':') { /* もし文字の後に':'が続いていた場合引数を伴う */
				if (sp.length() > (i + 2) && sp.charAt(i + 2) == ':') { /* もう一つ続いていたらラスト */
					opt_with_arg_rest = new NamedObject(sp.substring(i, i + 1));
					i += 3;
				} else {
					opt_with_arg.add(new NamedObject(sp.substring(i, i + 1)));
					i += 2;
				}
			} else {
				opt_flag.add(new NamedObject(sp.substring(i, i + 1)));
				i += 1;
			}
		}
		i = 0;
		while (i < lp.length) {
			if (lp[i].charAt(lp[i].length() - 1) == ':') { /* 最終の文字が':'なら引数を伴う */
				opt_with_arg.add(new NamedObject(lp[i].substring(0,
						lp[i].length() - 1)));
			} else {
				opt_flag.add(new NamedObject(lp[i]));
			}
			i += 1;
		}
		return true;
	}

	/**
	 * パターンに該当するフラグオプションがるかどうか
	 *
	 * @param ptn
	 *            パターン文字列
	 * @return 該当オプションがあるかどうか
	 */
	private int analy_shortopt(String ptn, String arg[], int offset) {
		int add = 0;
		for (int i = 0; i < ptn.length(); i++) {
			String flag = ptn.substring(i, i + 1);
			if (opt_flag.has(flag)) {
				opts.add(new NamedObject(flag));
				add += 0;
			} else if (opt_with_arg.has(flag)) {
				if (arg.length > offset + 1) {
					opts.add(new AssocPair(flag, arg[offset + 1]));
					add += 1;
				} else {
					result = false;
					add += 0;
				}
			}
		}
		return add;
	}

	/**
	 * 引数つきオプションの解析 hoge=fefe または hoge fefe をオプション hoge と、その引数 fefe と解析
	 *
	 * @param ptn
	 *            ためすパターン
	 * @param arg
	 *            オプション字列の配列(引数かもしれないから)
	 * @param offset
	 *            現在のパタンの配列中のオフセット
	 * @return 該当するオプションがあったかどうか
	 */
	private int analy_longopt(String ptn, String arg[], int offset) {
		int add = 0;
		if (opt_flag.has(ptn)) {
			opts.add(new NamedObject(ptn));
			add = 0;
		} else if (ptn.matches(".*=.*")) { /* hogehoge=*みたいな形 */
			int index = ptn.indexOf("=");
			String ptn2 = ptn.substring(0, index);
			if (opt_with_arg.has(ptn2)) {
				opts.add(new AssocPair(ptn2, ptn.substring(index + 1)));
			} else {
				result = false;
			}
			add = 0;
		} else if (opt_with_arg.has(ptn)) {
			if (arg.length > offset + 1) {
				opts.add(new AssocPair(ptn, arg[offset + 1]));
				add = 1;
			} else {
				opts.add(new AssocPair(ptn, ""));
				result = true;
				add = 0;
			}
		} else {
			result = false;
			add = 0;
		}
		return add;
	}

	public boolean isSuccess() {
		return result;
	}

	/**
	 * オプションが指定されていたかどうかを判定する
	 *
	 * @param key
	 *            検索するオプション名
	 * @return 指定されていた/いなかった
	 */
	public boolean flag(String key) {
		return opts.has(key);
	}

	/**
	 * オプションで指定されていた値を取得する。
	 *
	 * @param key
	 *            検索するオプション名
	 * @return 指定されていた値(文字列)
	 * @throws GetOptException
	 *             与えられた文字列のオプションがない場合
	 */
	public String getValue(String key) throws GetOptException {
		Object obj = null;

		try {
			obj = opts.search(key);
		} catch (NoSuchException e) {
			throw new GetOptException("no such options." + key);
		}

		if (obj instanceof AssocPair) {
			return ((AssocPair) obj).getValue();
		} else {
			throw new GetOptException("this option doesn't have any value.");
		}
	}

	/**
	 * すべてのオプションを配列で得る。
	 *
	 * @return オプションの配列
	 */
	private String[] getAllOpt() {
		String[] o = new String[opts.size()];
		for (int i = 0; i < opts.size(); i++) {
			o[i] = ((NamedObject) opts.get(i)).getName();
		}
		return o;
	}

	/**
	 * すべての引数を配列にして返す
	 *
	 * @return 引数の配列
	 */
	public String[] getArgs() {
		return args;
	}

	/**
	 * パタンのうちoffset以降を配列に格納して返す
	 *
	 * @param ptn
	 *            パタン
	 * @param offset
	 *            オフセット
	 * @return 文字列の配列
	 */
	private String[] setArgs(String[] ptn, int offset) {
		int argc = ptn.length - offset;
		String[] args = new String[argc];
		for (int i = 0; i < argc; i++) {
			args[i] = ptn[i + offset];
		}
		return args;
	}

	/**
	 * 名前と値の組を表わすオブジェクト
	 */
	class AssocPair extends NamedObject {
		/** フラグ */
		String value;

		/**
		 * コンストラクタ
		 *
		 * @param name
		 *            名前
		 * @param value
		 *            フラグ
		 */
		private AssocPair(String name, String value) {
			super(name);
			this.value = value;
		}

		private String getValue() {
			return value;
		}
	}

	private void print_opt_flag() {
		for (int i = 0; i < opt_flag.size(); i++) {
			System.out.println(((NamedObject) opt_flag.get(i)).getName());
		}
	}

	private void print_opt_with_arg() {
		for (int i = 0; i < opt_with_arg.size(); i++) {
			System.out.println(((NamedObject) opt_with_arg.get(i)).getName());
		}
	}

	public static void main(String args[]) throws Exception {
		System.out.println("GetOpt test.");

		String sp = "vh";
		String lp = "version:";

		GetOpt go = new GetOpt(sp, lp, args);

		go.print_opt_flag();
		go.print_opt_with_arg();

		String[] o = go.getAllOpt();
		String[] a = go.getArgs();

		for (int i = 0; i < o.length; i++)
			System.out.println("Option " + o[i]);
		if (a != null) {
			for (int i = 0; i < a.length; i++)
				System.out.println("Argument " + a[i]);
		} else {
			System.out.println("no argument");
		}

		System.out.println(go.flag("v"));
		System.out.println(go.flag("h"));
		System.out.println(go.flag("version"));

		try {
			System.out.println(go.getValue("version"));
		} catch (GetOptException e) {
			System.out.println("not specified such option.");
		}

		System.out.println(go.isSuccess());
	}

}
