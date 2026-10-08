#!/bin/bash
# สคริปต์คอมไพล์และรันเกม POTION PANIC

echo "=========================================="
echo "   POTION PANIC (อลวนคนปรุงยา) - OOP Game  "
echo "=========================================="

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
rm -f /tmp/potion_sources.txt

if [ $? -eq 0 ]; then
    echo "คอมไพล์สำเร็จ! กำลังเปิดเกม..."
    java -cp target/classes com.potionpanic.Main
else
    echo "เกิดข้อผิดพลาดในการคอมไพล์ กรุณาตรวจสอบโค้ด"
fi
