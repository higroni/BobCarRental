# 📊 Kako Videti Mermaid Dijagrame u VS Code

## Metoda 1: Markdown Preview Enhanced (Preporučeno) ⭐

### Korak 1: Instalacija Extension-a
1. Otvorite VS Code
2. Pritisnite `Ctrl+Shift+X` (Extensions panel)
3. Pretražite: **"Markdown Preview Mermaid Support"**
4. Kliknite **Install** na extension od autora **Matt Bierner**

### Korak 2: Otvaranje Dijagrama
1. Otvorite fajl: `bobcarrental/docs/ARCHITECTURE_DIAGRAMS.md`
2. Pritisnite `Ctrl+Shift+V` ili kliknite ikonu 📖 u gornjem desnom uglu
3. Dijagrami će biti automatski renderovani!

### Korak 3: Side-by-Side View
1. Otvorite fajl: `ARCHITECTURE_DIAGRAMS.md`
2. Pritisnite `Ctrl+K V` za split view
3. Levo: Markdown kod, Desno: Renderovani dijagrami

---

## Metoda 2: Markdown Preview Enhanced (Alternativa)

### Instalacija
1. `Ctrl+Shift+X` → Extensions
2. Pretražite: **"Markdown Preview Enhanced"**
3. Install extension od **Yiyi Wang**

### Korišćenje
1. Otvorite `ARCHITECTURE_DIAGRAMS.md`
2. Desni klik → **"Markdown Preview Enhanced: Open Preview to the Side"**
3. Ili pritisnite `Ctrl+K V`

---

## Metoda 3: Mermaid Editor Extension

### Instalacija
1. `Ctrl+Shift+X` → Extensions
2. Pretražite: **"Mermaid Editor"**
3. Install

### Korišćenje
1. Otvorite `ARCHITECTURE_DIAGRAMS.md`
2. Selektujte Mermaid kod (između \`\`\`mermaid i \`\`\`)
3. Desni klik → **"Open in Mermaid Editor"**
4. Dijagram će se otvoriti u novom tabu

---

## Metoda 4: Online Mermaid Live Editor

### Ako Extensions ne rade:
1. Otvorite: https://mermaid.live/
2. Kopirajte Mermaid kod iz fajla (između \`\`\`mermaid i \`\`\`)
3. Paste u editor
4. Dijagram će biti automatski renderovan
5. Možete exportovati kao PNG/SVG

### Primer:
```
1. Otvori ARCHITECTURE_DIAGRAMS.md
2. Nađi dijagram (npr. "AS-IS Sistemski Pregled")
3. Kopiraj kod između ```mermaid i ```
4. Paste na https://mermaid.live/
5. Vidi dijagram!
```

---

## Metoda 5: GitHub Preview

### Ako push-ujete na GitHub:
1. Push fajl na GitHub repository
2. Otvori fajl na GitHub.com
3. GitHub automatski renderuje Mermaid dijagrame
4. Dijagrami su interaktivni!

---

## 🔧 Troubleshooting

### Problem: Dijagrami se ne prikazuju

**Rešenje 1: Proveri Extension**
```
1. Ctrl+Shift+X
2. Proveri da li je "Markdown Preview Mermaid Support" instaliran
3. Ako nije, instaliraj ga
4. Restartuj VS Code
```

**Rešenje 2: Proveri Markdown Preview**
```
1. Otvori ARCHITECTURE_DIAGRAMS.md
2. Ctrl+Shift+V
3. Ako ne radi, probaj Ctrl+K V
```

**Rešenje 3: Koristi Online Editor**
```
1. Idi na https://mermaid.live/
2. Copy/paste dijagram kod
3. Vidi dijagram online
```

### Problem: Extension ne radi

**Rešenje:**
```
1. Zatvori VS Code
2. Otvori ponovo
3. Ctrl+Shift+P → "Developer: Reload Window"
4. Probaj ponovo
```

---

## 📸 Export Dijagrama kao Slike

### Iz Mermaid Live Editor:
1. Otvori https://mermaid.live/
2. Paste dijagram kod
3. Klikni **"Actions"** → **"PNG"** ili **"SVG"**
4. Download sliku

### Iz VS Code (sa Markdown Preview Enhanced):
1. Otvori preview (`Ctrl+K V`)
2. Desni klik na dijagram
3. **"Save Image As..."**
4. Sačuvaj kao PNG

---

## 🎨 Primer Korišćenja

### Korak po Korak:

1. **Otvori VS Code**
   ```
   File → Open Folder → d:/POSAO/alankar/bobcarrental
   ```

2. **Instaliraj Extension**
   ```
   Ctrl+Shift+X
   Search: "Markdown Preview Mermaid Support"
   Install
   ```

3. **Otvori Dijagrame**
   ```
   File → Open → docs/ARCHITECTURE_DIAGRAMS.md
   ```

4. **Vidi Preview**
   ```
   Ctrl+Shift+V (ili Ctrl+K V za split view)
   ```

5. **Scroll i Uživaj!**
   ```
   Svi dijagrami su renderovani i interaktivni
   ```

---

## 📋 Lista Dijagrama u Fajlu

1. ✅ AS-IS Sistemski Pregled
2. ✅ AS-IS Data Flow
3. ✅ AS-IS Limitations (Mindmap)
4. ✅ TO-BE Sistemski Pregled (Najveći)
5. ✅ TO-BE Data Flow
6. ✅ Security Architecture
7. ✅ Database Schema (ER Diagram)
8. ✅ Technology Stack Comparison
9. ✅ Deployment Architecture

---

## 💡 Tips & Tricks

### Zoom In/Out u Preview:
- `Ctrl + Mouse Wheel` - Zoom
- `Ctrl + 0` - Reset zoom

### Search u Dijagramu:
- `Ctrl + F` - Pretraži tekst u dijagramu

### Copy Dijagram:
- Desni klik na dijagram → Copy Image
- Paste u Word, PowerPoint, etc.

### Dark Mode:
- VS Code automatski prilagođava boje dijagrama
- Dijagrami izgledaju dobro i u dark i u light mode

---

## 🚀 Quick Start (30 sekundi)

```bash
# 1. Instaliraj extension
Ctrl+Shift+X → "Markdown Preview Mermaid Support" → Install

# 2. Otvori fajl
File → Open → bobcarrental/docs/ARCHITECTURE_DIAGRAMS.md

# 3. Vidi preview
Ctrl+Shift+V

# 4. Gotovo! 🎉
```

---

## 📞 Pomoć

Ako i dalje ne možete da vidite dijagrame:
1. Koristite https://mermaid.live/ (100% radi)
2. Ili push na GitHub (automatski renderuje)
3. Ili exportujte kao PNG iz online editora

---

**Napomena**: Mermaid dijagrami su tekstualni, tako da možete uvek čitati kod i razumeti strukturu čak i bez renderovanja!