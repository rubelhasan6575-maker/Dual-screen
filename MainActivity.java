package com.rubel.dualscreenpro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {
    static class AppItem {
        String label, pkg;
        AppItem(String l, String p) { label=l; pkg=p; }
        @Override public String toString() { return label; }
    }

    private final ArrayList<AppItem> apps = new ArrayList<>();
    private Spinner first, second;
    private TextView status;

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        buildUi();
        loadApps();
    }

    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density); }

    private TextView text(String s, int size, int color) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(color);
        return t;
    }

    private void buildUi() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(26), dp(20), dp(20));
        page.setBackgroundColor(0xFFF4F7FB);

        TextView title = text("DualScreen Pro", 26, 0xFF0F172A);
        title.setTypeface(null, Typeface.BOLD);
        page.addView(title);

        TextView sub = text("দুটি অ্যাপ নির্বাচন করে একসঙ্গে চালানোর চেষ্টা করুন", 14, 0xFF475569);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, -2);
        sp.topMargin = dp(8); sp.bottomMargin = dp(22);
        page.addView(sub, sp);

        page.addView(section("প্রথম অ্যাপ"));
        first = new Spinner(this);
        page.addView(first, fieldParams());

        page.addView(section("দ্বিতীয় অ্যাপ"));
        second = new Spinner(this);
        LinearLayout.LayoutParams p2 = fieldParams(); p2.topMargin = dp(8);
        page.addView(second, p2);

        Button go = new Button(this);
        go.setText("দুটি অ্যাপ চালু করুন");
        go.setTextSize(16);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, dp(54));
        bp.topMargin = dp(24);
        page.addView(go, bp);
        go.setOnClickListener(v -> launchBoth());

        Button refresh = new Button(this);
        refresh.setText("অ্যাপের তালিকা রিফ্রেশ");
        page.addView(refresh, new LinearLayout.LayoutParams(-1, dp(48)));
        refresh.setOnClickListener(v -> loadApps());

        status = text("নোট: Split Screen ফোনের Android/MagicOS সমর্থনের ওপর নির্ভরশীল।", 13, 0xFF64748B);
        LinearLayout.LayoutParams st = new LinearLayout.LayoutParams(-1, -2);
        st.topMargin = dp(18);
        page.addView(status, st);

        TextView foot = text("Offline • No account • No ads", 12, 0xFF94A3B8);
        foot.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(-1, -2);
        fp.topMargin = dp(26);
        page.addView(foot, fp);

        setContentView(page);
    }

    private TextView section(String s) {
        TextView t = text(s, 16, 0xFF1E293B);
        t.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.topMargin = dp(12); p.bottomMargin = dp(6);
        t.setLayoutParams(p);
        return t;
    }

    private LinearLayout.LayoutParams fieldParams() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dp(54));
        return p;
    }

    private void loadApps() {
        apps.clear();
        PackageManager pm = getPackageManager();
        Intent query = new Intent(Intent.ACTION_MAIN, null);
        query.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = pm.queryIntentActivities(query, 0);
        for (ResolveInfo ri : list) {
            String pkg = ri.activityInfo.packageName;
            if (pkg.equals(getPackageName())) continue;
            CharSequence label = ri.loadLabel(pm);
            apps.add(new AppItem(label == null ? pkg : label.toString(), pkg));
        }
        Collections.sort(apps, Comparator.comparing(a -> a.label.toLowerCase()));
        ArrayAdapter<AppItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, apps);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        first.setAdapter(adapter);
        second.setAdapter(adapter);
        if (apps.size() > 1) second.setSelection(1);
        status.setText(apps.isEmpty() ? "কোনো চালু করার মতো অ্যাপ পাওয়া যায়নি।" :
                apps.size() + "টি অ্যাপ পাওয়া গেছে।");
    }

    private void launchBoth() {
        if (apps.size() < 2) {
            new AlertDialog.Builder(this).setMessage("কমপক্ষে দুটি অ্যাপ থাকতে হবে।").setPositiveButton("ঠিক আছে", null).show();
            return;
        }
        AppItem a = (AppItem) first.getSelectedItem();
        AppItem b = (AppItem) second.getSelectedItem();
        if (a == null || b == null || a.pkg.equals(b.pkg)) {
            new AlertDialog.Builder(this).setMessage("দুটি আলাদা অ্যাপ নির্বাচন করুন।").setPositiveButton("ঠিক আছে", null).show();
            return;
        }
        try {
            Intent ia = getPackageManager().getLaunchIntentForPackage(a.pkg);
            Intent ib = getPackageManager().getLaunchIntentForPackage(b.pkg);
            if (ia == null || ib == null) throw new Exception("অ্যাপ চালু করা যাচ্ছে না");
            ia.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            ib.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startActivity(ia);
            new android.os.Handler().postDelayed(() -> {
                try { startActivity(ib); }
                catch (Exception e) {
                    Toast.makeText(this, "দ্বিতীয় অ্যাপ চালু হয়নি: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }, 450);
            Toast.makeText(this, "দুটি অ্যাপ চালুর অনুরোধ পাঠানো হয়েছে। Split Screen না এলে ফোন সমর্থন করছে না।", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "চালু করা যায়নি: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
