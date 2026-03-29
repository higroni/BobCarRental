#!/usr/bin/env python3
"""
Fix emoji characters in parse_fares_and_headers.py
"""

# Read the file
with open('parse_fares_and_headers.py', 'r', encoding='utf-8') as f:
    content = f.read()

# Replace emojis with plain text
replacements = {
    '🚀': '[>>]',
    '✓': '[OK]',
    '✗': '[FAIL]',
    '❌': '[ERROR]',
    '📤': '[UPLOAD]',
    '📥': '[DOWNLOAD]',
    '📊': '[STATS]',
    '🔐': '[AUTH]',
    '✅': '[OK]',
    '⚠️': '[WARN]',
    '📝': '[NOTE]',
    '🎯': '[TARGET]',
    '💾': '[SAVE]',
    '🔍': '[SEARCH]',
    '📂': '[FOLDER]',  # Added missing folder emoji
}

for emoji, text in replacements.items():
    content = content.replace(emoji, text)

# Write back
with open('parse_fares_and_headers.py', 'w', encoding='utf-8') as f:
    f.write(content)

print("Fixed all emoji characters!")

# Made with Bob
