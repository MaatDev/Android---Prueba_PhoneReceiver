package com.my;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


//http://developer.android.com/guide/topics/ui/notifiers/notifications.html
public class MiServicio extends Service {
	
	private final String TAG = getClass().getSimpleName();
	public static final String INTENT_ACTION = "com.my.MiServicio";
	public static final String variable_BR = "variable_BR";
	public static final String variable_Message = "message";
	public static final int HELLO_ID = 1;
	
	private final String variable_phone_number = "incoming_number";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.v(TAG, "Estoy en "+TAG+": onStartCommand");		
		
		Bundle bundle = intent.getExtras();
		
		//Solo realizar acciones si en bundle no está vacío
		
		if(bundle != null){
			
			String tipoBR = bundle.getString(variable_BR);
			
			String message="--";
			
			if(tipoBR.equals(BR_PhoneCall.variable_BR)){
				
				//Es invocado por una llamada
				
				message = actionPhone(this, bundle);
				
				showNotification(" Phone ", message, this);
				
				
			}else if(tipoBR.equals(BR_SMS.variable_BR)){
				
				//Es invocado por un sms
				
				message = actionSMS(this, bundle);
				showNotification(" SMS ", message, this);
				
			}else{
				
				//No es ningúno de los 2
				
			}
			
			Log.v(TAG, "Mostrar el mensaje");
			Log.v(TAG, message);			
			
		}
		
		
		
//		return START_NOT_STICKY;
		return START_STICKY; 
	}
	
	public String actionPhone(Context context, Bundle bundle){
		
		//Inicializar el sensor de teléfono
		
		SensorPhone sensor = new SensorPhone(context);
		
		//Obtener el número telefónico de la llamada
		
		String phoneNumber = bundle.getString(variable_phone_number);
		
		ArrayList<ContactInfoDTO> myContacts = sensor.searchContactInfoByNumber(phoneNumber);
		
		String cadena="";
		
		//Definir el mensaje devuelto según su origen
		
		if(myContacts == null || myContacts.size() == 0){
			
			cadena = "Es una llamada de origen desconocida";
			
		}else{
			
			for(ContactInfoDTO c: myContacts){
				cadena += "Number: "+c.getNumber()+'\n';
				cadena += "ID: "+c.getId()+'\n';
				cadena += "Nombre: "+c.getName()+'\n';
				cadena += '\n'+'\n'+'\n';
			}
					
			
		}				
	
		return cadena;
	}
	
	public String actionSMS(Context context, Bundle bundle){
		
		
		//Inicializar el sensor de teléfono
		
		SensorSMS sensor = new SensorSMS(context);
				
		//Obtener el número telefónico de la llamada
				
		sensor.setSMSInfo(bundle);
		
		//Solo para probar sms por correo
		
//		sensor.setEmailSender("cjb_netwar@hotmail.com");
//		sensor.setIsEmail(true);
		
		//La cadena que devolverá
		
		String cadena="";
		
		if(sensor.getIsEmail()){
			
			//Mensaje recibido desde Email
			
			ArrayList<ContactInfoDTO> myContacts = sensor.searchContactInfoByEmail(sensor.getEmailSender());
			
						
			//Definir el mensaje devuelto según su origen
			
			if(myContacts == null || myContacts.size() == 0){
				cadena = "From: "+sensor.getEmailSender()+'\n';
				cadena = "Es una mensaje de texto de origen desconocida: ";
						
			}else{
					
					cadena +="Email: "+sensor.getEmailSender()+'\n';
					
					for(ContactInfoDTO c: myContacts){
						cadena += "Number: "+c.getNumber()+'\n';
						cadena += "ID: "+c.getId()+'\n';
						cadena += "Nombre: "+c.getName()+'\n';
						cadena += '\n'+'\n'+'\n';
								
					}				
							
				
						
			}	
			
			//Mensaje del SMS
			
			cadena += "Contenido: "+sensor.getSmsBody();
			
			
		}else{
			
			//Mensaje recibido desde teléfono
			
			
			ArrayList<ContactInfoDTO> myContacts = sensor.searchContactInfoByNumber(sensor.getOrigenSMS());
			
			
					
			//Definir el mensaje devuelto según su origen
			
			if(myContacts == null || myContacts.size() == 0){
						
				cadena = "From: "+sensor.getOrigenSMS()+'\n';
				cadena = "Es una mensaje de texto de origen desconocida: ";
						
			}else{				
									
					for(ContactInfoDTO c: myContacts){
						cadena += "Number: "+c.getNumber()+'\n';
						cadena += "ID: "+c.getId()+'\n';
						cadena += "Nombre: "+c.getName()+'\n';
						cadena += '\n'+'\n'+'\n';
	
					}	
									
			}	
			
			//Mensaje del SMS
			
			cadena += "Contenido: "+sensor.getSmsBody();
			
		}
		
					
			
		return cadena;
	}

	public void showNotification(String typeNotification, String message, Context context){
		Log.v(TAG, "Estoy en "+TAG+": showNotification");
		
				
		//Obtener el servicio de manejador de notificaciones
		
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		//Instanciar notificación
		
		//El icono a mostrar de la notificación
		
		int icon = R.drawable.icon;
		
		//Tiempo actual a mostrar
		
		long when = System.currentTimeMillis();		
		
		//La notifiación que va lanzar
		
		Notification notification = new Notification(icon,typeNotification, when);
		
		//Mensaje a mostrar cuando se expande la notificación
		
		Context notiContext = context.getApplicationContext();
		
		String notiTitle = "Servicio de deteccion: "+typeNotification;			
		
		Intent notificationIntent = new Intent(this, MiServicio.class);
		
		//Insertar mensaje al intent
		
		Bundle bundle = new Bundle();
		
		bundle.putString(variable_Message, message);
		
		notificationIntent.putExtras(bundle);
		
		PendingIntent contentIntent = PendingIntent.getActivity(notiContext, 0, notificationIntent, 0);
		
		notification.setLatestEventInfo(notiContext, notiTitle, message, contentIntent);
		
		//Pasar la notificación al notificationManager		
		
		
		manager.notify(MiServicio.HELLO_ID, notification);
		
	}
	
}
