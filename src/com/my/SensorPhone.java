package com.my;

import java.util.ArrayList;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
//http://www.kiwidoc.com/java/l/x/android/android/8/p/android.provider/c/ContactsContract.PhoneLookup
//http://stackoverflow.com/questions/5320046/answer-phone-call-and-hang-up-phone-call-within-the-application
//http://www.androidjavadoc.com/2.3/android/provider/ContactsContract.PhoneLookup.html
//http://stackoverflow.com/questions/2174048/how-to-look-up-a-contacts-name-from-their-phone-number-on-android
public class SensorPhone {
	
	private final String TAG = getClass().getSimpleName();
	
	//La variable del bundle que tiene el número de origen
	
//	private final String variable_phone_number = "incoming_number";
	
		
	//Parámetros
	
	private ArrayList<ContactInfoDTO> myContacts;
	
	private Context context;
	
	public SensorPhone(Context context){
		
		Log.v(TAG, "Estoy en "+TAG+": Constructor");
		
		this.context = context;
		
		myContacts = new ArrayList<ContactInfoDTO>();
		
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

	public ArrayList<ContactInfoDTO> getMyContacts() {
		return myContacts;
	}


	public void setMyContacts(ArrayList<ContactInfoDTO> myContacts) {
		this.myContacts = myContacts;
	}	
	
	
}
