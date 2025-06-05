package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.Entity.Account;
import com.example.myapplication.Entity.Activity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private WebView webView;
    private Button btnExportPDF;

    // 模拟数据
    private String name = "张三";
    private int totalHours = 125;
    private int activityCount = 9;
    private List<String> activities = new ArrayList<>();
    private String joinDate = "2023年5月12日";

    private DatabaseHelper databaseHelper;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(requireContext());

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        VolunteerViewModel viewModel = new ViewModelProvider(requireActivity()).get(VolunteerViewModel.class);
        viewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
            if (username != null && !username.isEmpty()) {
                name = username;
                activityCount = databaseHelper.getActivityNumberByUserName(name);
                totalHours = databaseHelper.getUserVolunteerTime(name);
                List<Activity> activitiesByUsername = databaseHelper.getActivitiesByUsername(name);
                activities.clear();
                for (Activity activity : activitiesByUsername) {
                    activities.add(activity.getName());
                }

                // 构建 HTML 并加载到 WebView
                String htmlContent = buildProfileHTML();
                webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        webView = view.findViewById(R.id.webViewProfile);
        btnExportPDF = view.findViewById(R.id.btnExportPDF);

        // 导出按钮点击事件
        btnExportPDF.setOnClickListener(v -> createWebPrintJob(webView));

        return view;
    }

    private String buildProfileHTML() {
        String template = "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>志愿服务履历</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: \"Microsoft YaHei\", sans-serif;\n" +
                "            background-color: #f9f9f9;\n" +
                "            color: #333;\n" +
                "            padding: 40px;\n" +
                "            max-width: 960px;\n" +
                "            margin: auto;\n" +
                "        }\n" +
                "        h1 {\n" +
                "            text-align: center;\n" +
                "            color: #2c3e50;\n" +
                "            border-bottom: 2px solid #3498db;\n" +
                "            padding-bottom: 10px;\n" +
                "        }\n" +
                "        .section {\n" +
                "            margin-top: 30px;\n" +
                "        }\n" +
                "        .section h2 {\n" +
                "            color: #2980b9;\n" +
                "            border-left: 4px solid #3498db;\n" +
                "            padding-left: 10px;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        p {\n" +
                "            line-height: 1.6;\n" +
                "            font-size: 16px;\n" +
                "        }\n" +
                "        ul {\n" +
                "            list-style-type: square;\n" +
                "            padding-left: 20px;\n" +
                "        }\n" +
                "        li {\n" +
                "            margin-bottom: 6px;\n" +
                "            font-size: 15px;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            margin-top: 50px;\n" +
                "            text-align: right;\n" +
                "            font-style: italic;\n" +
                "            color: #777;\n" +
                "        }\n" +
                "        img.qr {\n" +
                "            display: block;\n" +
                "            margin: 20px auto;\n" +
                "            width: 120px;\n" +
                "            height: 120px;\n" +
                "        }\n" +
                "        @media print {\n" +
                "            body {\n" +
                "                background-color: #fff;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h1>志愿服务履历</h1>\n" +
                "\n" +
                "<div class=\"section\">\n" +
                "    <p><strong>姓名：</strong>%NAME%</p>\n" +
                "    <p><strong>注册时间：</strong>%JOIN_DATE%</p>\n" +
                "    <p><strong>总服务时长：</strong>%TOTAL_HOURS% 小时</p>\n" +
                "    <p><strong>参与活动数量：</strong>%ACTIVITY_COUNT% 项</p>\n" +
                "</div>\n" +
                "\n" +
                "<div class=\"section\">\n" +
                "    <h2>参与项目</h2>\n" +
                "    <ul>\n" +
                "%ACTIVITIES%\n" +
                "    </ul>\n" +
                "</div>\n" +
                "\n" +
                "<div class=\"section\">\n" +
                "    <h2>荣誉与证书</h2>\n" +
                "    <ul>\n" +
                "        <li>2024年社区优秀志愿者</li>\n" +
                "        <li>环保公益活动优秀贡献奖</li>\n" +
                "    </ul>\n" +
                "</div>\n" +
                "\n" +
                "<img src=\"%QR_CODE_URL%\" class=\"qr\" />\n" +
                "\n" +
                "<div class=\"footer\">\n" +
                "    此履历由【志愿服务平台】自动生成，记录截止至 %GENERATE_DATE%\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        // 替换变量
        String generateDate = new SimpleDateFormat("yyyy年M月d日", Locale.getDefault()).format(new Date());

        StringBuilder listItems = new StringBuilder();
        for (String act : activities) {
            listItems.append("<li>").append(act).append("</li>");
        }

        // 生成一个假的二维码图片链接（你也可以替换为实际生成的二维码图片 Base64 或网络地址）
        String qrCodeUrl = generateQRCodeBase64("http://www.ljhao.cn",300);

        return template
                .replace("%NAME%", name)
                .replace("%JOIN_DATE%", joinDate)
                .replace("%TOTAL_HOURS%", String.valueOf(totalHours))
                .replace("%ACTIVITY_COUNT%", String.valueOf(activityCount))
                .replace("%ACTIVITIES%", listItems.toString())
                .replace("%GENERATE_DATE%", generateDate)
                .replace("%QR_CODE_URL%", qrCodeUrl);
    }

    private void createWebPrintJob(WebView webView) {
        PrintManager printManager = (PrintManager) requireActivity().getSystemService(getActivity().PRINT_SERVICE);

        String jobName = "志愿服务履历_" + name;

        if (printManager != null) {
            PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

            PrintAttributes attributes = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(new PrintAttributes.Resolution("volunteer_app", "default", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build();

            printManager.print(jobName, printAdapter, attributes);
        } else {
            Toast.makeText(getActivity(), "无法启动打印服务", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close(); // 确保关闭
        }
        super.onDestroy();
    }

    private String generateQRCodeBase64(String content, int size) {
        try {
            Bitmap bitmap = encodeAsBitmap(content, size);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            }
            byte[] byteArray = stream.toByteArray();
            return "data:image/png;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("QRCode", "生成二维码失败", e);
            return "";
        }
    }

    private Bitmap encodeAsBitmap(String contents, int size) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, size, size, null);
        } catch (IllegalArgumentException iae) {
            return null; // Unsupported format
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
