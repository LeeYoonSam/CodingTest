package testproject.ys.com.kakaotest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import testproject.ys.com.kakaotest.common.*;
import testproject.ys.com.kakaotest.R;
import testproject.ys.com.kakaotest.helper.ViewHolderHelper;

public class GridImageAdapter extends BaseAdapter {

	Context _context;
	ArrayList<String> _alAll;

	public GridImageAdapter(Context context, ArrayList<String> alAll) {
		this._context = context;
		this._alAll = alAll;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return _alAll.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return _alAll.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;

		if(v == null) {
			LayoutInflater li  = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.cell_image, null);
		}

		// 재사용을 위해 ViewHolder 사용
		final ImageView ivImage = ViewHolderHelper.get(v, R.id.ivImage);

		// 가로, 세로 크기 리사이즈
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(CommonData.gridSize, CommonData.gridSize);
		ivImage.setLayoutParams(params);

		// 기본 이미지를 넣어주면 리스트가 내려갈때 중복해서 나오는것을 방지 할수있다.
		ivImage.setImageResource(R.drawable.loading);

		// get imagename
		String imgUrl = CommonURL.BASE_URL + (String)getItem(position);

		// 메모리 캐시 옵션을 넣으니 스크를 빨리 했을때 앱이 죽음
		//		Picasso p = new Picasso.Builder(_context)
		//	    .memoryCache(new LruCache(TestProjectApplication.MEMORY_CACHE_LIMIT_VENUE))
		//	    .build();

        Picasso.with(_context).load(imgUrl).placeholder(R.drawable.loading).error(android.R.drawable.ic_dialog_alert).into(ivImage);
//		Picasso.with(_context).load(imgUrl).resize(CommonData.gridSize, CommonData.gridSize).placeholder(R.drawable.loading).error(android.R.drawable.ic_dialog_alert).into(ivImage);
//		Picasso.with(_context).load(imgUrl).into(new Target() {
//			@Override
//			public void onBitmapLoaded(Bitmap bitmap, LoadedFrom loadedFrom) {
//
//				Bitmap resize = CommonData.convertBitmapToDownOption(bitmap);
//
//				ivImage.setImageBitmap(resize);
//			}
//
//
//			@Override
//			public void onBitmapFailed(Drawable arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onPrepareLoad(Drawable arg0) {
//				// TODO Auto-generated method stub
//
//			}
//		});

        /**
         * Picasso
         *
         * Picasso는 다른 라이브러리보다 간단하고 직관적인 메서드 체인 문법을 갖고 있다는 것이 차별화된 강점이다.
         * 이미지 로딩 문제 해결에 필요한 기능들도 충분하고 디버깅을 도와주는 기능도 독창적이다.
         * 쉽고 깔끔한 개발에 도움이 된다.
         *
         * 기본     HttpClient : OkHttp가 있을 때: OkHttp HttpURLConnection
         * 기본 지원 HttpClient : HttpURLConnection, OkHttp
         *
         * 기본 비트맵 디코딩 옵션 : BitmapConfig 기본값
         *
         * 디스크 캐시 사이즈(File dir에 해당하는 폴더의 전체 용량을 구해서 50으로 나눈 2%만 캐시용량으로 사용한다. 최대와 최소 범위안에서)
         * 메모리 캐시 사이즈 (전체 힙영역의 15%정도 사용)
         *
         * 네트워크의 형태에 따라 쓰레드 카운트 관리 (wifi, 4g, 3g, 2g)
         *
         * 연결 타임아웃 15초, 읽기 타임아웃 20초
         */

		return v;
	}

}
