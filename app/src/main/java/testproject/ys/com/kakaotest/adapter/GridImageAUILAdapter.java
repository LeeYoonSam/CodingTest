package testproject.ys.com.kakaotest.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import testproject.ys.com.kakaotest.R;
import testproject.ys.com.kakaotest.common.CommonData;
import testproject.ys.com.kakaotest.common.CommonURL;
import testproject.ys.com.kakaotest.helper.ViewHolderHelper;

public class GridImageAUILAdapter  extends BaseAdapter {

	Context _context;
	ArrayList<String> _alAll;

	ImageLoader imageLoader;
	DisplayImageOptions options;
		
	public GridImageAUILAdapter(Context context, ArrayList<String> alAll) {
		this._context = context;
		this._alAll = alAll;
		
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(android.R.drawable.stat_notify_error)     // 존재하지 않는 URI일때 이미지 처리
                .showImageOnFail(android.R.drawable.stat_notify_error)          // 이미지 로드 실패시 이미지 처리 | 로딩시 이미지 처리도 있지만 로딩시 이미지 처리는 cell의 imageview에 기본으로 이미지로 세팅
                .cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
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
		ImageView ivImage = ViewHolderHelper.get(v, R.id.ivImage);
		
		// 가로, 세로 크기 리사이즈
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(CommonData.gridSize, CommonData.gridSize);
		ivImage.setLayoutParams(params);
		
		// 기본 이미지를 넣어주면 리스트가 내려갈때 중복해서 나오는것을 방지 할수있다.
		ivImage.setImageResource(R.drawable.loading);

		// get imagename
		String imgUrl = CommonURL.BASE_URL + (String)getItem(position);
		imageLoader.displayImage(imgUrl, ivImage, options);

		/**
		 * AUIL
		 *
		 * 많은 앱에서 사용중이며, 화면크기를 기준으로 캐시용량을 제한하는 등 다양한 캐시 정책을 운영한다.
		 * Executor, 쓰레드풀 크기, 비트맵, 옵션등 다른 라이브러리들에 비해 사용자가 변경할수 있는 옵션이 많다.
		 * 이미지 다운로드 후 처리 가능(후처리에서 비트맵 리사이즈등의 작업을 할수도 있다.)
		 * HTTP , File, ContentProvider Assets, Drawable 모두 동일한 사용법을 통해 이미지를 보여줄 수 있다.
		 *
		 * 기본     HttpClient : HttpURLConnection
		 * 기본 지원 HttpClient : HttpURLConnection, Apache HttpClient
		 *
		 * Out of Memory 처리 : 가용 메모리를 확보하는 하기위해 메모리 캐시를 비운다.
		 *
		 * 기본 비트맵 디코딩 옵션 : ARGB_8888
		 *
		 * **디스크 캐시에 이미지 없음
		 - 3가지 이미지 다운로드 존재
		 if (engine.isNetworkDenied()) {
		 d = networkDeniedDownloader;     // scared, assets 등의 파일일때
		 } else if (engine.isSlowNetwork()) {
		 d = slowNetworkDownloader;       // 네트워크상태가 느릴때 사용
		 } else {
		 d = downloader;                  // 특별한 예외가 없을때 기본사용
		 }

		 - 연결 타임아웃 5초, 읽기 타임아웃 20초, 버퍼 사이즈 32kb, 최대 재시도 횟수 5회

		 */

		return v;
	}

}
