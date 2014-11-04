package com.ackon.privacy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.View.OnClickListener;

import com.olivestory.ackon.AckonDataManager;
import com.olivestory.ackon.AckonService;
import com.olivestory.ackon.AckonUpdateListener;
import com.olivestory.ackon.cms.AckonDataDel;
import com.olivestory.ackon.cms.OnAckonAgreeResult;
import com.olivestory.ackon.cms.OnAckonDataDelResult;
import com.olivestory.ackon.cms.SetAckonAgree;

/**
 * 사용자 동의를 받고 서비스를 실행 시키는 샘플입니다.
 * 
 * @author android
 * 
 */
public class MainActivity extends ActionBarActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SwitchCompat agree = (SwitchCompat) findViewById(R.id.opt);
		agree.setChecked(getSharedPreferences("Privacy", 0).getBoolean("agree", false));

		agree.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final SwitchCompat opt = (SwitchCompat) v;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (opt.isChecked()) {
			builder.setTitle(R.string.app_name);
			builder.setMessage(R.string.opt_in);
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SetAckonAgree agree = new SetAckonAgree(getApplicationContext());
					agree.setAckonAgree(new OnAckonAgreeResult() {
						@Override
						public void onAckonAgreeResult(boolean result) {
							// result가 true면 동의.
							if (result) {
								opt.setChecked(true);

								// 동의 후 CMS 데이터를 갱신.
								AckonDataManager.update(getApplicationContext(), new AckonUpdateListener() {

									@Override
									public void onUpdateResult(boolean result) {
										// CMS 데이터 갱신이 완료 되면 서비스를 시작.
										if (result) {
											/*
											 * Ackon 서비스가 시작 되면 AckonService.stopService (getApplicationContext()) 메소드를
											 * 호출하기 전에는 서비스는 항상 동작합니다.
											 */

											getSharedPreferences("Privacy", 0).edit().putBoolean("agree", true)
													.commit();
											AckonService.startService(getApplicationContext());

											/*
											 * 재부팅 후 서비스는 재시작 될 수 없기 때문에 BootReceiver를 상복받아 BootReceiver2를
											 * 등록한다.(BootReceiver를 상속받은 클래스)
											 */
										}
									}
								});
							}

						}
					});
				}
			});
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (opt != null)
						opt.setChecked(false);
				}
			});

		} else {
			builder.setTitle(R.string.app_name);
			builder.setMessage(R.string.opt_out);

			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AckonDataDel del = new AckonDataDel(getApplicationContext());
					del.setAckonDataDel(new OnAckonDataDelResult() {

						@Override
						public void onAckonDataDelResult(boolean result) {
							// result가 true면 서버에 저장된 모든 위치정보 값이 지워집니다.
							if (opt != null)
								if (result) {
									opt.setChecked(false);

									// 동의 상태 수정
									getSharedPreferences("Privacy", 0).edit().putBoolean("agree", false).commit();

									// 서비스 중지
									AckonService.stopService(getApplicationContext());
								}
						}
					});
				}
			});
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (opt != null)
						opt.setChecked(true);
				}
			});

		}

		// 다이얼로그 출력
		builder.show();
	}

}
