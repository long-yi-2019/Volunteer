// ChatAdapter.java
package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ChatAdapter extends BaseAdapter {

    private Context context;
    private List<ChatMessage> messageList;

    public ChatAdapter(Context context, List<ChatMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ChatMessage message = messageList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
            holder.userMessageLayout = convertView.findViewById(R.id.user_message_layout);
            holder.userMessageText = convertView.findViewById(R.id.user_message_text);
            holder.robotMessageLayout = convertView.findViewById(R.id.robot_message_layout);
            holder.robotMessageText = convertView.findViewById(R.id.robot_message_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 根据消息类型显示不同的视图
        if (message.isMe) {
            holder.userMessageLayout.setVisibility(View.VISIBLE);
            holder.robotMessageLayout.setVisibility(View.GONE);
            holder.userMessageText.setText(message.message);
        } else {
            holder.userMessageLayout.setVisibility(View.GONE);
            holder.robotMessageLayout.setVisibility(View.VISIBLE);
            holder.robotMessageText.setText(message.message);
        }

        return convertView;
    }

    private static class ViewHolder {
        View userMessageLayout;
        TextView userMessageText;
        View robotMessageLayout;
        TextView robotMessageText;
    }
}