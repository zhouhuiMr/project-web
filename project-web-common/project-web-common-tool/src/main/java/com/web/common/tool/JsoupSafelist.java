package com.web.common.tool;

import org.jsoup.safety.Safelist;

public class JsoupSafelist {
	
	private JsoupSafelist() {}

	private static Safelist safelist = Safelist.basic();
	
	/**
	 * XSS 需要过滤的标签
	 * @return Safelist
	 * 
	 * @author zhouhui
	 * @since 1.0.0
	 */
	public static Safelist xssSafelist() {
		return safelist.removeTags("a");
	}
}
