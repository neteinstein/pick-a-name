package org.neteinstein.pickaname.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.neteinstein.pickaname.domain.model.Name

class NameModelTest {
    
    @Test
    fun `Gender fromString returns MALE for M`() {
        val gender = Name.Gender.fromString("M")
        assertThat(gender).isEqualTo(Name.Gender.MALE)
    }
    
    @Test
    fun `Gender fromString returns FEMALE for F`() {
        val gender = Name.Gender.fromString("F")
        assertThat(gender).isEqualTo(Name.Gender.FEMALE)
    }
    
    @Test
    fun `Gender fromString returns UNSPECIFIED for empty string`() {
        val gender = Name.Gender.fromString("")
        assertThat(gender).isEqualTo(Name.Gender.UNSPECIFIED)
    }
    
    @Test
    fun `Gender fromString is case insensitive`() {
        assertThat(Name.Gender.fromString("m")).isEqualTo(Name.Gender.MALE)
        assertThat(Name.Gender.fromString("f")).isEqualTo(Name.Gender.FEMALE)
    }
    
    @Test
    fun `getGenderDisplayText returns correct text for MALE`() {
        val name = Name(1, "João", Name.Gender.MALE, "")
        assertThat(name.getGenderDisplayText()).isEqualTo("Male")
    }
    
    @Test
    fun `getGenderDisplayText returns correct text for FEMALE`() {
        val name = Name(1, "Maria", Name.Gender.FEMALE, "")
        assertThat(name.getGenderDisplayText()).isEqualTo("Female")
    }
    
    @Test
    fun `getGenderDisplayText returns empty for UNSPECIFIED`() {
        val name = Name(1, "Pat", Name.Gender.UNSPECIFIED, "")
        assertThat(name.getGenderDisplayText()).isEmpty()
    }
}
