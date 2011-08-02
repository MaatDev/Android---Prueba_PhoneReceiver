package com.my;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsMessage;
import android.util.Log;

//http://www.higherpass.com/Android/Tutorials/Working-With-Android-Contacts/
//http://developer.android.com/guide/topics/providers/content-providers.html
//http://archive.cnblogs.com/a/2088070/
//http://developer.android.com/reference/android/provider/ContactsContract.Contacts.html
public class SensorSMS {
	private final String TAG = getClass().getSimpleName();
	
		
	//Parámetros
	
	private String origenSMS;
	private boolean isEmail;
	private String emailSender;
	private String smsBody;
//	byte[] userData=null;
		
	private ArrayList<ContactInfoDTO> myContacts;

	private Context context;	
	
	
	public SensorSMS(Context context){
		Log.v(TAG, "Estoy en "+TAG+": Constructor");		
		
		this.context = context;
		
	}
	
	public void setSMSInfo(Bundle bundle){
		Log.v(TAG, "Estoy en "+TAG+": setSMSInfo");
		
		//Del bundle, obtener la información del sms
		if(bundle != null){
			Object[] pdus = (Object[]) bundle.get("pdus");
			final SmsMessage[] messages = new SmsMessage[pdus.length];
			for(int i = 0; i<pdus.length; i++){
				messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				
				this.origenSMS = messages[i].getOriginatingAddress();
				this.isEmail = messages[i].isEmail();
				this.emailSender = messages[i].getEmailFrom();
				this.smsBody = messages[i].getMessageBody();

//				userData = messages[i].getUserData();
				
			}
			
		}
		
	}
	
	public ArrayList<ContactInfoDTO> searchContactInfoByNumber(String number){
		Log.v(TAG, "Estoy en "+TAG+": searchContactInfoByNumber");
		
		
		//Inicializar datos
		ArrayList<ContactInfoDTO> myContacts = new ArrayList<ContactInfoDTO>();
						
		//Definir las columnas a buscar
		
		String[] columnas = new String[]{PhoneLookup._ID, PhoneLookup.DISPLAY_NAME};
		
		//Buscar la ruta del contacto por el número
		
		Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		
		Cursor cur = this.context.getContentResolver().query(lookupUri, columnas, null, null, null);
		
		//Index de las columnas
		
		int indexId = cur.getColumnIndex(PhoneLookup._ID);
		int indexName = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME);
		
		ContactInfoDTO contact;
		
		while(cur.moveToNext()){
			contact = new ContactInfoDTO();
			
			//Pasarle los datos obtenidos del ContentProvider
			
			contact.setId(cur.getString(indexId));
			contact.setName(cur.getString(indexName));
			contact.setNumber(number);
		
			//Agregar al arreglo
			
			myContacts.add(contact);
			
		};
		
		this.myContacts = myContacts;
		
		return myContacts;
		
	}
	
public ArrayList<ContactInfoDTO> searchContactInfoByEmail(String email){
		Log.v(TAG, "Estoy en "+TAG+": searchContactInfoByEmail");
	
	
		//Inicializar datos
		ArrayList<ContactInfoDTO> myContacts = new ArrayList<ContactInfoDTO>();					
		
		
		//Buscar el ID del contacto del Email
		
		Uri lookupUri1 = Uri.withAppendedPath(Email.CONTENT_LOOKUP_URI, Uri.encode(email));
		Cursor cur1 = this.context.getContentResolver().query(lookupUri1,
		          new String[]{Email.CONTACT_ID, Email.DATA},
		          null, null, null);
		
		//Los index que corresponde de la base de datos de Email
		
		int indexContactID = cur1.getColumnIndex(Email.CONTACT_ID);
		int indexEmail = cur1.getColumnIndex(Email.DATA);
		 
		ContactInfoDTO contact;
		
		while(cur1.moveToNext()){
			
			contact = new ContactInfoDTO();
			
			contact.setId(cur1.getString(indexContactID));
			contact.setEmail(cur1.getString(indexEmail));
			
			//Agregar al arraylist
			
			myContacts.add(contact);
						
		}
		
		
		//Buscar el teléfono del contácto
		for(int i =0; i<myContacts.size();i++){
					
			Cursor cur3 = this.context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = "+myContacts.get(i).getId(), 
					null, null);
			int indexNumber = cur3.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
			
			if(cur3.moveToNext()){
				
				myContacts.get(i).setNumber(cur3.getString(indexNumber));
				
			}
					
		}
		
				
		//Buscar el nombre del contacto
		
		for(int i = 0; i < myContacts.size(); i++){
			Uri lookupUri2 = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, myContacts.get(i).getId());
			
			Cursor cur2 = this.context.getContentResolver().query(lookupUri2, null, null, null, null);
			Log.v(TAG, "numero de columnas: "+cur2.getColumnCount());
			
			String name=null;
			
			//index de las tablas
			
			int indexName = cur2.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

			if(cur2.moveToNext()){
				
				name = cur2.getString(indexName);
			
			}
			myContacts.get(i).setName(name);
			
			
		}
		
		
		
		
		
//		//Index de las columnas
//		
//		int indexId = cur.getColumnIndex(PhoneLookup._ID);
//		int indexName = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME);
//		int indexType = cur.getColumnIndex(PhoneLookup.TYPE);
//		
////		ContactInfoDTO contact;
//		
//		Log.v(TAG, "Columnas: "+cur.getCount());
//		while(cur.moveToNext()){
//			contact = new ContactInfoDTO();
//			
//			//Pasarle los datos obtenidos del ContentProvider
//			
//			contact.setId(cur.getString(indexId));
//			contact.setName(cur.getString(indexName));
//			contact.setType(cur.getString(indexType));
//			contact.setNumber(email);
//		
//			//Agregar al arreglo
//			
//			myContacts.add(contact);
//			
//		};
		
		this.myContacts = myContacts;
		
		return myContacts;
		
	}

	public String getOrigenSMS() {
		return origenSMS;
	}

	public void setOrigenSMS(String origenSMS) {
		this.origenSMS = origenSMS;
	}

	public boolean getIsEmail() {
		return isEmail;
	}

	public void setIsEmail(boolean isEmail) {
		this.isEmail = isEmail;
	}

	public String getEmailSender() {
		return emailSender;
	}

	public void setEmailSender(String emailSender) {
		this.emailSender = emailSender;
	}

	public String getSmsBody() {
		return smsBody;
	}

	public void setSmsBody(String smsBody) {
		this.smsBody = smsBody;
	}

	public ArrayList<ContactInfoDTO> getMyContacts() {
		return myContacts;
	}

	public void setMyContacts(ArrayList<ContactInfoDTO> myContacts) {
		this.myContacts = myContacts;
	}
	
	

}
