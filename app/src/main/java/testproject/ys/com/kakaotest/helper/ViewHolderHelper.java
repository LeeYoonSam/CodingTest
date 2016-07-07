package testproject.ys.com.kakaotest.helper;

import android.util.SparseArray;
import android.view.View;

// 수동으로 클래스를 만들어서 관리할 필요가 없고 SparseArray에 adapter에서 사용할 view를 저장해서 재사용성을 높인다.
public class ViewHolderHelper {
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View convertView, int id) {

		try {
			SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();

			if (viewHolder == null) {
				viewHolder = new SparseArray<View>();
				convertView.setTag(viewHolder);
			}

			View childView = viewHolder.get(id);

			if (childView == null) {
				childView = convertView.findViewById(id);
				viewHolder.put(id, childView);
			}

			return (T) childView;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
