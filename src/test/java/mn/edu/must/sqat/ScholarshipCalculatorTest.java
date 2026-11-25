package mn.edu.must.sqat;

// ЛАБ 11 — хүчтэй болгосон тестийн багц.
// B1: assertNotNull / assertTrue(муж) шалгалтуудыг assertEquals(яг утга) болгосон.
// B2: зааг утгын (boundary) тестүүд нэмсэн.

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ScholarshipCalculatorTest {

    private static final double EPS = 1e-9;

    ScholarshipCalculator calc;

    @BeforeEach
    void setUp() { calc = new ScholarshipCalculator(); }

    // ---- letterGrade: муж бүрт ЯГ ямар үсэг буцаахыг шалгана ----

    @Test
    void letterGrade_returnsExactLetterForAllRanges() {
        assertEquals("A", calc.letterGrade(95));
        assertEquals("B", calc.letterGrade(85));
        assertEquals("C", calc.letterGrade(75));
        assertEquals("D", calc.letterGrade(65));
        assertEquals("F", calc.letterGrade(30));
    }

    @Test
    void letterGrade_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-1));
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(101));
    }

    // ---- gpa: голчийн ЯГ утгыг шалгана ----

    @Test
    void gpa_returnsExactAverage() {
        assertEquals(3.5, calc.gpa(new double[]{95, 85}), EPS); // (4 + 3) / 2
        assertEquals(1.5, calc.gpa(new double[]{75, 65}), EPS); // (2 + 1) / 2
        assertEquals(0.0, calc.gpa(new double[]{40}), EPS);     // F = 0
    }

    @Test
    void gpa_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(null));
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[]{}));
    }

    // ---- scholarship: дүрэм бүрийн ЯГ хувийг шалгана ----

    @Test
    void scholarship_returnsExactPercent() {
        assertEquals(100, calc.scholarship(3.9, 16, false));
        assertEquals(70,  calc.scholarship(3.5, 13, false));
        assertEquals(50,  calc.scholarship(3.1, 5, false));
        assertEquals(0,   calc.scholarship(2.0, 1, false));
        assertEquals(0,   calc.scholarship(4.0, 20, true));
    }

    @Test
    void scholarship_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.1, 10, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(3.0, -1, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(-0.1, 10, false));
    }

    // =====================================================================
    // B2: ЗААГ УТГЫН ТЕСТҮҮД
    // =====================================================================

    // ---- letterGrade: заг бүрийн хоёр талд ----

    @ParameterizedTest(name = "letterGrade({0}) = {1}")
    @CsvSource({
            "100,   A",   // дээд хязгаар (score > 100 -> score >= 100 мутантыг алана)
            "90,    A",   // A-гийн доод заг
            "89.99, B",
            "80,    B",
            "79.99, C",
            "70,    C",
            "69.99, D",
            "60,    D",
            "59.99, F",
            "0,     F"    // доод хязгаар (score < 0 -> score <= 0 мутантыг алана)
    })
    void letterGrade_boundaries(double score, String expected) {
        assertEquals(expected, calc.letterGrade(score));
    }

    @Test
    void letterGrade_justOutsideRange_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-0.01));
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(100.01));
    }

    // ---- gpa: switch-ийн үсэг бүрийг ТУСАД НЬ ----

    @ParameterizedTest(name = "gpa([{0}]) = {1}")
    @CsvSource({
            "95, 4.0",   // A
            "85, 3.0",   // B
            "75, 2.0",   // C
            "65, 1.0",   // D
            "40, 0.0"    // F (default салаа)
    })
    void gpa_singleScore_eachLetter(double score, double expected) {
        assertEquals(expected, calc.gpa(new double[]{score}), EPS);
    }

    @Test
    void gpa_allLetters_average() {
        // (4 + 3 + 2 + 1 + 0) / 5 = 2.0
        assertEquals(2.0, calc.gpa(new double[]{95, 85, 75, 65, 40}), EPS);
    }

    @Test
    void gpa_invalidScoreInside_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[]{90, 101}));
    }

    // ---- scholarship: 3.8/15, 3.4/12, 3.0 заагууд ----

    @Test
    void scholarship_exactly38and15_gets100() {
        assertEquals(100, calc.scholarship(3.8, 15, false));
    }

    @Test
    void scholarship_justBelow38_gets70() {
        assertEquals(70, calc.scholarship(3.79, 15, false));
    }

    @Test
    void scholarship_38butCredits14_gets70() {
        assertEquals(70, calc.scholarship(3.8, 14, false));
    }

    @Test
    void scholarship_exactly34and12_gets70() {
        assertEquals(70, calc.scholarship(3.4, 12, false));
    }

    @Test
    void scholarship_justBelow34_gets50() {
        assertEquals(50, calc.scholarship(3.39, 12, false));
    }

    @Test
    void scholarship_34butCredits11_gets50() {
        assertEquals(50, calc.scholarship(3.4, 11, false));
    }

    @Test
    void scholarship_exactly30_gets50() {
        assertEquals(50, calc.scholarship(3.0, 0, false));
    }

    @Test
    void scholarship_justBelow30_gets0() {
        assertEquals(0, calc.scholarship(2.99, 20, false));
    }

    // ---- scholarship: оролтын хязгаар (gpa 0 ба 4, credits 0) ----

    @Test
    void scholarship_gpaZero_isValid() {
        assertEquals(0, calc.scholarship(0.0, 10, false));
    }

    @Test
    void scholarship_gpaFour_isValid() {
        assertEquals(100, calc.scholarship(4.0, 15, false));
    }

    @Test
    void scholarship_creditsZero_isValid() {
        assertEquals(50, calc.scholarship(3.9, 0, false));
    }

    @Test
    void scholarship_violation_alwaysZero() {
        assertEquals(0, calc.scholarship(4.0, 20, true));
        assertEquals(0, calc.scholarship(3.5, 13, true));
        assertEquals(0, calc.scholarship(3.1, 5, true));
    }
}
