package testproject.ys.com.kakaotest.common;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.util.Log;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class CommonAPI {

	/**
	 * HTML 파싱으로 데이터 가져오기
	 *
	 * @return ArrayList<String>
	 */
	public static boolean getJerichoParsing()
	{
		try
		{
			Source source = new Source(new URL(CommonURL.SITE_URL));
			source.fullSequentialParse();

			ArrayList<String> resultList = new ArrayList<String>();

			// class에 해당하는 Element를 List로 받기
			List<Element> divList = source.getAllElementsByClass("gallery-item-group");

			// Element 내에서 필요한 a > img 태그 경로에 src에 있는 이미지값 가져오
			for(int i = 0; i < divList.size(); i ++)
			{
				Element tagA = divList.get(i).getAllElements(HTMLElementName.A).get(0);
				Element tagImg = tagA.getAllElements(HTMLElementName.IMG).get(0);

				Log.d("tagImg", "tagImg name : " + tagImg.getAttributeValue("src").toString());

				resultList.add(tagImg.getAttributeValue("src").toString());
			}

			// 파싱한 이미지 리스트를 CommonData클래스에 저장해서 리스트에서는 바로 보여주도록 한다.
			if(resultList != null)
			{
				CommonData._alAllImageList.clear();
				CommonData._alAllImageList.addAll(resultList);
			}

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
	}

	public static boolean getJsoupParsing()
	{
		Document document;
		try {
			document = Jsoup.connect(CommonURL.SITE_URL).get();

			ArrayList<String> resultList = new ArrayList<String>();

			if (null != document)
			{
				// gallery-item-group 클래스, a태그 > img태그 > src에 있는 이미지값 가져오기
				Elements elements = document.select("div.gallery-item-group > a > img");
				System.out.println("elements.size()"+ elements.size());

				for (int i = 0; i < elements.size(); i++)
				{
					Log.d("tagImg", "tagImg name : " + elements.get(i).attr("src"));
					resultList.add(elements.get(i).attr("src"));
				}

				// 파싱한 이미지 리스트를 CommonData클래스에 저장해서 리스트에서는 바로 보여주도록 한다.
				if(resultList != null)
				{
					CommonData._alAllImageList.clear();
					CommonData._alAllImageList.addAll(resultList);
				}
			}

			return true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return false;
		}
	}
}
