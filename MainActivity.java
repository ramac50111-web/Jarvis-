package com.jarvis.assistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.*;

public class MainActivity extends Activity {
    private LinearLayout chat;
    private EditText input;

    private int dp(int n) { return (int)(n * getResources().getDisplayMetrics().density); }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(7,19,33));
        getWindow().setNavigationBarColor(Color.rgb(7,19,33));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(18), dp(16), dp(12));
        root.setBackgroundColor(Color.rgb(5,14,26));

        TextView title = new TextView(this);
        title.setText("J.A.R.V.I.S.");
        title.setTextColor(Color.rgb(65,205,255));
        title.setTextSize(29);
        title.setGravity(Gravity.CENTER);
        title.setLetterSpacing(0.18f);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(48)));

        TextView subtitle = new TextView(this);
        subtitle.setText("PERSONAL AI ASSISTANT  •  ANDROID");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(10);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle, new LinearLayout.LayoutParams(-1, dp(25)));

        ScrollView scroll = new ScrollView(this);
        chat = new LinearLayout(this);
        chat.setOrientation(LinearLayout.VERTICAL);
        chat.setPadding(0, dp(12), 0, dp(12));
        scroll.addView(chat);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        input = new EditText(this);
        input.setSingleLine(false);
        input.setMinLines(1);
        input.setMaxLines(3);
        input.setHint("Tell JARVIS what to do...");
        input.setHintTextColor(Color.GRAY);
        input.setTextColor(Color.WHITE);
        input.setTextSize(15);
        input.setPadding(dp(12), dp(8), dp(12), dp(8));
        input.setBackgroundColor(Color.rgb(19,39,58));
        bar.addView(input, new LinearLayout.LayoutParams(0, dp(52), 1));

        Button send = new Button(this);
        send.setText("SEND");
        send.setTextColor(Color.BLACK);
        send.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(53,200,255)));
        bar.addView(send, new LinearLayout.LayoutParams(dp(88), dp(52)));
        root.addView(bar);
        setContentView(root);

        addMessage("JARVIS", "Good day. I am ready. Try: “message Alex: I will be late”, “call 9876543210”, “open YouTube”, or “search best Java tutorials”.");
        send.setOnClickListener(v -> runCommand());
        input.setOnEditorActionListener((v, action, event) -> { runCommand(); return true; });
    }

    private void addMessage(String who, String msg) {
        TextView t = new TextView(this);
        t.setText(who + "\\n" + msg);
        t.setTextColor(who.equals("YOU") ? Color.WHITE : Color.rgb(125,220,255));
        t.setTextSize(15);
        t.setPadding(dp(13), dp(10), dp(13), dp(10));
        t.setBackgroundColor(who.equals("YOU") ? Color.rgb(24,49,70) : Color.rgb(12,31,49));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(5), 0, dp(5));
        chat.addView(t, p);
    }

    private void reply(String s) { addMessage("JARVIS", s); }

    private void runCommand() {
        String command = input.getText().toString().trim();
        if (command.isEmpty()) return;
        input.setText("");
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(input.getWindowToken(), 0);
        addMessage("YOU", command);
        String lower = command.toLowerCase();

        if (lower.startsWith("message ") || lower.startsWith("send message ")) {
            int colon = command.indexOf(':');
            if (colon < 0 || colon == command.length()-1) {
                reply("Use this format: message Alex: I will be late. Android will let you choose a messaging app and recipient before sending.");
                return;
            }
            String recipient = command.substring(command.indexOf(' ')+1, colon).trim();
            String body = command.substring(colon+1).trim();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, body);
            share.putExtra(Intent.EXTRA_SUBJECT, "Message for " + recipient);
            try {
                startActivity(Intent.createChooser(share, "Choose an app and recipient for " + recipient));
                reply("I prepared the message. Choose WhatsApp, Telegram, Messages or another installed app, select the recipient, review it and tap Send.");
            } catch (Exception e) { reply("No compatible messaging app was found."); }
            return;
        }

        if (lower.startsWith("call ")) {
            String number = command.substring(5).trim().replace(" ", "");
            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number)));
            startActivity(i);
            reply("I opened the phone dialer. Review the number and tap Call.");
            return;
        }

        if (lower.startsWith("search ")) {
            String q = command.substring(7).trim();
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(q)));
            startActivity(i);
            reply("Opening web search for: " + q);
            return;
        }

        if (lower.startsWith("open ")) {
            String app = lower.substring(5).trim();
            String pkg = knownPackage(app);
            if (pkg != null) {
                Intent launch = getPackageManager().getLaunchIntentForPackage(pkg);
                if (launch != null) { startActivity(launch); reply("Opening " + app + "."); }
                else reply("That app is not installed or cannot be opened.");
            } else {
                Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode("open " + app + " app")));
                startActivity(web);
                reply("I couldn't identify that app locally, so I searched for it.");
            }
            return;
        }

        if (lower.equals("help") || lower.contains("what can you do")) {
            reply("I can prepare messages for any compatible messaging app, open common apps, start the phone dialer, and search the web. For safety, Android asks you to choose the app/contact and confirm sending or calling.");
            return;
        }

        reply("I can handle this command type in a future upgrade. For now try: message Name: text, call number, open YouTube, or search your question. Full conversational AI needs a connected AI service/API.");
    }

    private String knownPackage(String name) {
        if (name.contains("whatsapp")) return "com.whatsapp";
        if (name.contains("telegram")) return "org.telegram.messenger";
        if (name.contains("instagram")) return "com.instagram.android";
        if (name.contains("youtube")) return "com.google.android.youtube";
        if (name.contains("chrome")) return "com.android.chrome";
        if (name.contains("gmail")) return "com.google.android.gm";
        if (name.contains("maps")) return "com.google.android.apps.maps";
        if (name.contains("spotify")) return "com.spotify.music";
        if (name.contains("settings")) return "com.android.settings";
        return null;
    }
}
