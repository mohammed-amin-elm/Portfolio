# 🖨️ Custom `printf` Implementation

Dit project is een **from-scratch implementatie van de klassieke `printf` functie** zoals gebruikt in C-standaardbibliotheken.  
De focus ligt op het correct parsen van format-specifiers, efficiënt werken met variadische functies en robuuste low-level stringformattering.

Deze implementatie is bedoeld als leerproject om een beter inzicht te krijgen in:
- Hoe `printf` intern werkt  
- Parsing van format-strings  
- Variadic arguments (`va_list`, `va_start`, `va_end`)  
- Memory- en bufferbeheer  
- Typeformattering (ints, chars, strings, hexadecimaal, etc.)  

---

## ✨ Features

- Ondersteuning voor veelgebruikte format-specifiers:
  - `%d`, `%i` — signed integers  
  - `%u` — unsigned integers  
  - `%s` — strings  
  - `%%` — literal `%`
- Variadische argumentverwerking  
- Eigengemaakte number-to-string converters  
- Geen afhankelijkheid van de standaard `printf`  
- Compact en begrijpelijk codeontwerp