# Sistem Parkir CLI - Creative Box Bintaro

Tugas Kelompok PBO (Java OOP, TM02-TM06) - Sistem Informasi FTI UNTAR.
Program kasir parkir berbasis CLI yang dibuat dari 8 struk parkir motor asli Creative Box Bintaro.

## Cara menjalankan

```
javac -d bin src/*.java
java -cp bin App        # program utama
java -cp bin UjiStruk   # uji tarif terhadap 8 struk asli
```

Atau buka folder ini di VS Code lalu klik **Run** di `App.java`.

## Menu

1. Kendaraan masuk (waktu otomatis dari jam komputer)
2. Kendaraan keluar & bayar (Flazz / eMoney)
3. Lihat semua transaksi
4. Cetak ulang struk
5. Total pendapatan
0. Simpan & keluar (data disimpan ke `transaksi.txt`)

## Aturan tarif

| Kendaraan | Durasi | Tarif |
|---|---|---|
| Motor | < 1 jam | Rp3.000 (dari struk) |
| Motor | 1 - 4 jam | Rp5.000 (dari struk) |
| Motor | > 4 jam | Rp5.000 + Rp2.000/jam lebih (asumsi) |
| Mobil | jam pertama | Rp5.000 + Rp4.000/jam berikutnya (asumsi) |

## Validasi input

Input yang tidak valid ditolak dan diminta ulang, tidak langsung masuk sistem:

- Menu harus angka 0-5
- Jenis kendaraan harus 1 atau 2
- Plat nomor wajib diisi dan berformat plat Indonesia (contoh `B 1234 XYZ`)
- Kendaraan yang masih parkir tidak bisa masuk lagi
- Metode bayar harus 1 atau 2, nomor kartu 16 digit angka, saldo angka bulat tidak negatif
- Saldo kurang memunculkan `SaldoTidakCukupException`, transaksi tetap belum lunas
- Baris rusak di `transaksi.txt` dilewati saat program dibuka

## Format `transaksi.txt`

```
id;jenis;plat;gerbangMasuk;waktuMasuk;gerbangKeluar;waktuKeluar;metode;noKartu;saldo;tid;biaya
```

## Kelompok

Wira, Rico, Dave
