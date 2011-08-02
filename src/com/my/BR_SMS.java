package com.my;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
//http://ronrod.org/index.php?option=com_content&view=article&id=59:manejo-de-eventos-en-android-broadcastreceiver&catid=38:android&Itemid=56
public class BR_SMS extends BroadcastReceiver{
	private final String TAG = getClass().getSimpleName();
	public static final String variable_BR = "sms";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG,"Estoy en "+TAG+": onReceive");
		// TODO Auto-generated method stub
		//Obtener el mensaje del intent
		Bundle bundle = intent.getExtras();
		
		//Pasar los extras a un nuevo intent hacia el servicio
		bundle.putString(MiServicio.variable_BR, variable_BR);
		Intent intent2 = new Intent(MiServicio.INTENT_ACTION);
		
		intent2.putExtras(bundle);		
		
		context.startService(intent2);

	}
	
	
}
