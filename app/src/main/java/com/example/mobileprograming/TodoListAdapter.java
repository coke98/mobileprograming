package com.example.mobileprograming;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileprograming.model.TodoItem;

import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TodoListAdapter extends BaseAdapter {

    private final List dataList;


    DatabaseService dbService;

    public TodoListAdapter(List dataList, DatabaseService dbService) {
        this.dataList = dataList;
        this.dbService = dbService;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        }
        TodoItem todoItem = (TodoItem) dataList.get(position);
        String dateTimeStr = todoItem.getDate();
        long date = Long.parseLong(dateTimeStr.substring(0, dateTimeStr.indexOf(" ")));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        String dateStr = calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
        String time = dateTimeStr.substring(dateTimeStr.indexOf(" ")).trim();

        CheckBox todoCheckBox = convertView.findViewById(R.id.todo_item_cb);
        TextView todoTitle = convertView.findViewById(R.id.todo_item_title_tv);
        TextView todoContent = convertView.findViewById(R.id.todo_item_contents_tv);
        TextView todoDate = convertView.findViewById(R.id.todo_item_date_tv);

        todoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) return;
                dbService.deleteToDoItem(todoItem.getId());
                dataList.remove(position);
                Toast toast = Toast.makeText(parent.getContext(), todoItem.getTitle() +"일정을 완료하였습니다.", Toast.LENGTH_SHORT);
                toast.show();
                notifyDataSetChanged();
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), TodoUpdateActivity.class);
                intent.putExtra("id", todoItem.getId());
                intent.putExtra("title", todoItem.getTitle());
                intent.putExtra("content", todoItem.getContent());
                intent.putExtra("date", todoItem.getDate());
                parent.getContext().startActivity(intent);
            }
        });

        todoCheckBox.setChecked(todoItem.getIsDone());
        todoTitle.setText(todoItem.getTitle());
        todoContent.setText(todoItem.getContent());
        todoDate.setText(dateStr + " " + time);
        return convertView;
    }



    public void searchTodoTitle(String keyword) {
        dataList.clear();
        dataList.addAll(dbService.searchTodoTitle(keyword));
        notifyDataSetChanged();
    }

}
