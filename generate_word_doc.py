#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
สคริปต์แปลงเอกสาร Markdown เป็นไฟล์ Microsoft Word (.doc) 
ที่จัดรูปแบบสำหรับเปิดใน Microsoft Word ได้โดยตรง พร้อมฟอนต์และสไตล์ที่สวยงาม
"""

import re
import html

def md_to_word_html(md_text):
    # Basic Markdown to HTML converter tailored for Word compatibility
    lines = md_text.split('\n')
    out = []
    
    in_code_block = False
    code_lang = ""
    code_lines = []
    
    in_table = False
    table_rows = []
    
    for line in lines:
        # Code block toggle
        if line.startswith('```'):
            if in_code_block:
                in_code_block = False
                code_content = html.escape('\n'.join(code_lines))
                out.append(f'<pre class="code-block"><code>{code_content}</code></pre>')
                code_lines = []
            else:
                in_code_block = True
                code_lang = line[3:].strip()
            continue
        
        if in_code_block:
            code_lines.append(line)
            continue
            
        # Table parsing
        if line.strip().startswith('|') and line.strip().endswith('|'):
            # Check if delimiter line (|---|---|)
            if re.match(r'^\s*\|(?:\s*:?-+:?\s*\|)+\s*$', line):
                continue
            cols = [c.strip() for c in line.strip().split('|')[1:-1]]
            table_rows.append(cols)
            in_table = True
            continue
        else:
            if in_table:
                # Flush table
                out.append('<table class="report-table">')
                for r_idx, row in enumerate(table_rows):
                    out.append('<tr>')
                    tag = 'th' if r_idx == 0 else 'td'
                    for cell in row:
                        # Process inline styles
                        cell_html = process_inline(cell)
                        out.append(f'<{tag}>{cell_html}</{tag}>')
                    out.append('</tr>')
                out.append('</table>')
                in_table = False
                table_rows = []
        
        # Horizontal rule
        if re.match(r'^\s*---+\s*$', line):
            out.append('<hr class="divider"/>')
            continue
            
        # Headings
        m_h1 = re.match(r'^#\s+(.+)$', line)
        if m_h1:
            out.append(f'<h1 class="chapter-title">{process_inline(m_h1.group(1))}</h1>')
            continue
            
        m_h2 = re.match(r'^##\s+(.+)$', line)
        if m_h2:
            out.append(f'<h2 class="sub-chapter">{process_inline(m_h2.group(1))}</h2>')
            continue
            
        m_h3 = re.match(r'^###\s+(.+)$', line)
        if m_h3:
            out.append(f'<h3 class="section-title">{process_inline(m_h3.group(1))}</h3>')
            continue
            
        m_h4 = re.match(r'^####\s+(.+)$', line)
        if m_h4:
            out.append(f'<h4 class="subsection-title">{process_inline(m_h4.group(1))}</h4>')
            continue
            
        # Blockquote / Notice box
        if line.startswith('> '):
            quote_text = process_inline(line[2:].strip())
            out.append(f'<div class="callout-box">{quote_text}</div>')
            continue
            
        # Bullet list
        m_ul = re.match(r'^\s*[-*]\s+(.+)$', line)
        if m_ul:
            out.append(f'<li class="bullet-item">{process_inline(m_ul.group(1))}</li>')
            continue
            
        # Numbered list
        m_ol = re.match(r'^\s*(\d+)\.\s+(.+)$', line)
        if m_ol:
            out.append(f'<li class="numbered-item" value="{m_ol.group(1)}">{process_inline(m_ol.group(2))}</li>')
            continue
            
        # Normal text
        if line.strip():
            out.append(f'<p class="normal-para">{process_inline(line)}</p>')
            
    if in_table:
        out.append('<table class="report-table">')
        for r_idx, row in enumerate(table_rows):
            out.append('<tr>')
            tag = 'th' if r_idx == 0 else 'td'
            for cell in row:
                cell_html = process_inline(cell)
                out.append(f'<{tag}>{cell_html}</{tag}>')
            out.append('</tr>')
        out.append('</table>')
        
    return '\n'.join(out)

def process_inline(text):
    # Escape basic html entities
    text = html.escape(text)
    # Bold **text**
    text = re.sub(r'\*\*(.+?)\*\*', r'<b>\1</b>', text)
    # Italic *text*
    text = re.sub(r'\*(.+?)\*', r'<i>\1</i>', text)
    # Inline code `code`
    text = re.sub(r'`(.+?)`', r'<code class="inline-code">\1</code>', text)
    return text

def main():
    with open('POTION_PANIC_REPORT_WORD.md', 'r', encoding='utf-8') as f:
        md_content = f.read()
        
    body_html = md_to_word_html(md_content)
    
    word_document = f"""<!DOCTYPE html>
<html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'>
<head>
<meta charset="utf-8">
<title>POTION PANIC - รายงานฉบับสมบูรณ์ (Final Project Report)</title>
<!--[if gte mso 9]>
<xml>
 <w:WordDocument>
  <w:View>Print</w:View>
  <w:Zoom>100</w:Zoom>
  <w:DoNotOptimizeForBrowser/>
 </w:WordDocument>
</xml>
<![endif]-->
<style>
    @page {{
        size: A4 portrait;
        margin: 2.54cm 2.54cm 2.54cm 2.54cm;
        mso-header-margin: 1.25cm;
        mso-footer-margin: 1.25cm;
    }}
    body {{
        font-family: 'TH Sarabun New', 'Angsana New', 'Cordia New', Tahoma, sans-serif;
        font-size: 16pt;
        line-height: 1.35;
        color: #111111;
        background-color: #FFFFFF;
    }}
    h1.chapter-title {{
        font-size: 22pt;
        font-weight: bold;
        color: #1a365d;
        border-bottom: 2pt solid #2b6cb0;
        padding-bottom: 4pt;
        margin-top: 24pt;
        margin-bottom: 12pt;
        page-break-after: avoid;
    }}
    h2.sub-chapter {{
        font-size: 18pt;
        font-weight: bold;
        color: #2c5282;
        margin-top: 16pt;
        margin-bottom: 8pt;
        page-break-after: avoid;
    }}
    h3.section-title {{
        font-size: 17pt;
        font-weight: bold;
        color: #2b6cb0;
        margin-top: 14pt;
        margin-bottom: 6pt;
        page-break-after: avoid;
    }}
    h4.subsection-title {{
        font-size: 16pt;
        font-weight: bold;
        color: #4a5568;
        margin-top: 10pt;
        margin-bottom: 4pt;
        page-break-after: avoid;
    }}
    p.normal-para {{
        font-size: 16pt;
        margin-top: 3pt;
        margin-bottom: 6pt;
        text-align: justify;
    }}
    li.bullet-item, li.numbered-item {{
        font-size: 16pt;
        margin-left: 20pt;
        margin-bottom: 3pt;
    }}
    table.report-table {{
        width: 100%;
        border-collapse: collapse;
        margin-top: 10pt;
        margin-bottom: 14pt;
        font-size: 14pt;
    }}
    table.report-table th {{
        background-color: #ebf8ff;
        color: #2b6cb0;
        border: 1pt solid #cbd5e0;
        padding: 6pt 8pt;
        font-weight: bold;
        text-align: left;
    }}
    table.report-table td {{
        border: 1pt solid #cbd5e0;
        padding: 6pt 8pt;
    }}
    pre.code-block {{
        background-color: #f7fafc;
        border: 1pt solid #e2e8f0;
        border-left: 4pt solid #3182ce;
        padding: 8pt 10pt;
        font-family: 'Consolas', 'Courier New', monospace;
        font-size: 11pt;
        line-height: 1.25;
        color: #2d3748;
        margin-top: 8pt;
        margin-bottom: 10pt;
        overflow-x: auto;
    }}
    code.inline-code {{
        background-color: #edf2f7;
        color: #c53030;
        font-family: 'Consolas', monospace;
        font-size: 12pt;
        padding: 1pt 3pt;
        border-radius: 3pt;
    }}
    div.callout-box {{
        background-color: #fffaf0;
        border: 1.5pt solid #dd6b20;
        border-radius: 4pt;
        padding: 8pt 12pt;
        margin-top: 8pt;
        margin-bottom: 12pt;
        color: #7b341e;
        font-size: 15pt;
    }}
    hr.divider {{
        border: 0;
        height: 1pt;
        background-color: #cbd5e0;
        margin: 16pt 0;
    }}
</style>
</head>
<body>
{body_html}
</body>
</html>
"""
    
    with open('POTION_PANIC_REPORT.doc', 'w', encoding='utf-8') as out_f:
        out_f.write(word_document)
    print("SUCCESS: POTION_PANIC_REPORT.doc generated successfully!")

if __name__ == '__main__':
    main()
