package com.example.mynotes;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    ItemClicked activity;
    Context context;
    public ArrayList<TodoListClass> todoList;
    public static int f=0;

    public interface ItemClicked
    {
        public void onCheckedClick(int index);
        public void listText(int index,String text);
        public void clearItem(int index);
    }

    public TodoAdapter(Context context, ArrayList<TodoListClass> todoList)
    {
        this.context=context;
        this.todoList=todoList;
        activity=(ItemClicked)context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        RadioButton btnCheck;
        EditText etTodo;
        ImageView ivClear;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnCheck=itemView.findViewById(R.id.btnCheck);
            etTodo=itemView.findViewById(R.id.etTodo);
            ivClear=itemView.findViewById(R.id.ivClear);

            SharedPreferences sharedPreferences1=context.getSharedPreferences("PREFS",0);
            String bodyFont=sharedPreferences1.getString("bodyFont","convergence");
            if(bodyFont.equals("convergence"))
            {
                Typeface typeface = ResourcesCompat.getFont(context,R.font.convergence);
                etTodo.setTypeface(typeface);
            }
            else if(bodyFont.equals("monospace"))
            {
                etTodo.setTypeface(Typeface.MONOSPACE);
            }
            else
            {
                Typeface typeface = ResourcesCompat.getFont(context,R.font.andada);
                etTodo.setTypeface(typeface);
            }

            String fontSize=sharedPreferences1.getString("fontSize","medium");
            if(fontSize.equals("small"))
            {
                etTodo.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            }
            else if(fontSize.equals("medium"))
            {
                etTodo.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            }
            else
            {
                etTodo.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            }

            btnCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ind=todoList.indexOf((TodoListClass) itemView.getTag());
                    activity.onCheckedClick(ind);
                }
            });
            TextWatcher textWatcher=new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    todoList.get(getAdapterPosition()).setTodo(etTodo.getText().toString());
                    int ind=todoList.indexOf((TodoListClass) itemView.getTag());
                    activity.listText(ind,etTodo.getText().toString());
                }
            };
            etTodo.addTextChangedListener(textWatcher);

            etTodo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus)
                    {
                        ivClear.setVisibility(View.VISIBLE);
                        InputMethodManager inputMethodManager=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(inputMethodManager!=null)
                        {
                            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,1);
                        }
                    }
                    else
                    {
                        ivClear.setVisibility(View.GONE);
                    }
                }
            });

            ivClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ind=todoList.indexOf((TodoListClass) itemView.getTag());
                    activity.clearItem(ind);
                }
            });
        }
    }

    @NonNull
    @Override
    public TodoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list,parent,false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull TodoAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(todoList.get(position));
        String s=todoList.get(position).getTodo();
        holder.etTodo.setText(s);
        boolean isCancelled=todoList.get(position).isCancelled;
        if(isCancelled)
        {
            holder.btnCheck.setChecked(true);
            holder.btnCheck.setButtonDrawable(R.drawable.check_ok);
            holder.etTodo.setPaintFlags(holder.etTodo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else
        {
            holder.btnCheck.setChecked(false);
            holder.btnCheck.setButtonDrawable(R.drawable.check);
            holder.etTodo.setPaintFlags(0);
        }
        if(todoList.size()>0 && position==todoList.size()-1 && f==1)
        {
            holder.etTodo.requestFocus(1);
        }
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }
}
