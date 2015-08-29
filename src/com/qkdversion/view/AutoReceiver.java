package com.qkdversion.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
			 String action="android.intent.action.MAIN";   
		    String category="android.intent.category.LAUNCHER";   
		   Intent it=new Intent(context,MainActivity.class);   
		     it.setAction(action);   
		     it.addCategory(category);   
		    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    //延时一下，等待ARM板的IP配置成功
		    new Timer().schedule(new myTask(context,it), 10000); 
		}
	}
	private class myTask extends TimerTask{
		private Context context;
		private Intent it;
		public myTask(Context context,Intent it){
			this.context=context;
		    this.it=it;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			context.startActivity(it);
		}
		}	
}
