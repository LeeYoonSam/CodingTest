package testproject.ys.com.kakaotest.common;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CommonData {
	public static ArrayList<String> _alAllImageList = new ArrayList<String>();
	public static int displayWidth = 0;
	public static int displayHeight = 0;
	public static int gridSize = 0;

    // 공통으로 사용하는 클래스의 데이터를 저장하는 메서드
	public static Bundle saveDataUtil(Bundle outState)
	{
		outState.putStringArrayList("imageList", _alAllImageList);
		outState.putInt("displayWidth", displayWidth);
		outState.putInt("displayHeight", displayHeight);
        outState.putInt("gridSize", gridSize);

		return outState;
	}

    // 공통으로 사용하는 클래스의 데이터를 복구하는 메서드
	public static void restoreDataUtil(Bundle savedInstanceState)
	{
		_alAllImageList = savedInstanceState.getStringArrayList("imageList");
		displayWidth = savedInstanceState.getInt("displayWidth");
		displayHeight = savedInstanceState.getInt("displayHeight");
        gridSize = savedInstanceState.getInt("gridSize");

        Log.d("restoreDataUtil", "restoreDataUtil - _alAllImageList size : " + _alAllImageList.size());
        Log.d("displayWidth", "displayWidth  : " + displayWidth);
        Log.d("displayHeight", "displayHeight  : " + displayHeight);
        Log.d("gridSize", "gridSize  : " + gridSize);
	}

    // 화면 크기를 가저와서 그리드에서 사용할 이미지의 크기를 미리 세
	public static void getDisplayInfo(Activity activity, boolean isPortrait)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		displayWidth = metrics.widthPixels;
		displayHeight = metrics.heightPixels;


        // 세로모드일경우 그리드뷰 컬럼수를 2개로 하였기 때문에 전체화면의 1/2에 해당하는 크기를 지
		if(isPortrait)
		{
			if(displayWidth > 0)
				gridSize = (displayWidth / 2);
			else if(displayHeight > 0)
				gridSize = getDivision(360.0f);	// 높이가 1280인 디바이스 기준으로 가로 360px로 하면 가로를 꽉채우는 정사각형의 한 면의 길이를 구한다.
			else
				gridSize = 360;
		}
        // 가로모드일경우 그리드뷰 컬럼수를 3개로 하였기 때문에 전체화면의 1/3에 해당하는 크기를 지정
		else
		{
			if(displayWidth > 0)
				gridSize = (displayWidth / 3);
			else if(displayHeight > 0)
				gridSize = getDivision(240.0f);	// 높이가 1280인 디바이스 기준으로 가로 360px로 하면 가로를 꽉채우는 정사각형의 한 면의 길이를 구한다.
			else
				gridSize = 240;
		}

	}

    // 1280의 높이를 갖는 디바이스를 기준으로 다른 해상도와의 비율을 구해서 그에 해당하는 수치를 변환해줌
	public static int getDivision(float value)
	{
		return (int)(value * (displayHeight / 1280.0f));
	}

    // 사진의 화질이 엄청 중요하지 않은 이상 bitmap을 RGB_565로 컨버팅하면 모바일에서 사용하기 적당하다.
	public static Bitmap convertBitmapToDownOption(Bitmap bitmap)
	{
		/*
		 * LCD모니터의 경우 CRT모니터와 달리 색상 구현에 한계가 있다고 하며 LCD모니터가 출력하지 못 하는 비트를 제거하여 RGB555가 되며, 이중 사람의 눈에 가장 민감한 녹색 부분만을 1비트 추가하여 RGB565가 됩니다.
		 * 그래서 안드로이드에서 RGB_565의 값을 주고 decode된 이미지는 하나의 Pixel 정보를 R(5비트), G(6비트), B(5비트) 모두 2바이트 (16비트)로 처리하며, 이게 안드로이드의 기본 값입니다.
		 * (iOS에선 RGB8888이 기본값입니다.) 
		 */
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Config.RGB_565);

		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);

		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	// 이미지 초기화
	public static void unbindDrawables(View view) {

		try {
			if (view == null)
				return;

			if (view instanceof ImageView) {
				((ImageView) view).setImageDrawable(null);
                ((ImageView) view).setImageBitmap(null);
			}

			if (view.getBackground() != null) {
				view.getBackground().setCallback(null);
			}

			// 뷰그룹일 경우 재귀호출(recursive)을 통해서 하위뷰까지 초기화
			if (view instanceof ViewGroup) {
				for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
					unbindDrawables(((ViewGroup) view).getChildAt(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
