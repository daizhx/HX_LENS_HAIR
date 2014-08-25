package com.hengxuan.eht.logger;

public class Log {
	
	public static boolean V;
	public static boolean D = true;
	public static boolean I = true;
	public static boolean W;
	public static boolean E = true;

	
	public static void d(String paramString2) {
		if (D) {
			android.util.Log.d("eht", paramString2);
		}
	}
	public static void d(String paramString1, String paramString2) {
		if (D) {
			android.util.Log.d(paramString1, paramString2);
		}
	}

	public static void d(String paramString1, String paramString2,
			Throwable paramThrowable) {
		if (D) {
			// android.util.Log.d(paramString1, paramString2, paramThrowable);
			System.out.println(paramString1 + "--" + paramString2 + "throwable info: " + paramThrowable.getMessage());
			
		}
	}
	
	public static void i(String s1,String s2){
		if(I){
			android.util.Log.i(s1, s2);
		}
	}
	
	public static String getStackTraceString(Throwable e){
		return android.util.Log.getStackTraceString(e);
	}
	
	public static void e(String msg){
		e("eht", msg);
	}
	public static void e(String tag, String msg){
		if(E){
			android.util.Log.e(tag, msg);
		}
	}
}
