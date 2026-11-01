#!/bin/bash
# สคริปต์คอมไพล์และรันเกม POTION PANIC

echo "=========================================="
echo "   POTION PANIC (อลวนคนปรุงยา) - OOP Game  "
echo "=========================================="

# ตรวจสอบและตั้งค่า JAVA_HOME บน macOS อัตโนมัติ
if [ -z "$JAVA_HOME" ]; then
    if [ -d "/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home"
    elif [ -d "/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home"
    elif /usr/libexec/java_home >/dev/null 2>&1; then
        export JAVA_HOME=$(/usr/libexec/java_home)
    fi
fi

if [ -n "$JAVA_HOME" ]; then
    export PATH="$JAVA_HOME/bin:$PATH"
fi

# สร้างโฟลเดอร์ปลายทาง
mkdir -p target/classes

# คัดลอกทรัพยากร (ถ้ามี)
if [ -d "src/main/resources" ]; then
    cp -r src/main/resources/* target/classes/ 2>/dev/null || true
fi

# คอมไพล์ไฟล์ .java ทั้งหมด
echo "กำลังคอมไพล์ซอร์สโค้ด Java..."
find src/main/java -name "*.java" > /tmp/potion_sources.txt
javac -encoding UTF-8 -d target/classes @/tmp/potion_sources.txt
COMPILE_STATUS=$?
rm -f /tmp/potion_sources.txt

if [ $COMPILE_STATUS -eq 0 ]; then
    echo "คอมไพล์สำเร็จ! กำลังเปิดเกม..."
    java -cp target/classes com.potionpanic.Main
else
    echo "เกิดข้อผิดพลาดในการคอมไพล์ กรุณาตรวจสอบโค้ด"
    exit 1
fi
