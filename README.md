# Лаборатори №11: Mutation testing — PIT-ээр тестийн чанараа хэмжих

**F.CSA313 — Программ хангамжийн чанарын баталгаа ба тест (2026)**

| | |
|---|---|
| **Овог нэр** | А.Төгөлдөр |
| **Оюутны код** | B242270001 |

### Орчин (`results/env.txt`)

Бүх хэмжилтийг Windows 11 дээрх **WSL2 (Ubuntu)** орчинд ажиллуулсан.

```
$ java -version
openjdk version "17.0.20.1" 2026-08-18
OpenJDK Runtime Environment (build 17.0.20.1+1-1-26.04-Ubuntu)
OpenJDK 64-Bit Server VM (build 17.0.20.1+1-1-26.04-Ubuntu, mixed mode, sharing)

$ mvn -version
Apache Maven 3.9.12
Maven home: /usr/share/maven
Java version: 17.0.20.1, vendor: Ubuntu, runtime: /usr/lib/jvm/java-17-openjdk-amd64
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "6.18.33.2-microsoft-standard-wsl2", arch: "amd64", family: "unix"
```

> **Тэмдэглэл (орчны асуудлууд):**
> 1. WSL-ийн анхдагч Java 25 дээр PIT 1.16.1 нэг ч мутант үүсгэж чадаагүй (`No mutations found`). Тиймээс `JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64` болгож Java 17-оор ажиллуулсан.
> 2. Миний локал хавтасны замд кирилл үсэг (`Программ хангамжийн...`) байгаа үед PIT мөн `No mutations found` гэж унасан. Үүнийг тойрохын тулд PIT-ийг ASCII замтай хавтас (`~/lab11-pit`) руу `pom.xml` + `src/`-ийг хуулж ажиллуулсан. Clone хийсэн хавтасны зам ASCII бол ямар нэг тохиргоо шаардлагагүй.

### Ажиллуулах

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64   # Java 17+ (PIT 1.16.1)
mvn clean verify                                       # JUnit + JaCoCo
mvn test-compile org.pitest:pitest-maven:mutationCoverage   # PIT -> target/pit-reports/index.html
```

---

## 1. Эхний хэмжилт (starter тестийн багц)

### JaCoCo — Line 100% (25/25), Branch 38/41

`results/jacoco-start.csv`:

```
LINE_MISSED=0, LINE_COVERED=25, BRANCH_MISSED=3, BRANCH_COVERED=38
```

![JaCoCo starter](images/jacoco-start.png)

### PIT — Mutation Coverage 27% (17/63)

`results/pit-start.txt`:

```
>> Generated 63 mutations Killed 17 (27%)
>> Mutations with no coverage 0. Test strength 27%
```

![PIT starter](images/pit-start.png)

### Ажиглалт: Line Coverage ба Mutation Coverage-ийн зөрүү

JaCoCo мөр бүрийг 100% (25/25) ажилласан гэж харуулж байгаа ч PIT-ийн 63 мутантаас ердөө 17 нь (27%) алагдсан. Энэ нь тестүүд кодыг *ажиллуулж* байгаа боловч гаралтыг нь *шалгахгүй* байна гэсэн үг: `assertNotNull`, `assertTrue(0 <= x <= 100)` зэрэг сул assertion нь `"A"`-г `""` болгох, `100`-г `0` болгох өөрчлөлтийг ч барьж чаддаггүй. Хамралт нь "аль мөр ажилласныг" хэмждэг бол mutation score нь "алдаа гарвал тест унах эсэхийг" хэмждэг. Тиймээс 100% хамралт нь тестийн чанарыг баталгаажуулдаггүй.

---

## A. Амьд мутантуудын ангилал (starter: 46 SURVIVED)

PIT тайлангийн мөр бүрийн дэлгэрэнгүй (ногоон = алагдсан, улаан = амьд):

![PIT starter detail](images/pit-start-detail.png)

Мөрийн дугаар нь `ScholarshipCalculator.java`-ийн мөр. Нэг мөрөнд ижил шалтгаантай хэд хэдэн мутант байвал нэгтгэсэн.

| Мөр | Код | Мутаци (PIT-ийн тайлбар) | Тоо | Ангилал | Үндэслэл | Арга хэмжээ (алсан тест) |
|---|---|---|---|---|---|---|
| 20 | `score < 0 \|\| score > 100` | changed conditional boundary (`<` → `<=`, `>` → `>=`) | 2 | Тест дутуу | `letterGrade(0)` ба `letterGrade(100)` хүчинтэй оролт боловч мутантад exception шиддэг тул зан төлөв өөрчлөгддөг. | B2: `letterGrade_boundaries` (0 → "F", 100 → "A") |
| 23 | `score >= 90` | changed conditional boundary (`>=` → `>`) | 1 | Тест дутуу | `score = 90` үед "A"-гийн оронд "B" буцаадаг. | B2: `letterGrade_boundaries` (90 → "A") |
| 24 | `score >= 80` | changed conditional boundary | 1 | Тест дутуу | `score = 80` үед "B"-гийн оронд "C" буцаадаг. | B2: `letterGrade_boundaries` (80 → "B") |
| 25 | `score >= 70` | changed conditional boundary | 1 | Тест дутуу | `score = 70` үед "C"-гийн оронд "D" буцаадаг. | B2: `letterGrade_boundaries` (70 → "C") |
| 26 | `score >= 60` | changed conditional boundary | 1 | Тест дутуу | `score = 60` үед "D"-гийн оронд "F" буцаадаг. | B2: `letterGrade_boundaries` (60 → "D") |
| 23–26 | `if (score >= N)` | removed conditional — replaced comparison check with false / with true | 8 | Тест дутуу | Нөхцлийг үргэлж true/false болгоход буруу үсэг буцдаг, гэхдээ `assertNotNull` үүнийг ялгадаггүй. | B1: `assertEquals("A"...)`; B2: `letterGrade_boundaries` |
| 23–27 | `return "A"/"B"/"C"/"D"/"F"` | replaced return value with "" | 5 | Тест дутуу | `""` нь null биш тул `assertNotNull` давдаг, бодит утга нь өөр. | B1: `letterGrade_returnsExactLetterForAllRanges`; B2 |
| 36 | `switch (letterGrade(s))` | Changed switch default to be first case | 1 | Тест дутуу | Switch-ийн default ба эхний case-ийн үсрэлтийг сольсноор зарим үсэг буруу салаа руу орж голч өөрчлөгддөг; `0 <= gpa <= 4` муж үүнийг барихгүй. | B2: `gpa_singleScore_eachLetter` (үсэг бүрийг тусад нь) |
| 38 | `total += 3` (B) | Replaced double addition with subtraction | 1 | Тест дутуу | B үсэг +3-ын оронд −3 болгож голчийг өөрчилдөг; муж шалгалт үүнийг барихгүй. | B1: `gpa_returnsExactAverage` (3.5); B2: `gpa_allLetters_average` |
| 40 | `total += 1` (D) | Replaced double addition with subtraction | 1 | Тест дутуу | D үсэг +1-ийн оронд −1 болгодог (голч 1.5 → 0.5). | B1/B2: `gpa_singleScore_eachLetter` (65 → 1.0) |
| 44 | `return total / scores.length` | replaced double return with 0.0d | 1 | Тест дутуу | 0.0 нь `[0, 4]` мужид багтдаг тул сул assertion давдаг, бодит голч өөр. | B1: `gpa_returnsExactAverage` |
| 48 | `gpa < 0 \|\| gpa > 4 \|\| credits < 0` | changed conditional boundary (`gpa < 0` → `<= 0`, `credits < 0` → `<= 0`) | 2 | Тест дутуу | gpa = 0 ба credits = 0 хүчинтэй оролт боловч мутантад exception шиддэг. | B2: `scholarship_gpaZero_isValid`, `scholarship_exactly30_gets50` (credits = 0) |
| 48 | дээрхтэй ижил | removed conditional — replaced comparison check with true | 1 | Тест дутуу | Хязгаарын шалгалтын нэг хэсгийг алгасахад хүчинтэй оролт exception шиддэг. | B2: `scholarship_exactly38and15_gets100` |
| 51 | `if (hasViolation) return 0` | removed conditional — replaced equality check with false / true | 2 | Тест дутуу | Зөрчилтэй оюутан 100% авах, эсвэл бүх оюутан 0% авах болдог; муж шалгалт `[0, 100]` хоёуланг нь давдаг. | B1: `scholarship_returnsExactPercent`; B2: `scholarship_violation_alwaysZero` |
| 52 | `gpa >= 3.8 && credits >= 15` | changed conditional boundary (`>=` → `>`) ×2 | 2 | Тест дутуу | Яг 3.8 / 15-тай оюутан 100-ын оронд 70 авдаг. | B2: `scholarship_exactly38and15_gets100` |
| 52 | дээрхтэй ижил | removed conditional — with false ×2 / with true ×2 | 4 | Тест дутуу | Нэг нөхцлийг алгасахад кредит хүрэхгүй (3.8, 14) оюутан 100 авах, эсвэл 100 авах ёстой нь 70 авна. | B1 + B2: `scholarship_38butCredits14_gets70` |
| 53 | `gpa >= 3.4 && credits >= 12` | changed conditional boundary ×2 | 2 | Тест дутуу | Яг 3.4 / 12-той оюутан 70-ын оронд 50 авдаг. | B2: `scholarship_exactly34and12_gets70` |
| 53 | дээрхтэй ижил | removed conditional — with false ×2 / with true ×2 | 4 | Тест дутуу | (3.8, 14) эсвэл (3.4, 11) оюутны дүн өөрчлөгддөг. | B2: `scholarship_38butCredits14_gets70`, `scholarship_34butCredits11_gets50` |
| 54 | `gpa >= 3.0` | changed conditional boundary | 1 | Тест дутуу | Яг 3.0-той оюутан 50-ын оронд 0 авдаг. | B2: `scholarship_exactly30_gets50` |
| 54 | дээрхтэй ижил | removed conditional — with false / with true | 2 | Тест дутуу | 3.0-аас доош оюутан 50 авах, эсвэл 3.0+ оюутан 0 авах болдог. | B2: `scholarship_justBelow30_gets0`, `scholarship_34butCredits11_gets50` |
| 52–54 | `return 100 / 70 / 50` | replaced int return with 0 | 3 | Тест дутуу | 0 нь `[0, 100]` мужид багтдаг тул сул assertion давдаг. | B1: `scholarship_returnsExactPercent` |
| | | **Нийт** | **46** | **46 тест дутуу, 0 equivalent** | | |

**Дүгнэлт:** 46 амьд мутант бүгд **тест дутуу** ангилалд орсон, нэг ч **equivalent** мутант байхгүй. Мутант бүрийн хувьд бодит зан төлөвийг өөрчилдөг оролт олдсон. Үүнийг B2-ын дараа 63/63 мутант бүгд алагдсанаар баталсан. Эдгээрээс:
- **conditional boundary** мутантууд (13) нь **зааг утгын тест дутууг** заасан → B2-оор засав;
- **return value / math / removed conditional** мутантууд нь **assertion сул** байгааг заасан → B1-ээр ихэнхийг нь засав.

