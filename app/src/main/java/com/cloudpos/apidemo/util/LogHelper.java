/**
 * 
 */
package com.cloudpos.apidemo.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * @author john 打印信息的格式控制类。 红色：出现的问题。 绿色：正常的信息。 黄色：可能出现的问题。 1 : color is black 2
 *         : color is yellow 3 : color is blue 4 : color is red other number :
 *         color is black;
 */
public class LogHelper {

	/**
	 * TestView changed color and info message. Called after the TestView is
	 * created and whenever the TextView changes. Set your TextView's message
	 * here.
	 * 
	 * @param TextView
	 *            text
	 * @param String
	 *            infoMsg : color is red < color name="red">#FF0000< /color><
	 *            !--红色 -->
	 */
	@Deprecated
	public static void infoException(TextView text, String infoMsg) {
		text.setText(infoMsg, TextView.BufferType.SPANNABLE);
		int start = text.getText().length();
		int end = start + infoMsg.length();
		Spannable style = (Spannable) text.getText();
		style.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

	}

	/**
	 * @param TextView
	 *            text
	 * @param String
	 *            infoMsg : color is black
	 * 
	 * */
	@Deprecated
	public static void info(TextView text, String infoMsg) {
		// int start = text.getText().length();
		// int end = start +infoMsg.length();
		text.setText(infoMsg);
	}

	/**
	 * @param TextView
	 *            text
	 * @param String
	 *            infoMsg : color is yellow
	 * */
	@Deprecated
	public static void infoWarning(TextView text, String infoMsg) {

		text.setText(infoMsg);
		Spannable style = (Spannable) text.getText();
		;
		int start = text.getText().length();
		int end = start + infoMsg.length();
		style.setSpan(new ForegroundColorSpan(Color.YELLOW), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	/**
	 * @param TextView
	 *            text
	 * @param String
	 *            infoMsg
	 * @param order
	 *            :set background color. 1 : color is black. 2 : color is
	 *            yellow. 3 : color is blue .4 : color is red .other number :
	 *            color is black;
	 * */
	public static void infoMsgAndColor(TextView text, String infoMsg, int order) {
		text.setText(infoMsg);
		Spannable style = (Spannable) text.getText();
		;
		int start = 0;
		int end = start + infoMsg.length();
		ForegroundColorSpan color;
		switch (order) {
		case 1:
			color = new ForegroundColorSpan(Color.BLACK);
			break;
		case 2:
			color = new ForegroundColorSpan(Color.YELLOW);
			break;
		case 3:
			color = new ForegroundColorSpan(Color.BLUE);
			break;
		case 4:
			color = new ForegroundColorSpan(Color.RED);
			break;
		default:
			color = new ForegroundColorSpan(Color.BLACK);
			break;
		}
		style.setSpan(color, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	public static void infoAppendMsgAndColor(TextView text, String infoMsg, int order) {
		int start = 0;
		if (text.getText().length() == 0) {
		} else {
			start = text.getText().length();
		}
		text.append(infoMsg);
		Spannable style = (Spannable) text.getText();

		int end = start + infoMsg.length();
		ForegroundColorSpan color;
		switch (order) {
		case 1:
			color = new ForegroundColorSpan(Color.BLACK);
			break;
		case 2:
			color = new ForegroundColorSpan(Color.YELLOW);
			break;
		case 3:
			color = new ForegroundColorSpan(Color.BLUE);
			break;
		case 4:
			color = new ForegroundColorSpan(Color.RED);
			break;
		case 5:
			color = new ForegroundColorSpan(Color.RED);
			break;
		default:
			color = new ForegroundColorSpan(Color.YELLOW);
			break;
		}
		style.setSpan(color, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	/**
	 * 蓝色字体打印成功信息
	 * 
	 * @param msg
	 * @param text
	 */

	public static void infoAppendMsgForSuccess(String msg, TextView text) {
		// TextView text = PreMainActivity.txtResult;
		int start = 0;
		if (text.getText().length() == 0) {
		} else {
			start = text.getText().length();
		}
		text.append(msg);
		Spannable style = (Spannable) text.getText();
		int end = start + msg.length();
		ForegroundColorSpan color;
		color = new ForegroundColorSpan(Color.BLUE);
		style.setSpan(color, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		moveScroller(text);
		// text.setScrollY(text.getScrollY()+text.getLineHeight());
	}

	/**
	 * 红色字体打印失败信息
	 * 
	 * @param msg
	 * @param text
	 */

	public static void infoAppendMsgForFailed(String msg, TextView text) {
		// TextView text = PreMainActivity.txtResult;
		int start = 0;
		if (text.getText().length() == 0) {
		} else {
			start = text.getText().length();
		}
		text.append(msg);
		Spannable style = (Spannable) text.getText();
		int end = start + msg.length();
		ForegroundColorSpan color;
		color = new ForegroundColorSpan(Color.RED);
		style.setSpan(color, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		moveScroller(text);
		// Scroller scr = text.
		// text.setScrollY(text.getScrollY()+text.getLineHeight());
	}

	/**
	 * 给textresult上用黑色写上命令。
	 * */
	public static void infoAppendMsg(String msg, TextView text) {
		// TextView text = PreMainActivity.txtResult;
		int start = 0;
		if (text.getText().length() == 0) {
		} else {
			start = text.getText().length();
		}
		text.append(msg);
		Spannable style = (Spannable) text.getText();
		int end = start + msg.length();
		ForegroundColorSpan color;
		color = new ForegroundColorSpan(Color.BLACK);
		style.setSpan(color, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		moveScroller(text);
		//
	}

	private static void moveScroller(TextView text) {
		// find the amount we need to scroll. This works by
		// asking the TextView's internal layout for the position
		// of the final line and then subtracting the TextView's height
		final int scrollAmount = text.getLayout().getLineTop(text.getLineCount()) - text.getHeight();
		// if there is no need to scroll, scrollAmount will be <=0
		if (scrollAmount > 0) {
			text.scrollTo(0, scrollAmount + 30);
		} else{
			text.scrollTo(0, 0);
		}
	}
}