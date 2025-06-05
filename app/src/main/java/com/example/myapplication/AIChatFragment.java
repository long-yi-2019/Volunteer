package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ChatRequest;
import com.example.myapplication.api.ChatResponse;
import com.example.myapplication.api.DeepSeekApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class AIChatFragment extends Fragment {

    private EditText inputMessage;
    private Button sendButton;
    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> messageList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_chat, container, false);

        inputMessage = view.findViewById(R.id.inputMessage);
        sendButton = view.findViewById(R.id.sendButton);
        chatListView = view.findViewById(R.id.chatListView);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(getActivity(), messageList);
        chatListView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> {
            String question = inputMessage.getText().toString().trim();
            if (!question.isEmpty()) {
                addMessageToChat("用户", question, true);
                inputMessage.setText("");
                callDeepSeekAPI(question);
            }
        });

        return view;
    }

    private void addMessageToChat(String sender, String message, boolean isUser) {
        ChatMessage chatMessage = new ChatMessage(sender, message, isUser);
        messageList.add(chatMessage);
        chatAdapter.notifyDataSetChanged();
        chatListView.setSelection(chatAdapter.getCount() - 1);
    }

    private void callDeepSeekAPI(String question) {
        new Thread(() -> {
            try {
                String systemPrompt = getSystemPromptWithKnowledge(question);

                List<ChatRequest.Message> messages = new ArrayList<>();
                messages.add(new ChatRequest.Message("system", systemPrompt));

                for (ChatMessage msg : messageList) {
                    String role = msg.isMe ? "user" : "assistant";
                    messages.add(new ChatRequest.Message(role, msg.message));
                }

                messages.add(new ChatRequest.Message("user", question));

                ChatRequest request = new ChatRequest("deepseek-chat", messages);

                Call<ChatResponse> call = ApiClient.getDeepSeekApi().chat("Bearer sk-a75d7258857847e5ba72447cba0de927", request);
                Response<ChatResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    String answer = response.body().getAnswer();
                    requireActivity().runOnUiThread(() -> addMessageToChat("机器人", answer, false));
                } else {
                    requireActivity().runOnUiThread(() -> addMessageToChat("机器人", "无法获取回答，请稍后再试。", false));
                }
            } catch (IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> addMessageToChat("机器人", "网络错误，请检查连接。", false));
            }
        }).start();
    }

    private String getSystemPromptWithKnowledge(String question) {
        StringBuilder kb = new StringBuilder();

        if (containsAny(question, "注册", "账号", "登录")) {
            kb.append("Q:如何注册\n");
            kb.append("A:1. 打开 App，点击“注册”按钮。\n");
            kb.append("2. 输入手机号并获取验证码。\n");
            kb.append("3. 设置密码并填写基本信息（姓名、身份证号等）。\n");
            kb.append("4. 完成实名认证后即可登录使用。\n\n");

        } else if (containsAny(question, "报名", "活动")) {
            kb.append("Q:如何报名活动\n");
            kb.append("A:1. 进入活动详情页面\n");
            kb.append("2. 浏览活动信息\n");
            kb.append("3. 点击“预约活动”即可完成报名。\n\n");

            kb.append("Q:怎么申报活动\n");
            kb.append("A:1. 登录权限为活动组织者的账号\n");
            kb.append("2. 在个人中心，点击“发布活动”\n");
            kb.append("3. 按照顺序，填入活动名称、地点、志愿时长、活动时间等信息，上传宣传图片\n");
            kb.append("4. 点击立即发布即可完成申报活动。\n\n");

        } else if (containsAny(question, "审核", "通过", "拒绝")) {
            kb.append("Q:怎么审核活动\n");
            kb.append("A:1. 登录权限为管理员的账号\n");
            kb.append("2. 在个人中心，即可查看待审核活动\n");
            kb.append("3. 点击活动，查看详情\n");
            kb.append("4. 点击通过或拒绝按钮，完成活动审核。\n\n");

            kb.append("Q:怎么审核志愿服务信息\n");
            kb.append("A:1. 登录权限为活动组织者或管理员的账号\n");
            kb.append("2. 在个人中心，即可查看待审核志愿服务记录\n");
            kb.append("3. 点击志愿服务记录，查看详情\n");
            kb.append("4. 点击通过或拒绝按钮，完成志愿服务记录审核。\n\n");

        } else if (containsAny(question, "服务证明", "开具证明")) {
            kb.append("Q:如何导出服务证明\n");
            kb.append("A: 1. 进入“我的服务记录”页面。\n");
            kb.append("2. 选择需要导出的服务条目。\n");
            kb.append("3. 点击“申请开具证明”。\n");
            kb.append("4. 系统将自动生成 PDF 文件，可下载或分享。\n\n");

            kb.append("Q:怎么开志愿者证明\n");
            kb.append("A:1. 志愿者预约活动后，参加活动\n");
            kb.append("2. 完成活动后，可以从活动组织者获取活动证明\n");
            kb.append("3. 活动证明可以用于登记志愿服务记录。\n\n");

        } else if (containsAny(question, "上传", "记录", "提交")) {
            kb.append("Q:如何上传志愿服务记录\n");
            kb.append("A:1. 登录后进入“我的服务”页面。\n");
            kb.append("2. 点击“新增记录”。\n");
            kb.append("3. 填写服务时间、地点、内容。\n");
            kb.append("4. 上传相关照片或证明材料。\n");
            kb.append("5. 提交审核后等待管理员确认。\n\n");

        } else {
            // 默认通用帮助
            kb.append("这是通用帮助信息：您可以咨询注册、活动报名、服务记录上传、开具证明等问题。\n");
        }

        return "你是一个简洁高效的智能客服助手，专注于志愿者服务平台。\n"
                + "以下是相关知识库内容，请结合上下文和知识库内容回答用户的问题。\n"
                + "请以清晰的步骤方式呈现答案，保持口语化、简洁易懂，不要使用Markdown格式。\n\n"
                + kb.toString();
    }

    // 工具方法：判断问题中是否包含任意一个关键词
    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}