package net.wasamon.mjlib.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class analyzes options of arguments and manages the result.<br>
 * usage: GetOpt("hvf:", "prefix:", args)<br>
 * The 1st argument makes short options, and the 2nd arguments makes long-options.<br>
 * Each Option with ":" treats an argument for the option. The argument is recognized with space or '='.<br>
 * In addition to that, the option with "::" treats all of the following strings as the argument.<br>
 * The next arguments after '-', '--', or the string which does not start with '-' are treated as arguments not related with options<br>
 *
 * @version $Id: GetOpt.java,v 1.3 2004/05/24 05:24:35 miyo Exp $
 * @author Takefumi MIYOSHI
 *
 */
public class GetOpt {

	private String[] args;

	private NamedArrayList opts;

	/** options with an argument */
	private NamedArrayList opt_with_arg;

	/** options with no argument */
	private NamedArrayList opt_flag;

	/** unknown string */
	private ArrayList unknown;

	private NamedObject opt_with_arg_rest = new NamedObject("");

	private boolean result = true;

	/**
	 * Constructor
	 *
	 * @param sp
	 *            short options(such as '-v')
	 * @param lps
	 *            long options(specified as a commma separated string)
	 * @param ptn
	 *            target string
	 */
	public GetOpt(String sp, String lps, String ptn[]) {
		this(sp, lps, ptn, 0);
	}

	/**
	 * Constructor
	 *
	 * @param sp
	 *            short options(such as '-v')
	 * @param lps
	 *            long options(specified as a commma separated string)
	 * @param ptn
	 *            target string
	 * @param offset
	 *            offset for target string to analyze
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
	 * Constructor for debug
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
	 * Check the given options in all arguments
	 *
	 * @param ptn
	 *            array of arguments
	 * @param offset
	 *            offset of analysis options
	 *
	 * @TODO should be brush-up
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
	 * generate a list to analyze options from the given patterns of short-options and long-options
	 */
	private boolean makeOptList(String sp, String[] lp) {
		int i = 0;
		while (i < sp.length()) {
			if (sp.length() > (i + 1) && sp.charAt(i + 1) == ':') { /* with ':', required an argument */
				if (sp.length() > (i + 2) && sp.charAt(i + 2) == ':') { /* with additional ':', end of arguments */
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
			if (lp[i].charAt(lp[i].length() - 1) == ':') { /* with ':', required an argument */
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
	 * check flag options
	 *
	 * @param ptn
	 *            pattern string
	 * @return checking result
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
	 * analyze options with arguments. (ex. "hoge=fefe" or "hoge fefe" will be analyzed "hoge" option and "fefe" argument
	 *
	 * @param ptn
	 *            pattern
	 * @param arg
	 *            arguments candidate
	 * @param offset
	 *            offset for current pattern
	 * @return result
	 */
	private int analy_longopt(String ptn, String arg[], int offset) {
		int add = 0;
		if (opt_flag.has(ptn)) {
			opts.add(new NamedObject(ptn));
			add = 0;
		} else if (ptn.matches(".*=.*")) { /* like hogehoge=* */
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
	 * check whether the option is specified or not
	 *
	 * @param key
	 *            option name to search
	 * @return result
	 */
	public boolean flag(String key) {
		return opts.has(key);
	}

	/**
	 * get values for the given option
	 *
	 * @param key
	 *            option name to search
	 * @return the value for the key (String)
	 * @throws GetOptException
	 *            no such option has been given
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
	 * get all options
	 *
	 * @return the array of all options
	 */
	private String[] getAllOpt() {
		String[] o = new String[opts.size()];
		for (int i = 0; i < opts.size(); i++) {
			o[i] = ((NamedObject) opts.get(i)).getName();
		}
		return o;
	}

	/**
	 * get all arguments
	 *
	 * @return the array of all arguments (without strings related analyzed options)
	 */
	public String[] getArgs() {
		return args;
	}

	/**
	 * rest pattenrs after the offset
	 *
	 * @param ptn
	 *            patter
	 * @param offset
	 *            offset to skip
	 * @return result array of string
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
	 * associative pair of Object and its name
	 */
	class AssocPair extends NamedObject {
		/** flag */
		String value;

		/**
		 * Constructor
		 *
		 * @param name
		 *            name
		 * @param value
		 *            flag
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
