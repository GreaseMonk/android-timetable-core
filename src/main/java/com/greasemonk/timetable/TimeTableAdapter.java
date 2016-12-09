package com.greasemonk.timetable;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

/**
 * Created by Wiebe Geertsma on 9-12-2016.
 * E-mail: e.w.geertsma@gmail.com
 */
public class TimeTableAdapter extends FastItemAdapter<PannableItem>
{
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		return new ViewHolder(parent);
	}
	
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
	{
		super.onBindViewHolder(holder, position);
	}
	
	protected static class ViewHolder extends RecyclerView.ViewHolder
	{
		protected TextView name;
		
		public ViewHolder(View view)
		{
			super(view);
			this.name = (TextView) view.findViewById(R.id.text1);
		}
	}
}
