# Sport-tracker
  - Túra és Futó Ütvonal Rögzítő App

  - ## 📌 Projektról

       - Ez az Android-alkalmazás lehetővé teszi a felhasználók számára, rögzítsék futásaikat, túráikat vagy bármilyen szabadtéri mozgásukat. A rögzített útvonal megjelenik a térképen, kiegészítve statisztikai adatokkal (pl. megtett távolság, időtartam, átlagsebesség).

  - ## ✨ Funkciók 

       - ✓ Valós idejű GPS-alapú helymeghatározás.
      
       - ✓ Az útvonal megjelenítése a térképen.
      
       - ✓ Statisztikák kiszámítása (távolság, idő, sebesség).
      
       - ✓ Rögzített útvonalak mentése és visszanézhetősége.


  - # 📅 Fejlesztési Ütemterv

      - ## 1. Alapstruktúra kialakítása (Week 1-2)
           - ### 🗺️Térkép megjelenítése
              - [X] Google Maps SDK beállítása
              - [X] Alapértelmezett pozíció megadása
              - [X] Zoom és térképstílus beállítások konfigurálása
           - ### 📍 GPS információk lekérése
              - [X] Helymeghatározási engedélyek kérése
              - [X] Valós idejű helyzetfrissítés implementálása
           - ### 💅Alap UI elemek kialakítása
              - [X] Gombok létrehozása
              - [X] Különböző menük és elemek alapjai
     
      - ## 2. Alapfunkciók (Week 3-4)
           - ### 🛣️ Útvonal rögzítés
              - [X] Lokáció adatok folyamatos lekérése rögzítés közben
              - [X] Rögzített pontok listájának kezelése
              - [X] Időbélyegek tárolása minden ponthoz
           - ### 🔛 Útvonal megjelenítés térképen
              - [X] Rögzített pontok térképre rajzolása valós időben
           - ### 📏 Idő és távolság mérése
              - [X] Távolság számítása GPS koordináták alapján
              - [X] Időmérés hozzáadása az útvonalhoz
    
      - ## 3. Statisztikai Adatok (Week 5-6)
           - ### ⏱️ Sebességszámítás
              - [X] Átlagsebesség számítása az út során
              - [X] Maximális sebesség mérése
           - ### ⛰️ Szintemelkedés kiszámítása
              - [X] Magassági adatok lekérése az útvonal mentén
              - [X] Összes emelkedés és ereszkedés kiszámítása
           - ### 📊 Összegzett útvonal statisztikák megjelenítése
              - [X] Távolság, idő és sebesség kijelzése UI-ban
              - [X] Grafikon készítése sebesség/idő viszonyáról
    
      - ## 4. Mentés és Visszatöltés (Week 7-8)
           - ### ⛃ Útvonalak mentése SQLite adatbázisba
              - [X] Útvonal adatok (koordináták, időbélyegek, statisztikák) elmentése
              - [X] Adatmodell kialakítása az adatok tárolásához
           - ### 💾 Előzmények listázása
              - [X] Korábbi útvonalak megjelenítése egy listában
              - [X] Kiválasztott útvonal megjelenítése térképen
    
      - ## 5. UI Finomhangolás és Extra Funkciók (Week 9-10)


  - ## 🛠️ Technológiák
    
       - Kotlin + Jetpack Compose
    
       - Google Maps SDK
    
       - FusedLocationProvider API
    
       - Room over SQLite
