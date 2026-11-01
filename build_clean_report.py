#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
สคริปต์สร้างเอกสารรายงาน POTION PANIC ฉบับสมบูรณ์ (Clean Word Format)
- ไม่มีเส้นคั่น ไม่มีบล็อกกล่องสีล็อกข้อความ
- อิงโครงสร้างตามตัวอย่าง Paper - Athicha + Proposal จริงของผู้ใช้ 100%
- สร้างไฟล์ .docx, .doc, และอัปเดตไฟล์ .md
"""

import os
import subprocess

def get_report_html():
    return """<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>โครงงาน POTION PANIC (อลวนคนปรุงยา)</title>
<style>
    body {
        font-family: 'TH Sarabun New', 'Angsana New', 'Cordia New', Tahoma, sans-serif;
        font-size: 16pt;
        line-height: 1.35;
        color: #000000;
        background-color: #FFFFFF;
        margin: 2.54cm;
    }
    .cover-page {
        text-align: center;
        page-break-after: always;
        padding-top: 40pt;
    }
    .cover-title {
        font-size: 26pt;
        font-weight: bold;
        margin-top: 30pt;
        margin-bottom: 8pt;
    }
    .cover-subtitle {
        font-size: 20pt;
        margin-bottom: 70pt;
    }
    .cover-author {
        font-size: 18pt;
        margin-bottom: 70pt;
        line-height: 1.6;
    }
    .cover-teacher {
        font-size: 18pt;
        margin-bottom: 70pt;
        line-height: 1.6;
    }
    .cover-dept {
        font-size: 16pt;
        line-height: 1.5;
    }
    h1 {
        font-size: 20pt;
        font-weight: bold;
        color: #000000;
        margin-top: 24pt;
        margin-bottom: 12pt;
    }
    h2 {
        font-size: 18pt;
        font-weight: bold;
        color: #000000;
        margin-top: 18pt;
        margin-bottom: 8pt;
    }
    h3 {
        font-size: 16pt;
        font-weight: bold;
        color: #000000;
        margin-top: 14pt;
        margin-bottom: 6pt;
    }
    p {
        font-size: 16pt;
        margin-top: 4pt;
        margin-bottom: 6pt;
        text-align: justify;
        text-indent: 36pt;
    }
    .no-indent {
        text-indent: 0 !important;
    }
    ul, ol {
        margin-top: 4pt;
        margin-bottom: 8pt;
        padding-left: 40pt;
    }
    li {
        font-size: 16pt;
        margin-bottom: 3pt;
    }
    table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 10pt;
        margin-bottom: 14pt;
        font-size: 14pt;
    }
    th, td {
        border: 1pt solid #000000;
        padding: 6pt 8pt;
        text-align: left;
    }
    th {
        font-weight: bold;
        text-align: center;
    }
    .td-center {
        text-align: center;
    }
    .schedule-active {
        background-color: #FFF275;
    }
    pre {
        font-family: 'Consolas', 'Courier New', monospace;
        font-size: 11pt;
        line-height: 1.25;
        color: #000000;
        margin-top: 6pt;
        margin-bottom: 10pt;
        margin-left: 20pt;
        white-space: pre-wrap;
    }
    code {
        font-family: 'Consolas', monospace;
        font-size: 12pt;
    }
    .img-placeholder {
        text-align: center;
        margin: 12pt 0;
        font-style: italic;
        color: #333333;
    }
</style>
</head>
<body>

<!-- หน้าปก -->
<div class="cover-page">
    <p class="no-indent" style="text-align: center; font-size: 14pt; color: #555555;">[ พื้นที่สำหรับวางรูปตรามหาวิทยาลัยเทคโนโลยีพระจอมเกล้าพระนครเหนือ ]</p>
    
    <div class="cover-title">โครงงาน</div>
    <div class="cover-subtitle">POTION PANIC<br>( อลวนคนปรุงยา )</div>
    
    <div class="cover-author">
        <b>จัดทำโดย</b><br>
        6804062612366 นายปภาวิน โกการัตน์<br>
        ตอนเรียนที่ 3 (Section 3)
    </div>
    
    <div class="cover-teacher">
        <b>เสนอ</b><br>
        ผู้ช่วยศาสตราจารย์สถิตย์ ประสมพันธ์<br>
        วิชา 040613204 Object-Oriented Programming<br>
        ภาคเรียนที่ 1 ปีการศึกษา 2569
    </div>
    
    <div class="cover-dept">
        ภาควิชาวิทยาการคอมพิวเตอร์และสารสนเทศ คณะวิทยาศาสตร์ประยุกต์<br>
        มหาวิทยาลัยเทคโนโลยีพระจอมเกล้าพระนครเหนือ
    </div>
</div>

<!-- เกี่ยวกับโครงงาน -->
<h1>เกี่ยวกับโครงงาน</h1>
<p class="no-indent"><b>ชื่อโปรเจกต์:</b> POTION PANIC (อลวนคนปรุงยา)</p>
<p class="no-indent"><b>นำเสนอโดย:</b> นายปภาวิน โกการัตน์ (รหัสนักศึกษา 6804062612366 Section 3)</p>
<p class="no-indent"><b>อาจารย์ผู้สอน:</b> ผู้ช่วยศาสตราจารย์สถิตย์ ประสมพันธ์</p>
<p class="no-indent"><b>Source Code:</b> https://github.com/s6804062612366-web/POTION-PANIC-GAME</p>

<!-- บทที่ 1 -->
<h1>บทที่ 1 บทนำ</h1>

<h2>ที่มาและความสำคัญของโครงการ</h2>
<p>โครงงานนี้จัดทำขึ้นเพื่อวัดผลและประยุกต์ใช้ความรู้ความสามารถในการเรียนวิชา Object-Oriented Programming (การเขียนโปรแกรมเชิงวัตถุ) โดยการนำหลักการและทฤษฎีที่เรียนมาสร้างสรรค์เป็นชิ้นงานจริงในรูปแบบของเกมคอมพิวเตอร์ 2 มิติ ซึ่งระบบเกมจำเป็นต้องมีการจำลองสถานะ (State) และพฤติกรรม (Behavior) ของออบเจกต์จำนวนมาก เช่น หอคอย สายพาน หม้อต้มยา ขวดยาเวทมนตร์ มอนสเตอร์หลากหลายธาตุ และบอสใหญ่ ที่ต้องทำงานประสานสัมพันธ์กันในทุกๆ เสี้ยววินาทีแบบเรียลไทม์ ทำให้ผู้จัดทำได้ศึกษาเรียนรู้การออกแบบสถาปัตยกรรมซอฟต์แวร์ อุปกรณ์ และเครื่องมือในการพัฒนาโปรแกรมด้วยภาษา Java ได้อย่างลึกซึ้ง</p>

<h2>ประเภทของโครงการ</h2>
<p class="no-indent">เกม (Game) ในรูปแบบ 2D Standalone Desktop Application</p>

<h2>ประโยชน์</h2>
<ol>
    <li>ฝึกทักษะการแยกประสาท และการทำงานหลายอย่างพร้อมกัน (Multitasking)</li>
    <li>พัฒนาทักษะการคิดวิเคราะห์ และการวางแผน (Strategic Thinking)</li>
    <li>ฝึกความจำ และความรวดเร็วในการตอบสนอง (Memory &amp; Reflexes)</li>
    <li>ให้ความบันเทิง และคลายเครียด</li>
    <li>เพื่อนำแนวคิด การเขียนโปรแกรมแบบเชิงวัตถุ (Object-Oriented Programming) มาประยุกต์ใช้ในการสร้างระบบงานที่ทำงานได้จริง</li>
</ol>

<h2>ขอบเขตของโครงการ (แนบ Proposal)</h2>
<p>โครงงานนี้พัฒนาขึ้นตามกรอบข้อเสนอโครงการ (Proposal) เดิม และมีการพัฒนาต่อยอดขอบเขตระบบให้สมบูรณ์ โดยมีรายละเอียดขอบเขตการทำงานดังนี้:</p>
<ul>
    <li><b>ระบบเกมเพลย์หลัก:</b> ระบบสายพานวัตถุดิบ 5 ช่องพร้อมระบบสุ่มแก้ทางศัตรู, ระบบหม้อปรุงยา 3 วัตถุดิบพร้อมคาถาชี้แนะรูปภาพ, ระบบแท่นวางยา 4 ช่อง, และระบบหอคอยเวทมนตร์พร้อมการเตือนภัยเมื่อเลือดวิกฤต</li>
    <li><b>เนื้อหาของเกม:</b> ประกอบด้วย 4 ด่าน (3 ด่านเนื้อเรื่อง + 1 ด่าน Endless Mode), มอนสเตอร์ 5 ชนิดรวมบอสใหญ่ราชันย์อสูรเวท, และสูตรปรุงยา 7 ชนิด (รวมถึงยาระเบิดมหาเวท RARE)</li>
    <li><b>ระบบเทคโนโลยี:</b> พัฒนาด้วยภาษา Java (JDK 21) ใช้ Java Swing &amp; Graphics2D, ระบบเสียงดนตรีสังเคราะห์ Polyphonic Synthesizer BGM, รองรับโหมดเต็มจอ (F11), และปฏิบัติตามนโยบาย Zero-Emoji</li>
</ul>

<h3>1. ตารางเวลาการดำเนินโครงการ (Project Schedule)</h3>
<table>
    <thead>
        <tr>
            <th rowspan="2" style="width: 5%;">ลำดับ</th>
            <th rowspan="2" style="width: 35%;">รายการ</th>
            <th colspan="4" style="width: 30%;">กันยายน (ก.ย.)</th>
            <th colspan="4" style="width: 30%;">ตุลาคม (ต.ค.)</th>
        </tr>
        <tr>
            <th>W1</th>
            <th>W2</th>
            <th>W3</th>
            <th>W4</th>
            <th>W1</th>
            <th>W2</th>
            <th>W3</th>
            <th>W4</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td class="td-center">1</td>
            <td>ศึกษาข้อมูล และออกแบบโครงสร้างเกม</td>
            <td class="schedule-active"></td>
            <td class="schedule-active"></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td class="td-center">2</td>
            <td>ออกแบบกราฟิก, UI และหาภาพตัวละคร</td>
            <td></td>
            <td class="schedule-active"></td>
            <td class="schedule-active"></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td class="td-center">3</td>
            <td>ลงมือเขียนโปรแกรม และพัฒนาระบบเกม</td>
            <td></td>
            <td></td>
            <td class="schedule-active"></td>
            <td class="schedule-active"></td>
            <td class="schedule-active"></td>
            <td class="schedule-active"></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td class="td-center">4</td>
            <td>ตรวจสอบการทำงาน และแก้ไขข้อผิดพลาด</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td class="schedule-active"></td>
            <td class="schedule-active"></td>
            <td class="schedule-active"></td>
            <td></td>
        </tr>
        <tr>
            <td class="td-center">5</td>
            <td>จัดทำเอกสารรายงาน</td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td class="schedule-active"></td>
            <td class="schedule-active"></td>
        </tr>
    </tbody>
</table>

<!-- บทที่ 2 -->
<h1>บทที่ 2 ส่วนการพัฒนา</h1>

<h2>เนื้อเรื่องย่อหรือวิธีการเล่น (กรณีเกมส์)</h2>

<h3>เนื้อเรื่องย่อ</h3>
<p>ในเกม POTION PANIC (อลวนคนปรุงยา) คุณจะได้รับบทเป็น "ศิษย์ฝึกหัดนักปรุงยา" ที่เพิ่งเข้ามารับเวรกะดึกเฝ้าหอคอยเพียงลำพัง แต่จู่ๆ คืนที่ควรจะเงียบสงบก็พังทลายลง เมื่อรอยแยกมิติเปิดออกและฝูงมอนสเตอร์กลายพันธุ์กำลังแห่กันมาที่หอคอย! ซ้ำร้าย "สายธารวัตถุดิบเวทมนตร์" ที่เชื่อมต่อกับใต้ดินดันทํางานรวน มันสูบเอาสมุนไพรและแร่ธาตุต่างๆ ไหลทะลักขึ้นมาอย่างบ้าคลั่งและไม่ขาดสาย คุณไม่มีกองทหาร ไม่มีดาบวิเศษ มีเพียงหม้อต้มยาเก่าๆ กับความจำในสมอง คุณต้องตั้งสติรับมือกับความอลวนนี้ คว้าวัตถุดิบที่ไหลมาตามสายพาน โยนลงหม้อให้ถูกสูตร แล้วสาดขวดยาเวทมนตร์สกัดกั้นพวกมอนสเตอร์ให้ทันเวลา ก่อนที่หอคอยแห่งนี้จะแตก!</p>

<h3>วิธีการเล่น</h3>
<p>กดปุ่มตัวเลข 1, 2, 3, 4, 5 เพื่อดึงวัตถุดิบจากสายพานในแต่ละช่องลงหม้อต้ม กดปุ่ม Spacebar เพื่อทำการผสมยา และใช้เมาส์คลิกซ้ายเพื่อเลือกขวดยาพร้อมกับคลิกเป้าหมายเพื่อปาโจมตีมอนสเตอร์</p>
<p>นอกจากนี้ยังมีปุ่มลัดเสริมเพื่อความคล่องตัวในการเล่น ได้แก่ ปุ่ม [F] สำหรับสุ่มวัตถุดิบใหม่ทั้งสายพาน (Reroll), ปุ่ม [Q], [W], [E], [R] สำหรับสลับเลือกขวดยาบนแท่นวางยา, ปุ่ม [Backspace] สำหรับนำวัตถุดิบชิ้นล่าสุดออกจากหม้อ, ปุ่ม [C] สำหรับล้างหม้อต้มทั้งหมด, ปุ่ม [Tab] หรือ [H] เพื่อเปิดดูสมุดสูตรยา, ปุ่ม [M] สำหรับสลับเปิด/ปิดเสียงเพลง และปุ่ม [F11] เพื่อสลับโหมดเต็มหน้าจอ</p>

<h3>Storyboard</h3>
<p class="no-indent"><b>ตัวละครและไอเทม</b></p>
<table>
    <thead>
        <tr>
            <th style="width: 25%;">มอนสเตอร์</th>
            <th style="width: 25%;">หม้อปรุงยา</th>
            <th style="width: 25%;">วัตถุดิบปรุงยา</th>
            <th style="width: 25%;">ขวดยา</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td class="td-center">[ รูปมอนสเตอร์ ]</td>
            <td class="td-center">[ รูปหม้อปรุงยา ]</td>
            <td class="td-center">[ รูปวัตถุดิบ ]</td>
            <td class="td-center">[ รูปขวดยา ]</td>
        </tr>
    </tbody>
</table>

<p class="no-indent"><b>ฉากภายในเกม</b></p>
<ul>
    <li><b>หน้าเริ่มเกม (Main Menu):</b> หน้าจอเริ่มต้นของเกมที่แสดงป้ายเมนูหลัก ได้แก่ ปุ่ม "เริ่มเกม" สำหรับเข้าสู่เกม และปุ่ม "วิธีเล่น" สำหรับดูคู่มือปุ่มลัดและสูตรยาเบื้องต้น พร้อมระบบไตเติลเกมเปลี่ยนสีตัวอักษรแบบไดนามิก</li>
    <li><b>หน้าเลือกด่านและร้านค้า (Stage Select &amp; Shop):</b> หน้าจอแสดงด่านทั้ง 4 ด่าน พร้อมร้านค้าอัปเกรดพลังชีวิตหอคอย ความเร็วสายพาน รัศมีระเบิด และปุ่มรีเซ็ตสกิลคืนดาว 100%</li>
    <li><b>หน้าระหว่างเกม (Gameplay):</b> หน้าจอหลักขณะเล่นที่แบ่งสัดส่วนการควบคุมเป็น 3 โซน ส่วนบนคือเส้นทางที่มอนสเตอร์เดินบุกเข้ามาพร้อมหลอดพลังชีวิต (HP) ส่วนกลางคือสายพานลำเลียงวัตถุดิบ 5 ช่องที่ไหลมา และส่วนล่างคือพื้นที่ของนักปรุงยาที่มีหม้อต้มสำหรับผสมไอเทม พื้นที่พักขวดยาพร้อมใช้ และสมุดสูตรยาสำหรับดูสูตรการผสมยา</li>
    <li><b>หน้าแพ้เกม (Game Over):</b> หน้าจอแสดงผลเมื่อมอนสเตอร์บุกโจมตีหอคอยจนพลังชีวิตเหลือศูนย์ จะแสดงข้อความแสดงความพ่ายแพ้ พร้อมปุ่มตัวเลือกให้ผู้เล่นกด "ลองใหม่อีกครั้ง" เพื่อเริ่มเกมแก้ตัวในรอบใหม่ หรือกด "กลับหน้าหลัก" เพื่อออกจากด่าน</li>
    <li><b>หน้าชนะเกม (Victory):</b> หน้าจอแสดงผลเมื่อผู้เล่นสามารถปกป้องหอคอยและสกัดกั้นฝูงมอนสเตอร์ได้สำเร็จจนจบภารกิจ จะแสดงข้อความแสดงความยินดี คำนวณดวงดาวที่ได้รับ พร้อมปุ่มตัวเลือกให้ "เล่นอีกครั้ง" หรือกด "กลับหน้าหลัก"</li>
</ul>

<h2>แผนภาพ Class Diagram พร้อมคำอธิบาย</h2>
<p class="no-indent">โครงสร้างสถาปัตยกรรมของโครงการประกอบด้วยคลาสหลักดังต่อไปนี้:</p>
<ol>
    <li><b>คลาส GameEngine:</b> คลาสศูนย์กลางควบคุม Game Loop, ตรวจสอบสถานะแพ้/ชนะ, ประสานงานระหว่างหอคอย สายพาน หม้อต้ม และการปาขวดยา</li>
    <li><b>คลาส Tower:</b> จัดเก็บสถานะและพลังชีวิตของหอคอย ตรวจสอบการรับความเสียหายและการพังทลาย</li>
    <li><b>คลาส ConveyorBelt:</b> จัดการสายพานวัตถุดิบ 5 ช่อง ควบคุมระบบสุ่มอัจฉริยะ (Threat-Aware Spawning) และฟังก์ชัน Reroll</li>
    <li><b>คลาส Cauldron:</b> จัดการหม้อต้มยา รับวัตถุดิบ ตรวจสอบความจุ และผสมยาตามสูตร</li>
    <li><b>คลาส PotionRack:</b> จัดการแท่นพักขวดยาพร้อมใช้ 4 ช่อง พร้อมระบบเลือกช่องอัตโนมัติ (Auto-Select)</li>
    <li><b>คลาส RecipeBook:</b> บรรจุฐานข้อมูลสูตรยา 3 ชิ้น ตรวจสอบความถูกต้อง และสุ่มของแรร์ยาระเบิดมหาเวท (Miracle Brew)</li>
    <li><b>คลาส Monster (Abstract):</b> คลาสแม่ของมอนสเตอร์ ควบคุมการเดิน แอนิเมชัน การรับดาเมจ และระบบจุดอ่อนธาตุ</li>
    <li><b>คลาส Potion (Abstract):</b> คลาสแม่ของขวดยาเวทมนตร์ กำหนดคุณสมบัติธาตุ รัศมีระเบิด และเมธอด onImpact()</li>
    <li><b>คลาส VisualEffect:</b> จัดการเอฟเฟกต์ภาพสนามรบ คลื่นช็อกเวฟ เสาไฟ หนามน้ำแข็ง สายฟ้า และอนุภาคฟิสิกส์</li>
    <li><b>คลาส SoundManager (Singleton):</b> จัดการระบบเสียงดนตรีสังเคราะห์และเสียงเอฟเฟกต์ทั้งหมดในเกม</li>
    <li><b>คลาส GameWindow:</b> หน้าต่าง JFrame หลัก จัดการการสลับหน้าจอ (CardLayout) และโหมดเต็มจอ</li>
    <li><b>คลาส GameplayPanel:</b> หน้าจอแสดงผลหลักที่ Override paintComponent() เพื่อเรนเดอร์เกมเพลย์ 60 FPS</li>
</ol>

<h2>รูปแบบ Application / Applet</h2>
<p class="no-indent">- <b>รูปแบบ:</b> Java Standalone Desktop Application (สร้างด้วย Java Swing &amp; AWT)</p>
<p class="no-indent">- <b>ภาษา:</b> Java (JDK 21)</p>
<p class="no-indent">- <b>GUI:</b> javax.swing (JFrame, JPanel, CardLayout) และ java.awt.Graphics2D</p>
<p class="no-indent">- <b>ระบบเสียง:</b> javax.sound.sampled (SourceDataLine Synthesizer Engine)</p>
<p>โครงการนี้เลือกพัฒนาในรูปแบบ <b>Standalone Desktop Application</b> โดยมีฟังก์ชัน main() ในคลาส Main เป็นจุดเริ่มต้นโปรแกรม และไม่ได้ใช้เทคโนโลยี Java Applet เนื่องจาก Applet ได้ถูกยกเลิก (Deprecated และ Removed) ออกจากภาษา Java ยุคใหม่อย่างถาวรตั้งแต่ Java 11 เนื่องจากปัญหาด้านความปลอดภัยของเบราว์เซอร์ การพัฒนาเป็น Desktop Application จึงเป็นแนวทางมาตรฐานที่ถูกต้อง มีความปลอดภัยสูง เข้าถึงการเรนเดอร์ระดับฮาร์ดแวร์เร่งความเร็วได้เต็มที่ และสามารถสร้างไฟล์ Executable JAR ดับเบิลคลิกเล่นได้บนทุกระบบปฏิบัติการ</p>

<h2>อธิบายส่วนของโปรแกรมที่มี</h2>

<h3>Constructor</h3>
<p class="no-indent">Constructor ทำหน้าที่กำหนดค่าเริ่มต้นให้แก่ออบเจกต์เมื่อถูกสร้างขึ้นใหม่ ป้องกันข้อผิดพลาดจากค่าว่าง (Null) และเตรียมความพร้อมของข้อมูล</p>
<pre>
// ตัวอย่าง Constructor ในคลาส ChaosBoss.java
public ChaosBoss(double startX, double startY) {
    // เรียก Constructor ของคลาสแม่ (Monster) ด้วยคำสั่ง super
    super("ราชันย์อสูรเวท (Chaos Overlord)", 420, 0.28, 25, ElementType.FIRE, startX, startY);
    this.width = 96;
    this.height = 96;
}
</pre>
<p>ในตัวอย่างนี้ คอนสตรัคเตอร์ของ ChaosBoss จะเรียกใช้ super() เพื่อส่งค่าชื่อ เลือด 420 ความเร็ว 0.28 พลังโจมตี 25 และจุดอ่อนเริ่มต้นที่ธาตุไฟ ไปยังคอนสตรัคเตอร์ของคลาสแม่ Monster พร้อมกำหนดขนาดกว้างยาว 96x96 พิกเซล</p>

<h3>Encapsulation</h3>
<p class="no-indent">Encapsulation คือการซ่อนข้อมูลสถานะภายในไว้เป็นความลับด้วยการกำหนดแอตทริบิวต์เป็น private และเปิดให้ภายนอกเข้าถึงผ่านเมธอด Getter และ Setter ที่มีการตรวจสอบความถูกต้อง</p>
<pre>
// ตัวอย่าง Encapsulation ในคลาส Tower.java
public class Tower implements Damageable, Renderable {
    private int hp;
    private int maxHp;

    public void takeDamage(int amount, ElementType element) {
        // ควบคุมไม่ให้พลังชีวิตติดลบต่ำกว่า 0
        this.hp = Math.max(0, this.hp - amount);
    }

    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
}
</pre>
<p>ตัวแปร hp และ maxHp ถูกห่อหุ้มไว้เป็น private ทำให้คลาสอื่นไม่สามารถแก้ไขเลือดได้โดยตรง แต่ต้องเรียกผ่าน takeDamage() ซึ่งมีคำสั่ง Math.max(0, ...) เพื่อป้องกันไม่ให้เลือดหอคอยติดลบ</p>

<h3>Composition</h3>
<p class="no-indent">Composition คือการสร้างคลาสหลักโดยประกอบขึ้นจากออบเจกต์ของคลาสย่อยๆ (Has-A Relationship) เพื่อแบ่งแยกหน้าที่ความรับผิดชอบอย่างเป็นระเบียบ</p>
<pre>
// ตัวอย่าง Composition ในคลาส GameEngine.java
public class GameEngine {
    // GameEngine "ประกอบด้วย" หอคอย, สายพาน, หม้อต้ม, และแท่นวางยา
    private final Tower tower;
    private final ConveyorBelt belt;
    private final Cauldron cauldron;
    private final PotionRack potionRack;
    private final WaveManager waveManager;

    public GameEngine() {
        this.tower = new Tower(Constants.TOWER_MAX_HP);
        this.belt = new ConveyorBelt();
        this.cauldron = new Cauldron(Constants.ZONE_BOT_X, Constants.ZONE_BOT_Y + 15);
        this.potionRack = new PotionRack(Constants.ZONE_BOT_X + 220, Constants.ZONE_BOT_Y + 15);
        this.waveManager = new WaveManager();
    }
}
</pre>
<p>คลาส GameEngine ทำหน้าที่เป็นผู้ควบคุมส่วนกลาง โดยประกอบออบเจกต์ของหอคอย สายพาน หม้อต้ม และแท่นวางยา เข้ามาทำงานร่วมกันอย่างเป็นระบบ</p>

<h3>Polymorphism</h3>
<p class="no-indent">Polymorphism คือความสามารถของออบเจกต์ในคลาสลูกต่างๆ ในการตอบสนองต่อเมธอดชื่อเดียวกันด้วยพฤติกรรมเฉพาะตัวที่แตกต่างกันผ่านการ Override</p>
<pre>
// ตัวอย่าง Polymorphism ในคลาสลูกของ Potion.java
// คลาส FirePotion: ปะทุเป็นเสาเพลิงและติดสถานะเผาไหม้ (Burn)
public class FirePotion extends Potion {
    @Override
    public void onImpact(double impactX, double impactY, List&lt;Monster&gt; allMonsters, List&lt;VisualEffect&gt; effects) {
        effects.add(VisualEffect.createFireBurst(impactX, impactY, getRadius()));
        // ทำดาเมจธาตุไฟและเผาไหม้ต่อเนื่อง
    }
}

// คลาส IcePotion: ปะทุเป็นหนามผลึกน้ำแข็งและติดสถานะแช่แข็ง (Freeze)
public class IcePotion extends Potion {
    @Override
    public void onImpact(double impactX, double impactY, List&lt;Monster&gt; allMonsters, List&lt;VisualEffect&gt; effects) {
        effects.add(VisualEffect.createIceSpikes(impactX, impactY, getRadius()));
        // ทำดาเมจธาตุน้ำแข็งและสโลว์ 50%
    }
}
</pre>
<p>เมื่อ GameEngine สั่ง p.onImpact() ขวดยาแต่ละชนิดจะทำงานไม่เหมือนกัน ยาเพลิงจะสร้างเสาไฟและเผาไหม้ ส่วนยาน้ำแข็งจะสร้างหนามน้ำแข็งและสโลว์มอนสเตอร์ ทั้งที่เป็นการเรียกเมธอดเดียวกัน</p>

<h3>Abstract</h3>
<p class="no-indent">Abstract ใช้สำหรับสร้างคลาสต้นแบบและอินเทอร์เฟซเพื่อกำหนดโครงสร้างร่วมและข้อตกลงที่คลาสลูกจำเป็นต้องนำไปพัฒนาต่อ</p>
<pre>
// ตัวอย่าง Abstract Class ในคลาส Potion.java
public abstract class Potion implements Renderable {
    protected String name;
    protected ElementType element;
    protected int baseDamage;

    // บังคับให้คลาสลูกต้องเขียนการระเบิดตามธาตุของตัวเอง
    public abstract void onImpact(double impactX, double impactY, 
                                  List&lt;Monster&gt; allMonsters, List&lt;VisualEffect&gt; effects);
}
</pre>
<p>คลาส Potion ถูกกำหนดให้เป็น abstract class เพื่อป้องกันไม่ให้ถูก new เป็นออบเจกต์เปล่าๆ และบังคับให้คลาสลูกทุกตัวต้องเขียนโค้ดในเมธอด onImpact() ของตนเอง</p>

<h3>Inheritance</h3>
<p class="no-indent">Inheritance คือการสืบทอดคุณสมบัติและพฤติกรรมจากคลาสแม่มาสู่คลาสลูกด้วยคีย์เวิร์ด extends เพื่อลดความซ้ำซ้อนของโค้ด</p>
<pre>
// ตัวอย่าง Inheritance ในคลาส SwiftImp.java
public class SwiftImp extends Monster {
    public SwiftImp(double x, double y) {
        // สืบทอดฟังก์ชันการเดิน แอนิเมชัน และระบบหลอดเลือดจาก Monster ทั้งหมด
        super("อิมป์ว่องไว (Swift Imp)", 65, 1.15, 12, ElementType.LIGHTNING, x, y);
    }
}
</pre>
<p>คลาส SwiftImp สืบทอดคุณสมบัติมาจากคลาสแม่ Monster ทำให้ได้รับระบบการเดิน ระบบอนิเมชัน และการรับดาเมจมาใช้ทันทีโดยไม่ต้องเขียนโค้ดซ้ำใหม่</p>

<h2>ส่วนประกอบ GUI</h2>
<p>โครงสร้างของหน้าจอ GUI พัฒนาขึ้นโดยใช้ลำดับชั้นคอมโพเนนต์ของ Java Swing:</p>
<ul>
    <li><b>GameWindow (JFrame):</b> หน้าต่างหลักของโปรแกรมขนาด 1280x720 พิกเซล ควบคุมการสลับฉากด้วย CardLayout และควบคุมระบบ Fullscreen</li>
    <li><b>rootPanel (JPanel):</b> พาเนลหลักที่บรรจุหน้าจอต่างๆ ได้แก่ MainMenuPanel, StageSelectPanel, GameplayPanel, VictoryPanel, และ GameOverPanel</li>
    <li><b>GameplayPanel (JPanel):</b> แคนวาสหลักในการวาดภาพเกมทั้งหมด โดยทำการ Override เมธอด paintComponent(Graphics g) และใช้ java.awt.Graphics2D วาดกราฟิกแบบ Double Buffering ที่ 60 เฟรมต่อวินาที</li>
    <li><b>การแบ่งพื้นที่ 3 โซน:</b> ด้านบนคือโซนสนามรบ (Battlefield), ตรงกลางคือโซนสายพานวัตถุดิบ (Conveyor), และด้านล่างคือโซนโต๊ะปรุงยาและแท่นวางขวดยา (Alchemy Table)</li>
</ul>

<h2>Event handling</h2>
<p>เกมนี้มีระบบการจัดการเหตุการณ์ (Event Handling) 2 รูปแบบหลัก:</p>
<ol>
    <li><b>Keyboard Event Handling (KeyListener / KeyAdapter &amp; KeyEventDispatcher):</b>
        ใช้ดักจับปุ่มลัดเพื่อควบคุมการเล่นเกม เช่น ปุ่ม 1-5 ดึงวัตถุดิบ, Spacebar ผสมยา, F สุ่มสายพานใหม่, Q-W-E-R เลือกขวดยา, Backspace นำของออกจากหม้อ, C ล้างหม้อต้ม, Tab/H เปิดสมุดสูตร, F11 สลับเต็มจอ, และ M เปิด/ปิดเสียง
    </li>
    <li><b>Mouse Event Handling (MouseListener &amp; MouseMotionAdapter):</b>
        ใช้ mouseMoved ในการคำนวณพิกัดเมาส์เสมือน (Virtual Coordinates) เพื่อวาดวงแหวนล็อกเป้าหมาย และใช้ mousePressed เพื่อตรวจสอบการคลิกปุ่มบนหน้าจอ หรือการคลิกบนสนามรบเพื่อเล็งและขว้างขวดยาไปยังเป้าหมาย
    </li>
</ol>

<h2>อัลกอริทึมที่สำคัญในโปรแกรม</h2>
<ol>
    <li><b>อัลกอริทึมตรวจสอบสูตรผสมยา 3 วัตถุดิบ (Order-Independent Matching):</b> ใช้การนับความถี่ของวัตถุดิบ 3 ชิ้นในหม้อต้ม เพื่อให้ผู้เล่นสามารถหยิบวัตถุดิบชิ้นใดลงก่อนหรือหลังก็ได้ เช่น สมุนไพรเพลิง 2 ชิ้น + ผลึกน้ำแข็ง 1 ชิ้น จะได้ยาเพลิงเสมอ</li>
    <li><b>อัลกอริทึมสุ่มวัตถุดิบอัจฉริยะตามภัยคุกคาม (Smart Threat-Aware Spawning):</b> สายพานจะวิเคราะห์มอนสเตอร์ตัวที่อยู่ใกล้หอคอยที่สุด และตรวจสอบวัตถุดิบในหม้อต้ม เพื่อเร่งสุ่มวัตถุดิบชิ้นที่จำเป็นลงมาบนสายพานด้วยโอกาสสูงถึง 85% ช่วยให้ผู้เล่นได้รับของแก้ทางอย่างต่อเนื่อง</li>
    <li><b>อัลกอริทึมคำนวณวิถีโค้งพาราโบลาและการกระจายดาเมจวงกว้าง (Parabolic Trajectory &amp; AoE):</b> คำนวณความสูงตามสมการควอดราติก y = 4 * height * t * (1 - t) เพื่อให้ขวดยาลอยโค้งสมจริง และเมื่อตกกระทบจะใช้ระยะทางแบบยุคลิด (Euclidean Distance) เพื่อสร้างดาเมจแบบกระจายตัวรอบจุดตกกระทบ</li>
    <li><b>อัลกอริทึมการสลับเกราะของบอสใหญ่ (Boss Shield Shifting):</b> บอสใหญ่ Chaos Overlord จะทำการเปลี่ยนเกราะธาตุหมุนเวียนทุกๆ 7.5 วินาที ทำให้ผู้เล่นต้องสลับปรุงยาตามธาตุของบอสอย่างต่อเนื่อง</li>
</ol>

<!-- บทที่ 3 -->
<h1>บทที่ 3 สรุป</h1>

<h2>ปัญหาที่พบระหว่างการพัฒนา</h2>
<ol>
    <li><b>ปัญหามอนสเตอร์เดินทับซ้อนกันในเลนเดียวกัน:</b> แก้ไขโดยใช้ระบบสุ่มกระจายพิกัดเกิด (Staggered Spawning) ร่วมกับอัลกอริทึมจัดลำดับมิติความลึก (Depth Sorting) เรียงตามแกน Y ทำให้ภาพมอนสเตอร์ไม่บังกัน</li>
    <li><b>ปัญหาความไม่เข้ากันของอีโมจิและฟอนต์ระหว่างระบบปฏิบัติการ:</b> แก้ไขโดยใช้นโยบาย Zero-Emoji ทั้งโปรเจกต์ เปลี่ยนมาวาดเวกเตอร์เรขาคณิตแทน เพื่อให้แสดงผลได้ถูกต้อง 100% บนทุกเครื่อง</li>
    <li><b>ปัญหาเสียงเพลงไม่ดังเมื่อเปิดเสียงกลับมา (Mute/Unmute Bug):</b> แก้ไขโดยแยกตัวแปรสถานะเพลงที่ต้องการเล่น (desiredBgmType) ออกจากสถานะการเล่นจริง ทำให้เมื่อเปิดเสียงกลับมาเพลงจะกลับมาเล่นต่อได้ทันที</li>
    <li><b>ปัญหาการสั่นหน้าจอถี่เกินไประหว่างเล่น:</b> แก้ไขโดยนำการสั่นตอนปาขวดยาออกทั้งหมด และคงการสั่นไว้เฉพาะจังหวะที่หอคอยกำลังจะพัง (เลือด &lt; 35%) เพื่อไม่ให้รบกวนสายตาของผู้เล่น</li>
</ol>

<h2>จุดเด่นของโปรแกรมที่ไม่เหมือนใคร</h2>
<ol>
    <li><b>ระบบผสมยา 3 วัตถุดิบและสมการภาพ:</b> นำเสนอสูตรยาเป็นไอคอนภาพเข้าใจง่าย พร้อมระบบคาถาชี้แนะรูปภาพเหนือหม้อต้ม</li>
    <li><b>สายพานสุ่มวัตถุดิบอัจฉริยะ:</b> สายพานเข้าใจภัยคุกคามในสนามรบ ช่วยส่งของแก้ทางมอนสเตอร์มาให้อย่างแม่นยำ</li>
    <li><b>กราฟิกแฟนตาซี 2 มิติและเอฟเฟกต์ภาพมีชีวิตชีวา:</b> มีหางละอองเวทมนตร์ วงแหวนล็อกเป้าหมาย เสาไฟ หนามน้ำแข็ง และละอองมอนสเตอร์แตกสลาย</li>
    <li><b>ระบบเสียงดนตรีสังเคราะห์แท้:</b> สร้างเสียงดนตรีประกอบและเสียงเวทมนตร์ด้วยโค้ด Java แท้ๆ โดยไม่ต้องพึ่งพาไฟล์ภายนอก</li>
    <li><b>ความถูกต้องตามหลักการ OOP ระดับสูง:</b> นำแนวคิดทั้ง 6 เสาหลักมาประยุกต์ใช้จริงอย่างสมบูรณ์แบบ โค้ดสะอาดและเป็นระเบียบ</li>
</ol>

<h2>คำแนะนำสำหรับผู้สอนที่อยากให้อธิบาย หรือที่เรียนแล้วไม่เข้าใจ หรืออยากให้เพิ่มสำหรับน้อง ๆ รุ่นต่อไป</h2>
<p>อยากให้อาจารย์เพิ่มเนื้อหาการสอนเรื่อง Game Loop Architecture และเทคนิคการเรนเดอร์ภาพแบบ Double Buffering ด้วย Graphics2D ในคาบเรียน เพื่อให้นักศึกษาได้เข้าใจการทำงานเบื้องหลังของแอปพลิเคชันที่ต้องประมวลผลกราฟิกและฟิสิกส์แบบเรียลไทม์ 60 FPS รวมถึงอยากให้อธิบายเรื่อง Threading และการทำงานเบื้องหลัง (Background Worker / Daemon Thread) ใน Java Swing เพิ่มเติม เพื่อให้สามารถสร้างแอปพลิเคชันที่ทำงานหลายอย่างพร้อมกันได้โดยที่หน้าจอไม่ค้าง</p>
<p class="no-indent"><b>ความคิดเห็นส่วนตัวเพิ่มเติมของผู้เรียน:</b></p>
<p class="no-indent">[ พื้นที่สำหรับพิมพ์ความรู้สึกส่วนตัวหลังทำโครงงานเสร็จสิ้น หรือสิ่งที่ประทับใจในการเรียนวิชานี้ ]</p>

</body>
</html>
"""

def get_report_markdown():
    return """# โครงงาน
# POTION PANIC ( อลวนคนปรุงยา )

**จัดทำโดย**  
6804062612366 นายปภาวิน โกการัตน์  
ตอนเรียนที่ 3 (Section 3)  

**เสนอ**  
ผู้ช่วยศาสตราจารย์สถิตย์ ประสมพันธ์  
วิชา 040613204 Object-Oriented Programming  
ภาคเรียนที่ 1 ปีการศึกษา 2569  

ภาควิชาวิทยาการคอมพิวเตอร์และสารสนเทศ คณะวิทยาศาสตร์ประยุกต์  
มหาวิทยาลัยเทคโนโลยีพระจอมเกล้าพระนครเหนือ  

---

# เกี่ยวกับโครงงาน
- **ชื่อโปรเจกต์:** POTION PANIC (อลวนคนปรุงยา)  
- **นำเสนอโดย:** นายปภาวิน โกการัตน์ (รหัสนักศึกษา 6804062612366 Section 3)  
- **อาจารย์ผู้สอน:** ผู้ช่วยศาสตราจารย์สถิตย์ ประสมพันธ์  
- **Source Code:** https://github.com/s6804062612366-web/POTION-PANIC-GAME  

---

# บทที่ 1 บทนำ

## ที่มาและความสำคัญของโครงการ
โครงงานนี้จัดทำขึ้นเพื่อวัดผลและประยุกต์ใช้ความรู้ความสามารถในการเรียนวิชา Object-Oriented Programming (การเขียนโปรแกรมเชิงวัตถุ) โดยการนำหลักการและทฤษฎีที่เรียนมาสร้างสรรค์เป็นชิ้นงานจริงในรูปแบบของเกมคอมพิวเตอร์ 2 มิติ ซึ่งระบบเกมจำเป็นต้องมีการจำลองสถานะ (State) และพฤติกรรม (Behavior) ของออบเจกต์จำนวนมาก เช่น หอคอย สายพาน หม้อต้มยา ขวดยาเวทมนตร์ มอนสเตอร์หลากหลายธาตุ และบอสใหญ่ ที่ต้องทำงานประสานสัมพันธ์กันในทุกๆ เสี้ยววินาทีแบบเรียลไทม์ ทำให้ผู้จัดทำได้ศึกษาเรียนรู้การออกแบบสถาปัตยกรรมซอฟต์แวร์ อุปกรณ์ และเครื่องมือในการพัฒนาโปรแกรมด้วยภาษา Java ได้อย่างลึกซึ้ง

## ประเภทของโครงการ
เกม (Game) ในรูปแบบ 2D Standalone Desktop Application

## ประโยชน์
1. ฝึกทักษะการแยกประสาท และการทำงานหลายอย่างพร้อมกัน (Multitasking)
2. พัฒนาทักษะการคิดวิเคราะห์ และการวางแผน (Strategic Thinking)
3. ฝึกความจำ และความรวดเร็วในการตอบสนอง (Memory & Reflexes)
4. ให้ความบันเทิง และคลายเครียด
5. เพื่อนำแนวคิด การเขียนโปรแกรมแบบเชิงวัตถุ (Object-Oriented Programming) มาประยุกต์ใช้ในการสร้างระบบงานที่ทำงานได้จริง

## ขอบเขตของโครงการ (แนบ Proposal)
โครงงานนี้พัฒนาขึ้นตามกรอบข้อเสนอโครงการ (Proposal) เดิม และมีการพัฒนาต่อยอดขอบเขตระบบให้สมบูรณ์ โดยมีรายละเอียดขอบเขตการทำงานดังนี้:
- **ระบบเกมเพลย์หลัก:** ระบบสายพานวัตถุดิบ 5 ช่องพร้อมระบบสุ่มแก้ทางศัตรู, ระบบหม้อปรุงยา 3 วัตถุดิบพร้อมคาถาชี้แนะรูปภาพ, ระบบแท่นวางยา 4 ช่อง, และระบบหอคอยเวทมนตร์พร้อมการเตือนภัยเมื่อเลือดวิกฤต
- **เนื้อหาของเกม:** ประกอบด้วย 4 ด่าน (3 ด่านเนื้อเรื่อง + 1 ด่าน Endless Mode), มอนสเตอร์ 5 ชนิดรวมบอสใหญ่ราชันย์อสูรเวท, และสูตรปรุงยา 7 ชนิด (รวมถึงยาระเบิดมหาเวท RARE)
- **ระบบเทคโนโลยี:** พัฒนาด้วยภาษา Java (JDK 21) ใช้ Java Swing & Graphics2D, ระบบเสียงดนตรีสังเคราะห์ Polyphonic Synthesizer BGM, รองรับโหมดเต็มจอ (F11), และปฏิบัติตามนโยบาย Zero-Emoji

### 1. ตารางเวลาการดำเนินโครงการ (Project Schedule)
| ลำดับ | รายการ | ก.ย. W1 | ก.ย. W2 | ก.ย. W3 | ก.ย. W4 | ต.ค. W1 | ต.ค. W2 | ต.ค. W3 | ต.ค. W4 |
|:---:|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| 1 | ศึกษาข้อมูล และออกแบบโครงสร้างเกม | X | X | | | | | | |
| 2 | ออกแบบกราฟิก, UI และหาภาพตัวละคร | | X | X | | | | | |
| 3 | ลงมือเขียนโปรแกรม และพัฒนาระบบเกม | | | X | X | X | X | | |
| 4 | ตรวจสอบการทำงาน และแก้ไขข้อผิดพลาด | | | | | X | X | X | |
| 5 | จัดทำเอกสารรายงาน | | | | | | | X | X |

---

# บทที่ 2 ส่วนการพัฒนา

## เนื้อเรื่องย่อหรือวิธีการเล่น (กรณีเกมส์)

### เนื้อเรื่องย่อ
ในเกม POTION PANIC (อลวนคนปรุงยา) คุณจะได้รับบทเป็น "ศิษย์ฝึกหัดนักปรุงยา" ที่เพิ่งเข้ามารับเวรกะดึกเฝ้าหอคอยเพียงลำพัง แต่จู่ๆ คืนที่ควรจะเงียบสงบก็พังทลายลง เมื่อรอยแยกมิติเปิดออกและฝูงมอนสเตอร์กลายพันธุ์กำลังแห่กันมาที่หอคอย! ซ้ำร้าย "สายธารวัตถุดิบเวทมนตร์" ที่เชื่อมต่อกับใต้ดินดันทํางานรวน มันสูบเอาสมุนไพรและแร่ธาตุต่างๆ ไหลทะลักขึ้นมาอย่างบ้าคลั่งและไม่ขาดสาย คุณไม่มีกองทหาร ไม่มีดาบวิเศษ มีเพียงหม้อต้มยาเก่าๆ กับความจำในสมอง คุณต้องตั้งสติรับมือกับความอลวนนี้ คว้าวัตถุดิบที่ไหลมาตามสายพาน โยนลงหม้อให้ถูกสูตร แล้วสาดขวดยาเวทมนตร์สกัดกั้นพวกมอนสเตอร์ให้ทันเวลา ก่อนที่หอคอยแห่งนี้จะแตก!

### วิธีการเล่น
กดปุ่มตัวเลข 1, 2, 3, 4, 5 เพื่อดึงวัตถุดิบจากสายพานในแต่ละช่องลงหม้อต้ม กดปุ่ม Spacebar เพื่อทำการผสมยา และใช้เมาส์คลิกซ้ายเพื่อเลือกขวดยาพร้อมกับคลิกเป้าหมายเพื่อปาโจมตีมอนสเตอร์

นอกจากนี้ยังมีปุ่มลัดเสริมเพื่อความคล่องตัวในการเล่น ได้แก่ ปุ่ม [F] สำหรับสุ่มวัตถุดิบใหม่ทั้งสายพาน (Reroll), ปุ่ม [Q], [W], [E], [R] สำหรับสลับเลือกขวดยาบนแท่นวางยา, ปุ่ม [Backspace] สำหรับนำวัตถุดิบชิ้นล่าสุดออกจากหม้อ, ปุ่ม [C] สำหรับล้างหม้อต้มทั้งหมด, ปุ่ม [Tab] หรือ [H] เพื่อเปิดดูสมุดสูตรยา, ปุ่ม [M] สำหรับสลับเปิด/ปิดเสียงเพลง และปุ่ม [F11] เพื่อสลับโหมดเต็มหน้าจอ

### Storyboard
**ตัวละครและไอเทม**
- มอนสเตอร์: มอนสเตอร์หลากธาตุที่บุกโจมตีหอคอย
- หม้อปรุงยา: หม้อต้มสำหรับใส่ส่วนผสม 3 ชิ้นเพื่อเคี่ยวเป็นยา
- วัตถุดิบปรุงยา: สมุนไพรเพลิง ผลึกน้ำแข็ง เห็ดพิษ และรากสายฟ้า
- ขวดยา: ขวดยาธาตุต่างๆ ที่ปรุงสำเร็จพร้อมนำไปปา

**ฉากภายในเกม**
- **หน้าเริ่มเกม (Main Menu):** หน้าจอเริ่มต้นของเกมที่แสดงป้ายเมนูหลัก ได้แก่ ปุ่ม "เริ่มเกม" สำหรับเข้าสู่เกม และปุ่ม "วิธีเล่น" สำหรับดูคู่มือปุ่มลัดและสูตรยาเบื้องต้น พร้อมระบบไตเติลเกมเปลี่ยนสีตัวอักษรแบบไดนามิก
- **หน้าเลือกด่านและร้านค้า (Stage Select & Shop):** หน้าจอแสดงด่านทั้ง 4 ด่าน พร้อมร้านค้าอัปเกรดพลังชีวิตหอคอย ความเร็วสายพาน รัศมีระเบิด และปุ่มรีเซ็ตสกิลคืนดาว 100%
- **หน้าระหว่างเกม (Gameplay):** หน้าจอหลักขณะเล่นที่แบ่งสัดส่วนการควบคุมเป็น 3 โซน ส่วนบนคือเส้นทางที่มอนสเตอร์เดินบุกเข้ามาพร้อมหลอดพลังชีวิต (HP) ส่วนกลางคือสายพานลำเลียงวัตถุดิบ 5 ช่องที่ไหลมา และส่วนล่างคือพื้นที่ของนักปรุงยาที่มีหม้อต้มสำหรับผสมไอเทม พื้นที่พักขวดยาพร้อมใช้ และสมุดสูตรยาสำหรับดูสูตรการผสมยา
- **หน้าแพ้เกม (Game Over):** หน้าจอแสดงผลเมื่อมอนสเตอร์บุกโจมตีหอคอยจนพลังชีวิตเหลือศูนย์ จะแสดงข้อความแสดงความพ่ายแพ้ พร้อมปุ่มตัวเลือกให้ผู้เล่นกด "ลองใหม่อีกครั้ง" เพื่อเริ่มเกมแก้ตัวในรอบใหม่ หรือกด "กลับหน้าหลัก" เพื่อออกจากด่าน
- **หน้าชนะเกม (Victory):** หน้าจอแสดงผลเมื่อผู้เล่นสามารถปกป้องหอคอยและสกัดกั้นฝูงมอนสเตอร์ได้สำเร็จจนจบภารกิจ จะแสดงข้อความแสดงความยินดี คำนวณดวงดาวที่ได้รับ พร้อมปุ่มตัวเลือกให้ "เล่นอีกครั้ง" หรือกด "กลับหน้าหลัก"

## แผนภาพ Class Diagram พร้อมคำอธิบาย
โครงสร้างสถาปัตยกรรมของโครงการประกอบด้วยคลาสหลักดังต่อไปนี้:
1. **คลาส GameEngine:** คลาสศูนย์กลางควบคุม Game Loop, ตรวจสอบสถานะแพ้/ชนะ, ประสานงานระหว่างหอคอย สายพาน หม้อต้ม และการปาขวดยา
2. **คลาส Tower:** จัดเก็บสถานะและพลังชีวิตของหอคอย ตรวจสอบการรับความเสียหายและการพังทลาย
3. **คลาส ConveyorBelt:** จัดการสายพานวัตถุดิบ 5 ช่อง ควบคุมระบบสุ่มอัจฉริยะ (Threat-Aware Spawning) และฟังก์ชัน Reroll
4. **คลาส Cauldron:** จัดการหม้อต้มยา รับวัตถุดิบ ตรวจสอบความจุ และผสมยาตามสูตร
5. **คลาส PotionRack:** จัดการแท่นพักขวดยาพร้อมใช้ 4 ช่อง พร้อมระบบเลือกช่องอัตโนมัติ (Auto-Select)
6. **คลาส RecipeBook:** บรรจุฐานข้อมูลสูตรยา 3 ชิ้น ตรวจสอบความถูกต้อง และสุ่มของแรร์ยาระเบิดมหาเวท (Miracle Brew)
7. **คลาส Monster (Abstract):** คลาสแม่ของมอนสเตอร์ ควบคุมการเดิน แอนิเมชัน การรับดาเมจ และระบบจุดอ่อนธาตุ
8. **คลาส Potion (Abstract):** คลาสแม่ของขวดยาเวทมนตร์ กำหนดคุณสมบัติธาตุ รัศมีระเบิด และเมธอด onImpact()
9. **คลาส VisualEffect:** จัดการเอฟเฟกต์ภาพสนามรบ คลื่นช็อกเวฟ เสาไฟ หนามน้ำแข็ง สายฟ้า และอนุภาคฟิสิกส์
10. **คลาส SoundManager (Singleton):** จัดการระบบเสียงดนตรีสังเคราะห์และเสียงเอฟเฟกต์ทั้งหมดในเกม
11. **คลาส GameWindow:** หน้าต่าง JFrame หลัก จัดการการสลับหน้าจอ (CardLayout) และโหมดเต็มจอ
12. **คลาส GameplayPanel:** หน้าจอแสดงผลหลักที่ Override paintComponent() เพื่อเรนเดอร์เกมเพลย์ 60 FPS

## รูปแบบ Application / Applet
- **รูปแบบ:** Java Standalone Desktop Application (สร้างด้วย Java Swing & AWT)
- **ภาษา:** Java (JDK 21)
- **GUI:** javax.swing (JFrame, JPanel, CardLayout) และ java.awt.Graphics2D
- **ระบบเสียง:** javax.sound.sampled (SourceDataLine Synthesizer Engine)

โครงการนี้เลือกพัฒนาในรูปแบบ Standalone Desktop Application โดยมีฟังก์ชัน main() ในคลาส Main เป็นจุดเริ่มต้นโปรแกรม และไม่ได้ใช้เทคโนโลยี Java Applet เนื่องจาก Applet ได้ถูกยกเลิก (Deprecated และ Removed) ออกจากภาษา Java ยุคใหม่อย่างถาวรตั้งแต่ Java 11 เนื่องจากปัญหาด้านความปลอดภัยของเบราว์เซอร์ การพัฒนาเป็น Desktop Application จึงเป็นแนวทางมาตรฐานที่ถูกต้อง มีความปลอดภัยสูง เข้าถึงการเรนเดอร์ระดับฮาร์ดแวร์เร่งความเร็วได้เต็มที่ และสามารถสร้างไฟล์ Executable JAR ดับเบิลคลิกเล่นได้บนทุกระบบปฏิบัติการ

## อธิบายส่วนของโปรแกรมที่มี

### Constructor
Constructor ทำหน้าที่กำหนดค่าเริ่มต้นให้แก่ออบเจกต์เมื่อถูกสร้างขึ้นใหม่ ป้องกันข้อผิดพลาดจากค่าว่าง (Null) และเตรียมความพร้อมของข้อมูล

```java
// ตัวอย่าง Constructor ในคลาส ChaosBoss.java
public ChaosBoss(double startX, double startY) {
    // เรียก Constructor ของคลาสแม่ (Monster) ด้วยคำสั่ง super
    super("ราชันย์อสูรเวท (Chaos Overlord)", 420, 0.28, 25, ElementType.FIRE, startX, startY);
    this.width = 96;
    this.height = 96;
}
```
ในตัวอย่างนี้ คอนสตรัคเตอร์ของ ChaosBoss จะเรียกใช้ super() เพื่อส่งค่าชื่อ เลือด 420 ความเร็ว 0.28 พลังโจมตี 25 และจุดอ่อนเริ่มต้นที่ธาตุไฟ ไปยังคอนสตรัคเตอร์ของคลาสแม่ Monster พร้อมกำหนดขนาดกว้างยาว 96x96 พิกเซล

### Encapsulation
Encapsulation คือการซ่อนข้อมูลสถานะภายในไว้เป็นความลับด้วยการกำหนดแอตทริบิวต์เป็น private และเปิดให้ภายนอกเข้าถึงผ่านเมธอด Getter และ Setter ที่มีการตรวจสอบความถูกต้อง

```java
// ตัวอย่าง Encapsulation ในคลาส Tower.java
public class Tower implements Damageable, Renderable {
    private int hp;
    private int maxHp;

    public void takeDamage(int amount, ElementType element) {
        // ควบคุมไม่ให้พลังชีวิตติดลบต่ำกว่า 0
        this.hp = Math.max(0, this.hp - amount);
    }

    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
}
```
ตัวแปร hp และ maxHp ถูกห่อหุ้มไว้เป็น private ทำให้คลาสอื่นไม่สามารถแก้ไขเลือดได้โดยตรง แต่ต้องเรียกผ่าน takeDamage() ซึ่งมีคำสั่ง Math.max(0, ...) เพื่อป้องกันไม่ให้เลือดหอคอยติดลบ

### Composition
Composition คือการสร้างคลาสหลักโดยประกอบขึ้นจากออบเจกต์ของคลาสย่อยๆ (Has-A Relationship) เพื่อแบ่งแยกหน้าที่ความรับผิดชอบอย่างเป็นระเบียบ

```java
// ตัวอย่าง Composition ในคลาส GameEngine.java
public class GameEngine {
    // GameEngine "ประกอบด้วย" หอคอย, สายพาน, หม้อต้ม, และแท่นวางยา
    private final Tower tower;
    private final ConveyorBelt belt;
    private final Cauldron cauldron;
    private final PotionRack potionRack;
    private final WaveManager waveManager;

    public GameEngine() {
        this.tower = new Tower(Constants.TOWER_MAX_HP);
        this.belt = new ConveyorBelt();
        this.cauldron = new Cauldron(Constants.ZONE_BOT_X, Constants.ZONE_BOT_Y + 15);
        this.potionRack = new PotionRack(Constants.ZONE_BOT_X + 220, Constants.ZONE_BOT_Y + 15);
        this.waveManager = new WaveManager();
    }
}
```
คลาส GameEngine ทำหน้าที่เป็นผู้ควบคุมส่วนกลาง โดยประกอบออบเจกต์ของหอคอย สายพาน หม้อต้ม และแท่นวางยา เข้ามาทำงานร่วมกันอย่างเป็นระบบ

### Polymorphism
Polymorphism คือความสามารถของออบเจกต์ในคลาสลูกต่างๆ ในการตอบสนองต่อเมธอดชื่อเดียวกันด้วยพฤติกรรมเฉพาะตัวที่แตกต่างกันผ่านการ Override

```java
// ตัวอย่าง Polymorphism ในคลาสลูกของ Potion.java
// คลาส FirePotion: ปะทุเป็นเสาเพลิงและติดสถานะเผาไหม้ (Burn)
public class FirePotion extends Potion {
    @Override
    public void onImpact(double impactX, double impactY, List<Monster> allMonsters, List<VisualEffect> effects) {
        effects.add(VisualEffect.createFireBurst(impactX, impactY, getRadius()));
        // ทำดาเมจธาตุไฟและเผาไหม้ต่อเนื่อง
    }
}

// คลาส IcePotion: ปะทุเป็นหนามผลึกน้ำแข็งและติดสถานะแช่แข็ง (Freeze)
public class IcePotion extends Potion {
    @Override
    public void onImpact(double impactX, double impactY, List<Monster> allMonsters, List<VisualEffect> effects) {
        effects.add(VisualEffect.createIceSpikes(impactX, impactY, getRadius()));
        // ทำดาเมจธาตุน้ำแข็งและสโลว์ 50%
    }
}
```
เมื่อ GameEngine สั่ง p.onImpact() ขวดยาแต่ละชนิดจะทำงานไม่เหมือนกัน ยาเพลิงจะสร้างเสาไฟและเผาไหม้ ส่วนยาน้ำแข็งจะสร้างหนามน้ำแข็งและสโลว์มอนสเตอร์ ทั้งที่เป็นการเรียกเมธอดเดียวกัน

### Abstract
Abstract ใช้สำหรับสร้างคลาสต้นแบบและอินเทอร์เฟซเพื่อกำหนดโครงสร้างร่วมและข้อตกลงที่คลาสลูกจำเป็นต้องนำไปพัฒนาต่อ

```java
// ตัวอย่าง Abstract Class ในคลาส Potion.java
public abstract class Potion implements Renderable {
    protected String name;
    protected ElementType element;
    protected int baseDamage;

    // บังคับให้คลาสลูกต้องเขียนการระเบิดตามธาตุของตัวเอง
    public abstract void onImpact(double impactX, double impactY, 
                                  List<Monster> allMonsters, List<VisualEffect> effects);
}
```
คลาส Potion ถูกกำหนดให้เป็น abstract class เพื่อป้องกันไม่ให้ถูก new เป็นออบเจกต์เปล่าๆ และบังคับให้คลาสลูกทุกตัวต้องเขียนโค้ดในเมธอด onImpact() ของตนเอง

### Inheritance
Inheritance คือการสืบทอดคุณสมบัติและพฤติกรรมจากคลาสแม่มาสู่คลาสลูกด้วยคีย์เวิร์ด extends เพื่อลดความซ้ำซ้อนของโค้ด

```java
// ตัวอย่าง Inheritance ในคลาส SwiftImp.java
public class SwiftImp extends Monster {
    public SwiftImp(double x, double y) {
        // สืบทอดฟังก์ชันการเดิน แอนิเมชัน และระบบหลอดเลือดจาก Monster ทั้งหมด
        super("อิมป์ว่องไว (Swift Imp)", 65, 1.15, 12, ElementType.LIGHTNING, x, y);
    }
}
```
คลาส SwiftImp สืบทอดคุณสมบัติมาจากคลาสแม่ Monster ทำให้ได้รับระบบการเดิน ระบบอนิเมชัน และการรับดาเมจมาใช้ทันทีโดยไม่ต้องเขียนโค้ดซ้ำใหม่

## ส่วนประกอบ GUI
โครงสร้างของหน้าจอ GUI พัฒนาขึ้นโดยใช้ลำดับชั้นคอมโพเนนต์ของ Java Swing:
- **GameWindow (JFrame):** หน้าต่างหลักของโปรแกรมขนาด 1280x720 พิกเซล ควบคุมการสลับฉากด้วย CardLayout และควบคุมระบบ Fullscreen
- **rootPanel (JPanel):** พาเนลหลักที่บรรจุหน้าจอต่างๆ ได้แก่ MainMenuPanel, StageSelectPanel, GameplayPanel, VictoryPanel, และ GameOverPanel
- **GameplayPanel (JPanel):** แคนวาสหลักในการวาดภาพเกมทั้งหมด โดยทำการ Override เมธอด paintComponent(Graphics g) และใช้ java.awt.Graphics2D วาดกราฟิกแบบ Double Buffering ที่ 60 เฟรมต่อวินาที
- **การแบ่งพื้นที่ 3 โซน:** ด้านบนคือโซนสนามรบ (Battlefield), ตรงกลางคือโซนสายพานวัตถุดิบ (Conveyor), และด้านล่างคือโซนโต๊ะปรุงยาและแท่นวางขวดยา (Alchemy Table)

## Event handling
เกมนี้มีระบบการจัดการเหตุการณ์ (Event Handling) 2 รูปแบบหลัก:
1. **Keyboard Event Handling (KeyListener / KeyAdapter & KeyEventDispatcher):**
   ใช้ดักจับปุ่มลัดเพื่อควบคุมการเล่นเกม เช่น ปุ่ม 1-5 ดึงวัตถุดิบ, Spacebar ผสมยา, F สุ่มสายพานใหม่, Q-W-E-R เลือกขวดยา, Backspace นำของออกจากหม้อ, C ล้างหม้อต้ม, Tab/H เปิดสมุดสูตร, F11 สลับเต็มจอ, และ M เปิด/ปิดเสียง
2. **Mouse Event Handling (MouseListener & MouseMotionAdapter):**
   ใช้ mouseMoved ในการคำนวณพิกัดเมาส์เสมือน (Virtual Coordinates) เพื่อวาดวงแหวนล็อกเป้าหมาย และใช้ mousePressed เพื่อตรวจสอบการคลิกปุ่มบนหน้าจอ หรือการคลิกบนสนามรบเพื่อเล็งและขว้างขวดยาไปยังเป้าหมาย

## อัลกอริทึมที่สำคัญในโปรแกรม
1. **อัลกอริทึมตรวจสอบสูตรผสมยา 3 วัตถุดิบ (Order-Independent Matching):** ใช้การนับความถี่ของวัตถุดิบ 3 ชิ้นในหม้อต้ม เพื่อให้ผู้เล่นสามารถหยิบวัตถุดิบชิ้นใดลงก่อนหรือหลังก็ได้ เช่น สมุนไพรเพลิง 2 ชิ้น + ผลึกน้ำแข็ง 1 ชิ้น จะได้ยาเพลิงเสมอ
2. **อัลกอริทึมสุ่มวัตถุดิบอัจฉริยะตามภัยคุกคาม (Smart Threat-Aware Spawning):** สายพานจะวิเคราะห์มอนสเตอร์ตัวที่อยู่ใกล้หอคอยที่สุด และตรวจสอบวัตถุดิบในหม้อต้ม เพื่อเร่งสุ่มวัตถุดิบชิ้นที่จำเป็นลงมาบนสายพานด้วยโอกาสสูงถึง 85% ช่วยให้ผู้เล่นได้รับของแก้ทางอย่างต่อเนื่อง
3. **อัลกอริทึมคำนวณวิถีโค้งพาราโบลาและการกระจายดาเมจวงกว้าง (Parabolic Trajectory & AoE):** คำนวณความสูงตามสมการควอดราติก y = 4 * height * t * (1 - t) เพื่อให้ขวดยาลอยโค้งสมจริง และเมื่อตกกระทบจะใช้ระยะทางแบบยุคลิด (Euclidean Distance) เพื่อสร้างดาเมจแบบกระจายตัวรอบจุดตกกระทบ
4. **อัลกอริทึมการสลับเกราะของบอสใหญ่ (Boss Shield Shifting):** บอสใหญ่ Chaos Overlord จะทำการเปลี่ยนเกราะธาตุหมุนเวียนทุกๆ 7.5 วินาที ทำให้ผู้เล่นต้องสลับปรุงยาตามธาตุของบอสอย่างต่อเนื่อง

---

# บทที่ 3 สรุป

## ปัญหาที่พบระหว่างการพัฒนา
1. **ปัญหามอนสเตอร์เดินทับซ้อนกันในเลนเดียวกัน:** แก้ไขโดยใช้ระบบสุ่มกระจายพิกัดเกิด (Staggered Spawning) ร่วมกับอัลกอริทึมจัดลำดับมิติความลึก (Depth Sorting) เรียงตามแกน Y ทำให้ภาพมอนสเตอร์ไม่บังกัน
2. **ปัญหาความไม่เข้ากันของอีโมจิและฟอนต์ระหว่างระบบปฏิบัติการ:** แก้ไขโดยใช้นโยบาย Zero-Emoji ทั้งโปรเจกต์ เปลี่ยนมาวาดเวกเตอร์เรขาคณิตแทน เพื่อให้แสดงผลได้ถูกต้อง 100% บนทุกเครื่อง
3. **ปัญหาเสียงเพลงไม่ดังเมื่อเปิดเสียงกลับมา (Mute/Unmute Bug):** แก้ไขโดยแยกตัวแปรสถานะเพลงที่ต้องการเล่น (desiredBgmType) ออกจากสถานะการเล่นจริง ทำให้เมื่อเปิดเสียงกลับมาเพลงจะกลับมาเล่นต่อได้ทันที
4. **ปัญหาการสั่นหน้าจอถี่เกินไประหว่างเล่น:** แก้ไขโดยนำการสั่นตอนปาขวดยาออกทั้งหมด และคงการสั่นไว้เฉพาะจังหวะที่หอคอยกำลังจะพัง (เลือด < 35%) เพื่อไม่ให้รบกวนสายตาของผู้เล่น

## จุดเด่นของโปรแกรมที่ไม่เหมือนใคร
1. **ระบบผสมยา 3 วัตถุดิบและสมการภาพ:** นำเสนอสูตรยาเป็นไอคอนภาพเข้าใจง่าย พร้อมระบบคาถาชี้แนะรูปภาพเหนือหม้อต้ม
2. **สายพานสุ่มวัตถุดิบอัจฉริยะ:** สายพานเข้าใจภัยคุกคามในสนามรบ ช่วยส่งของแก้ทางมอนสเตอร์มาให้อย่างแม่นยำ
3. **กราฟิกแฟนตาซี 2 มิติและเอฟเฟกต์ภาพมีชีวิตชีวา:** มีหางละอองเวทมนตร์ วงแหวนล็อกเป้าหมาย เสาไฟ หนามน้ำแข็ง และละอองมอนสเตอร์แตกสลาย
4. **ระบบเสียงดนตรีสังเคราะห์แท้:** สร้างเสียงดนตรีประกอบและเสียงเวทมนตร์ด้วยโค้ด Java แท้ๆ โดยไม่ต้องพึ่งพาไฟล์ภายนอก
5. **ความถูกต้องตามหลักการ OOP ระดับสูง:** นำแนวคิดทั้ง 6 เสาหลักมาประยุกต์ใช้จริงอย่างสมบูรณ์แบบ โค้ดสะอาดและเป็นระเบียบ

## คำแนะนำสำหรับผู้สอนที่อยากให้อธิบาย หรือที่เรียนแล้วไม่เข้าใจ หรืออยากให้เพิ่มสำหรับน้อง ๆ รุ่นต่อไป
อยากให้อาจารย์เพิ่มเนื้อหาการสอนเรื่อง Game Loop Architecture และเทคนิคการเรนเดอร์ภาพแบบ Double Buffering ด้วย Graphics2D ในคาบเรียน เพื่อให้นักศึกษาได้เข้าใจการทำงานเบื้องหลังของแอปพลิเคชันที่ต้องประมวลผลกราฟิกและฟิสิกส์แบบเรียลไทม์ 60 FPS รวมถึงอยากให้อธิบายเรื่อง Threading และการทำงานเบื้องหลัง (Background Worker / Daemon Thread) ใน Java Swing เพิ่มเติม เพื่อให้สามารถสร้างแอปพลิเคชันที่ทำงานหลายอย่างพร้อมกันได้โดยที่หน้าจอไม่ค้าง

**ความคิดเห็นส่วนตัวเพิ่มเติมของผู้เรียน:**  
[ พื้นที่สำหรับพิมพ์ความรู้สึกส่วนตัวหลังทำโครงงานเสร็จสิ้น หรือสิ่งที่ประทับใจในการเรียนวิชานี้ ]
"""

def main():
    html_content = get_report_html()
    with open('/tmp/potion_clean_report.html', 'w', encoding='utf-8') as f:
        f.write(html_content)
    
    # 1. สร้างไฟล์ .docx ด้วย textutil
    subprocess.run(['textutil', '-convert', 'docx', '/tmp/potion_clean_report.html', '-output', 'POTION_PANIC_REPORT.docx'], check=True)
    print("SUCCESS: POTION_PANIC_REPORT.docx generated!")
    
    # 2. บันทึก POTION_PANIC_REPORT.doc (HTML สะอาดสำหรับ Word)
    with open('POTION_PANIC_REPORT.doc', 'w', encoding='utf-8') as f:
        f.write(html_content)
    print("SUCCESS: POTION_PANIC_REPORT.doc updated!")
    
    # 3. บันทึก POTION_PANIC_REPORT_WORD.md
    md_content = get_report_markdown()
    with open('POTION_PANIC_REPORT_WORD.md', 'w', encoding='utf-8') as f:
        f.write(md_content)
    print("SUCCESS: POTION_PANIC_REPORT_WORD.md updated!")
    
    # 4. บันทึก REPORT_DRAFT.md
    with open('REPORT_DRAFT.md', 'w', encoding='utf-8') as f:
        f.write(md_content)
    print("SUCCESS: REPORT_DRAFT.md updated!")

if __name__ == '__main__':
    main()
