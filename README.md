# Filehandler
[![Build Status](https://travis-ci.com/j4v4d3m0/filehandler.svg?branch=main)](https://travis-ci.com/j4v4d3m0/filehandler)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=j4v4d3m0_filehandler&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=j4v4d3m0_filehandler)

## Fordítás
```bash
git clone https://github.com/j4v4d3m0/filehandler.git
cd filehandler
./mvnw -DskipTests package
```
## Futtatás
```bash
java -jar target/filehandler-1.0.0.jar -i INCOMING
```

## Próbafeladat

Készíts egy futtatható java alkalmazást, ami az alábbi funkciókat valósítja meg:

Figyel egy magadott mappát, ahova file-ok érkezhetnek (INCOMING). Ezt, mint paramétert a -i kapcsolóval adhatjuk meg a programnak. Ez a paraméter kötelező jellegű. Legyen képes kezelni relatív és teljes elérésű útvonalakat is. Ha a mappa nem létezik, akkor a programnak létre kell azt hoznia.
A file-ok tartalmától függően más-más tevékenységet végez velük. A típusok a következők lehetnek:
image - Kép file. Lehetséges formátumok: bmp, jpg, gif, png
text - Nyers szöveg file.
Kép esetén készítsen a program a file adattartalmából egy szürkeségi-fokozatú hisztogrammot és ezt mentse el egy bitmap file-ba, amelynek a neve az eredeti file (kiterjesztés nélküli) neve és egy “-hist” toldalék.
Például:
Eredeti file: macska.jpg
Hisztogram file: macska-hist.bmp

A hisztogram file értelemszerűen 256 pixel széles és magassága pedig fixen 100 pixel legyen.

Szöveges állomány esetén gyűjtse ki azokat a szópárokat, amelyek N darabnál többször fordulnak elő a szövegben. Ezt a paramétert a -n kapcsolóval adhassuk meg a programnak. Alapértelmezett értéke: 3. A szópárok detektálásánál a whitespace karakterek és a kis-nagybetű differenciák nem számítanak. A program kimeneti állománya egy .txt file legyen amiben előfordulás szerinti csökkenő sorrendben felsorolja a szópárokat és az előfordulásuk számosságát sorokra bontva és kettősponttal tagolva.
Például:
jó dobás : 56
szép leány : 41
adjon isten : 33
lusta inas : 7

A sikeresen feldolgozott és a legyártott file-okat átmozgatja az INCOMING mappa alatt lévő <tipus>-done almappákba. Például: image-done, text-done
Azon állományokat, amelyekkel nem tudott a program megbírkózni, a <tipus>-error mappába kell helyezni a programnak, szintén az INCOMING mappáján belül.
A program legyen képes kihasználni a többmagos processzorok adta lehetőségeket és tudjon több szálon is feldolgozni a munkafolyamat során. A használható szálak számát a -t kapcsolóval lehessen megadni. Alapértelmezett értéke: 1.
A program fejezze be a működését, ha az INCOMING mappába X másodpercig nem érkezett már feldolgozandó file. Ezt az értéket a -d kapcsolóval lehessen megadni a programnak. Alapértelmezett értéke: 60.
Átadási kritériumok

Az adott github repository-ban legyen a forráskód átadva. A kódot szabadon lehessen fordítani egy ‘git clone’ után mindenféle fordítóeszköz telepítése nélkül, parancssorból. Természetesen a Java SDK megléte elvárható. :) Magyarul használj Gradle vagy Maven wrapper-t. A minimum Java SDK level legyen 8, de ettől felfele el lehet térni igény szerint.
