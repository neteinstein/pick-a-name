package org.neteinstein.pickaname.domain.model

/**
 * Domain model representing a name.
 * This is the business logic representation, separate from database entity.
 */
data class Name(
    val id: Long,
    val name: String,
    val gender: Gender,
    val notes: String
) {
    enum class Gender {
        MALE,
        FEMALE,
        UNSPECIFIED;
        
        companion object {
            fun fromString(value: String): Gender {
                return when (value.uppercase()) {
                    "M" -> MALE
                    "F" -> FEMALE
                    else -> UNSPECIFIED
                }
            }
        }
    }
    
    fun getGenderDisplayText(): String {
        return when (gender) {
            Gender.MALE -> "Male"
            Gender.FEMALE -> "Female"
            Gender.UNSPECIFIED -> ""
        }
    }
}
