package com.ackon.privacy;

import android.content.Context;
import android.content.Intent;

import com.olivestory.ackon.BootReceiver;

/**
 * SDK의 포함된 BootReceiver를 상속. AndroidManifest.xml 등록 후 사용 가능. 등록 시 퍼미션 설정 주의.
 * 
 * <p>
 * Permission<br>
 * android.permission.RECEIVE_BOOT_COMPLETED
 * <p>
 * Intent-filter<br>
 * android.intent.action.MAIN<br>
 * android.intent.category.LAUNCHER
 * 
 * 
 * @author android
 * 
 */
public class BootReceiver2 extends BootReceiver {

	// 부팅 리시버의 경우 AndroidManifest.xml에 등록할 경우 자동으로 서비스를 시작한다.
	// OPT IN의 동작에서만 서비스를 시작하려면 메소드를 오버라이딩 처리 해야 한다.

	/**
	 * super.onReceive(context, intent)가 호출되면 서비스는 자동실행됩니다.<br>
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// 동의 상태일 경우 서비스를 시작한다.
		if (context.getSharedPreferences("Privacy", 0).getBoolean("agree", false)) {
			super.onReceive(context, intent);
		}

	}

}
