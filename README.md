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

