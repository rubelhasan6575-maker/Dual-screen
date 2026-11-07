DualScreen Pro — Android Project

লক্ষ্য ফোন: HONOR X5c Plus, Android 15, MagicOS 9, 4GB RAM।

এই প্রজেক্টে:
- ফোনে ইনস্টল থাকা launchable অ্যাপের তালিকা দেখায়।
- দুটি আলাদা অ্যাপ নির্বাচন করা যায়।
- Android-কে adjacent/split-screen launch-এর অনুরোধ পাঠায়।
- কোনো সার্ভার, অ্যাকাউন্ট, বিজ্ঞাপন বা API নেই।

গুরুত্বপূর্ণ সীমাবদ্ধতা:
Android অ্যাপ অন্য অ্যাপকে জোর করে Split Screen-এ রাখতে পারে না।
ফোনের MagicOS/Android, অ্যাপগুলোর multi-window support এবং manufacturer policy
এটি অনুমতি দিলে তবেই পাশাপাশি দেখা যাবে। না হলে একটি অ্যাপ অন্যটিকে ঢেকে দিতে পারে।
এটি প্রথম পরীক্ষামূলক সংস্করণ; HONOR X5c Plus-এ বাস্তবে পরীক্ষা করা হয়নি।

APK তৈরির সহজ পথ (GitHub Actions):
1. GitHub-এ নতুন একটি repository তৈরি করুন।
2. এই ZIP-এর ভেতরের সব ফাইল repository-তে upload করুন।
3. উপরের Actions ট্যাবে যান।
4. "Build DualScreen Pro APK" workflow নির্বাচন করে Run workflow চাপুন।
5. কাজ শেষ হলে Artifacts থেকে DualScreenPro-debug-apk ZIP নামান।
6. ZIP খুলে app-debug.apk ফোনে নামিয়ে ইনস্টল করুন।
7. Android চাইলে "Install unknown apps" অনুমতি দিন—শুধু নিজের তৈরি APK-এর জন্য।

প্রজেক্টটি Android Studio + Gradle 8.9 + JDK 17 + Android SDK 35 দিয়ে build করার জন্য।
