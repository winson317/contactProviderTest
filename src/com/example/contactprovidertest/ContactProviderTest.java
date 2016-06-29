package com.example.contactprovidertest;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * ʹ��ContentResolver�������ݵĲ��裺
 * 1������Activity��ContentResolver()��ȡContentResolver����
 * 2��������Ҫ����ContentResolver��insert()��delete()��update()��query()�ȷ����������ݼ���
 */

public class ContactProviderTest extends Activity {
	
	Button search;
	Button add;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //��ȡϵͳ�����в��ҡ����������ť
        search = (Button)findViewById(R.id.search);
        add = (Button)findViewById(R.id.add);
        
        search.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View source) {
				// ��������List����װϵͳ����ϵ����Ϣ��ָ����ϵ�˵ĵ绰���롢Email������
				final ArrayList<String> names = new ArrayList<String>();
				final ArrayList<ArrayList<String>> details = new ArrayList<ArrayList<String>>();
				
				//ʹ��ContentResolver������ϵ������
				Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, 
						null, null, null, null);
				
				//������ѯ�������ȡϵͳ��������ϵ��
				while (cursor.moveToNext())
				{
					String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); //��ȡ��ϵ��ID
					String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));//��ȡ��ϵ�˵�����
					names.add(name);
					
					//ʹ��ContentResolver������ϵ�˵ĵ绰����
					Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
							null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
					
					ArrayList<String> detail = new ArrayList<String>();
					
					//������ѯ�������ȡ����ϵ�˵Ķ���绰����
					while (phones.moveToNext())
					{
						//��ȡ��ѯ����е绰�������е�����
						String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						detail.add("�绰���룺" + phoneNumber);
					}
					phones.close();
					
					//ʹ��ContentResolver������ϵ�˵�Email��ַ
					Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
							null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
					
					//������ѯ�������ȡ����ϵ�˵Ķ��Email��ַ
					while (emails.moveToNext())
					{
						//��ȡ��ѯ�����Email��ַ���е�����
						String emailAddress= emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
						detail.add("�ʼ���ַ:" + emailAddress);
					}
					emails.close();
					
					details.add(detail);
				}
				cursor.close();
				
				//����result.xml���沼�ִ������ͼ
				View resultDialog = getLayoutInflater().inflate(R.layout.result, null);
				//��ȡresultDialog��IDΪlist��ExpandableListView
				ExpandableListView list = (ExpandableListView)resultDialog.findViewById(R.id.list);
				
				//����һ��ExpandableListAdapter����
				ExpandableListAdapter adapter = new BaseExpandableListAdapter() 
				{
					
					@Override
					public boolean isChildSelectable(int groupPosition, int childPosition) {
						// TODO Auto-generated method stub
						return true;
					}
					
					@Override
					public boolean hasStableIds() {
						// TODO Auto-generated method stub
						return true;
					}
					
					//�÷�������ÿ����ѡ������
					@Override
					public View getGroupView(int groupPosition, boolean isExpanded, 
							View convertView, ViewGroup parent) {
						// TODO Auto-generated method stub
						TextView textView = getTextView();
						textView.setText(getGroup(groupPosition).toString());
						return textView;
					}
					
					@Override
					public long getGroupId(int groupPosition) {
						// TODO Auto-generated method stub
						return groupPosition;
					}
					
					@Override
					public int getGroupCount() {
						// TODO Auto-generated method stub
						return names.size();
					}
				
					//��ȡָ����λ�ô���������
					@Override
					public Object getGroup(int groupPosition) {
						// TODO Auto-generated method stub
						return names.get(groupPosition);
					}
					
					@Override
					public int getChildrenCount(int groupPosition) {
						// TODO Auto-generated method stub
						return details.get(groupPosition).size();
					}
					
					private TextView getTextView()
					{
						AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 64);
						TextView textView = new TextView(ContactProviderTest.this);
						textView.setLayoutParams(lp);
						textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
						textView.setPadding(36, 0, 0, 0);
						textView.setTextSize(20);
						return textView;
					}
					
					//�÷�������ÿ����ѡ������
					@Override
					public View getChildView(int groupPosition, int childPosition, boolean isLastChild, 
							View convertView, ViewGroup parent) {
						// TODO Auto-generated method stub
						TextView textView = getTextView();
						textView.setText(getChild(groupPosition, childPosition).toString());
						return textView;
					}
					
					@Override
					public long getChildId(int groupPosition, int childPosition) {
						// TODO Auto-generated method stub
						return childPosition;
					}
					
					//��ȡָ����λ�á�ָ�����б�������б�������
					@Override
					public Object getChild(int groupPosition, int childPosition) {
						// TODO Auto-generated method stub
						return details.get(groupPosition).get(childPosition);
					}
				};
				//ΪExpandableListView����Adapter����
				list.setAdapter(adapter);
				
				//ʹ�öԻ�������ʾ��ѯ���
				new AlertDialog.Builder(ContactProviderTest.this)
				.setView(resultDialog)
				.setPositiveButton("ȷ��", null)
				.show();
			}
		});
        
        //Ϊadd��ť�ĵ����¼��󶨼�����
        add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//��ȡ��������е�3���ı���
				String name = ((EditText)findViewById(R.id.name)).getText().toString();
				String phone = ((EditText)findViewById(R.id.phone)).getText().toString();
				String email = ((EditText)findViewById(R.id.email)).getText().toString();
				
				//����һ���յ�ContentValues
				ContentValues values = new ContentValues();
				//��RawContacts.CONTENT_URIִ��һ����ֵ����,Ŀ���ǻ�ȡϵͳ���ص�rawContactId
				Uri rawContactUri = getContentResolver().insert(RawContacts.CONTENT_URI, values);
				long rawContactId = ContentUris.parseId(rawContactUri);
				values.clear();
				values.put(Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE); //������������
				values.put(StructuredName.GIVEN_NAME, name);  //������ϵ������
				
				//����ϵ��URI�����ϵ������
				getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
				values.clear();
				values.put(Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
				values.put(Phone.NUMBER, phone);  //������ϵ�˵ĵ绰����
				values.put(Phone.TYPE, Phone.TYPE_MOBILE); //���õ绰����
				
				//����ϵ�˵绰����URI��ӵ绰����
				getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
				values.clear();
				values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
				values.put(Email.DATA, email); //������ϵ�˵�Email��ַ
				values.put(Email.TYPE, Email.TYPE_WORK); //���øõ����ʼ�������
				
				//����ϵ��Email URI���Email����
				getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
				Toast.makeText(ContactProviderTest.this, "��ϵ��������ӳɹ���", Toast.LENGTH_LONG).show();
			}
		});
    }
}
