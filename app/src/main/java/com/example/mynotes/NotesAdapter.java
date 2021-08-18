package com.example.mynotes;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> implements Filterable {

    ItemClicked activity;
    Context context;
    public static int f=0;
    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter=new Filter() {

        // run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(f==0)
            {
                listofNotesAll=new ArrayList<>(listOfNotes);
                f=1;
            }
            ArrayList<Notes> filteredList=new ArrayList<>();
            if(constraint.toString().isEmpty())
            {
                filteredList.addAll(listofNotesAll);
            }
            else
            {
                for(int i=0;i<listofNotesAll.size();i++)
                {
                    String s=listofNotesAll.get(i).getNotes();
                    String str=listofNotesAll.get(i).getHeading();
                    if(s.contains(constraint) || str.contains(constraint))
                    {
                        filteredList.add(listofNotesAll.get(i));
                    }
                }
            }
            FilterResults filterResults=new FilterResults();
            filterResults.values=filteredList;
            return filterResults;
        }


        // run on ui thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listOfNotes.clear();
            listOfNotes.addAll((Collection<? extends Notes>) results.values);
            notifyDataSetChanged();
        }
    };

    public interface ItemClicked
    {
        public void onItemClicked(int index);
        public void onLongItemClicked(int index);
    }

    ArrayList<Notes> listOfNotes;
    ArrayList<Notes> listofNotesAll;
    public NotesAdapter(ArrayList<Notes> list, Context context)
    {
        this.listOfNotes=list;
        activity=(ItemClicked)context;
        this.context=context;
        this.listofNotesAll=new ArrayList<>(list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvHeading,tvContent,tvDate;
        ImageView ivAlarm,ivThumbnail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent=itemView.findViewById(R.id.tvContent);
            tvDate=itemView.findViewById(R.id.tvDate);
            tvHeading=itemView.findViewById(R.id.tvHeading);
            ivAlarm=itemView.findViewById(R.id.ivAlarm);

            SharedPreferences sharedPreferences1=context.getSharedPreferences("PREFS",0);
            String bodyFont=sharedPreferences1.getString("bodyFont","convergence");
            if(bodyFont.equals("convergence"))
            {
                Typeface typeface = ResourcesCompat.getFont(context,R.font.convergence);
                tvContent.setTypeface(typeface);
                tvHeading.setTypeface(typeface);
            }
            else if(bodyFont.equals("monospace"))
            {
                tvContent.setTypeface(Typeface.MONOSPACE);
                tvHeading.setTypeface(Typeface.MONOSPACE);
            }
            else
            {
                Typeface typeface = ResourcesCompat.getFont(context,R.font.andada);
                tvContent.setTypeface(typeface);
                tvHeading.setTypeface(typeface);
            }

            String fontSize=sharedPreferences1.getString("fontSize","medium");
            if(fontSize.equals("small"))
            {
                tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
                tvHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
                tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
            }
            else if(fontSize.equals("medium"))
            {
                tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                tvHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            }
            else
            {
                tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                tvHeading.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onItemClicked(listOfNotes.indexOf((Notes)v.getTag()));
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    activity.onLongItemClicked(listOfNotes.indexOf((Notes)v.getTag()));
                    return true;
                }
            });
        }
    }
    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(listOfNotes.get(position));
        String s=listOfNotes.get(position).getNotes();
        String str=listOfNotes.get(position).getHeading();
        ArrayList<TodoListClass> todoList=listOfNotes.get(position).getTodoList();
        if(str.isEmpty() && todoList.size()>0)
        {
            str=todoList.get(0).getTodo();
            if(s.isEmpty())
            {
                for(int i=1;i<Math.min(5,todoList.size());i++)
                {
                    s+=todoList.get(i).getTodo();
                    s+="\n";
                }
            }
        }
        else
        {
            if(s.isEmpty() && todoList.size()>0)
            {
                for(int i=0;i<Math.min(5,todoList.size());i++)
                {
                    s+=todoList.get(i).getTodo();
                    s+="\n";
                }
            }
        }
        holder.tvContent.setText(s);
        holder.tvHeading.setText(str);

        boolean isAlarmSet=listOfNotes.get(position).isAlarmSet();
        if(isAlarmSet)
        {
            holder.ivAlarm.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.ivAlarm.setVisibility(View.GONE);
        }

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String date=listOfNotes.get(position).getDate();
        String time=listOfNotes.get(position).getTime();

        int cyear=Integer.parseInt(currentDate.substring(6));
        int year=Integer.parseInt(date.substring(6));
        int cmonth=Integer.parseInt(currentDate.substring(3,5));
        int month=Integer.parseInt(date.substring(3,5));
        int cday=Integer.parseInt(currentDate.substring(0,2));
        int day=Integer.parseInt(date.substring(0,2));

        String monthArr[]={"","January","February","March","April","May","June","July","August","September","October",
                "November","December"};

        if(cyear==year)
        {
            if(cmonth==month && cday==day)
            {
                int hour=Integer.parseInt(time.substring(0,2));
                String min=time.substring(3,5);
                String period="";
                if(hour<12)
                {
                    hour=hour%12;
                    if(hour==0) hour=12;
                    period="AM";
                }
                else
                {
                    hour-=12;
                    if(hour==0) hour=12;
                    period="PM";
                }
                String textTime=hour+":"+min+" "+period;
                holder.tvDate.setText(textTime);
            }
            else if(cmonth==month && day+1==cday)
            {
                holder.tvDate.setText("yesterday");
            }
            else
            {
                String mon=monthArr[month];
                String text=mon+" "+day;
                holder.tvDate.setText(text);
            }
        }
        else
        {
            String mon=monthArr[month];
            String text=mon+" "+day+", "+year;
            holder.tvDate.setText(text);
        }
        if(str.isEmpty())
        {
            holder.tvHeading.setVisibility(View.GONE);
        }
        else
        {
            holder.tvHeading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listOfNotes.size();
    }



}
