package com.example.cim.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.cim.R;
import com.example.cim.model.ChatEmoji;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

public class FaceConversionUtil {

	private static FaceConversionUtil mFaceConversionUtil;

	/** �������ڴ��еı���HashMap */
	private HashMap<String, String> emojiMap = new HashMap<String, String>();

	/** �������ڴ��еı��鼯�� */
	private List<ChatEmoji> emojis = new ArrayList<ChatEmoji>();

	/** �����ҳ�Ľ������ */
	public List<List<ChatEmoji>> emojiLists = new ArrayList<List<ChatEmoji>>();

	/** ÿһҳ����ĸ��� */
	private int pageSize = 20;

	private FaceConversionUtil(Context context) {
		getFileText(context);
	}

	public static FaceConversionUtil getInstance(Context context) {
		if (mFaceConversionUtil == null) {
			synchronized (FaceConversionUtil.class) {
				if (mFaceConversionUtil == null) {
					mFaceConversionUtil = new FaceConversionUtil(context);
				}
			}
		}
		return mFaceConversionUtil;
	}

	public void getFileText(Context context) {
		ParseData(FileUtil.getEmojiFile(context), context);
	}

	private void ParseData(List<String> data, Context context) {
		if (data == null) {
			return;
		}
		try {
			ChatEmoji emojiEntry;
			for (String str : data) {
				String[] text = str.split(",");
				String fileName = text[0]
						.substring(0, text[0].lastIndexOf("."));
				emojiMap.put(text[1], fileName);
				int resId = context.getResources().getIdentifier(fileName,
						"drawable", context.getPackageName());
				if (resId != 0) {
					emojiEntry = new ChatEmoji();
					emojiEntry.setId(resId);
					emojiEntry.setCharacter(text[1]);
					emojiEntry.setFaceName(fileName);
					emojis.add(emojiEntry);
				}
			}
			int pageCount = (int) Math.ceil(emojis.size() / 20 + 0.1);
			for (int i = 0; i < pageCount; i++) {
				emojiLists.add(getData(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<ChatEmoji> getData(int page) {
		int startIndex = page * pageSize;
		int endIndex = startIndex + pageSize;
		
		if (endIndex > emojis.size()) {
			endIndex = emojis.size();
		}
		
		List<ChatEmoji> list = new ArrayList<ChatEmoji>();
		list.addAll(emojis.subList(startIndex, endIndex));
		
		if (list.size() < pageSize) {
			for (int i = list.size(); i < pageSize; i++) {
				ChatEmoji object = new ChatEmoji();
				list.add(object);
			}
		}
		if (list.size() == pageSize) {
			ChatEmoji object = new ChatEmoji();
			object.setId(R.drawable.face_del_icon);
			list.add(object);
		}
		return list;
	}

	public SpannableString getExpressionString(Context context, String str) {
		SpannableString spannableString = new SpannableString(str);
		// ������ʽ�����ַ������Ƿ��б��飬�磺 �Һ�[����]��
		String zhengze = "\\[[^\\]]+\\]";
		// ͨ�������������ʽ������һ��pattern
		Pattern patten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
		try {
			dealExpression(context, spannableString, patten, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return spannableString;
	}

	@SuppressWarnings("deprecation")
	private void dealExpression(Context context,
			SpannableString spannableString, Pattern pattern, int start) {
		Matcher matcher = pattern.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			if (matcher.start() < start) {
				continue;
			}
			String fileName = emojiMap.get(key);
			if (TextUtils.isEmpty(fileName)) {
				continue;
			}
			int resId = context.getResources().getIdentifier(fileName,
					"drawable", context.getPackageName());
			if (resId != 0) {
				Bitmap bitmap = BitmapFactory.decodeResource(
						context.getResources(), resId);
				bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
				// ͨ��ͼƬ��Դid���õ�bitmap����һ��ImageSpan����װ
				ImageSpan imageSpan = new ImageSpan(bitmap);
				// �����ͼƬ���ֵĳ��ȣ�Ҳ����Ҫ�滻���ַ����ĳ���
				int end = matcher.start() + key.length();
				// ����ͼƬ�滻�ַ����й涨��λ����
				spannableString.setSpan(imageSpan, matcher.start(), end,
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				if (end < spannableString.length()) {
					dealExpression(context, spannableString, pattern, end);
				}
				break;
			}
		}
	}

	@SuppressWarnings("deprecation")
	public SpannableString addFace(Context context, int resId,
			String spannableString) {
		if (TextUtils.isEmpty(spannableString)) {
			return null;
		}
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				resId);
		bitmap = Bitmap.createScaledBitmap(bitmap, 35, 35, true);
		ImageSpan imageSpan = new ImageSpan(bitmap);
		SpannableString spannable = new SpannableString(spannableString);
		spannable.setSpan(imageSpan, 0, spannableString.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}

}
