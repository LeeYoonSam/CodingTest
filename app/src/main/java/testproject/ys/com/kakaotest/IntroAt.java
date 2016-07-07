package testproject.ys.com.kakaotest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import testproject.ys.com.kakaotest.common.CommonData;
import testproject.ys.com.kakaotest.common.CommonAPI;
import testproject.ys.com.kakaotest.helper.NetworkHelper;

public class IntroAt extends Activity {

	// 파싱 시간 체크
	long start;
	int gridType = 1;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

		outState.putInt("gridType", gridType);

		CommonData.saveDataUtil(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);

		if(savedInstanceState != null)
		{
			gridType = savedInstanceState.getInt("userSeq");

			CommonData.restoreDataUtil(savedInstanceState);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.at_intro);

		// jericho - 엘리먼트를 아이디, 클래스별로 리스트에 담을수있다. 라이브러리 지원 업데이트가 멈춤. 속도가 들쑥날쑥하며 jsoup에 비해 평균속도가 느리다.
		Button btJericho = (Button)findViewById(R.id.btJericho);
		btJericho.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				gridType = 1;
				getInitData();
			}
		});

		// jsoup - jquery 와 비슷하며 예제가 많다. selector문법으로 엘리먼트 검색 .class > a > img 형태로 jquery와 비슷함. 문서화도 잘되어있움. 속도가 일정하고 빠르다.
		Button btJsoup = (Button)findViewById(R.id.btJsoup);
		btJsoup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				gridType = 2;
				getInitData();
			}
		});

		CommonData.getDisplayInfo(IntroAt.this, true);
	}

	// 초기 데이터 가져오기(리스트 보여줄때 속도를 향상하고자 인트로에서 데이터를 받아서 미리 저장하고 리스트에서는 나눠서 보여주는 역할만하도록 구성)
	public void getInitData() {

		start = System.currentTimeMillis(); // 시작시간

		// 이미 받은 데이터가 있으면 화면 바로 이동
		if (CommonData._alAllImageList.size() > 0) {
			moveAt();
		} else {

            if(!NetworkHelper.isConnect(IntroAt.this))
            {
                Toast.makeText(IntroAt.this, "네트워크 연결상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

			try {
				// 데이터를 다 받은 후에 다음 화면으로 넘김
				Thread thread = new Thread(new Runnable() {
					public void run() {

						switch (gridType) {
							// jericho 파싱
							case 1:
								CommonAPI.getJerichoParsing();
								break;
							// jsoup 파싱
							case 2:
								CommonAPI.getJsoupParsing();
								break;

							default:
								break;
						}

						handler.sendEmptyMessage(0);
					}
				});
				thread.start();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	Handler handler = new Handler()
	{
		public void handleMessage(Message msg) {
			switch(msg.what)
			{
				case 0:
					moveAt();
					break;
			}
		}
	};

	public void moveAt()
	{
		long end = System.currentTimeMillis(); // 끝난 시간
		long total = (end - start);
		Log.d("RunTime Check", "RunTime Check : " + total);

		// 초단위로 변환해서 보여주기
		Toast.makeText(IntroAt.this, "RunTime Check : " + (total * 0.001), Toast.LENGTH_SHORT).show();

		// jsoup 평균 실행시간 테스트 	(평균 : 3.641) 3.161 / 3.450 / 3.298 / 5.296 / 3.219 / 3.617 / 3.449
		// jericho 평균 실행시간 테스트 	(평균 : 3.482) 3.832 / 3.591 / 3.268 / 3.440 / 3.025 / 3.614 / 3.581

		Intent i = new Intent(IntroAt.this, MainAt.class);
		i.putExtra("gridType", gridType);
		startActivity(i);
	}
}
