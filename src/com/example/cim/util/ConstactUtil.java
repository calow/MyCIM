package com.example.cim.util;

import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

public class ConstactUtil {

	public static Map<String, String> getAllCallRecords(Context context) {
		Map<String, String> temp = new HashMap<String, String>();
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI,
				null,
				null,
				null,
				ContactsContract.Contacts.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");
		if (cursor.moveToFirst()) {
			do {
				// �����ϵ�˵�ID��
				String contactId = cursor.getString(cursor
						.getColumnIndex(BaseColumns._ID));
				// �����ϵ������
				String name = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				// �鿴����ϵ���ж��ٸ��绰���롣���û���ⷵ��ֵΪ0
				int phoneCount = cursor
						.getInt(cursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				String number = null;
				if (phoneCount > 0) {
					// �����ϵ�˵ĵ绰����
					Cursor phones = context.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
					if (phones.moveToFirst()) {
						number = phones
								.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					}
					phones.close();
				}
				temp.put(name, number);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return temp;
	}
}
