package testproject.ys.com.kakaotest;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.content.Context;

public class TestProjectApplication extends Application {

    // AUIL에서 사용할 메모리 캐시, 디스크 캐시 용량 제한
	public static final int MEMORY_CACHE_LIMIT_VENUE = 1024 * 1024 * 12;        // 비트맵 디코딩을 거친 비트맵 데이터를 메모리 캐시에 저장
    public static final int DISK_CACHE_LIMIT_VENUE = 1024 * 1024 * 128;         // 이미지를 다운로드하고 비트맵 디코딩하기전의 이미지를 디스크 캐시에 저

    @Override
    public void onCreate() {
        super.onCreate();

        initImageLoader(this);
    }

    // Is no longer necessary to initialize
 	public static void initImageLoader(Context context)
 	{
        /*
        쓰레드 우선순위
        public static final int MAX_PRIORITY = 10;  // 최대 우선순위
        public static final int NORM_PRIORITY = 5;  // 보통 우선순위
        public static final int MIN_PRIORITY = 1;  // 최소 우선순위
         */

 		// Create global configuration and initialize ImageLoader with this config
         LRULimitedMemoryCache memoryCache = new LRULimitedMemoryCache(MEMORY_CACHE_LIMIT_VENUE);
         ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                 .threadPoolSize(2)                                         // 병렬작업을 위한 별도의 스레드풀
                 .threadPriority(Thread.NORM_PRIORITY - 2)                  // 중요도에 따라 쓰레드의 우선순위를 정하는데 '보통우선순위 - 2 = 3'으로 비교적 쓰레드 우선순위를 낮게 잡아서 다른 쓰레드가 더 많은 시간을 갖게 해준다.
                 .denyCacheImageMultipleSizesInMemory()
                 .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                 .diskCacheSize(DISK_CACHE_LIMIT_VENUE)
                 .tasksProcessingOrder(QueueProcessingType.LIFO)
                 .writeDebugLogs()                                          // 로그데이터 남김
                 .memoryCache(memoryCache)
                 .build();
         
         ImageLoader.getInstance().init(config);

        // ListView가 스크롤된 후 마지막에 보이는 아이템의 이미지가 마지막으로 큐에 들어가므로, LIFO 방식으로 처리하면 마지막 항목이 먼저 디큐되어 더욱 빠르게 ListView 안에서 이미지를 볼 수 있다.
 	}
}
