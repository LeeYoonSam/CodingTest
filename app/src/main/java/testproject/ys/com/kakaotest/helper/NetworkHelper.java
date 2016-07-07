package testproject.ys.com.kakaotest.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ys on 2016. 5. 9..
 */
public class NetworkHelper {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static boolean isConnect(Context context)
    {
        int conn = NetworkHelper.getConnectivityStatus(context);
        if (conn == NetworkHelper.TYPE_WIFI) {
            return true;
        } else if (conn == NetworkHelper.TYPE_MOBILE) {
            return true;
        } else if (conn == NetworkHelper.TYPE_NOT_CONNECTED) {
            return false;
        }
        return false;
    }
}
