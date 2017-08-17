package com.homework.wechat.util;

import java.util.HashMap;
import java.util.Map;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

public class OkHttpUtil {

	private static Request.Builder builder;
	private static Map<String, Request> curRequest;
	private static OkHttpClient okHttpClent;

	static {
		curRequest = new HashMap<String, Request>();
		builder = new Request.Builder();
	}

	private OkHttpUtil() {
	}

	/**
	 * �������get�����request
	 * 
	 * @param url
	 * @return
	 */
	public static Request getRequestByGet(String url) {
		if (curRequest.containsKey(url)) {
			return curRequest.get(url);
		}
		Request request = builder.get().url(url).build();
		curRequest.put(url, request);
		return request;
	}

	/**
	 * ��֤ȫ��ֻ��һ��httpClient����
	 * 
	 * @return
	 */
	public static OkHttpClient getHttpClient() {
		if (okHttpClent == null) {
			okHttpClent = new OkHttpClient();
		}
		return okHttpClent;
	}

}
