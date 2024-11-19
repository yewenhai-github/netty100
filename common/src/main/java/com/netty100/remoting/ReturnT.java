package com.netty100.remoting;

import java.io.Serializable;

/**
 * @author why
 * @version 1.0.0, 2022/3/23
 * @since 1.0.0, 2022/3/23
 */
public class ReturnT<T> implements Serializable {
	public static final long serialVersionUID = 42L;
	public static final int SUCCESS_CODE = 200;
	public static final int FAIL_CODE = 500;

	private int code;
	private String msg;
	private T content;

	public ReturnT(){}

	public ReturnT(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	public ReturnT(T content) {
		this.code = SUCCESS_CODE;
		this.content = content;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getContent() {
		return content;
	}
	public void setContent(T content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "ReturnT [code=" + code + ", msg=" + msg + ", logContent=" + content + "]";
	}

}
