package com.example.cim.test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.cim.model.RecentChat;
import com.example.cim.util.FileUtil;

public class TestData {
	public static String[] names = {"songhuiqiao.jpg","zhangzetian.jpg","songqian.jpg","hangxiaozhu.jpg","jingtian.jpg"
		,"liuyifei.jpg","kangyikun.jpg","dengziqi.jpg"};
	public static String dir=FileUtil.getRecentChatPath();
	public static List<RecentChat> getRecentChats(){
		String path=FileUtil.getRecentChatPath();
		List<RecentChat> chats=new ArrayList<RecentChat>();
		chats.add(new RecentChat("�λ���", "�ú�ѧϰ��������", "12:30", path+names[0], "0", "", "", 0, "", 0,""));
		chats.add(new RecentChat("������", "�ú�ѧϰ��������", "16:30", path+names[1], "0", "", "", 0, "", 0,""));
		chats.add(new RecentChat("����", "�ú�ѧϰ��������", "17:30", path+names[2], "0", "", "", 0, "", 0,""));
		chats.add(new RecentChat("��Т��", "�ú�ѧϰ��������", "����", path+names[3], "0", "", "", 0, "", 0,""));
		chats.add(new RecentChat("����", "�ú�ѧϰ��������", "����һ", path+names[4], "0", "", "", 0, "", 0,""));
		chats.add(new RecentChat("�����", "�ú�ѧϰ��������", "17:30", path+names[5], "0", "", "", 0, "", 0,""));
		chats.add(new RecentChat("������", "�ú�ѧϰ��������", "����", path+names[6], "0", "", "", 0, "", 0,""));
		chats.add(new RecentChat("������", "�ú�ѧϰ��������", "����һ", path+names[7], "0", "", "", 0, "", 0,""));
		return chats;
	}
	
	public static HashMap<String, String> getFriends(){
		HashMap<String, String> maps=new HashMap<String, String>();
		maps.put("�λ���", dir+"songhuiqiao.jpg");
		maps.put("������", dir+"zhangzetian.jpg");
		maps.put("����", dir+"songqian.jpg");
		maps.put("��Т��", dir+"hangxiaozhu.jpg");
		maps.put("����", dir+"jingtian.jpg");
		maps.put("�����", dir+"liuyifei.jpg");
		maps.put("������", dir+"kangyikun.jpg");
		maps.put("������", dir+"dengziqi.jpg");
		
		return maps;
	}
}
