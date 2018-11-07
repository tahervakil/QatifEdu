package com.taher.qatifedu.utility;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CustomAdapter extends BaseAdapter
{
	private ArrayList list;
	private Context context;
	private int iLayout;
	private ListFiller filler;
	boolean blLoading = true;
	private int iLoadingViewIndex = -1;
	
	boolean blScrolling = false;
	boolean blLastStateChanged = false;
	public CustomAdapter (Context pcontext, ArrayList plist, int piLayout, ListFiller pfiller)
	{
		this.context = pcontext;
		this.iLayout = piLayout;
		this.filler = pfiller;
		
		this.list = plist;		
	}
	
	
	@Override
	public int getCount() {
		String filter = filler.getFilter();
		if(filter==null)
			return 0;
		ArrayList temp = null;
		temp = filler.getFilteredList();
		/*if(filter.length() != 0)
		temp = filler.getFilteredList();
		else
			temp = list;*/
		return temp.size();
	}

	
	public int setFilter(int size) {
		ArrayList Newlist = new ArrayList(size);
		return Newlist.size();
	}
	
	@Override
	public Object getItem(int arg0) {
		
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
	public boolean isEnabled(int pos)
	{
		//return (filler == null ? true : filler.isEnabled(pos));
		return ( filler.isEnabled(pos));
	}
	
	
	
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2)
	{
		try
	{
		if (filler.useExtGetView())
		{
			arg1 = filler.getView(arg0, arg1);
		}
		else
		{
			if (arg1 == null)
			{
				LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				arg1 = li.inflate(iLayout, null);
			}
			else
				arg1.setTag("Recycled");
			
			
				filler.fillListData(arg1, arg0);
			}}
			catch (Exception ex)
			{ 
				ex.getMessage();
			}
		
		return arg1;
	}
	
	public interface ListFiller
    {
    	public void fillListData (View v, int pos);
    	public boolean isEnabled (int pos);
    	public boolean useExtGetView();
    	public View getView(int pos, View v);
    	public String getFilter();
    	public ArrayList getFilteredList();
    }

}
    
    