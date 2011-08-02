package com.my;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


//http://stackoverflow.com/questions/1853220/retrieve-incoming-calls-phone-number-in-android
public class BR_PhoneCall extends BroadcastReceiver{

	private final String TAG = getClass().getSimpleName();
	public static final String ACTION_INTENT="com.my.BR_PhoneCall";
	private final String variable_phone_number = "incoming_number";
	private String numero="";
	private TelephonyManager telephony;
	public static final String variable_BR = "phone";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Log.v(TAG, "Estoy en "+TAG+": onReceive");

		//Solo para probar algunas funciones de PhoneStateListener
		
		MyPhoneStateListener phoneListener=new MyPhoneStateListener();
		this.telephony = (TelephonyManager)
		context.getSystemService(Context.TELEPHONY_SERVICE);
		this.telephony.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE); 
		   
		//Sacar en bundle que trae del intent recibido
		 
		 Bundle bundle = intent.getExtras();
		 
		//Ver los items que trae en el intent
//		 Set<String> s = bundle.keySet();
//		 
//		 for(String temp: s){
//			 Log.v(TAG, "Bundle info: "+temp);
//		 }
		 
		 //Mostrar por LogCat cuál es el número de origen
		 
		 this.numero = bundle.getString(variable_phone_number);
		 Log.v(TAG,"numero llamada: "+this.numero);
		 
		 //Pasarle el bundle con la información que tiene de la llamada
		 
		 Intent intent2 = new Intent(MiServicio.INTENT_ACTION);
		 intent2.putExtras(bundle);
		 intent2.putExtra(MiServicio.variable_BR, BR_PhoneCall.variable_BR);

		 //Comenzar el servicio
		 
		 context.startService(intent2);
		 		 		 
	}
	
	public class MyPhoneStateListener extends PhoneStateListener {
		  public void onCallStateChanged(int state,String incomingNumber){
			  Log.v(TAG, "Estoy en "+TAG+": onCallStateChanged");
		  switch(state){
		    case TelephonyManager.CALL_STATE_IDLE:
		      Log.d(TAG, "IDLE");
		    break;
		    case TelephonyManager.CALL_STATE_OFFHOOK:
		      Log.d(TAG, "OFFHOOK");
		    break;
		    case TelephonyManager.CALL_STATE_RINGING:
		      Log.d(TAG, "RINGING");	
		    break;
		    }		  
		  
		  }
		}

}
