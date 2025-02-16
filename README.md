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
              - [ ] Google Maps SDK beállítása
              - [ ] Alapértelmezett pozíció megadása
              - [ ] Zoom és térképstílus beállítások konfigurálása
           - ### 📍 GPS információk lekérése
              - [ ] Helymeghatározási engedélyek kérése
              - [ ] Valós idejű helyzetfrissítés implementálása
           - ### 💅Alap UI elemek kialakítása
              - [ ] Gombok létrehozása
              - [ ] Különböző menük és elemek alapjai
     
      - ## 2. Alapfunkciók (Week 3-4)
           - ### 🛣️ Útvonal rögzítés
              - [ ] Lokáció adatok folyamatos lekérése rögzítés közben
              - [ ] Rögzített pontok listájának kezelése
              - [ ] Időbélyegek tárolása minden ponthoz
           - ### 🔛 Útvonal megjelenítés térképen
              - [ ] Rögzített pontok térképre rajzolása valós időben
           - ### 📏 Idő és távolság mérése
              - [ ] Távolság számítása GPS koordináták alapján
              - [ ] Időmérés hozzáadása az útvonalhoz
    
      - ## 3. Statisztikai Adatok (Week 5-6)
           - ### ⏱️ Sebességszámítás
              - [ ] Átlagsebesség számítása az út során
              - [ ] Maximális sebesség mérése
           - ### ⛰️ Szintemelkedés kiszámítása
              - [ ] Magassági adatok lekérése az útvonal mentén
              - [ ] Összes emelkedés és ereszkedés kiszámítása
           - ### 📊 Összegzett útvonal statisztikák megjelenítése
              - [ ] Távolság, idő és sebesség kijelzése UI-ban
              - [ ] Grafikon készítése sebesség/idő viszonyáról
    
      - ## 4. Mentés és Visszatöltés (Week 7-8)
           - ### ⛃ Útvonalak mentése SQLite adatbázisba
              - [ ] Útvonal adatok (koordináták, időbélyegek, statisztikák) elmentése
              - [ ] Adatmodell kialakítása az adatok tárolásához
           - ### 💾 Előzmények listázása
              - [ ] Korábbi útvonalak megjelenítése egy listában
              - [ ] Kiválasztott útvonal megjelenítése térképen
    
      - ## 5. UI Finomhangolás és Extra Funkciók (Week 9-10)


  - ## 🛠️ Technológiák
    
       - Kotlin + Jetpack Compose
    
       - Google Maps SDK
    
       - FusedLocationProvider API
    
       - SQLite
